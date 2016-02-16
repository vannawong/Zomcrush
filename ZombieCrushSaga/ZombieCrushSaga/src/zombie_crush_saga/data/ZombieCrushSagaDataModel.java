package zombie_crush_saga.data;

import zombie_crush_saga.ui.ZombieCrushSagaZombie;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import zombie_crush_saga.ZombieCrushSaga.ZombieCrushSagaPropertyType;
import mini_game.MiniGame;
import mini_game.MiniGameDataModel;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import static zombie_crush_saga.ZombieCrushSagaConstants.*;
import zombie_crush_saga.ui.ZombieCrushSagaMiniGame;
import zombie_crush_saga.ui.ZombieCrushSagaPanel;

/**
 * This class manages the game data for Zombie Crush Saga.
 *
 * @author Vanna Wong
 */
public class ZombieCrushSagaDataModel extends MiniGameDataModel {
    // THIS CLASS HAS A REFERERENCE TO THE MINI GAME SO THAT IT
    // CAN NOTIFY IT TO UPDATE THE DISPLAY WHEN THE DATA MODEL CHANGES

    private int totalTiles;
    private MiniGame miniGame;
    // THE LEVEL GRID REFERS TO THE LAYOUT FOR A GIVEN LEVEL, MEANING
    // HOW MANY TILES FIT INTO EACH CELL WHEN FIRST STARTING A LEVEL
    private int[][] levelGrid;
    // LEVEL GRID DIMENSIONS
    private int gridColumns;
    private int gridRows;
    // THIS STORES THE TILES ON THE GRID DURING THE GAME
    private ZombieCrushSagaZombie[][] zombieGrid;
    // ALL THE ZOMBIES IN THE CURRENT GAME
    private ArrayList<ZombieCrushSagaZombie> allZombies;
    // THESE ARE THE TILES THAT ARE MOVING AROUND, AND SO WE HAVE TO UPDATE
    private ArrayList<ZombieCrushSagaZombie> movingZombies;
    // THESE ARE THE TILES THAT MATCH, AND WILL BE PROCESSED IN HELPER METHODS.
    private ArrayList<ZombieCrushSagaZombie> matchingZombies;
    // THIS IS A SELECTED TILE, MEANING THE FIRST OF A PAIR THE PLAYER
    // IS TRYING TO MATCH. THERE CAN ONLY BE ONE OF THESE AT ANY TIME
    private ZombieCrushSagaZombie selectedZombie;
    // THE INITIAL LOCATION OF TILES BEFORE BEING PLACED IN THE GRID
    private int unassignedZombiesX;
    private int unassignedZombiesY;
    // THE REFERENCE TO THE FILE BEING PLAYED
    private String currentLevel;
    // THE NUMBER OF MOVES REMAINING
    private int moves;
    private int levelScore;
    // WE ARE GOING TO HAVE 2 THREADS AT WORK IN THIS APPLICATION,
    // THE MAIN GUI THREAD, WHICH WILL LISTEN FOR USER INTERACTION
    // AND CALL OUR EVENT HANDLERS, AND THE TIMER THREAD, WHICH ON
    // A FIXED SCHEDULE (e.g. 30 times/second) WILL UPDATE ALL THE
    // GAME DATA AND THEN RENDER. THIS LOCK WILL MAKE SURE THAT
    // ONE THREAD DOESN'T RUIN WHAT THE OTHER IS DOING (CALLED A
    // RACE CONDITION). FOR EXAMPLE, IT WOULD BE BAD IF WE STARTED
    // RENDERING THE GAME AND 1/2 WAY THROUGH, THE GAME DATA CHANGED.
    // THAT MAY CAUSE BIG PROBLEMS. EACH THREAD WILL NEED TO LOCK
    // THE DATA BEFORE EACH USE AND THEN UNLOCK WHEN DONE WITH IT.
    protected ReentrantLock dataLock;

    /**
     * Constructor for initializing this data model, it will create the data
     * structures for storing zombies, but not the tile grid itself, that is
     * dependent of file loading, and so should be subsequently initialized.
     *
     * @param initMiniGame The Mahjong game UI.
     */
    public ZombieCrushSagaDataModel(MiniGame initMiniGame) {
        // KEEP THE GAME FOR LATER
        miniGame = initMiniGame;

        // INIT THESE FOR HOLDING MATCHED AND MOVING TILES
        allZombies = new ArrayList();
        movingZombies = new ArrayList();
        // CONSTRUCT OUR LOCK, WHICH WILL MAKE SURE
        // WE ARE NOT UPDATING THE GAME DATA SIMULATEOUSLY
        // IN TWO DIFFERENT THREADS
        dataLock = new ReentrantLock();
    }

    // INIT METHODS - AFTER CONSTRUCTION, THESE METHODS SETUP A GAME FOR USE
    // - initZombies
    // - initZombie
    // - initLevelGrid
    // - initSpriteType
    /**
     * This method loads the zombies, creating an individual sprite for each.
     * Note that zombies may be of various types, which is important during the
     * tile matching tests.
     */
    public void initZombies() {
        matchingZombies = new ArrayList<>();

        for (int i = 0; i < totalTiles; i++) {
            initZombieHelper(BASIC_TYPE, "", INVISIBLE_STATE);
        }
    }

