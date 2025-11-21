import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements ActionListener {
    private EcoJumperGame parent;
    private JButton startButton, tutorialButton, exitButton;
    private int bestLevel = 0;

    public MenuPanel(EcoJumperGame parent) {
        this.parent = parent;
        setLayout(null);
        setBackground(new Color(200, 230, 200));

        JLabel title = new JLabel("🌍 ECO JUMPER 🌿", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBounds(200, 80, 400, 60);
        add(title);

        startButton = new JButton("Start gry");
        startButton.setBounds(320, 200, 160, 40);
        startButton.addActionListener(this);
        add(startButton);

        tutorialButton = new JButton("Samouczek");
        tutorialButton.setBounds(320, 260, 160, 40);
        tutorialButton.addActionListener(this);
        add(tutorialButton);

        exitButton = new JButton("Wyjście");
        exitButton.setBounds(320, 320, 160, 40);
        exitButton.addActionListener(this);
        add(exitButton);
    }

    public void updateBestLevel(int level) {
        this.bestLevel = level;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.DARK_GRAY);
        g.drawString("Najlepszy wynik: " + bestLevel + " poziomów", 290, 400);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) parent.showGamePanel();
        else if (e.getSource() == tutorialButton) {
            JOptionPane.showMessageDialog(this,
                    "Sterowanie: strzałki lub spacja\nZbieraj śmieci i unikaj przeszkód!\nNa końcu posortuj je do właściwego kosza.",
                    "Samouczek",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }
}
