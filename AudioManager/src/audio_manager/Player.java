/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package audio_manager;

import javax.swing.JOptionPane;
import java.io.File;

/**
 *
 * @author McKillaGorilla
 */
public class Player
{
    public static void main(String[] args)
    {
        AudioManager audio = new AudioManager();
        try
        {
            File audioFile = new File(Player.class.getResource("Na.mid").toURI());
            audio.loadAudio(audioFile, "NA", "NA.mid");
            audio.play("NA", true);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getStackTrace());
        }        
    }
}
