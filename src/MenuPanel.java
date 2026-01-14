import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Panel menu głównego gry Eco Jumper.
 * Odpowiada za wyświetlanie tytułu gry, przycisków sterujących
 * (start gry, samouczek, wyjście) oraz najlepszego wyniku gracza.
 */

public class MenuPanel extends JPanel implements ActionListener {

    /** Referencja do głównego okna gry (zarządza przełączaniem paneli). */
    private EcoJumperGame parent;
    /** Przycisk uruchamiający rozgrywkę. */
    private JButton startButton;
    /** Przycisk wyświetlający okno z instrukcją. */
    private JButton tutorialButton;
    /** Przycisk zamykający aplikację. */
    private JButton exitButton;
    /** Etykieta informująca o najlepszym osiągniętym poziomie. */
    private JLabel bestLevelLabel;
    /** Etykieta informująca o najlepszym wyniku punktowym. */
    private JLabel bestScoreLabel;
    /** Obraz tła menu wczytywany z zasobów projektu. */
    private Image backgroundImage;

    /**
     * Tworzy panel menu głównego i inicjalizuje przyciski, etykiety oraz tło.
     *
     * @param parent główne okno gry wykorzystywane do przełączania widoków (MENU/GAME)
     */
    public MenuPanel(EcoJumperGame parent) {
        this.parent = parent;

        backgroundImage = new ImageIcon("assets/menu_bg.png").getImage();

        setLayout(null);
        setBackground(Color.BLACK);

        startButton = createMenuButton("Start gry");
        tutorialButton = createMenuButton("Samouczek");
        exitButton = createMenuButton("Wyjście");

        startButton.addActionListener(this);
        tutorialButton.addActionListener(this);
        exitButton.addActionListener(this);

        add(startButton);
        add(tutorialButton);
        add(exitButton);

        bestLevelLabel = new JLabel("Najlepszy poziom: 0", SwingConstants.CENTER);
        bestScoreLabel = new JLabel("Najlepszy wynik: 0 pkt", SwingConstants.CENTER);

        bestLevelLabel.setForeground(Color.DARK_GRAY);
        bestScoreLabel.setForeground(Color.DARK_GRAY);
        bestLevelLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bestScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        bestLevelLabel.setOpaque(false);
        bestScoreLabel.setOpaque(false);

        add(bestLevelLabel);
        add(bestScoreLabel);
    }
    /**
     * Tworzy wystylizowany przycisk menu o spójnym wyglądzie.
     *
     * @param text tekst wyświetlany na przycisku
     * @return skonfigurowany przycisk {@link JButton}
     */
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(250, 252, 250));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 170, 150)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return btn;
    }

    /**
     * Aktualizuje informację o najwyższym osiągniętym poziomie gry.
     *
     * @param level najlepszy ukończony poziom
     */
    public void updateBestLevel(int level) {
        bestLevelLabel.setText("Najlepszy poziom: " + level);
    }

    /**
     * Aktualizuje etykietę z najlepszym wynikiem punktowym gracza.
     *
     * @param score najlepszy wynik punktowy
     */    public void updateBestScore(int score) {
        bestScoreLabel.setText("Najlepszy wynik: " + score + " pkt");
    }

    /**
     * Rozmieszcza komponenty menu zależnie od aktualnego rozmiaru panelu.
     * Wywoływana automatycznie przez Swing (np. po zmianie rozmiaru okna).
     */
    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();

        int btnW = 260;
        int btnH = 50;

        int x = (w - btnW) / 2;
        int firstY = h / 2 - 60;

        startButton.setBounds(x, firstY, btnW, btnH);
        tutorialButton.setBounds(x, firstY + 70, btnW, btnH);
        exitButton.setBounds(x, firstY + 140, btnW, btnH);

        int labelY = h - 80;
        bestLevelLabel.setBounds(0, labelY, w, 20);
        bestScoreLabel.setBounds(0, labelY + 22, w, 20);
    }


    /**
     * Rysuje tło menu (obraz) pod komponentami Swing.
     *
     * @param g kontekst graficzny używany do rysowania
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }


    /**
     * Obsługuje akcje przycisków menu.
     * W zależności od wybranego przycisku uruchamia grę, wyświetla samouczek
     * lub kończy działanie aplikacji.
     * @param e zdarzenie akcji (kliknięcie przycisku)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == startButton) {
            parent.showGamePanel();
        } else if (src == tutorialButton) {
            JOptionPane.showMessageDialog(this,
                    """
                    Sterowanie:
                    ← →  - ruch
                    Spacja lub  ↑ - skok
                    
                    Cel:
                    Zbieraj śmieci, unikaj przeszkód,
                    a na końcu posortuj odpady do odpowiednich koszy.
                    """,
                    "Samouczek",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (src == exitButton) {
            System.exit(0);
        }
    }
}
