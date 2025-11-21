
import java.awt.*;

public class OilSpill extends Obstacle {

    public OilSpill(int startX, int groundY) {
        super(startX, groundY, 60, 20); // szeroka plama na ziemi
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(x, y, width, height);
    }
}
