import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Panel sortowania odpadów.
 * Umożliwia przeciąganie zebranych śmieci do odpowiednich koszy
 * oraz podsumowuje wyniki poziomu.
 */
public class SortingPanel extends JPanel implements MouseListener, MouseMotionListener {
    /** Referencja do głównego okna gry (zarządza zmianą paneli i poziomami). */
    private EcoJumperGame parent;
    /** Generator losowości używany do tasowania śmieci i losowania wariantów sprite'ów. */
    private Random rand = new Random();
    /** Ikona kosza na papier (PNG). */
    private Image binPaperImg;
    /** Ikona kosza na plastik (PNG). */
    private Image binPlasticImg;
    /** Ikona kosza na szkło (PNG). */
    private Image binGlassImg;

    /**
     * Pojedynczy element odpadu widoczny w panelu sortowania.
     * Zawiera prostokąt kolizji (pozycję), typ odpadu oraz sprite używany do rysowania.
     */
    private static class SortingTrash {
        /** Obszar (pozycja i rozmiar) śmiecia na panelu sortowania. */
        Rectangle rect;
        /** Typ odpadu (PAPER/PLASTIC/GLASS) potrzebny do sprawdzania poprawnego kosza. */
        TrashType type;
        /** Obrazek przypisany do śmiecia. */
        Image sprite;

        /**
         * Tworzy obiekt śmiecia do sortowania.
         *
         * @param rect obszar rysowania/kolizji
         * @param type typ odpadu
         * @param sprite obrazek odpadu
         */
        SortingTrash(Rectangle rect, TrashType type, Image sprite) {
            this.rect = rect;
            this.type = type;
            this.sprite = sprite;
        }
    }

    /**
     * Kosz na odpady w panelu sortowania.
     * Kosz ma obszar kolizji (Rectangle) i typ odpadu, który akceptuje.
     */
    private static class Bin {
        /** Obszar kosza na panelu (do sprawdzania "dropu"). */
        Rectangle rect;
        /** Typ odpadu, który powinien trafić do tego kosza. */
        TrashType type;
        /** Opcjonalna etykieta kosza (może być niewyświetlana). */
        String label;

        /**
         * Tworzy kosz na odpady.
         *
         * @param rect obszar kosza
         * @param type typ odpadu przypisany do kosza
         * @param label tekst etykiety (np. "PAPIER")
         */
        Bin(Rectangle rect, TrashType type, String label) {
            this.rect = rect;
            this.type = type;
            this.label = label;
        }
    }
    /** Lista śmieci (prostokąt + typ + sprite), które gracz musi posortować. */
    private ArrayList<SortingTrash> trashList = new ArrayList<>();
    /** Lista koszy do sortowania (prostokąt + typ). */
    private ArrayList<Bin> bins = new ArrayList<>();

    /** Aktualnie przeciągany element śmiecia (lub {@code null} jeśli nic nie jest przeciągane). */
    private SortingTrash dragged = null;
    /** Przesunięcie kursora względem lewego górnego rogu przeciąganego śmiecia (oś X i Y). */
    private int offsetX, offsetY;

    /** Dostępne warianty obrazów dla plastiku (różne kolory). */
    private Image[] plasticVariants;
    /** Dostępne warianty obrazów dla szkła (różne kolory). */
    private Image[] glassVariants;
    /** Dostępne warianty obrazów dla papieru (różne kolory). */
    private Image[] paperVariants;

    /** Numer aktualnego poziomu (wyświetlany w podsumowaniu). */
    private int levelNumber;
    /** Punkty zdobyte w bieżącym poziomie (po uwzględnieniu sortowania). */
    private int levelScore;
    /** Suma punktów ze wszystkich ukończonych poziomów w bieżącej sesji gry. */
    private int totalScore;
    /** Czas trwania bieżącego poziomu w milisekundach. */
    private long levelTimeMs;
    /** Łączny czas gry w milisekundach (suma czasów poziomów w sesji). */
    private long totalTimeMs;
    /** Flaga informująca, czy ukończony poziom jest ostatnim poziomem gry. */
    private boolean lastLevel;
    /** Tło panelu sortowania (PNG). */
    private Image backgroundImage;

