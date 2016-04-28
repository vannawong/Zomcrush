package zombie_crush_saga;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;

public class ZombieCrushSagaConstants {
    // WE ONLY HAVE A LIMITED NUMBER OF UI COMPONENT TYPES IN THIS GAME

    // SOME GAME CONSTANTS
    public static final int LEVELS = 100;
    public static final int LEVELS_PER_SCREEN = 15;
    public static final int LEVELS_INIT_X = 220;
    public static final int LEVELS_INIT_Y = 600;
    public static final int LEVELS_BOUND_X = 845;
    public static final int LEVELS_BOUND_Y = 92;
    public static final int LEVELS_INC_X = 140;
    public static final int LEVELS_INC_Y = 230;
    public static final int NUM_BACKGROUNDS = 7;
    // TILE SPRITE TYPES
    public static final String TYPE_A_TILE = "TYPE_A_TILE";
    public static final String TYPE_B_TILE = "TYPE_B_TILE";
    public static final String TYPE_C_TILE = "TYPE_C_TILE";
    public static final String TYPE_D_TILE = "TYPE_D_TILE";
    public static final String TYPE_E_TILE = "TYPE_E_TILE";
    public static final String TYPE_F_TILE = "TYPE_F_TILE";
    public static final String SPECIAL_STRIPED_HORIZONTAL_TYPE = "SPECIAL_STRIPED_HORIZONTAL_TYPE";
    public static final String SPECIAL_STRIPED_VERTICAL_TYPE = "SPECIAL_STRIPED_VERTICAL_TYPE";
    public static final String SPECIAL_WRAPPED_TYPE = "SPECIAL_WRAPPED_TYPE";
    public static final String TILE_BOMB_TYPE = "TILE_BOMB_TYPE";
    // I.E. NOT SPECIAL
    public static final String BASIC_TYPE = "BASIC_TYPE";
    public static final String TILE_SPRITE_TYPE_PREFIX = "TYPE_";
    // EACH SCREEN HAS ITS OWN BACKGROUND TYPE
    public static final String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    // THIS REPRESENTS THE BUTTONS ON THE SPLASH SCREEN FOR LEVEL SELECTION
    public static final String LEVEL_SELECT_BUTTON_TYPE = "LEVEL_SELECT_BUTTON_TYPE";
    // SPLASH SCREEN BUTTONS
    public static final String PLAY_BUTTON_TYPE = "PLAY_BUTTON_TYPE";
    public static final String PLAY_LEVEL_BUTTON_TYPE = "PLAY_LEVEL_BUTTON_TYPE";
    public static final String RESET_BUTTON_TYPE = "BACK_BUTTON_TYPE";
    public static final String QUIT_BUTTON_TYPE = "QUIT_BUTTON_TYPE";
    public static final String QUIT_LEVEL_BUTTON_TYPE = "QUIT_LEVEL_BUTTON_TYPE";
    public static final String SCROLL_UP_BUTTON_TYPE = "SCROLL_UP_BUTTON_TYPE";
    public static final String SCROLL_DOWN_BUTTON_TYPE = "SCROLL_DOWN_BUTTON_TYPE";
    public static final String RETRY_BUTTON_TYPE = "RETRY_BUTTON_TYPE";
    public static final String POWER_UPS_BUTTON_TYPE = "POWER_UPS_BUTTON_TYPE";
    // IN GAME UI CONTROL TYPES
    public static final String BLOOD_COUNT_TYPE = "BLOOD_COUNT_TYPE";
    public static final String SCORE_COUNT_TYPE = "SCORE_COUNT_TYPE";
    public static final String MOVES_COUNT_TYPE = "MOVES_COUNT_TYPE";
    public static final String SCORE_CONTAINER_TYPE = "SCORE_CONTAINER_TYPE";
    public static final String MOVES_CONTAINER_TYPE = "MOVES_CONTAINER_TYPE";
    public static final String PROGRESS_TYPE = "PROGRESS_TYPE";

