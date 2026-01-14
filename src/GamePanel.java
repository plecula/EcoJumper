import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
/**
 * Panel gry odpowiedzialny za główną rozgrywkę platformową.
 * Zawiera logikę ruchu gracza, generowania przeszkód i śmieci,
 * obsługę kolizji, punktacji, życia oraz rysowanie HUD.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    /** Referencja do głównego okna gry – służy do przełączania paneli i pobierania poziomu. */
    private EcoJumperGame parent;

    /** Timer Swing uruchamiający pętlę gry (ok. 60 FPS). */
    private Timer timer;

    /** Obiekt gracza sterowany klawiaturą. */
    private Player player;

    /** Lista aktywnych przeszkód na planszy (np. chmury i plamy ropy). */
    private ArrayList<Obstacle> obstacles = new ArrayList<>();

    /** Lista aktywnych odpadów do zebrania na planszy. */
    private ArrayList<TrashItem> trashItems = new ArrayList<>();

    /** Generator liczb losowych wykorzystywany do generowania się obiektów. */
    private Random rand = new Random();

    /** Obraz tła rysowany w każdej klatce gry. */
    private Image backgroundImg = new ImageIcon("assets/background.png").getImage();

    /** Punkty życia gracza w skali 0–100 (wyświetlane jako 10 serc). */
    private int health = 100;

    /** Aktualny wynik punktowy w bieżącym poziomie. */
    private int score = 0;

    /** Liczba zebranych odpadów w bieżącym poziomie. */
    private int collectedTrash = 0;

    /** Liczba zebranych odpadów typu PAPER – przekazywana do sortowania. */
    private int paperCount = 0;

    /** Liczba zebranych odpadów typu PLASTIC – przekazywana do sortowania. */
    private int plasticCount = 0;

    /** Liczba zebranych odpadów typu GLASS – przekazywana do sortowania. */
    private int glassCount = 0;

    /** Przycisk pauzy/wyjścia do menu w trakcie rozgrywki. */
    private JButton pauseButton;

    /** Informacja czy gra jest aktualnie w stanie pauzy. */
    private boolean paused = false;

    /** Obraz pełnego serca wykorzystywany do rysowania życia w HUD. */
    private Image heartFull;

    /** Szerokość i wysokość serca na HUD. */
    private int heartW, heartH;

    /** Maksymalna liczba serc wyświetlanych na HUD. */
    private final int maxHearts = 10;

    /** Prędkość świata (planszy) – wpływa na przesuwanie przeszkód i śmieci. */
    private int worldSpeed = 5;

    /** Maksymalna prędkość świata osiągana na wysokich poziomach. */
    private final int maxWorldSpeed = 12;

    /** Liczba odpadów wymaganych do ukończenia poziomu. */
    private int trashToCollect = 10;

    /** Szansa (w %) na wygenerowanie przeszkody w danej klatce po spełnieniu odstępu czasowego. */
    private int obstacleSpawnChance = 2;

    /** Szansa (w %) na wygenerowanie odpadu w danej klatce po spełnieniu odstępu czasowego. */
    private int trashSpawnChance = 3;

    /** Liczba klatek od ostatniego wygenerowania przeszkody. */
    private int framesSinceLastObstacle = 0;

    /** Minimalny odstęp (w klatkach) pomiędzy generowaniem przeszkód. */
    private int minFramesBetweenObstacles = 35;

    /** Liczba klatek od ostatniego wygenerowania odpadu. */
    private int framesSinceLastTrash = 0;
    
    /** Minimalny odstęp (w klatkach) pomiędzy generowaniem odpadów. */
    private int minFramesBetweenTrash = 25;

    /**
     * Tworzy panel rozgrywki i inicjalizuje zasoby graficzne HUD (serca).
     * Ustawia także gracza, timer oraz przycisk pauzy/menu.
     *
     * @param parent główne okno gry zarządzające stanami i panelami
     */
    public GamePanel(EcoJumperGame parent) {
        this.parent = parent;
        init();

        heartFull = new ImageIcon("assets/heart.png").getImage();

        int originalW = heartFull.getWidth(null);
        int originalH = heartFull.getHeight(null);
        int targetH = 24;
        heartH = targetH;
        heartW = (int) ((double) originalW / originalH * targetH);
    }

    /** Inicjalizuje panel, timery, gracza oraz przycisk pauzy/menu. */
    private void init() {
        setFocusable(true);
        addKeyListener(this);

        setLayout(null);

        player = new Player(100, 400, 80, 400);
        timer = new Timer(16, this);

        pauseButton = new JButton("Menu");
        pauseButton.setFocusable(false);
        pauseButton.setMargin(new Insets(2, 8, 2, 8));
        pauseButton.addActionListener(e -> onPauseButton());
        add(pauseButton);
    }

    /**
     * Rozpoczyna nowy poziom gry.
     * Resetuje stan planszy, ustawia poziom trudności
     * oraz uruchamia timer odpowiedzialny za pętlę gry.
     */
    public void startLevel() {
        paused = false;

        restartLevel();

        int lvl = parent.getCurrentLevel();

        trashToCollect = Math.min(15, 6 + lvl * 2);

        worldSpeed = Math.min(maxWorldSpeed, 5 + (lvl - 1));

        obstacleSpawnChance = Math.min(12, 3 + lvl); // limit 12%
        trashSpawnChance    = Math.min(12, 3 + lvl);

        minFramesBetweenObstacles = Math.max(26, 50 - lvl * 2);
        minFramesBetweenTrash     = Math.max(20, 40 - lvl * 2);

        framesSinceLastObstacle = 0;
        framesSinceLastTrash = 0;

        player.setMoveSpeed(Math.min(10, 5 + lvl / 2));

        timer.start();
        requestFocusInWindow();
    }

    /**
     * Zatrzymuje rozgrywkę.
     * Wykorzystywane przy powrocie do menu lub pauzie.
     */
    public void stopGame()
    {
        paused = false;

        if (timer.isRunning()) timer.stop();
    }

    /**
     * Zwraca liczbę punktów zdobytych w bieżącym poziomie.
     *
     * @return punkty poziomu
     */
    public int getLevelScore() {
        return score;
    }

    /**
     * Resetuje aktualny poziom.
     * Czyści przeszkody i śmieci, przywraca zdrowie gracza
     * oraz zeruje liczniki punktów i postępu.
     */
    public void restartLevel() {
        health = 100;
        score = 0;

        collectedTrash = 0;
        paperCount = plasticCount = glassCount = 0;

        obstacles.clear();
        trashItems.clear();
        player.resetPosition();

        framesSinceLastObstacle = 0;
    }

    /**
     * Rysuje wszystkie elementy gry:
     * tło, gracza, przeszkody, śmieci oraz HUD
     * (punkty, postęp i serca życia).
     *
     * @param g obiekt graficzny do rysowania
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        g.drawImage(backgroundImg, 0, 0, w, h, null);
        player.draw(g);
        for (Obstacle o : obstacles) {
            o.draw(g);
        }
        for (TrashItem t : trashItems) {
            t.draw(g);
        }

        int hudX = 15;
        int hudY = 40;
        int hudW = 200;
        int hudH = 55;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(255, 255, 255, 180));
        g2.fillRoundRect(hudX, hudY, hudW, hudH, 15, 15);
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawRoundRect(hudX, hudY, hudW, hudH, 15, 15);
        g2.dispose();

        drawHearts(g, hudX, hudY);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        int textBaseY = hudY + 20;
        g.drawString("Punkty: " + score, hudX + 10, textBaseY);
        g.drawString("Śmieci: " + collectedTrash + "/" + trashToCollect,
                hudX + 10, textBaseY + 18);

        int btnW = 90;
        int btnH = 30;
        pauseButton.setBounds(getWidth() - btnW - 20, 20, btnW, btnH);
    }

    /**
     * Rysuje serca życia nad ramką HUD.
     * Liczba serc zależy od wartości health (0–100 → 0–10 serc).
     *
     * @param g kontekst graficzny do rysowania
     * @param hudX pozycja X HUD
     * @param hudY pozycja Y HUD
     */
    private void drawHearts(Graphics g, int hudX, int hudY) {
        // 100–91 → 10 serc, 90–81 → 9, itd.
        int heartsToDraw = Math.min(maxHearts, (health + 9) / 10);

        int spacing = 3;
        int startX = hudX + 10;
        int y = hudY - heartH - 6;

        for (int i = 0; i < heartsToDraw; i++) {
            int x = startX + i * (heartW + spacing);
            g.drawImage(heartFull, x, y, heartW, heartH, null);
        }
    }

    /**
     * Sprawdza, czy wskazany obszar jest wolny od innych obiektów.
     * Zapobiega pojawianiu się śmieci na przeszkodach lub nakładaniu się obiektów.
     *
     * @param rect testowany obszar
     * @return true, jeśli obszar nie koliduje z żadnym obiektem
     */
    private boolean isAreaFree(Rectangle rect) {
        for (Obstacle o : obstacles) {
            if (rect.intersects(o.getBounds())) return false;
        }
        for (TrashItem t : trashItems) {
            if (rect.intersects(t.getBounds())) return false;
        }
        return true;
    }
    /**
     * Obsługuje kliknięcie przycisku "Menu".
     * Zatrzymuje timer, wyświetla okno pauzy i pozwala wrócić do gry lub do menu głównego.
     */
    private void onPauseButton() {
        if (paused) return;

        paused = true;
        timer.stop();

        Object[] options = {"Wróć do gry", "Powrót do menu"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Gra została wstrzymana.",
                "Pauza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            paused = false;
            timer.start();
            requestFocusInWindow();
        } else {
            parent.registerScore(score);
            parent.levelCompleted();
        }
    }

    /**
     * Główna pętla gry wywoływana przez Timer.
     * Odpowiada za aktualizację obiektów,
     * generowanie przeszkód i śmieci,
     * sprawdzanie kolizji oraz warunki zakończenia poziomu.
     *
     * @param e zdarzenie timera
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int width = getWidth();
        framesSinceLastObstacle++;
        framesSinceLastTrash++;

        if (framesSinceLastObstacle >= minFramesBetweenObstacles &&
                rand.nextInt(100) < obstacleSpawnChance) {

            Obstacle o;
            if (rand.nextBoolean()) {
                int groundBaseline = player.getGroundY() + player.getSize();
                o = new OilSpill(width, groundBaseline);
            } else {
                int cloudY = 500;
                o = new Cloud(width, cloudY);
            }
            o.setSpeed(worldSpeed);

            if (isAreaFree(o.getBounds())) {
                obstacles.add(o);
                framesSinceLastObstacle = 0;
            }

        }
        //  ilość generowanych  śmieci na ekranie, taka żeby gracz musiał się ruszać.
        int remaining = trashToCollect - collectedTrash;
        int maxTrashOnScreen = Math.min(4, Math.max(2, remaining));
        if (trashItems.size() < maxTrashOnScreen &&
                framesSinceLastTrash >= minFramesBetweenTrash &&
                rand.nextInt(100) < trashSpawnChance) {

            TrashType type = TrashType.values()[rand.nextInt(TrashType.values().length)];
            int trashSize = 48;

            int groundBaseline = player.getGroundY() + player.getSize();
            int x = width;
            int y = groundBaseline - trashSize - 4;

            TrashItem candidate = new TrashItem(x, y, trashSize, trashSize, type);

            candidate.setSpeed(worldSpeed);

            if (isAreaFree(candidate.getBounds())) {
                trashItems.add(candidate);
                framesSinceLastTrash = 0;
            }
        }

        Iterator<Obstacle> itObs = obstacles.iterator();
        while (itObs.hasNext()) {
            Obstacle o = itObs.next();
            o.update();

            if (o.isOutOfScreen()) {
                itObs.remove();
                continue;
            }
            //kara za kolizje
            if (player.getBounds().intersects(o.getBounds())) {
                health -= 1;
                if (health <= 0) {
                    timer.stop();

                    JOptionPane.showMessageDialog(
                            this,
                            "Przegrałaś! Twój wynik: " + score,
                            "Koniec gry",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    parent.registerScore(score);
                    parent.levelCompleted();
                    return;
                }
            }
        }

        Iterator<TrashItem> itTrash = trashItems.iterator();
        while (itTrash.hasNext()) {
            TrashItem t = itTrash.next();
            t.update();

            if (t.isOutOfScreen()) {
                itTrash.remove();
                continue;
            }
            // zbieranie smieci
            if (player.getBounds().intersects(t.getBounds())) {

                collectedTrash++;
                score += 10;

                switch (t.getType()) {
                    case PAPER -> paperCount++;
                    case PLASTIC -> plasticCount++;
                    case GLASS -> glassCount++;
                }

                itTrash.remove();

                if (collectedTrash >= trashToCollect) {
                    score += health;

                    timer.stop();

                    JOptionPane.showMessageDialog(
                            this,
                            "Brawo! Zebrano wszystkie śmieci!\n" +
                                    "Przechodzisz do sortowania.",
                            "Poziom ukończony",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    parent.showSortingPanel(paperCount, plasticCount, glassCount, score);
                    return;
                }
            }
        }

        player.update();
        repaint();
    }

    /**
     * Obsługuje wciśnięcie klawiszy sterujących postacią gracza.
     *
     * @param e zdarzenie klawiatury
     */
    @Override
    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);
    }

    /**
     * Obsługuje puszczenie klawiszy sterujących ruchem gracza.
     *
     * @param e zdarzenie klawiatury
     */
    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    /**
     * Nieużywana metoda interfejsu KeyListener.
     *
     * @param e zdarzenie klawiatury
     */
    @Override
    public void keyTyped(KeyEvent e) {}
}
