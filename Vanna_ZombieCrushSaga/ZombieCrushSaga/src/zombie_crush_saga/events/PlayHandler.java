package zombie_crush_saga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;
/**
 * This class manages when the user clicks PLAY button on the splash screen.
 * 
 * @author Vanna Wong
 */
public class PlayHandler implements ActionListener{
    private ZombieCrushSagaMiniGame game;
    
    public PlayHandler(ZombieCrushSagaMiniGame initMiniGame)
    {
        game = initMiniGame;
    }
    
    /**
     * Here is the event response. This code is executed when
     * the user clicks on the button for starting a new game,
     * which can be done when the application starts up, during
     * a game, or after a game has been played. Note that the game 
     * data is already locked for this thread before it is called, 
     * and that it will be unlocked after it returns.
     * 
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        game.switchToSagaScreen();
    }
}
