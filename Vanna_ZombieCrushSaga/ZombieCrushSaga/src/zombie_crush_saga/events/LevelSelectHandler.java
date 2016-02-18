package zombie_crush_saga.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import properties_manager.PropertiesManager;
import zombie_crush_saga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import zombie_crush_saga.data.ZombieCrushSagaDataModel;
import zombie_crush_saga.file.ZombieCrushSagaFileManager;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;

/**
 *
 * @author vawong
 */
public class LevelSelectHandler implements ActionListener {

    // HERE'S THE GAME WE'LL UPDATE
    private ZombieCrushSagaMiniGame game;
    // THE LEVEL TO LOAD
    private String levelFile;

    /**
     * This constructor just stores the game for later.
     *
     * @param initGame the game to update
     */
    public LevelSelectHandler(ZombieCrushSagaMiniGame initGame, String initLevelFile) {
        game = initGame;
        levelFile = initLevelFile;
    }

    /**
     * Here is the event response. This code is executed when the user clicks on
     * the button for scrolling through the levels in the saga screen.
     *
     * @param ae the event object for the button press
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String levelPath = props.getProperty(ZombieCrushSagaPropertyType.LEVEL_PATH);
        
        // levelPath includes the /zomcrush/data/LEVEL_
        // UPDATE THE DATA
        ZombieCrushSagaFileManager fileManager = game.getFileManager();
        fileManager.loadLevel(levelPath + levelFile + ".zom");
        //game.switchToLevelScoreScreen(levelPath + levelFile + ".zom");
        int level = Integer.parseInt(levelFile);
    
        //data.setCurrentLevel("LEVEL_" + levelFile);
        game.switchToLevelScoreScreen("LEVEL_" + levelFile, level);
    }
}