    /**
     * Tworzy panel sortowania, wczytuje tło i ikony koszy oraz rejestruje obsługę myszy.
     *
     * @param parent główne okno gry wykorzystywane do przełączania paneli i zapisu wyników
     */
    public SortingPanel(EcoJumperGame parent) {
        this.parent = parent;
        addMouseListener(this);
        addMouseMotionListener(this);

        backgroundImage = new ImageIcon("assets/sort_bg.png").getImage();

        binPaperImg   = new ImageIcon("assets/bin_paper.png").getImage();
        binPlasticImg = new ImageIcon("assets/bin_plastic.png").getImage();
        binGlassImg   = new ImageIcon("assets/bin_glass.png").getImage();

        loadSprites();
    }

    /** Wczytuje zestawy sprite'ów dla papieru, plastiku i szkła (różne warianty kolorystyczne). */
    private void loadSprites() {
        plasticVariants = new Image[]{
                loadImage("assets/plastic_yellow.png"),
                loadImage("assets/plastic_red.png"),
                loadImage("assets/plastic_green.png")
        };

        glassVariants = new Image[]{
                loadImage("assets/glass_green.png"),
                loadImage("assets/glass_yellow.png"),
                loadImage("assets/glass_blue.png")
        };

        paperVariants = new Image[]{
                loadImage("assets/paper_grey.png"),
                loadImage("assets/paper_blue.png"),
                loadImage("assets/paper_yellow.png")
        };
    }

