/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zombie_crush_saga.data;

/**
 * ZombieCrushSagaLevelRecord class, stores 6 highest scores with names.  It 
 * will be fully manipulated by the ZombieCrushSagaRecord Class, which stores 
 * all the records and manages loading and saving.
 * 
 * @author vanna
 */
public class ZombieCrushSagaLevelRecord {
    public int zombies;
    public int blood;
    public int highestScore = -1;
    //public String[] playerNames = new String[6];
}
