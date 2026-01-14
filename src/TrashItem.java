import javax.swing.*;
import java.awt.*;

/**
 * Obiekt odpadu do zebrania podczas biegu.
 * Ma typ (papier/plastik/szkło), sprite oraz porusza się w lewo razem z "planszą".
 * Po zebraniu zwiększa licznik śmieci i wynik.
 */

public class TrashItem {
    /** Aktualna pozycja X odpadu na planszy. */
    private int x;

    /** Aktualna pozycja Y odpadu na planszy. */
    private int y;

    /** Szerokość sprite'a odpadu (do rysowania i kolizji). */
    private int width;

    /** Wysokość sprite'a odpadu (do rysowania i kolizji). */
    private int height;

    /** Typ odpadu (papier, plastik lub szkło). */
    private TrashType type;

    /** Obrazek (sprite) reprezentujący odpad. */
    private Image sprite;

    /** Prędkość przesuwania się odpadu w lewo (prędkość świata). */
    private int speed = 5;


    /**
     * Tworzy nowy obiekt odpadu na planszy.
     *
     * @param x początkowa pozycja X odpadu
     * @param y pozycja Y odpadu
     * @param width szerokość sprite'a
     * @param height wysokość sprite'a
     * @param type typ odpadu (papier, plastik lub szkło)
     */
    public TrashItem(int x, int y, int width, int height, TrashType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        switch (type) {
            case PLASTIC -> sprite = new ImageIcon("assets/plastic_yellow.png").getImage();
            case GLASS   -> sprite = new ImageIcon("assets/glass_green.png").getImage();
            case PAPER   -> sprite = new ImageIcon("assets/paper_grey.png").getImage();
        }
    }

    /**
     * Ustawia prędkość poruszania się odpadu.
     * Wykorzystywane do zwiększania trudności gry na wyższych poziomach.
     *
     * @param s nowa prędkość ruchu odpadu
     */
    public void setSpeed(int s) {
        this.speed = s;
    }

    /**
     * Aktualizuje pozycję odpadu.
     * Przesuwa go w lewo zgodnie z prędkością świata.
     */
    public void update() {
        x -= speed;
    }

    /**
     * Sprawdza, czy odpad opuścił ekran gry.
     *
     * @return true jeśli odpad znajduje się poza ekranem
     */
    public boolean isOutOfScreen() {
        return x + width < 0;
    }

    /**
     * Rysuje sprite odpadu na ekranie.
     *
     * @param g obiekt Graphics używany do rysowania
     */
    public void draw(Graphics g) {
        g.drawImage(sprite, x, y, width, height, null);
    }

    /**
     * Zwraca obszar kolizji odpadu.
     * Wykorzystywane do sprawdzania kontaktu z graczem.
     *
     * @return prostokąt kolizji odpadu
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Zwraca typ odpadu.
     * Wykorzystywane do zliczania oraz panelu sortowania.
     *
     * @return typ odpadu
     */
    public TrashType getType() {
        return type;
    }

    /**
     * Zwraca aktualną pozycję X odpadu.
     *
     * @return pozycja X
     */
    public int getX() { return x; }
}