    /**
     * This method helps create the zombies.
     *
     * @param specialType The special type of the tile.
     * @param initType The matching type of the tile.
     * @param initState The initial state of the tile.
     * @return
     */
    public ZombieCrushSagaZombie initZombieHelper(String specialType, String initType, String initState) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imgPath = props.getProperty(ZombieCrushSagaPropertyType.IMG_PATH);
        int spriteTypeID = 0;
        SpriteType sT;

        // WE'LL RENDER ALL THE TILES ON TOP OF THE BLANK TILE
        String blankZombieFileName = props.getProperty(ZombieCrushSagaPropertyType.BLANK_TILE_IMAGE_NAME);
        BufferedImage blankZombieImage = miniGame.loadImageWithColorKey(imgPath + blankZombieFileName, COLOR_KEY);
        ((ZombieCrushSagaPanel) (miniGame.getCanvas())).setBlankZombieImage(blankZombieImage);

        // THIS IS A HIGHLIGHTED BLANK TILE FOR WHEN THE PLAYER SELECTS ONE
        String blankZombieSelectedFileName = props.getProperty(ZombieCrushSagaPropertyType.BLANK_TILE_SELECTED_IMAGE_NAME);
        BufferedImage blankZombieSelectedImage = miniGame.loadImageWithColorKey(imgPath + blankZombieSelectedFileName, COLOR_KEY);
        ((ZombieCrushSagaPanel) (miniGame.getCanvas())).setBlankZombieSelectedImage(blankZombieSelectedImage);

