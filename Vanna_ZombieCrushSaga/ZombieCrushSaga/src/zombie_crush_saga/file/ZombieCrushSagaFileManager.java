/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zombie_crush_saga.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie_crush_saga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import zombie_crush_saga.data.ZombieCrushSagaLevelRecord;
import zombie_crush_saga.data.ZombieCrushSagaDataModel;
import zombie_crush_saga.data.ZombieCrushSagaRecord;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;
import properties_manager.PropertiesManager;

/**
 * This class provides services for efficiently loading and saving binary files
 * for the Mahjong game application.
 *
 * @author vanna
 */
public class ZombieCrushSagaFileManager {
    // WE'LL LET THE GAME KNOW WHEN DATA LOADING IS COMPLETE

    private ZombieCrushSagaMiniGame miniGame;

    /**
     * Constructor for initializing this file manager, it simply keeps the game
     * for later.
     *
     * @param initMiniGame The game for which this class loads data.
     */
    public ZombieCrushSagaFileManager(ZombieCrushSagaMiniGame initMiniGame) {
        // KEEP IT FOR LATER
        miniGame = initMiniGame;
    }

    public void resetRecord(String levelFile) {
        File fileToDelete = new File(levelFile);
        fileToDelete.delete();
    }

    /**
     * This method loads the contents of the levelFile argument so that the
     * player may then play that level.
     *
     * @param levelFile Level to load.
     */
    public void loadLevel(String levelFile) {
        // LOAD THE RAW DATA SO WE CAN USE IT
        // OUR LEVEL FILES WILL HAVE THE DIMENSIONS FIRST,
        // FOLLOWED BY THE GRID VALUES
        try {
            File fileToOpen = new File(levelFile);
           // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
            // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
            // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
            byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            FileInputStream fis = new FileInputStream(fileToOpen);
            BufferedInputStream bis = new BufferedInputStream(fis);

            // HERE IT IS, THE ONLY READY REQUEST WE NEED
            bis.read(bytes);
            bis.close();

            // NOW WE NEED TO LOAD THE DATA FROM THE BYTE ARRAY
            DataInputStream dis = new DataInputStream(bais);

            // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
            // ORDER AND FORMAT AS WE SAVED IT
            // FIRST READ THE TOTAL TILES USED
            int initTotalTiles = dis.readInt();

            // THEN READ THE GRID DIMENSIONS
            int initGridColumns = dis.readInt();
            int initGridRows = dis.readInt();
            int[][] newGrid = new int[initGridRows][initGridColumns];
            int[][] newJellyGrid = new int[initGridRows][initGridColumns];
            int jelly;
            int levelReq1, levelReq2, levelReq3;
            int moves;
            String levelDesc;

            // AND NOW ALL THE CELL VALUES
            for (int i = 0; i < initGridRows; i++) {
                for (int j = 0; j < initGridColumns; j++) {
                    newGrid[i][j] = dis.readInt();
                }
            }
            // AND NOW ALL THE JELLY VALUES
            for (int i = 0; i < initGridRows; i++) {
                for (int j = 0; j < initGridColumns; j++) {
                    newJellyGrid[i][j] = dis.readInt();
                }
            }
            // THE AMOUNT OF JELLY IN THE LEVEL
            jelly = dis.readInt();
            // THE POINTS REQUIRED TO WIN THE LEVEL
            levelReq1 = dis.readInt();
            levelReq2 = dis.readInt();
            levelReq3 = dis.readInt();
           
            // THE NUMBER OF MOVES AVAILABLE IN THE LEVEL
            moves = dis.readInt();
            StringBuilder input = new StringBuilder();
            String tmp;
            while ((tmp = dis.readLine()) != null) {
                input.append(tmp);
            }
            levelDesc = input.toString();
           
            // EVERYTHING WENT AS PLANNED SO LET'S MAKE IT PERMANENT
            ZombieCrushSagaDataModel dataModel = (ZombieCrushSagaDataModel) miniGame.getDataModel();
           
            dataModel.initLevelGrid(newGrid, newJellyGrid, initGridColumns, initGridRows, initTotalTiles);
            
            dataModel.setLevelStats(levelReq1, levelReq2, levelReq3, moves, levelDesc, jelly);
           
            miniGame.updateBoundaries();
        } catch (Exception e) {
            // LEVEL LOADING ERROR
            miniGame.getErrorHandler().processError(ZombieCrushSagaPropertyType.LOAD_LEVEL_ERROR);
        }
    }

    public void saveRecord(ZombieCrushSagaRecord record) {
        try {
            record.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(ZombieCrushSagaFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);
            String recordPath = dataPath + props.getProperty(ZombieCrushSagaPropertyType.RECORD_FILE_NAME);
            File fileToOpen = new File(recordPath);

            // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
            // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
            // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
            byte[] bytes = record.toByteArray();
            FileOutputStream fis = new FileOutputStream(fileToOpen);
            BufferedOutputStream bis = new BufferedOutputStream(fis);

            // HERE IT IS, THE ONLY READY REQUEST WE NEED
            bis.write(bytes);
            bis.close();

        } catch (Exception e) {
        }
    }

    /**
     * This method loads the player record from the records file so that the
     * user may view stats.
     *
     * @return The fully loaded record from the player record file.
     */
    public ZombieCrushSagaRecord loadRecord() {
        ZombieCrushSagaRecord recordToLoad = new ZombieCrushSagaRecord();

        // LOAD THE RAW DATA SO WE CAN USE IT
        // OUR LEVEL FILES WILL HAVE THE DIMENSIONS FIRST,
        // FOLLOWED BY THE GRID VALUES
        try {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);
            String recordPath = dataPath + props.getProperty(ZombieCrushSagaPropertyType.RECORD_FILE_NAME);
            File fileToOpen = new File(recordPath);

            // LET'S USE A FAST LOADING TECHNIQUE. WE'LL LOAD ALL OF THE
            // BYTES AT ONCE INTO A BYTE ARRAY, AND THEN PICK THAT APART.
            // THIS IS FAST BECAUSE IT ONLY HAS TO DO FILE READING ONCE
            byte[] bytes = new byte[Long.valueOf(fileToOpen.length()).intValue()];
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            FileInputStream fis = new FileInputStream(fileToOpen);
            BufferedInputStream bis = new BufferedInputStream(fis);

            // HERE IT IS, THE ONLY READY REQUEST WE NEED
            bis.read(bytes);
            bis.close();

            // NOW WE NEED TO LOAD THE DATA FROM THE BYTE ARRAY
            DataInputStream dis = new DataInputStream(bais);

            // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
            // ORDER AND FORMAT AS WE SAVED IT
            // FIRST READ THE NUMBER OF LEVELS
            int highestLevel = dis.readInt();
            int powerUps = dis.readInt();
            String level;

            // SAVES EACH LEVEL AS LEVEL_I
            for (int i = 0; i < highestLevel; i++) {
                //level = props.getProperty(ZombieCrushSagaPropertyType.LEVEL_PATH) + i;
                level = "LEVEL_" + (i + 1);
                ZombieCrushSagaLevelRecord rec = new ZombieCrushSagaLevelRecord();

                rec.highestScore = dis.readInt();
                recordToLoad.addZombieCrushSagaLevelRecord(level, rec);
            }
            recordToLoad.setHighestLevel(highestLevel);
            recordToLoad.setPowerUps(powerUps);
        } catch (Exception e) {
            // THERE WAS NO RECORD TO LOAD, SO WE'LL JUST RETURN AN
            // EMPTY ONE AND SQUELCH THIS EXCEPTION
        }
        return recordToLoad;
    }
}
