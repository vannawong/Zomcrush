package zombie_crush_saga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import zombie_crush_saga.data.ZombieCrushSagaDataModel;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;

/**
 *
 * @author Vanna Wong
 */
public class RetryHandler implements ActionListener {

    private ZombieCrushSagaMiniGame miniGame;
    private  ZombieCrushSagaDataModel data;

    public RetryHandler(ZombieCrushSagaMiniGame initMiniGame) {
        miniGame = initMiniGame;
        data = ((ZombieCrushSagaDataModel)miniGame.getDataModel());
    }

    /*
     * This method is called when the user clicks the QUIT button.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        miniGame.switchToGameScreen(data.getCurrentLevel());
    }
}