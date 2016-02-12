package zombiecrushsagaleveleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.text.Format;
import java.text.NumberFormat;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.NumberFormatter;

/**
 * This program serves as a level editor for the Zomjong game. It is capable of
 * making and saving new .zom levels, as well as opening, editing, and saving
 * existing ones. Note that a .zom level does not arrange tiles, it just
 * specifies where tiles may be arranged. Tile arrangement should be done
 * semi-randomly.
 *
 * Note that we have designed this level editor such that the entire program is
 * defined inside this one class using inner classes for all event handlers and
 * the renderer.
 *
 * @author Richard McKenna
 */
public class ZombieCrushSagaLevelEditor {
    // INITIALIZATION CONSTANTS

    // THESE CONSTANTS ARE FOR CUSTOMIZATION OF THE GRID
    private final int INIT_GRID_DIM = 10;
    private final int MIN_GRID_DIM = 1;
    private final int MAX_GRID_ROWS = 10;
    private final int MAX_GRID_COLUMNS = 20;
    private final int GRID_STEP = 1;
    // TEXTUAL CONSTANTS
    private final String WINDOW_ICON_FILE_NAME = "./";
    private final String APP_TITLE = "Zomjong Level Editor";
    private final String OPEN_BUTTON_TEXT = "Open";
    private final String SAVE_AS_BUTTON_TEXT = "Save As";
    private final String COLUMNS_LABEL_TEXT = "Columns: ";
    private final String ROWS_LABEL_TEXT = "Rows: ";
    private final String TILES_REMAINING_LABEL_TEXT = "Tiles Used: ";
    private final String ZOMJONG_DATA_DIR = "./";
    private final String ZOM_FILE_EXTENSION = ".zom";
    private final String OPEN_FILE_ERROR_FEEDBACK_TEXT = "File not loaded: .mazl files only";
    private final String SAVE_AS_ERROR_FEEDBACK_TEXT = "File not saved: must use .zom file extension";
    private final String FILE_LOADING_SUCCESS_TEXT = " loaded successfully";
    private final String FILE_READING_ERROR_TEXT = "Error reading from ";
    private final String FILE_WRITING_ERROR_TEXT = "Error writing to ";
    // CONSTANTS FOR FORMATTING THE GRID
    private final Font GRID_FONT = new Font("monospaced", Font.BOLD, 36);
    private final Color GRID_BACKGROUND_COLOR = new Color(200, 200, 120);
    // INSTANCE VARIABLES
    // HERE ARE THE UI COMPONENTS
    private JFrame window;
    private JPanel westPanel;
    private JButton openButton;
    private JButton saveAsButton;
    private JLabel columnsLabel;
    private JSpinner columnsSpinner;
    private JLabel rowsLabel;
    private JLabel bloodLabel;
    private JSpinner rowsSpinner;
    private JLabel tilesRemainingLabel;
    private JFormattedTextField levelReqField1 = new JFormattedTextField(new Integer(10));
    private JFormattedTextField levelReqField2 = new JFormattedTextField(new Integer(10));
    private JFormattedTextField levelReqField3 = new JFormattedTextField(new Integer(10));
    private JFormattedTextField levelMovesField = new JFormattedTextField(new Integer(5));
    private NumberFormat amountFormat = NumberFormat.getIntegerInstance();
    private JLabel levelReqLabel;
    private JLabel levelMoves;
    private JTextArea levelDescText;
    private JScrollPane levelDescPane;
    private String levelDesc;
    // WE'LL RENDER THE GRID IN THIS COMPONENT
    private GridRenderer gridRenderer;
    // AND HERE IS THE GRID WE'RE MAKING
    private int gridColumns;
    private int gridRows;
    private int has[][];
    private int blood[][];
    private int jelly = 0;
    private int moves;
    // THIS KEEPS TRACK OF THE NUMBER OF TILES
    // WE STILL HAVE TO PLACE
    private int totalTiles = 0;
    // THE NUMBER OF POINTS REQUIRED TO CLEAR THE LEVEL.  0 MEANS THERE IS NO 
    // POINT REQUIREMENT
    private int levelReq1 = 0;
    private int levelReq2 = 0;
    private int levelReq3 = 0;
    // THIS WILL LET THE USER SELECT THE FILES TO READ AND WRITE
    private JFileChooser fileChooser;
    // THIS WILL HELP US LIMIT THE USER FILE SELECTION CHOICES
    private FileFilter zomFileFilter;

