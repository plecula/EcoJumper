
import java.awt.*;
import java.awt.event.KeyEvent;

public class Player {
    private int x, y;
    private int size = 40;
    private int groundY = 400;

    private boolean isJumping = false;
    private int jumpStrength = 0;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public Player(int startX, int startY, int i, int i1) {
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        // ruch poziomy
        if (leftPressed && x > 0) x -= 5;
        if (rightPressed && x < 800 - size) x += 5;

        // skakanie
        if (isJumping) {
            y -= jumpStrength;
            jumpStrength -= 1;
            if (y >= groundY) {
                y = groundY;
                isJumping = false;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, size, size);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE && !isJumping) {
            isJumping = true;
            jumpStrength = 15;
        }
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public void resetPosition() {
        x = 100;
        y = groundY;
    }

    public int getSize() { return size; }
    public int getGroundY() { return groundY; }
}
