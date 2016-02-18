/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zombie_crush_saga;

import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;
import zombie_crush_saga.ui.ZombieCrushSagaErrorHandler;
import xml_utilities.InvalidXMLFileFormatException;
import properties_manager.PropertiesManager;

/**
 *
 * @author vanna
 */
public class ZombieCrushSaga {
    // THIS HAS THE FULL USER INTERFACE AND ONCE IN EVENT
    // HANDLING MODE, BASICALLY IT BECOMES THE FOCAL
    // POINT, RUNNING THE UI AND EVERYTHING ELSE

    static ZombieCrushSagaMiniGame miniGame = new ZombieCrushSagaMiniGame();
    // WE'LL LOAD ALL THE UI AND ART PROPERTIES FROM FILES,
    // BUT WE'LL NEED THESE VALUES TO START THE PROCESS
    static String PROPERTY_TYPES_LIST = "property_types.txt";
    static String UI_PROPERTIES_FILE_NAME = "properties.xml";
    static String PROPERTIES_SCHEMA_FILE_NAME = "properties_schema.xsd";
    static String DATA_PATH = "./data/";

    /**
     * This is where the Mahjong Solitaire game application starts execution.
     * We'll load the application properties and then use them to build our user
     * interface and start the window in event handling mode. Once in that mode,
     * all code execution will happen in response to a user request.
     */
    public static void main(String[] args) {
        try {
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(ZombieCrushSagaPropertyType.UI_PROPERTIES_FILE_NAME, UI_PROPERTIES_FILE_NAME);
            props.addProperty(ZombieCrushSagaPropertyType.PROPERTIES_SCHEMA_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            props.addProperty(ZombieCrushSagaPropertyType.DATA_PATH.toString(), DATA_PATH);
            props.loadProperties(UI_PROPERTIES_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);

            // THEN WE'LL LOAD THE MAHJONG FLAVOR AS SPECIFIED BY THE PROPERTIES FILE
            String gameFlavorFile = props.getProperty(ZombieCrushSagaPropertyType.GAME_FLAVOR_FILE_NAME);
            props.loadProperties(gameFlavorFile, PROPERTIES_SCHEMA_FILE_NAME);

            // NOW WE CAN LOAD THE UI, WHICH WILL USE ALL THE FLAVORED CONTENT
            String appTitle = props.getProperty(ZombieCrushSagaPropertyType.GAME_TITLE_TEXT);
            int fps = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.FPS));
            miniGame.initMiniGame(appTitle, fps);
            miniGame.startGame();
            miniGame.switchToSplashScreen();
        } // THERE WAS A PROBLEM LOADING THE PROPERTIES FILE
        catch (InvalidXMLFileFormatException ixmlffe) {
            // LET THE ERROR HANDLER PROVIDE THE RESPONSE
            ZombieCrushSagaErrorHandler errorHandler = miniGame.getErrorHandler();
            errorHandler.processError(ZombieCrushSagaPropertyType.INVALID_XML_FILE_ERROR_TEXT);
        }
    }

    /**
     * Mahjong SolitairePropertyType represents the types of data that will need
     * to be extracted from XML files.
     */
    public enum ZombieCrushSagaPropertyType {
        /* SETUP FILE NAMES */
        UI_PROPERTIES_FILE_NAME,
        PROPERTIES_SCHEMA_FILE_NAME,
        GAME_FLAVOR_FILE_NAME,
        RECORD_FILE_NAME,
        /* DIRECTORIES FOR FILE LOADING */
        AUDIO_PATH,
        DATA_PATH,
        IMG_PATH,
        LEVEL_PATH,
        /* WINDOW DIMENSIONS & FRAME RATE */
        WINDOW_WIDTH,
        WINDOW_HEIGHT,
        FPS,
        GAME_WIDTH,
        GAME_HEIGHT,
        GAME_LEFT_OFFSET,
        GAME_TOP_OFFSET,
        /* GAME TEXT */
        GAME_TITLE_TEXT,
        EXIT_REQUEST_TEXT,
        INVALID_XML_FILE_ERROR_TEXT,
        ERROR_DIALOG_TITLE_TEXT,
        /* ERROR TYPES */
        AUDIO_FILE_ERROR,
        LOAD_LEVEL_ERROR,
        RECORD_SAVE_ERROR,
        /* IMAGE FILE NAMES */
        WINDOW_ICON,
        SPLASH_SCREEN_IMAGE_NAME,
        GAME_BACKGROUND_IMAGE_NAME,
        ABOUT_SCREEN_IMAGE_NAME,
        LEVEL_SCORE_SCREEN_IMAGE_NAME,
        SAGA_SCREEN_IMAGE_NAME,
        BLANK_TILE_IMAGE_NAME,
        BLANK_TILE_SELECTED_IMAGE_NAME,
        BLANK_LEVEL_IMAGE_NAME,
        BLANK_LEVEL_MOUSE_OVER_IMAGE_NAME,
        // SAGA SCREEN BACKGROUNDS
        SAGA_SCREEN_IMAGE_OPTIONS,
        QUIT_LEVEL_BUTTON_IMAGE_NAME,
        QUIT_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME,
        RETRY_BUTTON_IMAGE_NAME,
        RETRY_BUTTON_MOUSE_OVER_IMAGE_NAME,
        PLAY_LEVEL_BUTTON_IMAGE_NAME,
        PLAY_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME,
        // BUTTONS
        PLAY_BUTTON_IMAGE_NAME,
        PLAY_BUTTON_MOUSE_OVER_IMAGE_NAME,
        RESET_BUTTON_IMAGE_NAME,
        RESET_BUTTON_MOUSE_OVER_IMAGE_NAME,
        QUIT_BUTTON_IMAGE_NAME,
        QUIT_BUTTON_MOUSE_OVER_IMAGE_NAME,
        SCROLL_UP_BUTTON_IMAGE_NAME,
        SCROLL_UP_BUTTON_MOUSE_OVER_IMAGE_NAME,
        SCROLL_DOWN_BUTTON_IMAGE_NAME,
        SCROLL_DOWN_BUTTON_MOUSE_OVER_IMAGE_NAME,
        // IN GAME DISPLAYS
        BLOOD_COUNT_IMAGE_NAME,
        MOVES_COUNT_IMAGE_NAME,
        SCORE_COUNT_IMAGE_NAME,
        BLANK_COUNT_IMAGE_NAME,
        POWER_UPS_IMAGE_NAME,
        POWER_UPS_MOUSE_OVER_IMAGE_NAME,
        // AND THE DIALOGS
        STATS_DIALOG_IMAGE_NAME,
        WIN_DIALOG_IMAGE_NAME,
        LOSS_DIALOG_IMAGE_NAME,
        STAR_IMAGE_NAME,
        /* TILE LOADING STUFF */
        LEVEL_OPTIONS,
        LEVEL_IMAGE_OPTIONS,
        LEVEL_MOUSE_OVER_IMAGE_OPTIONS,
        TYPE_A_TILE,
        TYPE_B_TILE,
        TYPE_C_TILE,
        TYPE_D_TILE,
        TYPE_E_TILE,
        TYPE_F_TILE,
        TILE_BOMB_TYPE,
        /* AUDIO CUES */
        SELECT_AUDIO_CUE,
        MATCH_AUDIO_CUE,
        NO_MATCH_AUDIO_CUE,
        BLOCKED_TILE_AUDIO_CUE,
        UNDO_AUDIO_CUE,
        WIN_AUDIO_CUE,
        LOSS_AUDIO_CUE,
        SPLASH_SCREEN_SONG_CUE,
        GAMEPLAY_SONG_CUE
    }
}