package zombie_crush_saga.events;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static zombie_crush_saga.ZombieCrushSagaConstants.GAME_SCREEN_STATE;
import zombie_crush_saga.data.ZombieCrushSagaDataModel;
import zombie_crush_saga.data.ZombieCrushSagaMove;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;

/**
 * This event handler lets us provide additional custom responses to key presses
 * while Mahjong is running.
 *
 * @author Richard McKenna
 */
public class KeyHandler extends KeyAdapter {
    // THE MAHJONG GAME ON WHICH WE'LL RESPOND

    private ZombieCrushSagaMiniGame game;

    /**
     * This constructor simply inits the object by keeping the game for later.
     *
     * @param initGame The Mahjong game that contains the back button.
     */
    public KeyHandler(ZombieCrushSagaMiniGame initGame) {
        game = initGame;
    }

    /**
     * This method provides a custom game response to when the user presses a
     * keyboard key.
     *
     * @param ke Event object containing information about the event, like which
     * key was pressed.
     */
    @Override
    public void keyPressed(KeyEvent ke) {
        ZombieCrushSagaDataModel data = (ZombieCrushSagaDataModel) game.getDataModel();
        // CHEAT BY ONE MOVE. NOTE THAT IF WE HOLD THE C
        // KEY DOWN IT WILL CONTINUALLY CHEAT
        if (ke.getKeyCode() == KeyEvent.VK_3) {
            data.setLevelScore(data.threeStar);
            data.endGameAsWin();
        }
        if (ke.getKeyCode() == KeyEvent.VK_2) {
            data.setLevelScore(data.twoStar);
            data.endGameAsWin();
        }
        if (ke.getKeyCode() == KeyEvent.VK_1) {
            data.setLevelScore(data.oneStar);
            data.endGameAsWin();
        }
        
        if (ke.getKeyCode() == KeyEvent.VK_S) {
            data.shuffleBoard();
        }

        if (ke.getKeyCode() == KeyEvent.VK_L) {
            data.endGameAsLoss();
        }
    }
}
