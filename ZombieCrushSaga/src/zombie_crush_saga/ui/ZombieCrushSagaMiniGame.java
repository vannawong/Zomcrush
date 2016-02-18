package zombie_crush_saga.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import zombie_crush_saga.data.ZombieCrushSagaDataModel;
import mini_game.MiniGame;
import static zombie_crush_saga.ZombieCrushSagaConstants.*;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import zombie_crush_saga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import zombie_crush_saga.file.ZombieCrushSagaFileManager;
import zombie_crush_saga.data.ZombieCrushSagaRecord;
import zombie_crush_saga.events.ExitHandler;
import zombie_crush_saga.events.KeyHandler;
import zombie_crush_saga.events.LevelSelectHandler;
import zombie_crush_saga.events.PlayHandler;
import zombie_crush_saga.events.PlayLevelHandler;
import zombie_crush_saga.events.PowerUpsHandler;
import zombie_crush_saga.events.QuitLevelHandler;
import zombie_crush_saga.events.ResetHandler;
import zombie_crush_saga.events.RetryHandler;
import zombie_crush_saga.events.ScrollHandler;

public class ZombieCrushSagaMiniGame extends MiniGame {

    // THE PLAYER RECORD FOR EACH LEVEL, WHICH LIVES BEYOND ONE SESSION
    private ZombieCrushSagaRecord record;
    // HANDLES ERROR CONDITIONS
    private ZombieCrushSagaErrorHandler errorHandler;
    // MANAGES LOADING OF LEVELS AND THE PLAYER RECORDS FILES
    private ZombieCrushSagaFileManager fileManager;
    // THE SCREEN CURRENTLY BEING PLAYED
    private String currentScreenState;
    // THE SAGA SCREEN CURRENTLY BEING DISPLAYED
    private int currentSagaScreen = 1;
    // THE LEVELS PER SCREEN BOUNDS
    private int lower = 0;
    private int upper = 15;
    // THE LEVEL THAT IS CURRENTLY DISPLAYED IN LEVEL SCORE OR GAME PLAY
    private String currentLevel = "";
    public int level;
    private int highestLevelScore;
    public boolean inProgress;

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    // ACCESSOR METHODS
    // - getPlayerRecord
    // - getErrorHandler
    // - getFileManager
    // - isCurrentScreenState
    /**
     * Accessor method for getting the player record object, which summarizes
     * the player's record on all levels.
     *
     * @return The player's complete record.
     */
    public ZombieCrushSagaRecord getPlayerRecord() {
        return record;
    }

    /**
     * Accessor method for getting the application's current screen state.
     *
     * @return The error handler.
     */
    public String getCurrentScreenState() {
        return currentScreenState;
    }

    /**
     * Accessor method for getting the application's error handler.
     *
     * @return The error handler.
     */
    public ZombieCrushSagaErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Accessor method for getting the app's file manager.
     *
     * @return The file manager.
     */
    public ZombieCrushSagaFileManager getFileManager() {
        return fileManager;
    }

    /**
     * Used for testing to see if the current screen state matches the
     * testScreenState argument. If it mates, true is returned, else false.
     *
     * @param testScreenState Screen state to test against the current state.
     *
     * @return true if the current state is testScreenState, false otherwise.
     */
    public boolean isCurrentScreenState(String testScreenState) {
        return testScreenState.equals(currentScreenState);
    }

    // SERVICE METHODS
    // - displayStats
    // - savePlayerRecord
    // - switchToGameScreen
    // - switchToSplashScreen
    // - updateBoundaries
    /**
     * This method displays makes the stats dialog display visible, which
     * includes the text inside.
     */
    public void displayStats() {
        ZombieCrushSagaDataModel data = new ZombieCrushSagaDataModel(this);
        // MAKE SURE ONLY THE PROPER DIALOG IS VISIBLE
        guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDialogs.get(STATS_DIALOG_TYPE).setState(VISIBLE_STATE);
        guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
    }

