package zombie_crush_saga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;

/**
 * This event handler responds to when the user requests to reset their record.
 *
 * @author Vanna Wong
 */
public class ResetHandler implements ActionListener {

    // HERE'S THE GAME WE'LL UPDATE
    private ZombieCrushSagaMiniGame game;

    /**
     * This constructor just stores the game for later.
     *
     * @param initGame the game to update
     */
    public ResetHandler(ZombieCrushSagaMiniGame initGame) {
        game = initGame;
    }

    /**
     * Here is the event response. This code is executed when the user clicks on
     * the button for resetting their record, which can be done when the
     * application starts via the splash screen. Note that the game data is
     * already locked for this thread before it is called, and that it will be
     * unlocked after it returns.
     *
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        game.reset();
    }
}
