package zombie_crush_saga.events;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import static zombie_crush_saga.ZombieCrushSagaConstants.POWER_UPS_X;
import static zombie_crush_saga.ZombieCrushSagaConstants.VISIBLE_STATE;
import static zombie_crush_saga.ZombieCrushSagaConstants.*;
import zombie_crush_saga.data.ZombieCrushSagaDataModel;
import zombie_crush_saga.data.ZombieCrushSagaRecord;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;
import zombie_crush_saga.ui.ZombieCrushSagaPanel;
import zombie_crush_saga.ui.ZombieCrushSagaZombie;

public class PowerUpsHandler implements ActionListener {

    ZombieCrushSagaMiniGame miniGame;
    ZombieCrushSagaDataModel data;
    ZombieCrushSagaRecord rec;

    public PowerUpsHandler(ZombieCrushSagaMiniGame initGame) {
        miniGame = initGame;
        data = (ZombieCrushSagaDataModel) miniGame.getDataModel();
        rec = miniGame.getPlayerRecord();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String purchase = "Uh oh, you've run out of power ups!  Would you like to buy more power ups?";
        
        if (data.powerUps == 0) {
            int reply = JOptionPane.showConfirmDialog(null, purchase, "Out of power ups", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Congrats, you've got 5 more power ups!");
                data.powerUps = 5;
                rec.setPowerUps(5);
            } else {
                JOptionPane.showMessageDialog(null, "Okay.");
            }
        }
        if (data.powerUpPressed) {
            ((ZombieCrushSagaPanel) miniGame.getCanvas()).setPowerUpsCursor(false, 0, 0);
            data.powerUps++;
            rec.setPowerUps(data.powerUps);
            data.powerUpPressed = false;
        } else {
            ((ZombieCrushSagaPanel) miniGame.getCanvas()).setPowerUpsCursor(true, 0, 0);
            ZombieCrushSagaZombie selectZombie = data.selectedZombie;
            if (selectZombie != null) {
                switch (selectZombie.getSpecialType()) {
                    case BASIC_TYPE:
                        selectZombie.setState(VISIBLE_STATE);
                        break;
                    case SPECIAL_WRAPPED_TYPE:
                        selectZombie.setState(SPECIAL_WRAPPED_STATE);
                        break;
                    case SPECIAL_STRIPED_HORIZONTAL_TYPE:
                        selectZombie.setState(SPECIAL_STRIPED_HORIZONTAL_STATE);
                        break;
                    case SPECIAL_STRIPED_VERTICAL_TYPE:
                        selectZombie.setState(SPECIAL_STRIPED_VERTICAL_STATE);
                        break;
                    default:
                        selectZombie.setState(VISIBLE_STATE);
                }
            }
            data.powerUpPressed = true;
            data.powerUps--;
            rec.setPowerUps(data.powerUps);
        }
    }
}
