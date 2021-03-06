/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zombie_crush_saga.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.Timer;
import mini_game.MiniGame;
import mini_game.Sprite;
import mini_game.SpriteType;
import properties_manager.PropertiesManager;
import zombie_crush_saga.ZombieCrushSaga;
import zombie_crush_saga.data.ZombieCrushSagaDataModel;
import static zombie_crush_saga.ZombieCrushSagaConstants.*;
import zombie_crush_saga.data.ZombieCrushSagaRecord;
import zombie_crush_saga.file.ZombieCrushSagaFileManager;

/**
 * This class performs all of the rendering for the Zombie Crush Saga game.
 *
 * @author Vanna Wong
 */
public class ZombieCrushSagaPanel extends JPanel {
    // THIS IS ACTUALLY OUR Mahjong Solitaire APP, WE NEED THIS
    // BECAUSE IT HAS THE GUI STUFF THAT WE NEED TO RENDER

    private MiniGame game;
    // AND HERE IS ALL THE GAME DATA THAT WE NEED TO RENDER
    private ZombieCrushSagaDataModel data;
    // WE'LL USE THIS TO FORMAT SOME TEXT FOR DISPLAY PURPOSES
    private NumberFormat numberFormatter;
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING UNSELECTED TILES
    private BufferedImage blankZombieImage;
    // WE'LL USE THIS AS THE BASE IMAGE FOR RENDERING SELECTED TILES
    private BufferedImage blankZombieSelectedImage;

    /**
     * This constructor stores the game and data references, which we'll need
     * for rendering.
     *
     * @param initGame the Mahjong Solitaire game that is using this panel for
     * rendering.
     *
     * @param initData the Mahjong Solitaire game data.
     */
    public ZombieCrushSagaPanel(MiniGame initGame, ZombieCrushSagaDataModel initData) {
        game = initGame;
        data = initData;
        numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMinimumFractionDigits(3);
        numberFormatter.setMaximumFractionDigits(3);
    }

    public void setPowerUpsCursor(boolean yes, int x2, int y2) {
        if (yes) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String powerUps = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.POWER_UPS_IMAGE_NAME);
            String imgPath = props.getProperty(ZombieCrushSaga.ZombieCrushSagaPropertyType.IMG_PATH);

            Image image = toolkit.getImage(imgPath + powerUps);
            Cursor c = toolkit.createCustomCursor(image, new Point(x2, y2), "img");

            setCursor(c);

        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    // MUTATOR METHODS
    // -setBlankZombieImage
    // -setBlankZombieSelectedImage
    /**
     * This mutator method sets the base image to use for rendering tiles.
     *
     * @param initBlankZombieImage The image to use as the base for rendering
     * tiles.
     */
    public void setBlankZombieImage(BufferedImage initBlankZombieImage) {
        blankZombieImage = initBlankZombieImage;
    }

    /**
     * This mutator method sets the base image to use for rendering selected
     * tiles.
     *
     * @param initBlankZombieSelectedImage The image to use as the base for
     * rendering selected tiles.
     */
    public void setBlankZombieSelectedImage(BufferedImage initBlankZombieSelectedImage) {
        blankZombieSelectedImage = initBlankZombieSelectedImage;
    }

    public Graphics gr;

    /**
     * This is where rendering starts. This method is called each frame, and the
     * entire game application is rendered here with the help of a number of
     * helper methods.
     *
     * @param g The Graphics context for this panel.
     */
    @Override
    public void paintComponent(Graphics g) {
        gr = g;
        try {
            // MAKE SURE WE HAVE EXCLUSIVE ACCESS TO THE GAME DATA
            game.beginUsingData();

            // CLEAR THE PANEL
            super.paintComponent(g);

            // RENDER THE BACKGROUND, WHICHEVER SCREEN WE'RE ON
            renderBackground(g);

            // AND THE BUTTONS AND DECOR
            renderGUIControls(g);

            // AND THE TILES
            renderZombies(g);

            // AND THE DIALOGS, IF THERE ARE ANY
            renderDialogs(g);

            // AND THE TIME AND TILES STATS
            renderStats(g);

            // AND THE LEVEL SCORE
            renderLevelScore(g);

            // RENDERING THE GRID WHERE ALL THE TILES GO CAN BE HELPFUL
            // DURING DEBUGGIN TO BETTER UNDERSTAND HOW THEY RE LAID OUT
            renderGrid(g);

            // AND FINALLY, TEXT FOR DEBUGGING
            renderDebuggingText(g);
            
            ((ZombieCrushSagaDataModel)data).updateAll(game);

        } finally {
            // RELEASE THE LOCK
            game.endUsingData();
        }
    }