    /**
     * Wczytuje obrazek z pliku. Jeśli obraz nie został poprawnie załadowany, zwraca {@code null}.
     *
     * @param path ścieżka do pliku PNG w katalogu assets
     * @return wczytany obraz lub {@code null} w razie błędu
     */
    private Image loadImage(String path) {
        try {
            Image img = new ImageIcon(path).getImage();
            if (img.getWidth(null) <= 0) {
                System.out.println("Nie udało się wczytać: " + path);
                return null;
            }
            return img;
        } catch (Exception e) {
            System.out.println("Błąd wczytywania: " + path);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Losuje jeden wariant obrazu z tablicy (maksymalnie z pierwszych {@code maxCount} elementów).
     * Używane do utrudniania gry (kolory odpadów mogą być mylące na wyższych poziomach).
     *
     * @param variants tablica dostępnych wariantów obrazków
     * @param maxCount ile pierwszych wariantów wolno użyć (np. 1 na łatwych poziomach)
     * @return wylosowany obrazek lub {@code null}, jeśli brak poprawnych obrazków
     */
    private Image chooseRandom(Image[] variants, int maxCount) {
        ArrayList<Image> nonNull = new ArrayList<>();
        for (int i = 0; i < variants.length && i < maxCount; i++) {
            if (variants[i] != null) nonNull.add(variants[i]);
        }
        if (nonNull.isEmpty()) return null;
        return nonNull.get(rand.nextInt(nonNull.size()));
    }

    /**
     * Ładuje listę śmieci do posortowania oraz inicjalizuje kosze.
     * Wyświetla podsumowanie punktów i czasu po ukończeniu poziomu.
     *
     * @param paperCount liczba zebranych papierów
     * @param plasticCount liczba zebranych plastików
     * @param glassCount liczba zebranych szkieł
     * @param levelNumber numer poziomu
     * @param levelScore punkty zdobyte w poziomie
     * @param totalScore łączny wynik gracza
     * @param levelTimeMs czas trwania poziomu
     * @param totalTimeMs łączny czas gry
     * @param lastLevel informacja, czy jest to ostatni poziom
     */

    public void loadTrash(int paperCount, int plasticCount, int glassCount,
                          int levelNumber,
                          int levelScore,
                          int totalScore,
                          long levelTimeMs,
                          long totalTimeMs,
                          boolean lastLevel) {

        this.levelNumber = levelNumber;
        this.levelScore = levelScore;
        this.totalScore = totalScore;
        this.levelTimeMs = levelTimeMs;
        this.totalTimeMs = totalTimeMs;
        this.lastLevel = lastLevel;

        trashList.clear();
        bins.clear();

        int level = parent.getCurrentLevel();
        boolean easyColors = level <= 2;

        int plasticVariantsCount = easyColors ? 1 : plasticVariants.length;
        int glassVariantsCount   = easyColors ? 1 : glassVariants.length;
        int paperVariantsCount   = easyColors ? 1 : paperVariants.length;

        ArrayList<TrashType> types = new ArrayList<>();
        for (int i = 0; i < paperCount; i++)   types.add(TrashType.PAPER);
        for (int i = 0; i < plasticCount; i++) types.add(TrashType.PLASTIC);
        for (int i = 0; i < glassCount; i++)   types.add(TrashType.GLASS);

        Collections.shuffle(types, rand); // tasowanie śmieci, żeby elementy tego samego typu nie leżały obok siebie.

        int size     = 48;
        int startX   = 50;
        int startY   = 100;
        int spacingX = size + 15;
        int spacingY = size + 20;
        int perRow   = 8;

        for (int i = 0; i < types.size(); i++) {
            TrashType type = types.get(i);

            int col = i % perRow;
            int row = i / perRow;

            int x = startX + col * spacingX;
            int y = startY + row * spacingY;

            Image sprite = null;
            switch (type) {
                case PLASTIC -> sprite = chooseRandom(plasticVariants, plasticVariantsCount);
                case GLASS   -> sprite = chooseRandom(glassVariants,   glassVariantsCount);
                case PAPER   -> sprite = chooseRandom(paperVariants,   paperVariantsCount);
            }

            Rectangle rect = new Rectangle(x, y, size, size);
            trashList.add(new SortingTrash(rect, type, sprite));
        }

        int panelW = getWidth()  > 0 ? getWidth()  : 800;
        int panelH = getHeight() > 0 ? getHeight() : 600;

        int binW = 330;
        int binH = 240;


        int binY = panelH - binH - 40;

        int x1 = 0;
        int x3 =  binW * 2;

        bins.add(new Bin(new Rectangle(x1, binY, binW, binH), TrashType.PAPER,   "PAPIER"));
        bins.add(new Bin(new Rectangle(binW, binY, binW, binH), TrashType.PLASTIC, "PLASTIK"));
        bins.add(new Bin(new Rectangle(x3, binY, binW, binH), TrashType.GLASS,   "SZKŁO"));

        repaint();
    }

    /**
     * Rysuje panel sortowania: kosze, śmieci oraz interfejs użytkownika.
     * @param g kontekst graficzny używany do rysowania
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        for (Bin bin : bins) {
            Image icon = switch (bin.type) {
                case PAPER   -> binPaperImg;
                case PLASTIC -> binPlasticImg;
                case GLASS   -> binGlassImg;
            };

            if (icon != null) {
                g.drawImage(icon, bin.rect.x, bin.rect.y,
                        bin.rect.width, bin.rect.height, null);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(bin.rect.x, bin.rect.y, bin.rect.width, bin.rect.height);
            }
        }

        for (SortingTrash t : trashList) {
            if (t.sprite != null) {
                g.drawImage(t.sprite, t.rect.x, t.rect.y, t.rect.width, t.rect.height, null);
            } else {
                switch (t.type) {
                    case PAPER   -> g.setColor(Color.WHITE);
                    case PLASTIC -> g.setColor(Color.YELLOW);
                    case GLASS   -> g.setColor(Color.CYAN);
                }
                g.fillRect(t.rect.x, t.rect.y, t.rect.width, t.rect.height);
            }
        }
    }

    /**
     * Obsługuje rozpoczęcie przeciągania śmiecia przez gracza.
     *
     * @param e zdarzenie myszy
     */
    @Override
    public void mousePressed(MouseEvent e) {
        for (SortingTrash t : trashList) {
            if (t.rect.contains(e.getPoint())) {
                dragged = t;
                offsetX = e.getX() - t.rect.x;
                offsetY = e.getY() - t.rect.y;
                break;
            }
        }
    }

    /**
     * Sprawdza poprawność sortowania po upuszczeniu śmiecia do kosza.
     *
     * @param e zdarzenie myszy
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragged != null) {
            Bin hitBin = null;
            for (Bin bin : bins) {
                if (bin.rect.contains(dragged.rect.getCenterX(), dragged.rect.getCenterY())) {
                    hitBin = bin;
                    break;
                }
            }

            if (hitBin != null) {
                if (hitBin.type == dragged.type) {
                    trashList.remove(dragged);
                    if (trashList.isEmpty()) {
                        levelFinished();
                    }
                } else {
                    // kara za zły kosz
                    levelScore = Math.max(0, levelScore - 5);
                    totalScore = Math.max(0, totalScore - 5);

                    JOptionPane.showMessageDialog(this,
                            "To nie jest właściwy kosz! -5 punktów.",
                            "Błąd",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

            dragged = null;
            repaint();
        }
    }
    /**
     * Kończy etap sortowania i wyświetla podsumowanie poziomu:
     * czas, punkty za poziom (po karach za błędne sortowanie) oraz sumę punktów i czasu w sesji.
     * Następnie pozwala przejść do kolejnego poziomu lub wrócić do menu.
     */
    private void levelFinished() {
        String msg = String.format(
                "Poziom %d ukończony!\n" +
                        "Czas tego poziomu: %.1f s\n" +
                        "Punkty za ten poziom (po sortowaniu): %d\n\n" +
                        "Łączny czas: %.1f s\n" +
                        "Łączne punkty: %d",
                levelNumber,
                levelTimeMs / 1000.0,
                levelScore,
                totalTimeMs / 1000.0,
                totalScore
        );

        Object[] options;
        String title;

        if (lastLevel) {
            title = "Koniec gry";
            options = new Object[]{"Nowa gra", "Powrót do menu"};
        } else {
            title = "Koniec poziomu";
            options = new Object[]{"Następny poziom", "Powrót do menu"};
        }

        int choice = JOptionPane.showOptionDialog(
                this,
                msg,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        parent.registerScore(totalScore);

        if (lastLevel) {
            if (choice == JOptionPane.YES_OPTION) {
                parent.levelCompleted();
                parent.showGamePanel();
            } else {
                parent.levelCompleted();
            }
        } else {
            if (choice == JOptionPane.YES_OPTION) {
                parent.showGamePanel();
            } else {
                parent.levelCompleted();
            }
        }
    }
    /**
     * Aktualizuje pozycję przeciąganego śmiecia.
     *
     * @param e zdarzenie myszy
     */

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragged != null) {
            dragged.rect.setLocation(e.getX() - offsetX, e.getY() - offsetY);
            repaint();
        }
    }

    /**
     * Metody interfejsu MouseListener – niewykorzystywane w logice gry.
     */
    public void mouseClicked(MouseEvent e) {}
    /**
     * Metody interfejsu MouseListener – niewykorzystywane w logice gry.
     */
    public void mouseEntered(MouseEvent e) {}
    /**
     * Metody interfejsu MouseListener – niewykorzystywane w logice gry.
     */
    public void mouseExited(MouseEvent e) {}
    /**
     * Metody interfejsu MouseListener – niewykorzystywane w logice gry.
     */
    public void mouseMoved(MouseEvent e) {}
}
