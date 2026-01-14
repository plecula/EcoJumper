import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Reprezentuje postać gracza (player) w części platformowej gry.
 * Klasa obsługuje:
 * <ul>
 *   <li>Ruch w lewo/prawo (flagi klawiszy),</li>
 *   <li>Skok i grawitację (velY, gravity, jumpStrength),</li>
 *   <li>Kolizje poprzez {@link #getBounds()},</li>
 *   <li>Animację sprite'a (zmiana klatek podczas ruchu),</li>
 *   <li>Kierunek patrzenia (facingRight).</li>
 * </ul>
 *
 * Gracz jest rysowany metodą {@link #draw(Graphics)} z wykorzystaniem sprite sheetu
 * (2x2 = 4 klatki) lub fallback (zielony prostokąt), gdy sprite się nie wczyta.
 */
public class Player {
    /** Aktualna pozycja X gracza (piksele). */
    private int x;
    /** Aktualna pozycja Y gracza (piksele). */
    private int y;
    /** Docelowy rozmiar gracza na ekranie (kwadrat size x size). */
    private int size;
    /** Pozycja ziemi (Y) – gdy gracz ma y >= groundY, stoi na ziemi. */
    private int groundY;

    /** Prędkość pionowa (skok/spadanie). */
    private double velY = 0;
    /** Wartość grawitacji dodawana do velY w każdej aktualizacji. */
    private double gravity = 0.8;
    /** Siła skoku (ujemna = w górę). */
    private double jumpStrength = -12;
    /** Flaga: czy gracz aktualnie porusza się w lewo. */
    private boolean movingLeft = false;
    /** Flaga: czy gracz aktualnie porusza się w prawo. */
    private boolean movingRight = false;
    /** Prędkość ruchu poziomego gracza (piksele na tick). */
    private int moveSpeed = 5;

    /**
     * Ustawia prędkość ruchu poziomego gracza.
     * Używane np. do zwiększania trudności wraz z poziomem.
     *
     * @param speed nowa prędkość (piksele na tick)
     */
    public void setMoveSpeed(int speed) {
        this.moveSpeed = speed;
    }

    /** Sprite sheet gracza ( 2x2 ). */
    private Image sprite;
    /** Szerokość pojedynczej klatki w sprite sheet. */
    private int frameWidth;
    /** Wysokość pojedynczej klatki w sprite sheet. */
    private int frameHeight;
    /** Indeks aktualnej klatki animacji (0..3). */
    private int currentFrame = 0;
    /** Licznik czasu do zmiany klatki (ticki). */
    private int frameTimer = 0;
    /** Co ile ticków zmieniać klatkę animacji (mniej = szybciej). */
    private int frameTimerMax = 6;

    /** Kierunek, w którym patrzy gracz: true = prawo, false = lewo. */
    private boolean facingRight = true;

    /**
     * Tworzy nową postać gracza.
     *
     * @param x pozycja startowa X
     * @param y pozycja startowa Y
     * @param size rozmiar gracza (kwadrat)
     * @param groundY poziom ziemi (Y)
     */
    public Player(int x, int y, int size, int groundY) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.groundY = groundY;

        loadSprite();
    }

    /**
     * Wczytuje sprite sheet gracza z zasobów (np. assets/player.png)
     * i wylicza wymiary pojedynczej klatki.
     * Jeśli wczytanie się nie powiedzie, sprite pozostaje null
     * i gracz będzie rysowany jako prostokąt (fallback).
     */
    private void loadSprite() {
        try {
            ImageIcon icon = new ImageIcon("assets/player.png");
            sprite = icon.getImage();

            int imgW = icon.getIconWidth();
            int imgH = icon.getIconHeight();

            frameWidth = imgW / 2;
            frameHeight = imgH / 2;

            System.out.println("Sprite loaded: " + imgW + "x" + imgH +
                    "  single frame: " + frameWidth + "x" + frameHeight);
        } catch (Exception e) {
            e.printStackTrace();
            sprite = null;
        }
    }
    /**
     * Aktualizuje stan gracza:
     * <ul>
     *   <li>ruch poziomy na podstawie flag movingLeft/movingRight,</li>
     *   <li>grawitację i ruch pionowy,</li>
     *   <li>lądowanie na ziemi (groundY),</li>
     *   <li>animację klatek podczas ruchu.</li>
     * </ul>
     */
    public void update() {
        if (movingLeft)  x -= moveSpeed;
        if (movingRight) x += moveSpeed;

        if (x < 0) x = 0;
        if (x + size > 800) x = 800 - size;

        velY += gravity;
        y += velY;

        if (y > groundY) {
            y = groundY;
            velY = 0;
        }

        updateAnimation();
    }

    /**
     * Aktualizuje indeks klatki animacji na podstawie ruchu.
     * Jeśli gracz stoi – ustawia klatkę "idle" (0).
     */
    private void updateAnimation() {
        boolean moving = movingLeft || movingRight;

        if (!moving) {
            currentFrame = 0;
            frameTimer = 0;
            return;
        }

        frameTimer++;
        if (frameTimer >= frameTimerMax) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % 4;
        }
    }

    /**
     * Rysuje gracza w aktualnej pozycji.
     * Jeśli sprite sheet jest dostępny, rysuje odpowiedni fragment obrazka (klatkę),
     * a jeśli gracz patrzy w lewo – odbija klatkę w poziomie.*
     * @param g kontekst graficzny komponentu Swing
     */
    public void draw(Graphics g) {
        if (sprite != null) {
            int col = currentFrame % 2;    // 0 lub 1
            int row = currentFrame / 2;    // 0 lub 1

            int sx1 = col * frameWidth;
            int sy1 = row * frameHeight;
            int sx2 = sx1 + frameWidth;
            int sy2 = sy1 + frameHeight;

            if (facingRight) {
                g.drawImage(
                        sprite,
                        x, y, x + size, y + size,
                        sx1, sy1, sx2, sy2,
                        null
                );
            } else {
                g.drawImage(
                        sprite,
                        x + size, y, x, y + size,
                        sx1, sy1, sx2, sy2,
                        null
                );
            }
        } else {
            g.setColor(Color.GREEN.darker());
            g.fillRect(x, y, size, size);
        }
    }
    /**
     * Wykonuje skok gracza, jeśli znajduje się on na ziemi.
     */
    public void jump() {
        if (y >= groundY) {
            velY = jumpStrength;
        }
    }

    /**
     * Resetuje pozycję i stan gracza do wartości początkowych.
     * Wywoływane przy rozpoczęciu nowego poziomu.
     */
    public void resetPosition() {
        x = 100;
        y = groundY;
        velY = 0;
        movingLeft = false;
        movingRight = false;
        currentFrame = 0;
        frameTimer = 0;
    }

    /**
     * Zwraca prostokąt kolizji gracza.
     *
     * @return obszar zajmowany przez gracza (do kolizji z przeszkodami i śmieciami)
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /**
     * Zwraca współrzędną Y poziomu ziemi.
     *
     * @return pozycja Y ziemi
     */
    public int getGroundY() { return groundY; }

    /**
     * Zwraca rozmiar postaci gracza - kwadrat.
     *
     * @return rozmiar sprite'a
     */
    public int getSize()   { return size; }

    /**
     * Obsługa wciśnięcia klawisza:
     * <ul>
     *   <li>LEFT/A – ruch w lewo</li>
     *   <li>RIGHT/D – ruch w prawo</li>
     *   <li>SPACE/UP – skok</li>
     * </ul>
     *
     * @param e zdarzenie klawiatury
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            movingLeft = true;
            facingRight = false;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            movingRight = true;
            facingRight = true;
        }
        if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) {
            jump();
        }
    }
    /**
     * Obsługa puszczenia klawisza – wyłącza flagi ruchu.
     *
     * @param e zdarzenie klawiatury
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)  movingLeft = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) movingRight = false;
    }
}