    // DIALOG TYPES
    public static final String ONE_STAR_TYPE = "ONE_STAR_TYPE";
    public static final String TWO_STAR_TYPE = "TWO_STAR_TYPE";
    public static final String THREE_STAR_TYPE = "THREE_STAR_TYPE";
    public static final String STATS_DIALOG_TYPE = "STATS_DIALOG_TYPE";
    public static final String WIN_DIALOG_TYPE = "WIN_DIALOG_TYPE";
    public static final String LOSS_DIALOG_TYPE = "LOSS_DIALOG_TYPE";
    // WE'LL USE THESE STATES TO CONTROL SWITCHING BETWEEN THE TWO
    public static final String SPLASH_SCREEN_STATE = "SPLASH_SCREEN_STATE";
    public static final String SAGA_SCREEN_STATE = "SAGA_SCREEN_STATE";
    public static final String ABOUT_SCREEN_STATE = "ABOUT_SCREEN_STATE";
    public static final String LEVEL_SCORE_SCREEN_STATE = "LEVEL_SCORE_SCREEN_STATE";
    public static final String GAME_SCREEN_STATE = "GAME_SCREEN_STATE";
    // THE TILES MAY HAVE 4 STATES:
    // - INVISIBLE_STATE: USED WHEN ON ANY SCREEN OTHER THAN GAME SCREEN, MEANS 
    // A TILE IS NOT DRAWN AND CANNOT BE CLICKED
    // - VISIBLE_STATE: USED WHEN ON THE GAME SCREEN, MEANS A TILE
    // IS VISIBLE AND CAN BE CLICKED (TO SELECT IT), BUT IS NOT CURRENTLY SELECTED
    // - SELECTED_STATE: USED WHEN ON THE GAME SCREEN, MEANS A TILE
    // IS VISIBLE AND CAN BE CLICKED (TO UNSELECT IT), AND IS CURRENTLY SELECTED     
    // - SPECIAL_STATE: USED FOR A TILE THE USER HAS CLICKED ON THAT
    // IS SPECIAL. ONLY FOR DISPLAY PURPOSES.
    public static final String INVISIBLE_STATE = "INVISIBLE_STATE";
    public static final String VISIBLE_STATE = "VISIBLE_STATE";
    public static final String SELECTED_STATE = "SELECTED_STATE";
    public static final String SPECIAL_STRIPED_HORIZONTAL_STATE = "SPECIAL_STRIPED_HORIZONTAL_STATE";
    public static final String SPECIAL_STRIPED_VERTICAL_STATE = "SPECIAL_STRIPED_VERTICAL_STATE";
    public static final String SPECIAL_WRAPPED_STATE = "SPECIAL_WRAPPED_STATE";
    public static final String MOUSE_OVER_STATE = "MOUSE_OVER_STATE";
    // THE BUTTONS MAY HAVE 2 STATES:
    // - INVISIBLE_STATE: MEANS A BUTTON IS NOT DRAWN AND CAN'T BE CLICKED
    // - VISIBLE_STATE: MEANS A BUTTON IS DRAWN AND CAN BE CLICKED
    // - MOUSE_OVER_STATE: MEANS A BUTTON IS DRAWN WITH SOME HIGHLIGHTING
    // BECAUSE THE MOUSE IS HOVERING OVER THE BUTTON
    // UI CONTROL SIZE AND POSITION SETTINGS
    // OR POSITIONING THE LEVEL SELECT BUTTONS
    public static final int PLAY_BUTTON_X = 630;
    public static final int PLAY_BUTTON_Y = 200;
    public static final int RESET_BUTTON_X = 830;
    public static final int RESET_BUTTON_Y = 200;
    public static final int QUIT_BUTTON_X = 1000;
    public static final int QUIT_BUTTON_Y = 600;
    public static final int SCROLL_UP_BUTTON_X = 1040;
    public static final int SCROLL_UP_BUTTON_Y = 200;
    public static final int SCROLL_DOWN_BUTTON_X = 1040;
    public static final int SCROLL_DOWN_BUTTON_Y = 400;
    public static final int PLAY_LEVEL_BUTTON_X = 750;
    public static final int PLAY_LEVEL_BUTTON_Y = 600;
    public static final int QUIT_LEVEL_BUTTON_X = 1000;
    public static final int QUIT_LEVEL_BUTTON_Y = 600;
    // FOR STACKING TILES ON THE GRID
    public static final int TILE_IMAGE_OFFSET = 1;
    public static final int TILE_IMAGE_WIDTH = 60;
    public static final int TILE_IMAGE_HEIGHT = 55;
    public static final int Z_TILE_OFFSET = 5;
    // FOR MOVING TILES AROUND
    public static final int MAX_TILE_VELOCITY = 70;
    public static final int MAX_IN_GAME_VELOCITY = 5;
    // UI CONTROLS POSITIONS IN THE GAME SCREEN
    public static final int CONTROLS_MARGIN = 0;
    public static final int MOVES_COUNT_X = 0;
    public static final int SCORE_COUNT_X = 380;
    public static final int MOVES_CONTAINER_X = 190;
    public static final int SCORE_CONTAINER_X = 570;
    public static final int TEXT_OFFSET = 20;
    public static final int POWER_UPS_X = 1200 - 127;
    public static final int HIGH_SCORE_X = 371;
    public static final int STAR_X = 384;
    public static final int STAR_Y = 555;
    public static final int STAR_OFFSET = 90;
    // USED FOR DOING OUR VICTORY ANIMATION
    public static final int WIN_PATH_NODES = 8;
    public static final int WIN_PATH_TOLERANCE = 100;
    public static final int WIN_PATH_COORD = 200;
    // COLORS USED FOR RENDERING VARIOUS THINGS, INCLUDING THE
    // COLOR KEY, WHICH REFERS TO THE COLOR TO IGNORE WHEN
    // LOADING ART.
    public static final Color COLOR_KEY = new Color(255, 174, 201);
    public static final Color DEBUG_TEXT_COLOR = Color.BLACK;
    public static final Color TEXT_DISPLAY_COLOR = new Color(10, 160, 10);
    public static final Color SELECTED_TILE_COLOR = new Color(255, 255, 0, 100);

