import javax.swing.*;
import java.awt.*;

/**
 * Główne okno aplikacji oraz kontroler stanów gry.
 * <p>
 * Klasa zarządza przełączaniem widoków (Menu / Gra / Sortowanie) za pomocą {@link CardLayout},
 * pilnuje numeru poziomu, sumarycznych punktów i czasu sesji oraz przekazuje dane między panelami.
 * </p>
 *
 * <h2>Stany gry</h2>
 * <ul>
 *   <li>MENU – ekran główny z przyciskami i rekordami</li>
 *   <li>GAME – poziom platformowy (zbieranie śmieci i omijanie przeszkód)</li>
 *   <li>SORTING – sortowanie zebranych odpadów do właściwych koszy</li>
 * </ul>
 */
public class EcoJumperGame extends JFrame {
    /**
     * Układ kart pozwala przełączać panele po nazwie ("MENU","GAME","SORTING").
     */
    private CardLayout cardLayout;

    /** Kontener główny przechowujący wszystkie panele w CardLayout. */
    private JPanel mainPanel;

    /** Panel menu głównego. */
    private MenuPanel menuPanel;

    /** Panel rozgrywki platformowej. */
    private GamePanel gamePanel;

    /** Panel sortowania odpadów po ukończeniu poziomu. */
    private SortingPanel sortingPanel;

    /** Aktualny poziom w bieżącej sesji gry (0 oznacza brak rozpoczętej sesji). */
    private int currentLevel = 0;

    /** Najwyższy osiągnięty poziom ze wszystkich sesji (rekord). */
    private int bestLevel = 0;

    /** Suma punktów z bieżącej sesji gry (kolejne poziomy dodają się). */
    private int currentScore = 0;

    /** Najlepszy wynik punktowy ze wszystkich sesji (rekord). */
    private int bestScore = 0;

    /** Sumaryczny czas bieżącej sesji gry (ms). */
    private long totalTimeMs = 0;

    /** Timestamp rozpoczęcia aktualnego poziomu (ms). */
    private long levelStartTime = 0;

    /**
     * Tworzy główne okno gry, inicjalizuje panele i ustawia widok MENU jako startowy.
     */
    public EcoJumperGame() {
        setTitle("Eco Jumper");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        sortingPanel = new SortingPanel(this);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(sortingPanel, "SORTING");

        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");
    }

    /**
     * Zwraca numer aktualnego poziomu w bieżącej sesji.
     *
     * @return numer poziomu (0, jeśli sesja jeszcze nie została rozpoczęta)
     */
    public int getCurrentLevel() {
        return currentLevel;
    }
    /**
     * Przełącza widok na panel gry i uruchamia nowy poziom.
     * <p>
     * Jeśli gra startuje z menu (currentLevel==0), resetowane są punkty i czas sesji.
     * </p>
     */
    public void showGamePanel() {
        if (currentLevel == 0) {
            currentScore = 0;
            totalTimeMs = 0;
        }

        currentLevel++;
        levelStartTime = System.currentTimeMillis();

        cardLayout.show(mainPanel, "GAME");
        gamePanel.startLevel();

        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }
    /**
     * Przełącza widok na menu główne oraz aktualizuje etykiety z rekordami.
     * Zatrzymuje timer w {@link GamePanel}, aby gra nie działała w tle.
     */
    public void showMenuPanel() {
        gamePanel.stopGame();
        cardLayout.show(mainPanel, "MENU");
        menuPanel.requestFocusInWindow();

        menuPanel.updateBestLevel(bestLevel);
        menuPanel.updateBestScore(bestScore);
    }

    /**
     * Przechodzi do panelu sortowania i przekazuje dane z poziomu:
     *
     * @param paperCount liczba zebranych odpadów typu PAPER - papier
     * @param plasticCount liczba zebranych odpadów typu PLASTIC - plastik
     * @param glassCount liczba zebranych odpadów typu GLASS - szkło
     * @param levelScore punkty zdobyte na planszy biegania w tym poziomie
     */
    public void showSortingPanel(int paperCount, int plasticCount, int glassCount, int levelScore) {
        long levelTime = System.currentTimeMillis() - levelStartTime;
        totalTimeMs += levelTime;

        currentScore += levelScore;

        boolean isLastLevel = false;

        sortingPanel.loadTrash(
                paperCount,
                plasticCount,
                glassCount,
                currentLevel,
                levelScore,
                currentScore,
                levelTime,
                totalTimeMs,
                isLastLevel
        );

        cardLayout.show(mainPanel, "SORTING");
    }

    /**
     * Aktualizuje rekord punktowy (bestScore), jeśli finalScore jest większy.
     *
     * @param finalScore końcowy wynik punktowy z poziomu
     */
    public void registerScore(int finalScore) {
        if (finalScore > bestScore) {
            bestScore = finalScore;
        }
    }

    /**
     * Zamyka bieżącą sesję gry: aktualizuje rekord poziomów (bestLevel),
     * resetuje licznik poziomu i sumy sesji oraz wraca do menu.
     */
    public void levelCompleted() {
        if (currentLevel > bestLevel) {
            bestLevel = currentLevel;
        }

        currentLevel = 0;
        currentScore = 0;
        totalTimeMs = 0;

        showMenuPanel();
    }

    /**
     * Punkt wejścia aplikacji – uruchamia okno gry w wątku EDT.
     *
     * @param args argumenty uruchomieniowe programu
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EcoJumperGame game = new EcoJumperGame();
            game.setVisible(true);
        });
    }
}
