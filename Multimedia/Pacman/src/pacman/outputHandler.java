/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Leonidas Avdelas
 */
public class outputHandler {
    
    private PrintWriter writer = null;
    /**
     * Opens a PrintWriter class to edit later.
     * @param path path to file to be written
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    outputHandler(String path) throws FileNotFoundException, 
            UnsupportedEncodingException {
        
        try {
            writer = new PrintWriter(path, "UTF-8");
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println(e);
            }
    }
    /**
     * Takes scores array and writes them to file.
     * @param scores String 2-D array with name and score.
     */
    public void writeToFile (String[][] scores) {
        int i = 0;
   
        while (i<=4 && scores[i][0] != null) {
            String line = scores[i][0] + "-" + scores[i][1];
            
            writer.println(line);
            i++;    
        }
    }
    /**
     * Close the PrinterWriter object for safety.
     */
    public void close() {
        writer.close();
    }
}
