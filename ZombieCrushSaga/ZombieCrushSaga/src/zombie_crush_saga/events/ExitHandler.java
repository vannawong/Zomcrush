package zombie_crush_saga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;

/**
 *
 * @author Vanna Wong
 */
public class ExitHandler extends WindowAdapter implements ActionListener {

    private ZombieCrushSagaMiniGame miniGame;

    public ExitHandler(ZombieCrushSagaMiniGame initMiniGame) {
        miniGame = initMiniGame;
    }

    /**
     * This method is called when the user clicks the window'w X.
     *
     * @param we Window event object.
     */
    @Override
    public void windowClosing(WindowEvent we) {
        System.exit(0);
    }

    /*
     * This method is called when the user clicks the QUIT button.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}