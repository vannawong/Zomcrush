package zombie_crush_saga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;

/**
 *
 * @author vawong
 */
public class PlayLevelHandler implements ActionListener {

    // HERE'S THE GAME WE'LL UPDATE
    private ZombieCrushSagaMiniGame game;
    private String levelFile;

    /**
     * This constructor just stores the game for later.
     *
     * @param initGame the game to update
     */
    public PlayLevelHandler(ZombieCrushSagaMiniGame initGame) {
        game = initGame;
    }

    /**
     * Here is the event response. This code is executed when the user clicks on
     * the button for scrolling through the levels in the saga screen.
     *
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        game.switchToGameScreen(levelFile);
    }
}