    // RENDERING HELPER METHODS
    // - renderBackground
    // - renderGUIControls
    // - renderZombies
    // - renderDialogs
    // - renderGrid
    // - renderDebuggingText
    /**
     * Renders the background image, which is different depending on the screen.
     *
     * @param g the Graphics context of this panel.
     */
    public void renderBackground(Graphics g) {
        // THERE IS ONLY ONE CURRENTLY SET
        Sprite bg = game.getGUIDecor().get(BACKGROUND_TYPE);
        renderSprite(g, bg);
    }

    /**
     * Renders all the GUI decor and buttons.
     *
     * @param g this panel's rendering context.
     */
    public void renderGUIControls(Graphics g) {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> decorSprites = game.getGUIDecor().values();
        for (Sprite s : decorSprites) {
            renderSprite(g, s);
        }

        // AND NOW RENDER THE BUTTONS
        Collection<Sprite> buttonSprites = game.getGUIButtons().values();
        for (Sprite s : buttonSprites) {
            renderSprite(g, s);
        }
    }

    /**
     * This method renders the on-screen stats that change as the game
     * progresses. This means things like the game time and the number of tiles
     * remaining.
     *
     * @param g the Graphics context for this panel
     */
    public void renderStats(Graphics g) {
        ZombieCrushSagaDataModel dat = (ZombieCrushSagaDataModel) ((ZombieCrushSagaMiniGame) game).getDataModel();

        // RENDER REMAINING MOVES
        if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(GAME_SCREEN_STATE)) {
            String moves = ((ZombieCrushSagaDataModel) data).getMoves() + "";
            int x2 = MOVES_CONTAINER_X + TEXT_OFFSET;
            g.setFont(TEXT_DISPLAY_FONT);
            g.drawString(moves, x2, 60);
        }