    /**
     * This method closes the stats dialog display.
     */
    public void closeStats() {
        guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);
    }

    /**
     * This method forces the file manager to save the current player record.
     */
    public void savePlayerRecord() {
        fileManager.saveRecord(record);
    }

    /**
     * This method switches the application to the game screen, making all the
     * appropriate UI controls visible & invisible.
     */
    public void switchToGameScreen(String levelFile) {
        currentScreenState = GAME_SCREEN_STATE;
        ((ZombieCrushSagaDataModel) data).initZombies();
        ((ZombieCrushSagaDataModel) data).reset(this);

        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(GAME_SCREEN_STATE);

        guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(false);

        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setEnabled(false);

        guiDecor.get(SCORE_CONTAINER_TYPE).setState(VISIBLE_STATE);
        guiDecor.get(MOVES_CONTAINER_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(POWER_UPS_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(POWER_UPS_BUTTON_TYPE).setEnabled(true);
        guiDecor.get(SCORE_COUNT_TYPE).setState(VISIBLE_STATE);
        guiDecor.get(MOVES_COUNT_TYPE).setState(VISIBLE_STATE);

        guiDecor.get(THREE_STAR_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(TWO_STAR_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(ONE_STAR_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(PROGRESS_TYPE).setState(VISIBLE_STATE);

        // PLAY THE GAMEPLAY SCREEN SONG
        audio.stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
        audio.play(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString(), true);
    }

    /**
     * This method switches the application to the splash screen, making all the
     * appropriate UI controls visible & invisible.
     */
    public void switchToSplashScreen() {
        inProgress = false;
        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(SPLASH_SCREEN_STATE);

        // ACTIVATE THE TOOLBAR CONTROLS
        guiButtons.get(PLAY_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(PLAY_BUTTON_TYPE).setEnabled(true);
        guiButtons.get(QUIT_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(QUIT_BUTTON_TYPE).setEnabled(true);

        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setEnabled(false);

        if (record.getHighestLevel() > 0) {
            guiButtons.get(RESET_BUTTON_TYPE).setState(VISIBLE_STATE);
            guiButtons.get(RESET_BUTTON_TYPE).setEnabled(true);
        } else {
            guiButtons.get(RESET_BUTTON_TYPE).setState(INVISIBLE_STATE);
            guiButtons.get(RESET_BUTTON_TYPE).setEnabled(false);
        }

        for (int i = 1; i <= LEVELS; i++) {
            guiButtons.get("LEVEL_" + i).setState(INVISIBLE_STATE);
            guiButtons.get("LEVEL_" + i).setEnabled(false);
        }

        /*Code for later use?
         * // ACTIVATE THE LEVEL SELECT BUTTONS
         // DEACTIVATE THE LEVEL SELECT BUTTONS
         PropertiesManager props = PropertiesManager.getPropertiesManager();
         ArrayList<String> levels = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.LEVEL_OPTIONS);
         for (String level : levels) {
         guiButtons.get(level).setState(VISIBLE_STATE);
         guiButtons.get(level).setEnabled(true);
         }
         * */
        // DEACTIVATE ALL DIALOGS
        guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(THREE_STAR_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(TWO_STAR_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(ONE_STAR_TYPE).setState(INVISIBLE_STATE);

        //guiDialogs.get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);
        // HIDE THE TILES
        ((ZombieCrushSagaDataModel) data).enableZombies(false);

        // MAKE THE CURRENT SCREEN THE SPLASH SCREEN
        currentScreenState = SPLASH_SCREEN_STATE;

        // PLAY THE WELCOME SCREEN SONG
        audio.play(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString(), true);
        audio.stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
    }

    /**
     * Helper method to create dialog for when the board is shuffling.
     */
    public void shuffleBoard() {
        JOptionPane.showMessageDialog(null, "No more matches available. Board is shuffled!");
    }

    public void switchToSagaScreen() {
        currentScreenState = SAGA_SCREEN_STATE;
        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(SAGA_SCREEN_STATE + "_" + currentSagaScreen);

        // ACTIVATE THE TOOLBAR CONTROLS
        guiButtons.get(PLAY_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(PLAY_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(RESET_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(RESET_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setEnabled(true);
        guiButtons.get(QUIT_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(QUIT_BUTTON_TYPE).setEnabled(true);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setEnabled(true);
        guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(false);

        guiButtons.get(QUIT_LEVEL_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(QUIT_LEVEL_BUTTON_TYPE).setEnabled(false);

        guiDecor.get(SCORE_CONTAINER_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(MOVES_CONTAINER_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(POWER_UPS_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(POWER_UPS_BUTTON_TYPE).setEnabled(false);
        guiDecor.get(SCORE_COUNT_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(MOVES_COUNT_TYPE).setState(INVISIBLE_STATE);

        guiDecor.get(THREE_STAR_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(TWO_STAR_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(ONE_STAR_TYPE).setState(INVISIBLE_STATE);

        guiButtons.get(RETRY_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(RETRY_BUTTON_TYPE).setEnabled(false);

        guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(PROGRESS_TYPE).setState(INVISIBLE_STATE);

        audio.stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
        audio.stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());

        for (int i = 1; i <= LEVELS; i++) {
            guiButtons.get("LEVEL_" + i).setState(VISIBLE_STATE);
            if (i <= record.getHighestLevel() + 1) {
                guiButtons.get("LEVEL_" + i).setEnabled(true);
            } else {
                guiButtons.get("LEVEL_" + i).setEnabled(false);
            }

        }
    }

    /**
     * Helper method to display the retry button after a loss.
     */
    public void endGameAsLoss() {
        guiButtons.get(RETRY_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(RETRY_BUTTON_TYPE).setEnabled(true);
    }

    /**
     * This method updates the game grid boundaries, which will depend on the
     * level loaded.
     */
    public void updateBoundaries() {
        // NOTE THAT THE ONLY ONES WE CARE ABOUT ARE THE LEFT & TOP BOUNDARIES
        float totalWidth = ((ZombieCrushSagaDataModel) data).getGridColumns() * TILE_IMAGE_WIDTH;
        float halfTotalWidth = totalWidth / 2.0f;
        float halfViewportWidth = data.getGameWidth() / 2.0f;
        boundaryLeft = halfViewportWidth - halfTotalWidth;

        // THE LEFT & TOP BOUNDARIES ARE WHERE WE START RENDERING TILES IN THE GRID
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        float topOffset = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_TOP_OFFSET.toString()));
        float totalHeight = ((ZombieCrushSagaDataModel) data).getGridRows() * TILE_IMAGE_HEIGHT;
        float halfTotalHeight = totalHeight / 2.0f;
        float halfViewportHeight = (data.getGameHeight() - topOffset) / 2.0f;
        boundaryTop = topOffset + halfViewportHeight - halfTotalHeight;
    }

    // METHODS OVERRIDDEN FROM MiniGame
    // - initAudioContent
    // - initData
    // - initGUIControls
    // - initGUIHandlers
    // - reset
    // - updateGUI
    @Override
    /**
     * Initializes the sound and music to be used by the application.
     */
    public void initAudioContent() {
        try {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String audioPath = props.getProperty(ZombieCrushSagaPropertyType.AUDIO_PATH);

            // LOAD ALL THE AUDIO
            loadAudioCue(ZombieCrushSagaPropertyType.SELECT_AUDIO_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.MATCH_AUDIO_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.NO_MATCH_AUDIO_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.BLOCKED_TILE_AUDIO_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.UNDO_AUDIO_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.WIN_AUDIO_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.LOSS_AUDIO_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE);
            loadAudioCue(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE);

            // PLAY THE WELCOME SCREEN SONG
            audio.play(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString(), true);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InvalidMidiDataException | MidiUnavailableException e) {
            errorHandler.processError(ZombieCrushSagaPropertyType.AUDIO_FILE_ERROR);
        }
    }

    /**
     * This helper method loads the audio file associated with audioCueType,
     * which should have been specified via an XML properties file.
     */
    private void loadAudioCue(ZombieCrushSagaPropertyType audioCueType)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException,
            InvalidMidiDataException, MidiUnavailableException {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String audioPath = props.getProperty(ZombieCrushSagaPropertyType.AUDIO_PATH);
        String cue = props.getProperty(audioCueType.toString());
        audio.loadAudio(audioCueType.toString(), audioPath + cue);
    }

    /**
     * Initializes the game data used by the application. Note that it is this
     * method's obligation to construct and set this Game's custom GameDataModel
     * object as well as any other needed game objects.
     */
    @Override
    public void initData() {
        // INIT OUR ERROR HANDLER
        errorHandler = new ZombieCrushSagaErrorHandler(window);

        // INIT OUR FILE MANAGER
        fileManager = new ZombieCrushSagaFileManager(this);

        // LOAD THE PLAYER'S RECORD FROM A FILE
        record = fileManager.loadRecord();

        // INIT OUR DATA MANAGER
        data = new ZombieCrushSagaDataModel(this);
        ((ZombieCrushSagaDataModel) data).powerUps = record.getPowerUps();

        // LOAD THE GAME DIMENSIONS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        int gameWidth = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_WIDTH.toString()));
        int gameHeight = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_HEIGHT.toString()));
        data.setGameDimensions(gameWidth, gameHeight);

        // THIS WILL CHANGE WHEN WE LOAD A LEVEL
        boundaryLeft = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_LEFT_OFFSET.toString()));
        boundaryTop = Integer.parseInt(props.getProperty(ZombieCrushSagaPropertyType.GAME_TOP_OFFSET.toString()));
        boundaryRight = gameWidth - boundaryLeft;
        boundaryBottom = gameHeight;
    }

    /**
     * Initializes the game controls, like buttons, used by the game
     * application. Note that this includes the tiles, which serve as buttons of
     * sorts.
     */
    @Override
    public void initGUIControls() {
        // WE'LL USE AND REUSE THESE FOR LOADING STUFF
        BufferedImage img;
        float x, y;
        SpriteType sT;
        Sprite s;

        // FIRST PUT THE ICON IN THE WINDOW
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(ZombieCrushSagaPropertyType.IMG_PATH);
        String windowIconFile = props.getProperty(ZombieCrushSagaPropertyType.WINDOW_ICON);
        img = loadImage(imgPath + windowIconFile);
        window.setIconImage(img);

        // CONSTRUCT THE PANEL WHERE WE'LL DRAW EVERYTHING
        canvas = new ZombieCrushSagaPanel(this, (ZombieCrushSagaDataModel) data);

        // LOAD THE BACKGROUNDS, WHICH ARE GUI DECOR
        currentScreenState = SPLASH_SCREEN_STATE;
        img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.SPLASH_SCREEN_IMAGE_NAME));
        sT = new SpriteType(BACKGROUND_TYPE);
        s = new Sprite(sT, 0, 0, 0, 0, SPLASH_SCREEN_STATE);
        sT.addState(SPLASH_SCREEN_STATE, img);

        img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.GAME_BACKGROUND_IMAGE_NAME));
        sT.addState(GAME_SCREEN_STATE, img);

        img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.SAGA_SCREEN_IMAGE_NAME));
        sT.addState(SAGA_SCREEN_STATE, img);

        img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.ABOUT_SCREEN_IMAGE_NAME));
        sT.addState(ABOUT_SCREEN_STATE, img);

        img = loadImage(imgPath + props.getProperty(ZombieCrushSagaPropertyType.LEVEL_SCORE_SCREEN_IMAGE_NAME));
        sT.addState(LEVEL_SCORE_SCREEN_STATE, img);

        ArrayList<String> sagaBackgrounds = props.getPropertyOptionsList(ZombieCrushSagaPropertyType.SAGA_SCREEN_IMAGE_OPTIONS);

        for (int i = 0; i < sagaBackgrounds.size(); i++) {
            img = loadImageWithColorKey(imgPath + sagaBackgrounds.get(i), COLOR_KEY);
            sT.addState(SAGA_SCREEN_STATE + "_" + (i + 1), img);
        }

        guiDecor.put(BACKGROUND_TYPE, s);

        // THEN THE PLAY BUTTON
        String playButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_BUTTON_IMAGE_NAME);
        sT = new SpriteType(PLAY_BUTTON_TYPE);
        img = loadImage(imgPath + playButton);
        sT.addState(VISIBLE_STATE, img);
        String newMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + newMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, PLAY_BUTTON_X, PLAY_BUTTON_Y, 0, 0, VISIBLE_STATE);
        guiButtons.put(PLAY_BUTTON_TYPE, s);

        String resetButton = props.getProperty(ZombieCrushSagaPropertyType.RESET_BUTTON_IMAGE_NAME);
        sT = new SpriteType(RESET_BUTTON_TYPE);
        img = loadImage(imgPath + resetButton);
        sT.addState(VISIBLE_STATE, img);
        String backMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.RESET_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + backMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        if (record.getHighestLevel() > 0) {
            s = new Sprite(sT, RESET_BUTTON_X, RESET_BUTTON_Y, 0, 0, VISIBLE_STATE);
        } else {
            s = new Sprite(sT, RESET_BUTTON_X, RESET_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        }
        guiButtons.put(RESET_BUTTON_TYPE, s);

        String quitButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_IMAGE_NAME);
        sT = new SpriteType(QUIT_BUTTON_TYPE);
        img = loadImage(imgPath + quitButton);
        sT.addState(VISIBLE_STATE, img);
        String quitMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + quitMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, QUIT_BUTTON_X, QUIT_BUTTON_Y, 0, 0, VISIBLE_STATE);
        guiButtons.put(QUIT_BUTTON_TYPE, s);

        String scrollUpButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_UP_BUTTON_IMAGE_NAME);
        sT = new SpriteType(SCROLL_UP_BUTTON_TYPE);
        img = loadImage(imgPath + scrollUpButton);
        sT.addState(VISIBLE_STATE, img);
        String scrollUpMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_UP_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + scrollUpMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SCROLL_UP_BUTTON_X, SCROLL_UP_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(SCROLL_UP_BUTTON_TYPE, s);

        String scrollDownButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_DOWN_BUTTON_IMAGE_NAME);
        sT = new SpriteType(SCROLL_DOWN_BUTTON_TYPE);
        img = loadImage(imgPath + scrollDownButton);
        sT.addState(VISIBLE_STATE, img);
        String scrollDownMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.SCROLL_DOWN_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + scrollDownMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, SCROLL_DOWN_BUTTON_X, SCROLL_DOWN_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(SCROLL_DOWN_BUTTON_TYPE, s);

        // THEN THE PLAY LEVEL BUTTON
        String playLevelButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_LEVEL_BUTTON_IMAGE_NAME);
        sT = new SpriteType(PLAY_LEVEL_BUTTON_TYPE);
        img = loadImage(imgPath + playLevelButton);
        sT.addState(VISIBLE_STATE, img);
        String playLevelMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.PLAY_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + playLevelMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, PLAY_LEVEL_BUTTON_X, PLAY_LEVEL_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(PLAY_LEVEL_BUTTON_TYPE, s);

        String quitLevelButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_LEVEL_BUTTON_IMAGE_NAME);
        sT = new SpriteType(QUIT_LEVEL_BUTTON_TYPE);
        img = loadImage(imgPath + quitLevelButton);
        sT.addState(VISIBLE_STATE, img);
        String quitLevelMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.QUIT_LEVEL_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + quitLevelMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, QUIT_LEVEL_BUTTON_X, QUIT_LEVEL_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(QUIT_LEVEL_BUTTON_TYPE, s);

        // THE RETRY BUTTON
        String retryButton = props.getProperty(ZombieCrushSagaPropertyType.RETRY_BUTTON_IMAGE_NAME);
        sT = new SpriteType(RETRY_BUTTON_TYPE);
        img = loadImage(imgPath + retryButton);
        sT.addState(VISIBLE_STATE, img);
        String retryMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.RETRY_BUTTON_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + retryMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, PLAY_LEVEL_BUTTON_X, PLAY_LEVEL_BUTTON_Y, 0, 0, INVISIBLE_STATE);
        guiButtons.put(RETRY_BUTTON_TYPE, s);

        x = LEVELS_INIT_X;
        y = LEVELS_INIT_Y;
        int j = 0;
        String levelImagePath = props.getProperty(ZombieCrushSagaPropertyType.BLANK_LEVEL_IMAGE_NAME);
        String levelMouseOverImageName = props.getProperty(ZombieCrushSagaPropertyType.BLANK_LEVEL_MOUSE_OVER_IMAGE_NAME);
        for (int i = 0; i < LEVELS; i++) {
            /* if (x > LEVELS_BOUND_X & y < LEVELS_BOUND_Y) {
             } else */
            if (x > LEVELS_BOUND_X || x < LEVELS_INIT_X) {
                x -= Math.pow(-1, j) * LEVELS_INC_X;
                y -= LEVELS_INC_Y;
                j++;
            }
            if (y < LEVELS_BOUND_Y) {
                j = 0;
                x = LEVELS_INIT_X;
                y = LEVELS_INIT_Y;
            }
            sT = new SpriteType(LEVEL_SELECT_BUTTON_TYPE);
            img = loadImageWithColorKey(imgPath + levelImagePath, COLOR_KEY);
            sT.addState(VISIBLE_STATE, img);

            levelImagePath = props.getProperty(ZombieCrushSagaPropertyType.BLANK_LEVEL_IMAGE_NAME);
            img = loadImageWithColorKey(imgPath + levelMouseOverImageName, COLOR_KEY);
            sT.addState(MOUSE_OVER_STATE, img);

            s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
            guiButtons.put("LEVEL_" + (i + 1), s);
            x += Math.pow(-1, j) * LEVELS_INC_X;
        }
        lower = 1;
        upper = 15;

        // NOW THE PROGRESS BAR, BOTTOM RIGHT
        sT = new SpriteType(PROGRESS_TYPE);
        img = loadImage("./img/zomcrush/UIZomcrushProgressBar.png");
        sT.addState(VISIBLE_STATE, img);
        x = 1200 - 315;
        y = 150;
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(PROGRESS_TYPE, s);

        // ADD THE CONTROLS ALONG THE NORTH OF THE GAME SCREEN AND POWER UPS DISPLAY
        String powerUps = props.getProperty(ZombieCrushSagaPropertyType.POWER_UPS_IMAGE_NAME);
        sT = new SpriteType(POWER_UPS_BUTTON_TYPE);
        img = loadImage(imgPath + powerUps);
        sT.addState(VISIBLE_STATE, img);
        String powerUpsMouseOverButton = props.getProperty(ZombieCrushSagaPropertyType.POWER_UPS_MOUSE_OVER_IMAGE_NAME);
        img = loadImage(imgPath + powerUpsMouseOverButton);
        sT.addState(MOUSE_OVER_STATE, img);
        s = new Sprite(sT, POWER_UPS_X, 0, 0, 0, INVISIBLE_STATE);
        guiButtons.put(POWER_UPS_BUTTON_TYPE, s);

        // AND THE CONTAINERS
        String scoreContainer = props.getProperty(ZombieCrushSagaPropertyType.BLANK_COUNT_IMAGE_NAME);
        sT = new SpriteType(SCORE_CONTAINER_TYPE);
        img = loadImage(imgPath + scoreContainer);
        sT.addState(VISIBLE_STATE, img);
        s = new Sprite(sT, SCORE_CONTAINER_X, 0, 0, 0, INVISIBLE_STATE);
        guiDecor.put(SCORE_CONTAINER_TYPE, s);

        String moveContainer = props.getProperty(ZombieCrushSagaPropertyType.BLANK_COUNT_IMAGE_NAME);
        sT = new SpriteType(MOVES_CONTAINER_TYPE);
        img = loadImage(imgPath + moveContainer);
        sT.addState(VISIBLE_STATE, img);
        s = new Sprite(sT, MOVES_CONTAINER_X, 0, 0, 0, INVISIBLE_STATE);
        guiDecor.put(MOVES_CONTAINER_TYPE, s);

        String scoreCount = props.getProperty(ZombieCrushSagaPropertyType.SCORE_COUNT_IMAGE_NAME);
        sT = new SpriteType(SCORE_COUNT_TYPE);
        img = loadImage(imgPath + scoreCount);
        sT.addState(VISIBLE_STATE, img);
        s = new Sprite(sT, SCORE_COUNT_X, 0, 0, 0, INVISIBLE_STATE);
        guiDecor.put(SCORE_COUNT_TYPE, s);

        String moveCount = props.getProperty(ZombieCrushSagaPropertyType.MOVES_COUNT_IMAGE_NAME);
        sT = new SpriteType(MOVES_COUNT_TYPE);
        img = loadImage(imgPath + moveCount);
        sT.addState(VISIBLE_STATE, img);
        s = new Sprite(sT, MOVES_COUNT_X, 0, 0, 0, INVISIBLE_STATE);
        guiDecor.put(MOVES_COUNT_TYPE, s);

        String oneStar = props.getProperty(ZombieCrushSagaPropertyType.STAR_IMAGE_NAME);
        sT = new SpriteType(ONE_STAR_TYPE);
        img = loadImageWithColorKey(imgPath + oneStar, COLOR_KEY);
        sT.addState(VISIBLE_STATE, img);
        x = STAR_X;
        y = STAR_Y;
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(ONE_STAR_TYPE, s);

        sT = new SpriteType(TWO_STAR_TYPE);
        img = loadImageWithColorKey(imgPath + oneStar, COLOR_KEY);
        sT.addState(VISIBLE_STATE, img);
        x = STAR_X + STAR_OFFSET;
        y = STAR_Y;
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(TWO_STAR_TYPE, s);

        sT = new SpriteType(THREE_STAR_TYPE);
        img = loadImageWithColorKey(imgPath + oneStar, COLOR_KEY);
        sT.addState(VISIBLE_STATE, img);
        x = STAR_X + 2 * STAR_OFFSET;
        y = STAR_Y;
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDecor.put(THREE_STAR_TYPE, s);

        /*
         // AND THE STATS DISPLAY
         String statsDialog = props.getProperty(ZombieCrushSagaPropertyType.STATS_DIALOG_IMAGE_NAME);
         sT = new SpriteType(STATS_DIALOG_TYPE);
         img = loadImageWithColorKey(imgPath + statsDialog, COLOR_KEY);
         sT.addState(VISIBLE_STATE, img);
         x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
         y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
         s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
         guiDialogs.put(STATS_DIALOG_TYPE, s);
         * */
        // AND THE WIN CONDITION DISPLAY
        String winDisplay = props.getProperty(ZombieCrushSagaPropertyType.WIN_DIALOG_IMAGE_NAME);
        sT = new SpriteType(WIN_DIALOG_TYPE);
        img = loadImageWithColorKey(imgPath + winDisplay, COLOR_KEY);
        sT.addState(VISIBLE_STATE, img);
        x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
        y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);
        guiDialogs.put(WIN_DIALOG_TYPE, s);
        // LOSS CONDITION DISPLAY
        String lossDisplay = props.getProperty(ZombieCrushSagaPropertyType.LOSS_DIALOG_IMAGE_NAME);
        sT = new SpriteType(LOSS_DIALOG_TYPE);
        img = loadImageWithColorKey(imgPath + lossDisplay, COLOR_KEY);

        sT.addState(VISIBLE_STATE, img);
        x = (data.getGameWidth() / 2) - (img.getWidth(null) / 2);
        y = (data.getGameHeight() / 2) - (img.getHeight(null) / 2);
        s = new Sprite(sT, x, y, 0, 0, INVISIBLE_STATE);

        guiDialogs.put(LOSS_DIALOG_TYPE, s);
        // THEN THE TILES STACKED TO THE TOP LEFT
        //((ZombieCrushSagaDataModel) data).initZombies();
    }

    /**
     * Initializes the game event handlers for things like game gui buttons.
     */
    @Override
    public void initGUIHandlers() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String dataPath = props.getProperty(ZombieCrushSagaPropertyType.DATA_PATH);

        // WE'LL HAVE A CUSTOM RESPONSE FOR WHEN THE USER CLOSES THE WINDOW
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ExitHandler eh = new ExitHandler(this);
        window.addWindowListener(eh);

        PlayHandler ngh = new PlayHandler(this);
        guiButtons.get(PLAY_BUTTON_TYPE).setActionListener(ngh);

        ResetHandler bgh = new ResetHandler(this);
        guiButtons.get(RESET_BUTTON_TYPE).setActionListener(bgh);

        guiButtons.get(QUIT_BUTTON_TYPE).setActionListener(eh);

        ScrollHandler sh = new ScrollHandler(this);
        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setActionListener(sh);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setActionListener(sh);

        for (int i = 1; i <= LEVELS; i++) {
            LevelSelectHandler slh = new LevelSelectHandler(this, "" + i);
            guiButtons.get("LEVEL_" + i).setActionListener(slh);
        }

        PlayLevelHandler plh = new PlayLevelHandler(this);
        guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setActionListener(plh);

        QuitLevelHandler qlh = new QuitLevelHandler(this);
        guiButtons.get(QUIT_LEVEL_BUTTON_TYPE).setActionListener(qlh);

        RetryHandler rh = new RetryHandler(this);
        guiButtons.get(RETRY_BUTTON_TYPE).setActionListener(rh);

        KeyHandler kh = new KeyHandler(this);
        this.setKeyListener(kh);

        PowerUpsHandler pu = new PowerUpsHandler(this);
        guiButtons.get(POWER_UPS_BUTTON_TYPE).setActionListener(pu);
    }

    public void switchToLevelScoreScreen(String levelFile, int levelNum) {
        inProgress = false;
        currentLevel = levelFile;
        ((ZombieCrushSagaDataModel) data).setCurrentLevel(currentLevel);
        level = levelNum;
        highestLevelScore = record.getHighestScore(currentLevel);

        currentScreenState = LEVEL_SCORE_SCREEN_STATE;
        // CHANGE THE BACKGROUND
        guiDecor.get(BACKGROUND_TYPE).setState(LEVEL_SCORE_SCREEN_STATE);

        guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(PLAY_LEVEL_BUTTON_TYPE).setEnabled(true);

        guiButtons.get(QUIT_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(QUIT_BUTTON_TYPE).setEnabled(false);

        guiButtons.get(QUIT_LEVEL_BUTTON_TYPE).setState(VISIBLE_STATE);
        guiButtons.get(QUIT_LEVEL_BUTTON_TYPE).setEnabled(true);

        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCROLL_UP_BUTTON_TYPE).setEnabled(false);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(SCROLL_DOWN_BUTTON_TYPE).setEnabled(false);

        guiDecor.get(SCORE_CONTAINER_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(MOVES_CONTAINER_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(POWER_UPS_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(POWER_UPS_BUTTON_TYPE).setEnabled(false);
        guiDecor.get(SCORE_COUNT_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(MOVES_COUNT_TYPE).setState(INVISIBLE_STATE);
        guiDecor.get(PROGRESS_TYPE).setState(INVISIBLE_STATE);

        guiDialogs.get(WIN_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiDialogs.get(LOSS_DIALOG_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(RETRY_BUTTON_TYPE).setState(INVISIBLE_STATE);
        guiButtons.get(RETRY_BUTTON_TYPE).setEnabled(false);

        if (highestLevelScore >= ((ZombieCrushSagaDataModel) data).threeStar) {
            guiDecor.get(THREE_STAR_TYPE).setState(VISIBLE_STATE);
        }
        if (highestLevelScore >= ((ZombieCrushSagaDataModel) data).twoStar) {
            guiDecor.get(TWO_STAR_TYPE).setState(VISIBLE_STATE);
        }
        if (highestLevelScore >= ((ZombieCrushSagaDataModel) data).oneStar) {
            guiDecor.get(ONE_STAR_TYPE).setState(VISIBLE_STATE);
        }

        for (int i = 1; i <= LEVELS; i++) {
            guiButtons.get("LEVEL_" + i).setState(INVISIBLE_STATE);
            guiButtons.get("LEVEL_" + i).setEnabled(false);
        }
        audio.stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
    }

    /**
     * Invoked when a new game is started, resets the player's record.
     */
    @Override
    public void reset() {
        record.reset();
        fileManager.saveRecord(record);
        //record = fileManager.loadRecord();
        switchToSplashScreen();
    }

    public void scroll(String up) {
        for (int i = lower; i <= upper; i++) {
            guiButtons.get("LEVEL_" + i).setState(INVISIBLE_STATE);
            guiButtons.get("LEVEL_" + i).setEnabled(false);
        }
        if (up.equals(SCROLL_UP_BUTTON_TYPE)) {
            if (upper == LEVELS) {
                ;
            } else {
                if ((upper + 15) > LEVELS) {
                    upper = LEVELS;
                    lower += 15;
                } else {
                    lower += 15;
                    upper += 15;
                }
                if (currentSagaScreen < NUM_BACKGROUNDS) {
                    currentSagaScreen++;
                }
            }
        }

        if (up.equals(SCROLL_DOWN_BUTTON_TYPE)) {
            if (upper == LEVELS) {
                upper = lower - 1;
                lower -= 15;
            } else {
                if (lower == 1)
                ; else {
                    if ((lower - 15) < 1) {
                        lower = 1;
                        upper = 15;
                    } else {
                        lower -= 15;
                        upper -= 15;
                    }
                }
                if (currentSagaScreen > 2) {
                    currentSagaScreen--;
                }
            }
        }

        for (int i = lower; i <= upper; i++) {
            guiButtons.get("LEVEL_" + i).setState(VISIBLE_STATE);
            guiButtons.get("LEVEL_" + i).setEnabled(true);
        }
        guiDecor.get(BACKGROUND_TYPE).setState(SAGA_SCREEN_STATE + "_" + currentSagaScreen);
    }

    /**
     * Updates the state of all gui controls according to the current game
     * conditions.
     */
    @Override
    public void updateGUI() {
        // GO THROUGH THE VISIBLE BUTTONS TO TRIGGER MOUSE OVERS
        Iterator<Sprite> buttonsIt = guiButtons.values().iterator();
        while (buttonsIt.hasNext()) {
            Sprite button = buttonsIt.next();

            // ARE WE ENTERING A BUTTON?
            if (button.getState().equals(VISIBLE_STATE)) {
                if (button.containsPoint(data.getLastMouseX(), data.getLastMouseY())) {
                    button.setState(MOUSE_OVER_STATE);
                }
            } // ARE WE EXITING A BUTTON?
            else if (button.getState().equals(MOUSE_OVER_STATE)) {
                if (!button.containsPoint(data.getLastMouseX(), data.getLastMouseY())) {
                    button.setState(VISIBLE_STATE);
                }
            }
        }
    }
}
