/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Leonidas
 */
public class Pacman {
    
    private static GUI gui;

    /**
     * Starts a pacman game and does nothing else.
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    
    public static void main(String[] args) throws IOException {
       SwingUtilities.invokeLater(() -> {
           try {
               gui = new GUI("MediaLab Pac-Man");
           } catch (IOException ex) {
               Logger.getLogger
                        (Pacman.class.getName()).log(Level.SEVERE, null, ex);
           }
       });
    }
    
}
