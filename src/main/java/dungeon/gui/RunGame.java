package dungeon.gui;

/**
 * This class is just to start up the GUI without requiring any VM options.
 *
 */
public class RunGame {

    /*
    Rozvržení packages je fajn. Jen bych:
    - Dal Controller do samotného package, např. controller (takže by jsi měla dungeon.controller)
    - UI .fxml bych možná dal do nějaké své package, ať nejsou vedle tříd. Ale to je jen moje preference
     */

    public static void main(String[] args) {
        GameGUI.main(args);
    }
}
