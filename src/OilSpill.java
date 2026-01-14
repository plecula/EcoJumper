import javax.swing.*;
import java.awt.*;

/**
 * Przeszkoda typu plama ropy.
 * <p>
 * Plama ropy pojawia się na ziemi i zmusza gracza do wykonania skoku,
 * aby jej uniknąć.
 * </p>
 */
public class OilSpill extends Obstacle {

    /**
     * Obraz graficzny plamy ropy
     */
    private Image sprite;

    /**
     * Tworzy nową przeszkodę typu plama ropy.
     *
     * @param startX początkowa pozycja X przeszkody
     * @param groundBaselineY pozycja linii ziemi
     */
    public OilSpill(int startX, int groundBaselineY) {
        super(startX, 0, 96, 24);
        this.y = groundBaselineY + 10 - this.height;
        sprite = new ImageIcon("assets/pool.png").getImage();
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