        // RENDER SCORE
        if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(GAME_SCREEN_STATE)) {
            String score = ((ZombieCrushSagaDataModel) data).getScore() + "";
            int x2 = SCORE_CONTAINER_X + TEXT_OFFSET;
            g.setFont(TEXT_DISPLAY_FONT);
            g.drawString(score, x2, 60);
        }

        // AND THEN THE PROGRESS BAR ON THE BOTTOM
        // FIRST THE ACTUAL BAR
        if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(GAME_SCREEN_STATE)) {
            int progX = (int) game.getGUIDecor().get(PROGRESS_TYPE).getX();
            int progY = (int) game.getGUIDecor().get(PROGRESS_TYPE).getY();
            float barHeight = PROGRESS_BAR_CORNERS.bottom - PROGRESS_BAR_CORNERS.top;
            float barPercentage = 0;
            if (dat.getScore() <= dat.oneStar) {
                barPercentage = ((float) dat.getScore() / (float) dat.oneStar);
            } else if (dat.getScore() <= dat.twoStar) {
                barPercentage = ((float) dat.getScore() / (float) dat.twoStar);
            } else if (dat.getScore() < dat.threeStar) {
                barPercentage = ((float) dat.getScore() / (float) dat.threeStar);
            } else if (dat.getScore() >= dat.threeStar) {
                barPercentage = 1;
            }
            float barWidth = barPercentage * (PROGRESS_BAR_CORNERS.right - PROGRESS_BAR_CORNERS.left);
            int barX = progX + PROGRESS_BAR_CORNERS.left;
            int barY = progY + PROGRESS_BAR_CORNERS.top;
            g.setColor(PROGRSS_BAR_COLOR);
            g.fillRect(barX, barY, (int) barWidth, (int) barHeight);

            // AND THEN THE TEXT ON THE PROGRESS BAR
            String progressText = "";
            if (dat.getScore() <= dat.oneStar) {
                progressText = dat.getScore() + "/" + dat.oneStar;
            } else if (dat.getScore() <= dat.twoStar) {
                progressText = dat.getScore() + "/" + dat.twoStar;
            } else if (dat.getScore() >= dat.threeStar) {
                progressText = dat.getScore() + "/" + dat.threeStar;
            }

            g.setFont(PROGRESS_METER_FONT);
            g.setColor(PROGRESS_METER_TEXT_COLOR);
            int x2 = 1200 - 315 + 30;
            int y2 = 215;
            g.drawString(progressText, x2, y2);
        }

        // RENDER LEVEL DESCRIPTION
        if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(LEVEL_SCORE_SCREEN_STATE)) {
            int p = 0;
            String levelDesc = ((ZombieCrushSagaDataModel) data).getLevelDesc();
            String levelDescLines[] = levelDesc.split("\\s+");
            String temp = "";
            ArrayList<String> tempList = new ArrayList<>();
            g.setFont(LEVEL_DESC_FONT);
            while (p < levelDescLines.length) {
                if ((temp.length() + levelDescLines[p].length()) > 35) {
                    tempList.add(temp);
                    temp = "";
                }
                temp += levelDescLines[p] + " ";
                p++;
            }
            tempList.add(temp);
            int j = 0;
            for (int i = 0; i < tempList.size(); i++) {
                g.drawString(tempList.get(i), 378, 90 + j * 25);
                j++;
            }
            j++;
            g.drawString("1 star: " + data.oneStar, 378, 90 + j * 25);
            g.drawString("2 stars: " + data.twoStar, 378 + 150, 90 + j * 25);
            g.drawString("3 stars: " + data.threeStar, 378 + 300, 90 + j * 25);
            String levelName = ((ZombieCrushSagaDataModel) data).getCurrentLevel();
            int highScore = ((ZombieCrushSagaMiniGame) game).getPlayerRecord().
                    getHighestScore(levelName);
            if (highScore < 0) {
                g.drawString("Level not yet completed", 371, 437);
            } else {
                g.drawString(highScore + "", 371, 437);
            }
        }

        int x = LEVELS_INIT_X + 15;
        int y = LEVELS_INIT_Y - 20;
        int j = 0;
        if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(SAGA_SCREEN_STATE)) {
            for (int i = ((ZombieCrushSagaMiniGame) game).getLower(); i <= ((ZombieCrushSagaMiniGame) game).getUpper(); i++) {
                if (x > LEVELS_BOUND_X || x < LEVELS_INIT_X) {
                    x -= Math.pow(-1, j) * LEVELS_INC_X;
                    y -= LEVELS_INC_Y;
                    j++;
                }
                g.setFont(STATS_FONT);
                g.drawString(i + "", x, y);
                x += Math.pow(-1, j) * LEVELS_INC_X;
            }
        }

        /*
         if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(LEVEL_SCORE_SCREEN_STATE)
         && (((ZombieCrushSagaMiniGame) game).getGUIDialogs().get(STATS_DIALOG_TYPE).getState().equals(VISIBLE_STATE))) {
         g.setFont(STATS_FONT);

         ZombieCrushSagaRecord record = new ZombieCrushSagaFileManager((ZombieCrushSagaMiniGame) game).loadRecord();
         int[] scores = record.getLevelScores(data.getCurrentLevel());
         String[] gamesPlayed = record.getPlayerNames(data.getCurrentLevel());
         String stats = "High Scores";

         g.drawString(stats, 55, 305);
         } */
    }

    /**
     * Renders all the game tiles, doing so carefully such that they are
     * rendered in the proper order.
     *
     * @param g the Graphics context of this panel.
     */
    public void renderZombies(Graphics g) {
        if (((ZombieCrushSagaMiniGame) game).getCurrentScreenState().equals(GAME_SCREEN_STATE)) {
            if (!data.won()) {
                ArrayList<ZombieCrushSagaZombie> stackZombies = data.getAllZombies();
                for (int i = 0; i < stackZombies.size(); i++) {
                    if (stackZombies.get(i).getGridRow() < 0 || stackZombies.get(i).getGridColumn() < 0) {
                        continue;
                    }
                    renderZombie(g, stackZombies.get(i));
                }

                // THEN DRAW ALL THE MOVING TILES
                Iterator<ZombieCrushSagaZombie> movingZombies = data.getMovingZombies();

                while (movingZombies.hasNext()) {
                    ZombieCrushSagaZombie tile = movingZombies.next();
                    renderZombie(g, tile);
                }
            }
        }
    }

    /**
     * Helper method for rendering the tiles that are currently moving.
     *
     * @param g Rendering context for this panel.
     *
     * @param zombieToRender Zombie to render to this panel.
     */
    public void renderZombie(Graphics g, ZombieCrushSagaZombie zombieToRender) {
        // ONLY RENDER VISIBLE TILES
        if (!zombieToRender.getState().equals(INVISIBLE_STATE)) {
            // FIRST DRAW THE BLANK TILE IMAGE
            switch (zombieToRender.getState()) {
                case SELECTED_STATE:
                    g.drawImage(blankZombieSelectedImage, (int) zombieToRender.getX(), (int) zombieToRender.getY(), null);
                    break;
                case VISIBLE_STATE:
                    g.drawImage(blankZombieImage, (int) zombieToRender.getX(), (int) zombieToRender.getY(), null);
                    break;
                case SPECIAL_WRAPPED_STATE:
                    g.drawImage(blankZombieImage, (int) zombieToRender.getX(), (int) zombieToRender.getY(), null);
                    break;
                case SPECIAL_STRIPED_HORIZONTAL_STATE:
                    g.drawImage(blankZombieImage, (int) zombieToRender.getX(), (int) zombieToRender.getY(), null);
                    break;
                case SPECIAL_STRIPED_VERTICAL_STATE:
                    g.drawImage(blankZombieImage, (int) zombieToRender.getX(), (int) zombieToRender.getY(), null);
                    break;
            }

            // THEN THE TILE IMAGE
            SpriteType bgST = zombieToRender.getSpriteType();
            Image img = bgST.getStateImage(zombieToRender.getState());
            g.drawImage(img, (int) zombieToRender.getX() + TILE_IMAGE_OFFSET, (int) zombieToRender.getY() + TILE_IMAGE_OFFSET, bgST.getWidth(), bgST.getHeight(), null);

            // IF THE TILE IS SELECTED, HIGHLIGHT IT
            if (zombieToRender.getState().equals(SELECTED_STATE)) {
                g.setColor(SELECTED_TILE_COLOR);
                g.drawRoundRect((int) zombieToRender.getX(), (int) zombieToRender.getY(), bgST.getWidth(), bgST.getHeight(), 5, 5);
            }
            if (zombieToRender.getState().equals(SPECIAL_WRAPPED_STATE)) {
                g.setColor(SPECIAL_WRAPPED_COLOR);
                g.drawRoundRect((int) zombieToRender.getX(), (int) zombieToRender.getY(), bgST.getWidth(), bgST.getHeight(), 5, 5);
            }
            if (zombieToRender.getState().equals(SPECIAL_STRIPED_HORIZONTAL_STATE)) {
                g.setColor(SPECIAL_STRIPED_HORIZONTAL_COLOR);
                g.drawRoundRect((int) zombieToRender.getX(), (int) zombieToRender.getY(), bgST.getWidth(), bgST.getHeight(), 5, 5);
            }
            if (zombieToRender.getState().equals(SPECIAL_STRIPED_VERTICAL_STATE)) {
                g.setColor(SPECIAL_STRIPED_VERTICAL_COLOR);
                g.drawRoundRect((int) zombieToRender.getX(), (int) zombieToRender.getY(), bgST.getWidth(), bgST.getHeight(), 5, 5);
            }
        }
    }

    /**
     * Renders the game dialog boxes.
     *
     * @param g This panel's graphics context.
     */
    public void renderDialogs(Graphics g) {
        // GET EACH DECOR IMAGE ONE AT A TIME
        Collection<Sprite> dialogSprites = game.getGUIDialogs().values();
        for (Sprite s : dialogSprites) {
            // RENDER THE DIALOG, NOTE IT WILL ONLY DO IT IF IT'S VISIBLE
            renderSprite(g, s);
        }
    }

    /**
     * Renders the s Sprite into the Graphics context g. Note that each Sprite
     * knows its own x,y coordinate location.
     *
     * @param g the Graphics context of this panel
     *
     * @param s the Sprite to be rendered
     */
    public void renderSprite(Graphics g, Sprite s) {
        // ONLY RENDER THE VISIBLE ONES
        if (!s.getState().equals(INVISIBLE_STATE)) {
            SpriteType bgST = s.getSpriteType();
            Image img = bgST.getStateImage(s.getState());
            g.drawImage(img, (int) s.getX(), (int) s.getY(), bgST.getWidth(), bgST.getHeight(), null);
        }
    }

    /**
     * This method renders grid lines in the game tile grid to help during
     * debugging.
     *
     * @param g Graphics context for this panel.
     */
    public void renderGrid(Graphics g) {
        /**
         * // ONLY RENDER THE GRID IF WE'RE DEBUGGING if
         * (data.isDebugTextRenderingActive()) { for (int i = 0; i <
         * ((ZombieCrushSagaDataModel) data).getGridRows(); i++) { for (int j =
         * 0; j < ((ZombieCrushSagaDataModel) data).getGridColumns(); j++) { int
         * x = ((ZombieCrushSagaDataModel) data).calculateZombieXInGrid(j); int
         * y = ((ZombieCrushSagaDataModel) data).calculateZombieYInGrid(i);
         * g.drawRect(x, y, TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT); } } } }
         *
         */

        if (((ZombieCrushSagaMiniGame) game).isCurrentScreenState(GAME_SCREEN_STATE)){
            int gridRows = data.getGridRows();
            int gridColumns = data.getGridColumns();
            int x1;
            int y1;
            g.setColor(JELLY_COLOR);
            for (int i = 0; i < gridRows; i++) {
                for (int j = 0; j < gridColumns; j++) {
                    x1 = data.calculateZombieXInGrid(j);
                    y1 = data.calculateZombieYInGrid(i);
                    if (data.bloodCopy[i][j] == 1) {
                        g.drawRect(x1, y1, TILE_IMAGE_WIDTH, TILE_IMAGE_HEIGHT);
                    }
                }
            }
        }
    }

    /**
     * Renders the debugging text to the panel. Note that the rendering will
     * only actually be done if data has activated debug text rendering.
     *
     * @param g the Graphics context for this panel
     */
    public void renderDebuggingText(Graphics g) {
        // IF IT'S ACTIVATED
        if (data.isDebugTextRenderingActive()) {
            // ENABLE PROPER RENDER SETTINGS
            g.setFont(DEBUG_TEXT_FONT);
            g.setColor(DEBUG_TEXT_COLOR);

            // GO THROUGH ALL THE DEBUG TEXT
            Iterator<String> it = data.getDebugText().iterator();
            int x = data.getDebugTextX();
            int y = data.getDebugTextY();
            while (it.hasNext()) {
                // RENDER THE TEXT
                String text = it.next();
                g.drawString(text, x, y);
                y += 20;
            }
        }
    }

    public void renderLevelScore(Graphics g) {
        ;
    }

    public void showPoints(int i, Graphics g, int x2, int y2) {
        /*
         TimeListener al = new TimeListener();
         Timer timer = new Timer(1000, al);
         timer.setRepeats(false);
         g.setFont(STATS_FONT);
         g.setColor(STATS_COLOR);
         g.drawString(i + " ", x2, y2);
         timer.start();
         while (timer.isRunning()) {
         g.drawString(i + " ", x2, y2);
         }
         }

         class TimeListener implements ActionListener {

         @Override
         public void actionPerformed(ActionEvent ae) {
         ;
         }

         }
         */
    }
}
