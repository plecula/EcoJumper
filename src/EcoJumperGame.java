
import javax.swing.*;
import java.awt.*;

public class EcoJumperGame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private SortingPanel sortingPanel;

    private int bestLevel = 0;
    private int currentLevel = 0;

    public EcoJumperGame() {
        setTitle("Eco Jumper");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Tworzymy panele
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        sortingPanel = new SortingPanel(this);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(sortingPanel, "SORTING");



        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");


    }

    public void showGamePanel() {
        cardLayout.show(mainPanel, "GAME");
        currentLevel++;
        gamePanel.restartlevel();

    }

    public void showMenuPanel() {
        cardLayout.show(mainPanel, "MENU");
        menuPanel.updateBestLevel(bestLevel);
    }

    public void showSortingPanel() {
        cardLayout.show(mainPanel, "SORTING");
        sortingPanel.loadTrash(10);

    }



    public void levelCompleted() {
        if (currentLevel > bestLevel) {
            bestLevel = currentLevel;
        }
        showMenuPanel();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EcoJumperGame game = new EcoJumperGame();
            game.setVisible(true);
        });
    }
}