    /**
     * This method initializes the level editor application, setting up all data
     * an UI components for use.
     */
    private void init() {
        // INIT THE EDITOR APP'S CONTAINER
        initWindow();

        // INITIALIZES THE GRID DATA
        initData();

        // LAYOUT THE INITIAL CONTROLS
        initGUIControls();

        // HOOK UP THE EVENT HANDLERS
        initHandlers();

        // INITIALIZE
        initFileControls();
    }

    /**
     * Initializes the window.
     */
    private void initWindow() {
        // MAKE THE WINDOW AND SET THE WINDOW TITLE
        window = new JFrame(APP_TITLE);

        // THEN LOAD THE IMAGE
        try {
            File imageFile = new File(WINDOW_ICON_FILE_NAME);
            Image windowImage = ImageIO.read(imageFile);
            MediaTracker mt = new MediaTracker(window);
            mt.addImage(windowImage, 0);
            mt.waitForAll();
            window.setIconImage(windowImage);
        } catch (Exception e) {
            // WE CAN LIVE WITHOUT THE ICON IMAGE IN CASE AN ERROR HAPPENS,
            // SO WE'LL JUST SQUELCH THIS EXCEPTION
        }

        // MAKE IT FULL SCREEN
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // JUST CLOSE WHEN SOMEONE HITS X
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Initializes the app data.
     */
    private void initData() {
        // START OUT OUR GRID WITH DEFAULT DIMENSIONS
        gridColumns = INIT_GRID_DIM;
        gridRows = INIT_GRID_DIM;

        // NOW MAKE THE INITIALLY EMPTY GRID
        has = new int[gridRows][gridColumns];
        blood = new int[gridRows][gridColumns];
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridColumns; j++) {
                has[i][j] = 0;
                blood[i][j] = 0;
            }
        }
    }

    /**
     * Constructs and lays out all UI controls.
     */
    private void initGUIControls() {
        amountFormat.setGroupingUsed(false);
        // ALL THE GRID DIMENSIONS CONTROLS GO IN THE WEST
        westPanel = new JPanel();
        westPanel.setLayout(new GridBagLayout());

        // WE HAVE 2 SPINNERS FOR UPDATING THE GRID DIMENSIONS, THESE
        // MODELS SPECIFY HOW THEY GET INITIALIZED AND THEIR VALUE BOUNDARIES
        SpinnerModel columnsSpinnerModel = new SpinnerNumberModel(INIT_GRID_DIM,
                MIN_GRID_DIM,
                MAX_GRID_COLUMNS,
                GRID_STEP);
        SpinnerModel rowsSpinnerModel = new SpinnerNumberModel(INIT_GRID_DIM,
                MIN_GRID_DIM,
                MAX_GRID_ROWS,
                GRID_STEP);

        // CONSTRUCT ALL THE WEST TOOLBAR COMPONENTS
        openButton = new JButton(OPEN_BUTTON_TEXT);
        saveAsButton = new JButton(SAVE_AS_BUTTON_TEXT);
        saveAsButton.setEnabled(false);
        columnsLabel = new JLabel(COLUMNS_LABEL_TEXT);
        columnsSpinner = new JSpinner(columnsSpinnerModel);
        rowsLabel = new JLabel(ROWS_LABEL_TEXT);
        rowsSpinner = new JSpinner(rowsSpinnerModel);
        bloodLabel = new JLabel();
        tilesRemainingLabel = new JLabel();
        levelReqField1 = new JFormattedTextField(amountFormat);
        levelReqField1.setColumns(10);
        levelReqField2 = new JFormattedTextField(amountFormat);
        levelReqField2.setColumns(10);
        levelReqField3 = new JFormattedTextField(amountFormat);
        levelReqField3.setColumns(10);
        levelReqLabel = new JLabel("Level Points Requirement for 1 star, 2 stars and 3 stars");
        levelDescText = new JTextArea("Level Description", 5, 20);
        levelMoves = new JLabel("Level Moves");
        levelMovesField = new JFormattedTextField(amountFormat);
        levelMovesField.setColumns(5);
        levelDescText.setLineWrap(true);
        levelDescPane = new JScrollPane(levelDescText);
        levelDescPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        levelDescPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        levelDescPane.setWheelScrollingEnabled(true);

        // MAKE SURE THIS LABEL HAS THE CORRECT TEXT
        updateTilesRemainingLabel();

        // NOW PUT ALL THE CONTROLS IN THE WEST TOOLBAR
        addToWestPanel(openButton, 0, 0, 1, 1);
        addToWestPanel(saveAsButton, 1, 0, 1, 1);
        addToWestPanel(columnsLabel, 0, 1, 1, 1);
        addToWestPanel(columnsSpinner, 1, 1, 1, 1);
        addToWestPanel(rowsLabel, 0, 2, 1, 1);
        addToWestPanel(rowsSpinner, 1, 2, 1, 1);
        addToWestPanel(tilesRemainingLabel, 0, 3, 2, 1);
        addToWestPanel(levelDescPane, 0, 4, 2, 1);
        addToWestPanel(levelReqLabel, 0, 5, 1, 1);
        addToWestPanel(levelReqField1, 0, 6, 1, 1);
        addToWestPanel(levelReqField2, 0, 7, 1, 1);
        addToWestPanel(levelReqField3, 0, 8, 1, 1);
        addToWestPanel(levelMoves, 0, 9, 1, 1);
        addToWestPanel(levelMovesField, 0, 10, 1, 1);
        addToWestPanel(bloodLabel, 0, 11, 1, 1);

        // THIS GUY RENDERS OUR GRID
        gridRenderer = new GridRenderer();

        // PUT EVERYTHING IN THE FRAME
        window.add(westPanel, BorderLayout.WEST);
        window.add(gridRenderer, BorderLayout.CENTER);
    }

    /**
     * This method updates the display of the label in the west that displays
     * how many tiles are left to be located. This needs to be called after
     * every time the grid dimensions change or when a new .zom file is loaded.
     */
    private void updateTilesRemainingLabel() {
        tilesRemainingLabel.setText(TILES_REMAINING_LABEL_TEXT + totalTiles);
        bloodLabel.setText("Jelly in level: " + jelly);
    }

    /**
     * This helper method assists in using Java's GridBagLayout to arrange
     * components inside the west panel.
     */
    private void addToWestPanel(JComponent comp, int initGridX, int initGridY, int initGridWidth, int initGridHeight) {
        // GridBagLayout IS A JAVA LayoutManager THAT CAN BE USED TO
        // ARRANGE COMPONENTS IN A MULTI-DIMENSIONAL GRID WITH COMPONENTS
        // SPANNING MULTIPLE CELLS. FIRST WE INIT THE SETTINGS
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = initGridX;
        gbc.gridy = initGridY;
        gbc.gridwidth = initGridWidth;
        gbc.gridheight = initGridHeight;
        gbc.insets = new Insets(10, 10, 5, 5);

        // THEN USING THOSE SETTINGS WE PUT THE COMPONENT IN THE PANEL
        westPanel.add(comp, gbc);
    }

    /**
     * This method initializes all the event handlers needed by this
     * application.
     */
    private void initHandlers() {
        // WE'LL RESPOND TO WHEN THE USER CHANGES THE
        // GRID DIMENSIONS USING THE SPINNERS
        GridSizeHandler gsh = new GridSizeHandler();
        columnsSpinner.addChangeListener(gsh);
        rowsSpinner.addChangeListener(gsh);

        // WE'LL UPDATE THE CELL-TILE COUNTS WHEN THE
        // USER CLICKS THE MOUSE ON THE RENDERING PANEL
        GridClickHandler gch = new GridClickHandler();
        gridRenderer.addMouseListener(gch);

        // WE'LL LET THE USER SELECT AND THEN WE'LL OPEN
        // A .ZOM FILE WHEN THE USER CLICKS ON THE OPEN BUTTON
        OpenLevelHandler olh = new OpenLevelHandler();
        openButton.addActionListener(olh);

        // AND WE'LL SAVE THE CURRENT LEVEL WHEN THE USER
        // PRESSES THE SAVE AS BUTTON
        SaveAsLevelHandler salh = new SaveAsLevelHandler();
        saveAsButton.addActionListener(salh);

    }

    /**
     * This method initializes the file chooser and the file filter for that
     * control so that the user may select files for saving and loading.
     */
    public void initFileControls() {
        // INIT THE FILE CHOOSER CONTROL
        fileChooser = new JFileChooser(ZOMJONG_DATA_DIR);

        // AND THE FILE FILTER, WE'LL DEFINE A SIMPLE
        // ANONYMOUS TYPE FOR THIS
        zomFileFilter = new FileFilter() {
            /**
             * This method limits the types the file chooser can see to .zom
             * files.
             */
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(ZOM_FILE_EXTENSION);
            }

            /**
             * Describes the types of files we'll accept.
             */
            @Override
            public String getDescription() {
                return ZOM_FILE_EXTENSION;
            }
        };

        // AND MAKE SURE THE FILE CHOOSER USES THE FILTER
        fileChooser.setFileFilter(zomFileFilter);
    }

    class LevelPointsHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            double value;
            String str = ((JTextField) e.getSource()).getText();
            try {
                value = Double.parseDouble(str);
            } catch (Exception p) {
                JOptionPane error = new JOptionPane("Error: Input is not a number.");
                ((JTextField) e.getSource()).setText(str.substring(0, str.length() - 1));
            }
        }
    }

    /**
     * This event handler responds to when the user mouse clicks on the
     * rendering panel. The result is we update the tile assignments on the cell
     * that was clicked.
     */
    class GridClickHandler extends MouseAdapter {

        /**
         * This is the method where we respond to mouse clicks. Note that the me
         * argument knows the x,y coordinates of the mouse click on the panel
         * and we can translate that into a click on a cell.
         */
        @Override
        public void mousePressed(MouseEvent me) {
            // FIGURE OUT THE CORRESPONDING COLUMN & ROW
            int w = gridRenderer.getWidth() / gridRows;
            int col = me.getX() / w;
            int h = gridRenderer.getHeight() / gridColumns;
            int row = me.getY() / h;

            // IF IT'S A LEFT MOUSE CLICK WE INC
            if (me.getButton() == MouseEvent.BUTTON1) {
                // ONLY IF WE HAVE MORE TILES TO ASSIGN
                if (has[row][col] == 1) {
                    has[row][col] = 0;
                    totalTiles--;
                } else {
                    totalTiles++;
                    has[row][col] = 1;
                }
            }
            // RIGHT CLICK CHANGES BLOOD (JELLY) SETTINGS
            if (me.getButton() == MouseEvent.BUTTON3) {
                if (blood[row][col] == 1) {
                    blood[row][col] = 0;
                    jelly--;
                } else {
                    jelly++;
                    blood[row][col] = 1;
                }
            }
            saveAsButton.setEnabled(true);

            // AND REDRAW THE GRID
            gridRenderer.repaint();
        }
    }

    /**
     * This class serves as the event handler for the two spinners, which allow
     * the user to change the grid dimensions.
     */
    class GridSizeHandler implements ChangeListener {

        /**
         * Called when the user changes the value in one of the two spinners,
         * this method rebuilds the grid using the most recent dimension
         * settings, it also tries to copy data from the previous sized grid
         * into the new one. Note that as a grid is made larger we keep all the
         * data, but as a grid is made smaller, we will lose some data.
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            // GET THE NEW GRID DIMENSIONS
            int newGridColumns = Integer.parseInt(columnsSpinner.getValue().toString());
            int newGridRows = Integer.parseInt(rowsSpinner.getValue().toString());

            //  MAKE A NEW GRID
            int[][] newGrid = new int[newGridRows][newGridColumns];

            // COPY THE OLD DATA TO THE NEW GRID
            for (int i = 0; i < gridRows; i++) {
                for (int j = 0; j < gridColumns; j++) {
                    if ((i < newGridRows) && (j < newGridColumns)) {
                        newGrid[i][j] = has[i][j];
                    }
                }
            }

            // NOW UPDATE THE GRID
            gridColumns = newGridColumns;
            gridRows = newGridRows;
            has = newGrid;

            // AND RE-RENDER THE GRID
            gridRenderer.repaint();
        }
    }

    /**
     * This class serves as the event handler for the open level button.
     */
    class OpenLevelHandler implements ActionListener {

        /**
         * This method responds to a click on the open level button. It prompts
         * the user for a file to open and then proceeds to load it.
         */
        @Override
        public void actionPerformed(ActionEvent ae) {
            // FIRST PROMPT THE USER FOR A FILE NAME
            int buttonSelection = fileChooser.showOpenDialog(openButton);

            // MAKE SURE THE USER WANTS TO CONTINUE AND DIDN'T
            // PRESS THE CANCEL OPTION
            if (buttonSelection == JFileChooser.APPROVE_OPTION) {
                // GET THE FILE THE USER SELECTED
                File fileToOpen = fileChooser.getSelectedFile();
                String fileName = fileToOpen.getPath();

                // MAKE SURE IT'S A .ZOM FILE
                if (!fileName.endsWith(ZOM_FILE_EXTENSION)) {
                    JOptionPane.showMessageDialog(saveAsButton, OPEN_FILE_ERROR_FEEDBACK_TEXT);
                    return;
                }

                // NOW LOAD THE RAW DATA SO WE CAN USE IT
                // OUR LEVEL FILES WILL HAVE THE DIMENSIONS FIRST,
                // FOLLOWED BY THE GRID VALUES
                try {
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
                    // FIRST READ THE TOTAL TILES
                    totalTiles = dis.readInt();

                    // FIRST READ THE GRID DIMENSIONS
                    int initGridRows = dis.readInt();
                    int initGridColumns = dis.readInt();
                    int[][] newGrid = new int[initGridRows][initGridColumns];
                    int[][] newJellyGrid = new int[initGridRows][initGridColumns];

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
                    columnsSpinner.setValue(initGridColumns);
                    rowsSpinner.setValue(initGridRows);
                    has = newGrid;
                    blood = newJellyGrid;
                    gridColumns = initGridColumns;
                    gridRows = initGridRows;
                    saveAsButton.setEnabled(true);
                    levelReqField1.setText(levelReq1 + "");
                    levelReqField2.setText(levelReq2 + "");
                    levelReqField3.setText(levelReq3 + "");

                    levelMovesField.setText(moves + "");
                    levelDescText.setText(levelDesc);

                    // UPDATE THE DISPLAY
                    updateTilesRemainingLabel();
                    gridRenderer.repaint();

                    // FINALLY TELL THE USER THE LEVEL SUCCESSFULLY LOADED
                    JOptionPane.showMessageDialog(window, fileToOpen.getName() + FILE_LOADING_SUCCESS_TEXT);
                } catch (IOException ioe) {
                    // AN ERROR HAPPENED, LET THE USER KNOW.
                    JOptionPane.showMessageDialog(saveAsButton, FILE_READING_ERROR_TEXT + fileName, FILE_READING_ERROR_TEXT + fileName, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * This class serves as the event handler for the Save As button.
     */
    class SaveAsLevelHandler implements ActionListener {

        /**
         * This method responds to when the user clicks the save as button. It
         * prompts the user for a file name and then saves the level to that
         * file.
         */
        @Override
        public void actionPerformed(ActionEvent ae) {

            // FIRST PROMPT THE USER FOR A FILE NAME
            int buttonSelection = fileChooser.showSaveDialog(saveAsButton);

            // MAKE SURE THE USER WANTS TO CONTINUE AND DIDN'T SELECT
            // THE CANCEL BUTTON
            if (buttonSelection == JFileChooser.APPROVE_OPTION) {
                // GET THE FILE THE USER SELECTED
                File fileToSave = fileChooser.getSelectedFile();
                String fileName = fileToSave.getPath();

                // MAKE SURE IT'S THE CORRECT FILE TYPE
                if (!fileName.endsWith(ZOM_FILE_EXTENSION)) {
                    JOptionPane.showMessageDialog(saveAsButton, SAVE_AS_ERROR_FEEDBACK_TEXT);
                    return;
                }

                // OUR LEVEL FILES WILL HAVE THE TOTAL FILES FIRST,
                // THEN DIMENSIONS, FOLLOWED BY THE GRID VALUES
                try {
                    // WE'LL WRITE EVERYTHING IN BINARY. NOTE THAT WE
                    // NEED TO MAKE SURE WE SAVE THE DATA IN THE SAME
                    // FORMAT AND ORDER WITH WHICH WE READ IT LATER
                    FileOutputStream fos = new FileOutputStream(fileName);
                    DataOutputStream dos = new DataOutputStream(fos);

                    // FIRST WRITE THE TILES USED
                    dos.writeInt(totalTiles);

                    // THEN WRITE THE DIMENSIONS
                    dos.writeInt(gridRows);
                    dos.writeInt(gridColumns);

                    // AND NOW ALL THE CELL VALUES
                    for (int i = 0; i < gridRows; i++) {
                        for (int j = 0; j < gridColumns; j++) {
                            dos.writeInt(has[i][j]);
                        }
                    }
                    // AND NOW ALL THE JELLY VALUES
                    for (int i = 0; i < gridRows; i++) {
                        for (int j = 0; j < gridColumns; j++) {
                            dos.writeInt(blood[i][j]);
                        }
                    }
                    // THE AMOUNT OF JELLY IN THE LEVEL
                    dos.writeInt(jelly);

                    // THE POINTS REQUIRED TO WIN THE LEVEL
                    String temp = levelReqField1.getText();
                    levelReq1 = Integer.parseInt(temp);
                    dos.writeInt(levelReq1);
                    
                    temp = levelReqField2.getText();
                    levelReq2 = Integer.parseInt(temp);
                    
                    dos.writeInt(levelReq2);
                    temp = levelReqField3.getText();
                    
                    levelReq3 = Integer.parseInt(temp);
                    dos.writeInt(levelReq3);

                    // THE NUMBER OF MOVES AVAILABLE IN THE LEVEL
                    temp = levelMovesField.getText();
                    moves = Integer.parseInt(temp);
                    dos.writeInt(moves);

                    // THE LEVEL DESCRIPTION
                    levelDesc = levelDescText.getText();
                    dos.writeBytes(levelDesc);
                } catch (IOException ioe) {
                    // AN ERROR HAS HAPPENED, LET THE USER KNOW
                    JOptionPane.showMessageDialog(saveAsButton, FILE_WRITING_ERROR_TEXT + fileName, FILE_WRITING_ERROR_TEXT + fileName, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * This class renders the grid for us. Note that we also listen for mouse
     * clicks on it.
     */
    class GridRenderer extends JPanel {
        // PIXEL DIMENSIONS OF EACH CELL

        int cellWidth;
        int cellHeight;
        // WE'LL USE THIS TO MEASURE THE TEXT WE RENDER
        // SO THAT WE CAN PROPERLY CENTER IT
        FontMetrics fm;

        /**
         * Default constructor, it will initialize the background color for the
         * grid.
         */
        public GridRenderer() {
            setBackground(GRID_BACKGROUND_COLOR);
        }

        /**
         * This function is called each time the panel is rendered. It will draw
         * the grid, including all the cells and the numeric values in each
         * cell.
         */
        @Override
        public void paintComponent(Graphics g) {
            // CLEAR THE PANEL
            super.paintComponent(g);

            updateTilesRemainingLabel();

            // MAKE SURE WE'RE USING THE CORRECT FONT
            g.setFont(GRID_FONT);

            // MEASURE THE FONT 
            fm = g.getFontMetrics(GRID_FONT);

            // THIS IS THE HEIGHT OF OUR CHARACTERS
            int charHeight = fm.getHeight();

            // CALCULATE THE GRID CELL DIMENSIONS
            int w = this.getWidth() / ZombieCrushSagaLevelEditor.this.gridColumns;
            int h = this.getHeight() / ZombieCrushSagaLevelEditor.this.gridRows;

            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            for (int i = 0; i < gridRows; i++) {
                x = 0;
                for (int j = 0; j < gridColumns; j++) {
                    // DRAW THE CELL
                    g.drawRoundRect(x, y, w, h, 10, 10);
                    String numToDraw = "";
                    String bloodToDraw = "";
                    // THEN RENDER THE TEXT
                    numToDraw = "" + ZombieCrushSagaLevelEditor.this.has[i][j];
                    bloodToDraw = "" + ZombieCrushSagaLevelEditor.this.blood[i][j];
                    int charWidth = fm.stringWidth(numToDraw);
                    int xInc = (w / 4);
                    int yInc = (h / 2) + (charHeight / 4);
                    x += xInc;
                    y += yInc;
                    g.drawString(numToDraw, x, y);
                    x += xInc;
                    g.drawString(bloodToDraw, x, y);
                    x -= 2 * xInc;
                    y -= yInc;

                    // ON TO THE NEXT COLUMN
                    x += w;
                }
                // ON TO THE NEXT ROW
                y += h;
            }
        }
    }

    /**
     * This is where execution of the level editor begins.
     */
    public static void main(String[] args) {
        // MAKE THE EDITOR
        ZombieCrushSagaLevelEditor app = new ZombieCrushSagaLevelEditor();

        // INITIALIZE THE APP
        app.init();

        // AND OPEN THE WINDOW
        app.window.setVisible(true);
    }
}