    public static final Color SPECIAL_STRIPED_HORIZONTAL_COLOR = new Color(200, 0, 100);
    public static final Color SPECIAL_STRIPED_VERTICAL_COLOR = new Color(100, 0, 100);
    public static final Color JELLY_COLOR = new Color(58, 100, 210);
    // WRAPPED CANDIES ARE RED
    public static final Color SPECIAL_WRAPPED_COLOR = new Color(50, 100, 100);
    public static final Color STATS_COLOR = new Color(0, 60, 0);
    // FONTS USED DURING FOR TEXTUAL GAME DISPLAYS
    public static final Font TEXT_DISPLAY_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 48);
    public static final Font DEBUG_TEXT_FONT = new Font(Font.MONOSPACED, Font.BOLD, 14);
    public static final Font STATS_FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);
    /*try{
     public static final Font BAVEUSE = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Windows\\Fonts\\Baveuse-Regular.ttf"));
     }
     catch(Exception e){
     System.out.println("Bad file.");
     }*/
    //public static final Font LEVEL_DESC_FONT = BAVEUSE.deriveFont(Font.BOLD, 12);
    public static final Font LEVEL_DESC_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    // PROGRESS BAR STUFF
    public static final Insets PROGRESS_BAR_CORNERS = new Insets(12, 13, 29, 302);
    public static final Color PROGRSS_BAR_COLOR = new Color(0, 100, 0);
    public static final Font PROGRESS_METER_FONT = new Font("Serif", Font.BOLD, 16);
    public static final Color PROGRESS_METER_TEXT_COLOR = new Color(201, 168, 88);
    
    // AND AUDIO STUFF
    public static final String SUCCESS_AUDIO_TYPE = "SUCCESS_AUDIO_TYPE";
    public static final String FAILURE_AUDIO_TYPE = "FAILURE_AUDIO_TYPE";
    public static final String THEME_SONG_TYPE = "THEME_SONG_TYPE";
}
