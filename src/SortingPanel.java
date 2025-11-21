
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SortingPanel extends JPanel implements MouseListener, MouseMotionListener {
    private EcoJumperGame parent;
    private ArrayList<Rectangle> trashList = new ArrayList<>();
    private Rectangle bin;
    private Rectangle dragged = null;
    private int offsetX, offsetY;

    public SortingPanel(EcoJumperGame parent) {
        this.parent = parent;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void loadTrash(int count) {
        trashList.clear();
        for (int i = 0; i < count; i++) {
            trashList.add(new Rectangle(50 + i * 30, 100, 20, 20));
        }
        bin = new Rectangle(600, 300, 100, 100);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // kosz
        g.setColor(Color.DARK_GRAY);
        g.fillRect(bin.x, bin.y, bin.width, bin.height);
        g.setColor(Color.WHITE);
        g.drawString("KOSZ", bin.x + 20, bin.y + 50);

        // śmieci
        g.setColor(Color.BLUE);
        for (Rectangle r : trashList) {
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (Rectangle r : trashList) {
            if (r.contains(e.getPoint())) {
                dragged = r;
                offsetX = e.getX() - r.x;
                offsetY = e.getY() - r.y;
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragged != null && bin.contains(dragged)) {
            trashList.remove(dragged);
            if (trashList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Poziom ukończony!");
                parent.showMenuPanel();
            }
        }
        dragged = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragged != null) {
            dragged.setLocation(e.getX() - offsetX, e.getY() - offsetY);
            repaint();
        }
    }

    // nieużywane
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
