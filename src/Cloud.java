
import java.awt.*;

public class Cloud extends Obstacle {

    public Cloud(int startX, int groundY) {
        super(startX, groundY - 80, 80, 40); // zawieszona w powietrzu
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(x, y, width, height); // chmura jako elipsa
    }
}
