
import java.awt.*;

public abstract class Obstacle {
    protected int x, y, width, height;

    public Obstacle(int x, int y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }

    public void update() {
        x -= 5; // przesuwa się w lewo
    }

    public abstract void draw(Graphics g);

    public boolean isOutOfScreen() {
        return x + width < 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
