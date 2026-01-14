/**
 * Typy odpadów występujących w grze Eco Jumper.
 * Enum wykorzystywany jest do:
 * <ul>
 *   <li>generowania śmieci na planszy,</li>
 *   <li>zliczania zebranych odpadów,</li>
 *   <li>sprawdzania poprawności sortowania.</li>
 * </ul>
 */
public enum TrashType {

    /** Odpad papierowy */
    PAPER,

    /** Odpad plastikowy */
    PLASTIC,

    /** Odpad szklany */
    GLASS
}
