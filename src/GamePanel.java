
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private EcoJumperGame parent;
    private Timer timer;
    private Player player;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<TrashItem> trashItems = new ArrayList<>();
    private Random rand = new Random();

    private int health = 100;
    private int score = 0;
    public int collectedTrash = 0;



    public int getCollectedTrashCount() {
        return collectedTrash;
    }

    public GamePanel(EcoJumperGame parent) {
        this.parent = parent;
        timer = new Timer(500, this);

        init();
    }

    private void init() {
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(this);
        player = new Player(100, 400, 40, 400);
        timer = new Timer(16, this);
        timer.start();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // tło
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, getWidth(), getHeight());

        // ziemia
        g.setColor(Color.ORANGE);
        g.fillRect(0, player.getGroundY() + player.getSize(), getWidth(), getHeight() - player.getGroundY());

        // gracz
        player.draw(g);

        // przeszkody
        for (Obstacle o : obstacles) {
            o.draw(g);
        }

        // śmieci
        for (TrashItem t : trashItems) {
            t.draw(g);
        }

        // HUD
        g.setColor(Color.BLACK);
        g.drawString("Zdrowie: " + health, 20, 20);
        g.drawString("Zebrane śmieci: " + collectedTrash, 20, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // generowanie przeszkód
        if (rand.nextInt(100) < 2) {
            if (rand.nextBoolean()) {
                obstacles.add(new OilSpill(getWidth(), player.getGroundY()));
            } else {
                obstacles.add(new Cloud(getWidth(), player.getGroundY()));
            }
        }

        // generowanie śmieci
        if (rand.nextInt(100) < 2) {
            trashItems.add(new TrashItem(getWidth(), player.getGroundY() - 30, 20, 20));
        }

        // aktualizacja przeszkód
        Iterator<Obstacle> itObs = obstacles.iterator();
        while (itObs.hasNext()) {
            Obstacle o = itObs.next();
            o.update();

            if (o.isOutOfScreen()) itObs.remove();

            if (player.getBounds().intersects(o.getBounds())) {
                health -= 1;
                if (health <= 0) restartlevel();
            }
        }

        // aktualizacja śmieci
        Iterator<TrashItem> itTrash = trashItems.iterator();
        while (itTrash.hasNext()) {
            TrashItem t = itTrash.next();
            t.update();

            if (t.isOutOfScreen()) itTrash.remove();

            if (player.getBounds().intersects(t.getBounds())) {
                collectedTrash++;
                itTrash.remove();

                if (collectedTrash >= 10) {
                    timer.stop();
                    parent.showSortingPanel(); // przejście do panelu kosza
                }
            }
        }

        // aktualizacja gracza
        player.update();
        repaint();
    }


    public void restartlevel() {
        health = 100;
        score = 0;
        obstacles.clear();
        trashItems.clear();
        player.resetPosition();
        collectedTrash = 0;
        requestFocusInWindow(); // <- żeby KeyListener działał


    }
    public void resetGame() {
        collectedTrash = 0;
        timer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
