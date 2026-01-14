
import java.awt.*;
/**
 * Abstrakcyjna klasa bazowa dla przeszkód w grze.
 * Definiuje wspólne cechy i zachowanie wszystkich przeszkód,
 * takich jak ruch, kolizje i sprawdzanie wyjścia poza ekran.
 */
public abstract class Obstacle {
    /** Pozycja pozioma przeszkody na planszy. */
    protected int x;
    /** Pozycja pionowa przeszkody na planszy. */
    protected int y;
    /** Szerokość przeszkody (używana do rysowania i kolizji). */
    protected int width;
    /** Wysokość przeszkody (używana do rysowania i kolizji). */
    protected int height;
    /**
     * Tworzy nową przeszkodę o podanych parametrach.
     *
     * @param x pozycja X
     * @param y pozycja Y
     * @param width szerokość przeszkody
     * @param height wysokość przeszkody
     */
    public Obstacle(int x, int y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }

    /** Prędkość przesuwania przeszkody w lewo (prędkość świata). */
    protected int speed = 5;

    /**
     * Ustawia prędkość przeszkody.
     * Używane do zwiększania trudności gry.
     *
     * @param s nowa prędkość przeszkody
     */
    public void setSpeed(int s) {
        this.speed = s;
    }

    /**
     * Aktualizuje pozycję przeszkody.
     * Domyślnie przesuwa ją w lewo z określoną prędkością.
     */
    public void update() {
        x -= speed;
    }

    /**
     * Rysuje przeszkodę na ekranie.
     * Metoda implementowana w klasach potomnych.
     *
     * @param g obiekt Graphics używany do rysowania
     */
    public abstract void draw(Graphics g);

    /**
     * Sprawdza, czy przeszkoda opuściła ekran gry.
     *
     * @return true jeśli przeszkoda jest poza ekranem
     */
    public boolean isOutOfScreen() {
        return x + width < 0;
    }

    /**
     * Zwraca obszar kolizji przeszkody.
     *
     * @return prostokąt kolizji
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }



}
