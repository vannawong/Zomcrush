package zombie_crush_saga.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class represents the complete playing history for the player since
 * originally starting the application. Note that it stores stats separately for
 * different levels.
 *
 * @author Vanna Wong
 */
public class ZombieCrushSagaRecord {
    // HERE ARE ALL THE RECORDS

    private HashMap<String, ZombieCrushSagaLevelRecord> levelRecords;
    // HIGHEST LEVEL PLAYED
    private int highestLevel;
    // NUMBER OF POWER UPS AVAILABLE
    private int powerUps;

    /**
     * Default constructor, it simply creates the hash table for storing all the
     * records stored by level.
     */
    public ZombieCrushSagaRecord() {
        levelRecords = new HashMap<String, ZombieCrushSagaLevelRecord>();
        highestLevel = 0;
        powerUps = 0;
    }

    // GET METHODS
    // -getLevelScores
    // -getPlayerNames
    /**
     * This method gets the games played for a given level.
     *
     * @param levelName Level for the request.
     *
     * @return The number of games played for the levelName level.
     */
    public int[] getLevelScores(int level) {
        ZombieCrushSagaLevelRecord rec = levelRecords.get(level);

        // IF levelName ISN'T IN THE RECORD OBJECT
        // THEN SIMPLY RETURN 0
        if (rec == null) {
            return new int[0];
        } // OTHERWISE RETURN THE GAMES PLAYED
        else {
            return rec.levelScores;
        }
    }

    /*
     * This method gets the player names for the six highest scores.
     * 
     * @param levelName level for the request.
     */
    public String[] getPlayerNames(int level) {
        ZombieCrushSagaLevelRecord rec = levelRecords.get(level);

        // IF levelName ISN'T IN THE RECORD OBJECT
        // THEN SIMPLY RETURN 0
        if (rec == null) {
            return new String[0];
        } // OTHERWISE RETURN THE GAMES PLAYED
        else {
            return rec.playerNames;
        }
    }
    // ADD METHODS
    // -addMahjongLevelRecord
    // -addWin
    // -addLoss

    /**
     * Adds the record for a level
     *
     * @param levelName
     *
     * @param rec
     */
    public void addZombieCrushSagaLevelRecord(String level, ZombieCrushSagaLevelRecord rec) {
        levelRecords.put(level, rec);
    }

    /*
     * Returns the highestLevel played.
     */
    public int getHighestLevel() {
        return highestLevel;
    }
    /*
     * Sets the highestLevel played by player.
     * 
     * @param level
     */

    public void setHighestLevel(int level) {
        highestLevel = level;
    }
    /*
     * Sets the number of power ups player has.
     * 
     * @param power
     */

    public void setPowerUps(int power) {
        powerUps = power;
    }

    /*
     * This method resets player's progress, but leaves power ups intact.
     */
    public void reset() {
        highestLevel = 0;
        levelRecords = new HashMap<String, ZombieCrushSagaLevelRecord>();
    }

    /**
     * This method adds a win to the current player's record according to the
     * level being played.
     *
     * @param levelName The level being played that the player won.
     *
     * @param winTime The time it took to win the game.
     */
    public void addWin(String level, int levelScore) {
        // GET THE RECORD FOR levelName
        ZombieCrushSagaLevelRecord rec = levelRecords.get(level);

        // IF THE PLAYER HAS NEVER PLAYED A GAME ON levelName
        if (rec == null) {
            // MAKE A NEW RECORD FOR THIS LEVEL, SINCE THIS IS
            // THE FIRST TIME WE'VE PLAYED IT
            rec = new ZombieCrushSagaLevelRecord();
            rec.levelScores[0] = levelScore;
            rec.playerNames[0] = "Bob";
            levelRecords.put(level, rec);
        } else {
            // WE'VE PLAYED THIS LEVEL BEFORE, SO SIMPLY
            // UPDATE THE STATS BY COMPARING LEVEL SCORES
            for (int i = 0; i < 6; i++) {
                if (rec.levelScores[i] == -1) {
                    rec.levelScores[i] = levelScore;
                    rec.playerNames[i] = "Bob";
                    levelRecords.put(level, rec);
                    return;
                }
            }
            for (int i = 0; i < 6; i++) {
                if (rec.levelScores[i] < levelScore) {
                    for (int j = i; j < 5; j++) {
                        rec.levelScores[j + 1] = rec.levelScores[j];
                        rec.playerNames[j + 1] = rec.playerNames[j];
                    }
                    rec.levelScores[i] = levelScore;
                    rec.playerNames[i] = "Bob";
                    return;
                }
            }
        }
    }

    // ADDITIONAL SERVICE METHODS
    // -toByteArray
    /**
     * This method constructs and fills in a byte array with all the necessary
     * data stored by this object. We do this because writing a byte array all
     * at once to a file is fast. Certainly much faster than writing to a file
     * across many write operations.
     *
     * @return A byte array filled in with all the data stored in this object,
     * which means all the player records in all the levels.
     *
     * @throws IOException Note that this method uses a stream that writes to an
     * internal byte array, not a file. So this exception should never happen.
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(highestLevel);
        dos.writeInt(powerUps);
        for (int i = 0; i < levelRecords.size(); i++) {
            dos.writeInt(i);
            ZombieCrushSagaLevelRecord rec = levelRecords.get(i);
            for (int j : rec.levelScores) {
                dos.writeInt(j);
            }
            /*for (String i : rec.playerNames) {
             dos.writeChars(i);
             }
             */
        }
        // AND THEN RETURN IT
        return baos.toByteArray();
    }
}