        // TYPES WILL BE GENERATED RANDOMLY
        int type = 0;
        String zombieType = "";
        String zombieImage = "";
        type = (int) (Math.random() * 6.00);
        if (initType.equals("")) {
            switch (type) {
                case 0:
                    zombieType = TYPE_A_TILE;
                    zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_A_TILE);
                    break;
                case 1:
                    zombieType = TYPE_B_TILE;
                    zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_B_TILE);
                    break;
                case 2:
                    zombieType = TYPE_C_TILE;
                    zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_C_TILE);
                    break;
                case 3:
                    zombieType = TYPE_D_TILE;
                    zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_D_TILE);
                    break;
                case 4:
                    zombieType = TYPE_E_TILE;
                    zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_E_TILE);
                    break;
                case 5:
                    zombieType = TYPE_F_TILE;
                    zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_F_TILE);
                    break;
            }
        } else {
            zombieType = initType;
            if (zombieType.equals(TYPE_A_TILE)) {
                zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_A_TILE);
            }

            if (zombieType.equals(TYPE_B_TILE)) {
                zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_B_TILE);
            }

            if (zombieType.equals(TYPE_C_TILE)) {
                zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_C_TILE);
            }

            if (zombieType.equals(TYPE_D_TILE)) {
                zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_D_TILE);
            }

            if (zombieType.equals(TYPE_E_TILE)) {
                zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_E_TILE);
            }

            if (zombieType.equals(TYPE_F_TILE)) {
                zombieImage = props.getProperty(ZombieCrushSagaPropertyType.TYPE_F_TILE);
            }
        }
        spriteTypeID = type;
        String imgFile = imgPath + zombieImage;

        sT = initZombieSpriteType(imgFile, TILE_SPRITE_TYPE_PREFIX + spriteTypeID);
        ZombieCrushSagaZombie newZombie = new ZombieCrushSagaZombie(sT,
                unassignedZombiesX, unassignedZombiesY, 0, 0, initState, zombieType);
        newZombie.setSpecialType(specialType);
        newZombie.setState(initState);
        allZombies.add(newZombie);
        return newZombie;
    }

    /**
     * Called after a level has been selected, it initializes the grid so that
     * it is the proper dimensions.
     *
     * @param initGrid The grid distribution of zombies, where each cell
     * specifies the number of zombies to be stacked in that cell.
     *
     * @param initGridColumns The columns in the grid for the level selected.
     *
     * @param initGridRows The rows in the grid for the level selected.
     */
    public void initLevelGrid(int[][] initGrid, int initGridColumns, int initGridRows, int initTotalTiles) {
        // KEEP ALL THE GRID INFO
        totalTiles = initTotalTiles;
        levelGrid = initGrid;
        gridColumns = initGridColumns;
        gridRows = initGridRows;

        // AND BUILD THE TILE GRID FOR STORING THE TILES
        // SINCE WE NOW KNOW ITS DIMENSIONS
        zombieGrid = new ZombieCrushSagaZombie[gridColumns][gridRows];
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridColumns; j++) {
                zombieGrid[i][j] = null;
            }
        }
        // MAKE ALL THE TILES VISIBLE
        enableZombies(true);
    }

    /**
     * This helper method initializes a sprite type for a tile or set of similar
     * zombies to be created.
     */
    private SpriteType initZombieSpriteType(String imgFile, String spriteTypeID) {
        // WE'LL MAKE A NEW SPRITE TYPE FOR EACH GROUP OF SIMILAR LOOKING TILES
        SpriteType sT = getSpriteType(spriteTypeID);
        if (sT == null) {
            sT = new SpriteType(spriteTypeID);
            // LOAD THE ART
            BufferedImage img = miniGame.loadImageWithColorKey(imgFile, COLOR_KEY);
            Image tempImage = img.getScaledInstance(TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT, BufferedImage.SCALE_SMOOTH);
            img = new BufferedImage(TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            img.getGraphics().drawImage(tempImage, 0, 0, null);

            // WE'LL USE THE SAME IMAGE FOR ALL STATES
            sT.addState(INVISIBLE_STATE, img);
            sT.addState(VISIBLE_STATE, img);
            sT.addState(SELECTED_STATE, img);
            sT.addState(SPECIAL_STRIPED_STATE, img);
            sT.addState(SPECIAL_WRAPPED_STATE, img);
            
            addSpriteType(sT);
        }
        return sT;
    }

    // ACCESSOR METHODS
    /**
     * Accessor method for getting the level currently being played.
     *
     * @return The level name used currently for the game screen.
     */
    public String getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Accessor method for getting the number of tile columns in the game grid.
     *
     * @return The number of zombies in the grid
     */
    public int getTotalTiles() {
        return totalTiles;
    }

    /**
     * Accessor method for getting the number of tile columns in the game grid.
     *
     * @return The number of columns (left to right) in the grid for the level
     * currently loaded.
     */
    public int getGridColumns() {
        return gridColumns;
    }

    /**
     * Accessor method for getting the number of tile rows in the game grid.
     *
     * @return The number of rows (top to bottom) in the grid for the level
     * currently loaded.
     */
    public int getGridRows() {
        return gridRows;
    }

    /**
     * Accessor method for getting the tile grid, which has all the zombies the
     * user may select from.
     *
     * @return The main 2D grid of zombies the user selects zombies from.
     */
    public ZombieCrushSagaZombie[][] getZombieGrid() {
        return zombieGrid;
    }

    /**
     * Accessor method for getting the stack zombies.
     *
     * @return The stack zombies, which are the zombies the matched zombies are
     * placed in.
     */
    public ArrayList<ZombieCrushSagaZombie> getAllZombies() {
        return allZombies;
    }

    /**
     * Accessor method for getting the moving zombies.
     *
     * @return The moving zombies, which are the zombies currently being
     * animated as they move around the game.
     */
    public Iterator<ZombieCrushSagaZombie> getMovingZombies() {
        return movingZombies.iterator();
    }

    /**
     * Mutator method for setting the currently loaded level.
     *
     * @param initCurrentLevel The level name currently being used to play the
     * game.
     */
    public void setCurrentLevel(String initCurrentLevel) {
        currentLevel = initCurrentLevel;
    }

    /**
     * Used to calculate the x-axis pixel location in the game grid for a tile
     * placed at column with stack position z.
     *
     * @param column The column in the grid the tile is located.
     *
     * @param z The level of the tile in the stack at the given grid location.
     *
     * @return The x-axis pixel location of the tile
     */
    public int calculateZombieXInGrid(int column) {
        int cellWidth = TILE_IMAGE_WIDTH;
        float leftEdge = miniGame.getBoundaryLeft();
        return (int) (leftEdge + (cellWidth * column));
    }

    /**
     * Used to calculate the y-axis pixel location in the game grid for a tile
     * placed at row with stack position z.
     *
     * @param row The row in the grid the tile is located.
     *
     * @param z The level of the tile in the stack at the given grid location.
     *
     * @return The y-axis pixel location of the tile
     */
    public int calculateZombieYInGrid(int row) {
        int cellHeight = TILE_IMAGE_HEIGHT;
        float topEdge = miniGame.getBoundaryTop();
        return (int) (topEdge + (cellHeight * row));
    }

    /**
     * Used to calculate the grid column for the x-axis pixel location.
     *
     * @param x The x-axis pixel location for the request.
     *
     * @return The column that corresponds to the x-axis location x.
     */
    public int calculateGridCellRow(int x) {
        float leftEdge = miniGame.getBoundaryLeft();
        x = (int) (x - leftEdge);
        return x / TILE_IMAGE_WIDTH;
    }

    /**
     * Used to calculate the grid row for the y-axis pixel location.
     *
     * @param y The y-axis pixel location for the request.
     *
     * @return The row that corresponds to the y-axis location y.
     */
    public int calculateGridCellColumn(int y) {
        float topEdge = miniGame.getBoundaryTop();
        y = (int) (y - topEdge);
        return y / TILE_IMAGE_HEIGHT;
    }

    // GAME DATA SERVICE METHODS
    // -enableZombies
    // -findMove
    // -moveAllZombiesToStack
    // -moveZombies
    // -playWinAnimation
    // -processMove
    // -selectZombie
    // -undoLastMove
    /**
     * This method can be used to make all of the zombies either visible (true)
     * or invisible (false). This should be used when switching between the
     * splash and game screens.
     *
     * @param enable Specifies whether the zombies should be made visible or
     * not.
     */
    public void enableZombies(boolean enable) {

        // GO THROUGH ALL OF THEM 
        for (ZombieCrushSagaZombie tile : allZombies) {
            // AND SET THEM PROPERLY
            if (enable) {
                if (tile.getSpecialType().equals(BASIC_TYPE)) {
                    tile.setState(VISIBLE_STATE);
                } else if (tile.getSpecialType().equals(SPECIAL_WRAPPED_TYPE)) {
                    tile.setState(SPECIAL_WRAPPED_STATE);
                } else if (tile.getSpecialType().equals(SPECIAL_STRIPED_TYPE)) {
                    tile.setState(SPECIAL_STRIPED_STATE);
                }
            } else {
                tile.setState(INVISIBLE_STATE);
            }
        }
    }

    /**
     * This method examines the current game grid and finds and returns a valid
     * move that is available.
     *
     * @return A move that can be made, or null if none exist.
     */
    public ZombieCrushSagaMove findMove() {
        /*
         // MAKE A MOVE TO FILL IN 
         ZombieCrushSagaMove move = new ZombieCrushSagaMove();

         // GO THROUGH THE ENTIRE GRID TO FIND A MATCH BETWEEN AVAILABLE TILES
         for (int i = 0; i < gridColumns; i++) {
         for (int j = 0; j < gridRows; j++) {
         ArrayList<ZombieCrushSagaZombie> stack1 = zombieGrid[i][j];
         if (stack1.size() > 0) {
         // GET THE FIRST TILE
         ZombieCrushSagaZombie testZombie1 = stack1.get(stack1.size() - 1);
         for (int k = 0; k < gridColumns; k++) {
         for (int l = 0; l < gridRows; l++) {
         if (!((i == k) && (j == l))) {
         ArrayList<ZombieCrushSagaZombie> stack2 = zombieGrid[k][l];
         if (stack2.size() > 0) {
         // AND TEST IT AGAINST THE SECOND TILE
         ZombieCrushSagaZombie testZombie2 = stack2.get(stack2.size() - 1);

         // DO THEY MATCH
         if (testZombie1.match(testZombie2)) {
         // YES, FILL IN THE MOVE AND RETURN IT
         move.col1 = i;
         move.row1 = j;
         move.col2 = k;
         move.row2 = l;
         return move;
         }
         }
         }
         }
         }
         }
         }
         }
         * */
        // WE'VE SEARCHED THE ENTIRE GRID AND THERE
        // ARE NO POSSIBLE MOVES REMAINING

        return null;

    }

    /**
     * This method removes all the zombies in from argument and moves them to
     * argument.
     *
     * @param from The source data structure of zombies.
     *
     * @param to The destination data structure of zombies.
     */
    private void moveZombies(ZombieCrushSagaZombie from, ArrayList<ZombieCrushSagaZombie> to) {
    }

    /**
     * This method sets up and starts the animation shown after a game is won.
     */
    public void playWinAnimation() {
        // MAKE A NEW PATH
        ArrayList<Integer> winPath = new ArrayList();

        // THIS HAS THE APPROXIMATE PATH NODES, WHICH WE'LL SLIGHTLY
        // RANDOMIZE FOR EACH TILE FOLLOWING THE PATH.
        // NODE 1 - LOWER RIGHT HAND CORNER
        winPath.add(getGameWidth() - WIN_PATH_COORD);
        winPath.add(WIN_PATH_COORD);
        // NODE 2 - MIDDLE LOWER
        winPath.add((getGameWidth() / 2 - WIN_PATH_COORD));
        winPath.add(WIN_PATH_COORD / 2);
        // NODE 3 - LOWER RIGHT HAND CORNER
        winPath.add(WIN_PATH_COORD);
        winPath.add(WIN_PATH_COORD);
        // NODE 4 - MIDDLE RIGHT
        winPath.add(WIN_PATH_COORD / 2);
        winPath.add((getGameHeight() - WIN_PATH_COORD) / 2);
        //NODE 5 - UPPER RIGHT HAND CORNER
        winPath.add(WIN_PATH_COORD);
        winPath.add(getGameHeight() - WIN_PATH_COORD);
        // NODE 6 - MIDDLE UPPER
        winPath.add((getGameWidth() / 2 - WIN_PATH_COORD));
        winPath.add(getGameHeight() - WIN_PATH_COORD / 2);
        // NODE 7 - UPPER LEFT HAND CORNER
        winPath.add(getGameWidth() - WIN_PATH_COORD);
        winPath.add(getGameHeight() - WIN_PATH_COORD);
        // NODE 8 - MIDDLE LEFT
        winPath.add(getGameWidth() - WIN_PATH_COORD / 2);
        winPath.add((getGameHeight() - WIN_PATH_COORD) / 2);

        // START THE ANIMATION FOR ALL THE TILES
        for (int i = 0; i < allZombies.size(); i++) {
            // GET EACH TILE
            ZombieCrushSagaZombie tile = allZombies.get(i);

            // MAKE SURE IT'S MOVED EACH FRAME
            movingZombies.add(tile);

            // AND GET IT ON A PATH
            tile.initWinPath(winPath);
        }
    }

    /**
     * This method updates all the necessary state information to process the
     * move argument.
     *
     * @param move The move to make. Note that a move specifies the cell
     * locations for a match.
     */
    public void processMove(ZombieCrushSagaMove move) {
        // REMOVE THE MOVE TILES FROM THE GRID
        ZombieCrushSagaZombie tile1 = zombieGrid[move.col1][move.row1];
        ZombieCrushSagaZombie tile2 = zombieGrid[move.col2][move.row2];

        // MAKE SURE BOTH ARE UNSELECTED
        tile1.setState(VISIBLE_STATE);
        tile2.setState(VISIBLE_STATE);

        // DISAPPEAR
        // MAKE SURE THEY MOVE
        movingZombies.add(tile1);
        movingZombies.add(tile2);

        // AND MAKE SURE NEW TILES CAN BE SELECTED
        selectedZombie = null;

        // PLAY THE AUDIO CUE
        miniGame.getAudio().play(ZombieCrushSagaPropertyType.MATCH_AUDIO_CUE.toString(), false);

        // NOW CHECK TO SEE IF THE GAME HAS EITHER BEEN WON OR LOST
        // HAS THE PLAYER WON?
        if (moves == 0) {
            // YUP UPDATE EVERYTHING ACCORDINGLY
            endGameAsWin();
        } else {
            // SEE IF THERE ARE ANY MOVES LEFT
            ZombieCrushSagaMove possibleMove = this.findMove();
            if (possibleMove == null) {
                // NOPE, WITH NO MOVES LEFT BUT TILES LEFT ON
                // THE GRID, THE PLAYER HAS LOST
                endGameAsLoss();
            }
        }
    }

    /**
     * This method attempts to select the selectZombie argument. Note that this
     * may be the first or second selected tile. If a tile is already selected,
     * it will attempt to process a match/move.
     *
     * @param selectZombie The tile to select.
     */
    public void selectZombie(ZombieCrushSagaZombie selectZombie) {
        // IF IT'S ALREADY THE SELECTED TILE, DESELECT IT
        if (selectZombie == selectedZombie) {
            selectedZombie = null;
            selectZombie.setState(VISIBLE_STATE);
            return;
        }

        if (selectedZombie == null) {
            selectedZombie = selectZombie;
            selectedZombie.setState(SELECTED_STATE);
            miniGame.getAudio().play(ZombieCrushSagaPropertyType.SELECT_AUDIO_CUE.toString(), false);
        } else {
            selectZombie.setState(SELECTED_STATE);
            if (isValidSwap(selectedZombie, selectZombie)) {
                swapZombies(selectedZombie, selectZombie);
                selectedZombie = null;
            } else {
                selectedZombie.setState(VISIBLE_STATE);
                selectedZombie = selectZombie;
                selectZombie.setState(SELECTED_STATE);
                miniGame.getAudio().play(ZombieCrushSagaPropertyType.SELECT_AUDIO_CUE.toString(), false);
            }
        }
    }

    /**
     * Helper method to determine the type of swap that was done.
     *
     * @param zombie_1
     * @param zombie_2
     */
    public boolean processSwap(ZombieCrushSagaZombie zombie_1, ZombieCrushSagaZombie zombie_2) {
        zombie_1.setState(VISIBLE_STATE);
        zombie_2.setState(VISIBLE_STATE);

        int h1 = matchHorizontal(zombie_1) + 1;
        int h2 = matchHorizontal(zombie_2) + 1;

        int v1 = matchVertical(zombie_1) + 1;
        int v2 = matchVertical(zombie_2) + 1;

        System.out.println(h1 + " " + v1);
        System.out.println(h2 + " " + v2);

        if (h1 >= 5 || v1 >= 5 || h2 >= 5 || v2 >= 5) {
            if (h1 >= 5 || v1 >= 5) {
                matchingZombies.add(zombie_1);
                processMove(zombie_1, TILE_BOMB_TYPE, VISIBLE_STATE);
            }

            if (h2 >= 5 || v2 >= 5) {
                matchingZombies.add(zombie_2);
                processMove(zombie_2, TILE_BOMB_TYPE, VISIBLE_STATE);
            }
            return true;
        }

        if ((h1 >= 3 && v1 >= 3) || (h2 >= 3 && v2 >= 3)) {
            if (h1 >= 3 && v1 >= 3) {
                matchingZombies.add(zombie_1);
                processMove(zombie_1, SPECIAL_WRAPPED_TYPE, SPECIAL_WRAPPED_STATE);
            }

            if (h2 >= 3 && v2 >= 3) {
                matchingZombies.add(zombie_2);
                processMove(zombie_1, SPECIAL_WRAPPED_TYPE, SPECIAL_WRAPPED_STATE);
            }
            return true;
        }

        if (h1 == 4 || v1 == 4 || h2 == 4 || v2 == 4) {
            if (h1 == 4 || v1 == 4) {
                matchingZombies.add(zombie_1);
                processMove(zombie_1, SPECIAL_STRIPED_TYPE, SPECIAL_STRIPED_STATE);
            }
            if (h2 == 4 || v2 == 4) {
                matchingZombies.add(zombie_2);
                processMove(zombie_2, SPECIAL_STRIPED_TYPE, SPECIAL_STRIPED_STATE);
            }
            return true;
        }

        if (h1 == 3 || v1 == 3 || h2 == 3 || v2 == 3) {
            if (h1 == 3 || v1 == 3) {
                matchingZombies.add(zombie_1);
                processMove(zombie_1, BASIC_TYPE, VISIBLE_STATE);
            }
            if (h2 == 3 || v2 == 3) {
                matchingZombies.add(zombie_2);
                processMove(zombie_2, BASIC_TYPE, VISIBLE_STATE);
            }
            return true;
        }

        miniGame.getAudio().play(ZombieCrushSagaPropertyType.NO_MATCH_AUDIO_CUE.toString(), false);
        return false;
    }

    /**
     * This method ONLY CREATES NEW SPRITES. It does not remove any zombies.
     *
     * @param zombie The zombie that the new type is to be created.
     * @param type The type of zombie to be created.
     */
    public void processMove(ZombieCrushSagaZombie zombie, String type, String state) {
        if (type.equals(BASIC_TYPE)) {
            return;
        }

        int r = zombie.getGridRow();
        int c = zombie.getGridColumn();
        float x = zombie.getX();
        float y = zombie.getY();

        // SPECIAL_BOMB_TYPE, VISIBLE STATE
        ZombieCrushSagaZombie z = initZombieHelper(type, zombie.getTileType(), state);
        z.setState(state);
        System.out.println(zombie.getTileType());
        System.out.println(zombie.getState() + " " + state);
        z.setGridCell(r, c);
        z.setX(x);
        z.setY(y);

        zombieGrid[r][c] = z;
        updateGrid();
    }

    public void updateGrid() {
        int r;
        int c;
        for (ZombieCrushSagaZombie z : matchingZombies) {
            z.setState(INVISIBLE_STATE);
            r = z.getGridRow();
            c = z.getGridColumn();
            zombieGrid[r][c] = null;
            ZombieCrushSagaZombie replacementTile = initZombieHelper(BASIC_TYPE, "", VISIBLE_STATE);
            replacementTile.setGridCell(r, c);
        }
        matchingZombies = new ArrayList<>();

    }

    /**
     * Tests for matches in the horizontal direction.
     *
     * @param zombie_1 The zombie in question.
     * @param counter Tells us when the matching started.
     * @param dir Direction we are moving along the grid.
     */
    public int matchVertical(ZombieCrushSagaZombie zombie_1) {
        int r = zombie_1.getGridRow();
        int c = zombie_1.getGridColumn();
        ArrayList<ZombieCrushSagaZombie> temp = new ArrayList<>();
        int t = 0;

        outerloop_1:
        for (int i = c + 1; i < gridRows; i++) {
            if (zombieGrid[r][i] != null) {
                if (zombieGrid[r][i].getTileType().equals(zombie_1.getTileType())) {
                    t++;
                    temp.add(zombieGrid[r][i]);
                } else {
                    break outerloop_1;
                }
            } else {
                break outerloop_1;
            }
        }
        outerloop_2:
        for (int i = c - 1; i >= 0; i--) {
            if (zombieGrid[r][i] != null) {
                if (zombieGrid[r][i].getTileType().equals(zombie_1.getTileType())) {
                    t++;
                    temp.add(zombieGrid[r][i]);
                } else {
                    break outerloop_2;
                }
            } else {
                break outerloop_2;
            }
        }

        if (t >= 2) {
            matchingZombies.addAll(temp);
        }
        return t;
        //} finally {
        //   endUsingData();
        //}
    }

    /**
     * Tests for matches in the horizontal direction.
     *
     * @param zombie_1 The zombie in question.
     * @param counter Tells us when the matching started.
     * @param dir Direction we are moving along the grid.
     */
    public int matchHorizontal(ZombieCrushSagaZombie zombie_1) {
        // try {
        //   beginUsingData();
        int r = zombie_1.getGridRow();
        int c = zombie_1.getGridColumn();
        ArrayList<ZombieCrushSagaZombie> temp = new ArrayList<>();
        int t = 0;
        outerloop_1:
        for (int i = r + 1; i < gridColumns; i++) {
            if (zombieGrid[i][c] != null) {
                if (zombieGrid[i][c].getTileType().equals(zombie_1.getTileType())) {
                    t++;
                    temp.add(zombieGrid[i][c]);
                } else {
                    break outerloop_1;
                }
            } else {
                break outerloop_1;
            }
        }
        outerloop_2:
        for (int i = r - 1; i >= 0; i--) {
            if (zombieGrid[i][c] != null) {
                if (zombieGrid[i][c].getTileType().equals(zombie_1.getTileType())) {
                    t++;
                    temp.add(zombieGrid[i][c]);
                } else {
                    break outerloop_2;
                }
            } else {
                break outerloop_2;
            }
        }

        if (t >= 2) {
            matchingZombies.addAll(temp);
        }
        return t;
        //} finally {
        //   endUsingData();
        //}
    }

    /**
     * Testing to see how many matches in a row
     *
     * @param zombie_1 The zombie in question.
     * @param rDir The direction of the test.
     * @param cDir Direction of the test.
     * @param counter Number of zombies in a row.
     * @return Total number of zombies in a row.
     */
    public boolean isValidSwap(ZombieCrushSagaZombie zombie_1, ZombieCrushSagaZombie zombie_2) {
        int r1 = zombie_1.getGridRow();
        int r2 = zombie_2.getGridRow();
        int c1 = zombie_1.getGridColumn();
        int c2 = zombie_2.getGridColumn();

        if (r1 == r2) {
            if (Math.abs(c1 - c2) == 1) {
                return true;
            }
        }
        if (c1 == c2) {
            if (Math.abs(r1 - r2) == 1) {
                return true;
            }
        }
        return false;
    }

    public void swapZombies(ZombieCrushSagaZombie zombie_1, ZombieCrushSagaZombie zombie_2) {
        int r1 = zombie_1.getGridRow();
        int r2 = zombie_2.getGridRow();
        int c1 = zombie_1.getGridColumn();
        int c2 = zombie_2.getGridColumn();

        float x1 = zombie_1.getX();
        float x2 = zombie_2.getX();
        float y1 = zombie_1.getY();
        float y2 = zombie_2.getY();

        zombie_1.setTarget(x2, y2);
        zombie_2.setTarget(x1, y1);

        zombie_1.startMovingToTarget(MAX_IN_GAME_VELOCITY);
        zombie_2.startMovingToTarget(MAX_IN_GAME_VELOCITY);

        zombie_1.setGridCell(r2, c2);
        zombie_2.setGridCell(r1, c1);

        zombieGrid[r1][c1] = zombie_2;
        zombieGrid[r2][c2] = zombie_1;

        movingZombies.add(zombie_1);
        movingZombies.add(zombie_2);

        if (!processSwap(zombie_1, zombie_2)) {
            try {
                beginUsingData();
                r1 = zombie_2.getGridRow();
                r2 = zombie_1.getGridRow();
                c1 = zombie_2.getGridColumn();
                c2 = zombie_1.getGridColumn();

                x1 = zombie_2.getX();
                x2 = zombie_1.getX();
                y1 = zombie_2.getY();
                y2 = zombie_1.getY();

                zombie_1.setTarget(x2, y2);
                zombie_2.setTarget(x1, y1);

                zombie_1.startMovingToTarget(MAX_IN_GAME_VELOCITY);
                zombie_2.startMovingToTarget(MAX_IN_GAME_VELOCITY);

                zombie_1.setGridCell(r2, c2);
                zombie_2.setGridCell(r1, c1);

                zombieGrid[r1][c1] = zombie_1;
                zombieGrid[r2][c2] = zombie_2;

                movingZombies.add(zombie_1);
                movingZombies.add(zombie_2);

            } finally {
                endUsingData();
            }

        } else {
            updateGrid();
        }
    }

    /**
     * This method undoes the previous move, sending the two zombies on top of
     * the tile stack back to the game grid.
     */
    public void undoLastMove() {
        if (inProgress() && (allZombies.size() > 1)) {
            // TAKE THE TOP 2 TILES
            ZombieCrushSagaZombie topZombie = allZombies.remove(allZombies.size() - 1);
            ZombieCrushSagaZombie nextToTopZombie = allZombies.remove(allZombies.size() - 1);

            // SET THEIR DESTINATIONS
            float boundaryLeft = miniGame.getBoundaryLeft();
            float boundaryTop = miniGame.getBoundaryTop();

            // FIRST TILE 1
            int col = topZombie.getGridColumn();
            int row = topZombie.getGridRow();
            float targetX = this.calculateZombieXInGrid(col);
            float targetY = this.calculateZombieYInGrid(row);
            topZombie.setTarget(targetX, targetY);
            movingZombies.add(topZombie);
            topZombie.startMovingToTarget(MAX_TILE_VELOCITY);

            // AND THEN TILE 2
            col = nextToTopZombie.getGridColumn();
            row = nextToTopZombie.getGridRow();

            targetX = this.calculateZombieXInGrid(col);
            targetY = this.calculateZombieYInGrid(row);
            nextToTopZombie.setTarget(targetX, targetY);
            movingZombies.add(nextToTopZombie);
            nextToTopZombie.startMovingToTarget(MAX_TILE_VELOCITY);

            // PLAY THE AUDIO CUE
            miniGame.getAudio().play(ZombieCrushSagaPropertyType.UNDO_AUDIO_CUE.toString(), false);
        }
    }

    // OVERRIDDEN METHODS
    // - checkMousePressOnSprites
    // - endGameAsWin
    // - endGameAsLoss
    // - reset
    // - updateAll
    // - updateDebugText
    /**
     * This method provides a custom game response for handling mouse clicks on
     * the game screen. We'll use this to close game dialogs as well as to
     * listen for mouse clicks on grid cells.
     *
     * @param game The Mahjong game.
     *
     * @param x The x-axis pixel location of the mouse click.
     *
     * @param y The y-axis pixel location of the mouse click.
     */
    @Override
    public void checkMousePressOnSprites(MiniGame game, int x, int y) {
        // FIGURE OUT THE CELL IN THE GRID
        int row = calculateGridCellRow(x);
        int col = calculateGridCellColumn(y);

        // DISABLE THE STATS DIALOG IF IT IS OPEN
        /*
         if (game.getGUIDialogs().get(STATS_DIALOG_TYPE).getState().equals(VISIBLE_STATE)) {
         game.getGUIDialogs().get(STATS_DIALOG_TYPE).setState(INVISIBLE_STATE);
         return;
         }
         * */
        // CHECK THE TOP OF THE STACK AT col, row
        // GET AND TRY TO SELECT THE TOP TILE IN THAT CELL, IF THERE IS ONE
        ZombieCrushSagaZombie testZombie = zombieGrid[row][col];
        if (testZombie == null)
            ; else {
            selectZombie(testZombie);
        }
    }

    /**
     * This method locks access to the game data for the thread that invokes
     * this method. All other threads will be locked out upon their own call to
     * this method and will be forced to wait until this thread ends its use.
     */
    public void beginUsingData() {
        // System.out.println("LOCK #" +dataLock.getHoldCount() + ": " + dataLock); 
        dataLock.lock();
    }

    /**
     * This method frees access to the game data for the thread that invokes
     * this method. This will result in notifying any waiting thread that it may
     * proceed.
     */
    public void endUsingData() { //System.out.println("UNLOCK #" + dataLock.getHoldCount() + ": " + dataLock); 
        dataLock.unlock();
    }

    /**
     * Called when the game is won, it will record the ending game time, update
     * the player record, display the win dialog, and play the win animation.
     */
    @Override
    public void endGameAsWin() {
        // UPDATE THE GAME STATE USING THE INHERITED FUNCTIONALITY
        super.endGameAsWin();

        // RECORD IT AS A WIN
        ((ZombieCrushSagaMiniGame) miniGame).getPlayerRecord().addWin(currentLevel, levelScore);
        ((ZombieCrushSagaMiniGame) miniGame).savePlayerRecord();

        // DISPLAY THE WIN DIALOG
        miniGame.getGUIDialogs().get(WIN_DIALOG_TYPE).setState(VISIBLE_STATE);

        // AND PLAY THE WIN ANIMATION
        playWinAnimation();

        // AND PLAY THE WIN AUDIO
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
        miniGame.getAudio().play(ZombieCrushSagaPropertyType.WIN_AUDIO_CUE.toString(), false);
    }

    @Override
    public void endGameAsLoss() {
        // UPDATE THE GAME STATE USING THE INHERITED FUNCTIONALITY
        super.endGameAsLoss();

        // DISPLAY THE LOSS DIALOG
        miniGame.getGUIDialogs().get(LOSS_DIALOG_TYPE).setState(VISIBLE_STATE);

        // AND PLAY THE LOSS AUDIO
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.SPLASH_SCREEN_SONG_CUE.toString());
        miniGame.getAudio().stop(ZombieCrushSagaPropertyType.GAMEPLAY_SONG_CUE.toString());
        miniGame.getAudio().play(ZombieCrushSagaPropertyType.LOSS_AUDIO_CUE.toString(), false);
    }

    /**
     * Called each frame, this method updates all the game objects.
     *
     * @param game The Mahjong game to be updated.
     */
    @Override
    public void updateAll(MiniGame game) {
        // MAKE SURE THIS THREAD HAS EXCLUSIVE ACCESS TO THE DATA
        try {
            game.beginUsingData();

            // WE ONLY NEED TO UPDATE AND MOVE THE MOVING TILES
            for (int i = 0; i < movingZombies.size(); i++) {
                // GET THE NEXT TILE
                ZombieCrushSagaZombie tile = movingZombies.get(i);

                // THIS WILL UPDATE IT'S POSITION USING ITS VELOCITY
                tile.update(game);

                // IF IT'S REACHED ITS DESTINATION, REMOVE IT
                // FROM THE LIST OF MOVING TILES
                if (!tile.isMovingToTarget()) {
                    movingZombies.remove(tile);
                }
            }

        } finally {
            // MAKE SURE WE RELEASE THE LOCK WHETHER THERE IS
            // AN EXCEPTION THROWN OR NOT
            game.endUsingData();
        }
    }

    /**
     * This method is for updating any debug text to present to the screen. In a
     * graphical application like this it's sometimes useful to display data in
     * the GUI.
     *
     * @param game The Mahjong game about which to display info.
     */
    @Override
    public void updateDebugText(MiniGame game) {
    }

    @Override
    public void reset(MiniGame game) {
        enableZombies(true);

        // NOW LET'S REMOVE THEM FROM THE STACK
        // AND PUT THE TILES IN THE GRID       
        int k = 0;
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridColumns; j++) {
                // PUT IT IN THE GRID
                if (levelGrid[i][j] == 1) {
                    ZombieCrushSagaZombie tile = allZombies.get(k);
                    zombieGrid[i][j] = tile;
                    tile.setGridCell(i, j);

                    // WE'LL ANIMATE IT GOING TO THE GRID, SO FIGURE
                    // OUT WHERE IT'S GOING AND GET IT MOVING
                    float x = calculateZombieXInGrid(i);
                    float y = calculateZombieYInGrid(j);
                    tile.setTarget(x, y);
                    tile.startMovingToTarget(MAX_TILE_VELOCITY);
                    movingZombies.add(tile);
                    k++;
                }
            }
        }
        // AND START ALL UPDATES
        beginGame();
    }

    public int[][] getLevelGrid() {
        return levelGrid;

    }
}
