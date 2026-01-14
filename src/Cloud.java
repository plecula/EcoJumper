import javax.swing.*;
import java.awt.*;

/**
 * Przeszkoda typu chmura.
 * <p>
 * Chmura pojawia się nad ziemią i ogranicza możliwość skakania gracza,
 * zmuszając go do pozostania na niskim poziomie planszy.
 * </p>
 */
public class Cloud extends Obstacle {

    /**
     * Obraz graficzny chmury
     */
    private Image sprite;

    /**
     * Tworzy nową chmurę przeszkodę.
     *
     * @param startX początkowa pozycja X chmury
     * @param groundBaselineY pozycja linii ziemi (referencja wysokości)
     */
    public Cloud(int startX, int groundBaselineY) {
        super(startX, 0, 96, 64);

        int verticalOffset = 180;   // ile NAD ziemią wisi chmura
        this.y = groundBaselineY - verticalOffset;

        sprite = new ImageIcon("assets/cloud.png").getImage();
    }

    /**
     * Rysowanie przeszkody
     * @param g obiekt Graphics używany do rysowania
     */
    @Override
    public void draw(Graphics g) {
        g.drawImage(sprite, x, y, width, height, null);
    }
}

