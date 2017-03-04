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
    
    outputHandler(String path) throws FileNotFoundException, 
            UnsupportedEncodingException {
        
        try {
            writer = new PrintWriter(path, "UTF-8");
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println(e);
            }
    }
    
    public void writeToFile (String[][] scores) {
        int i = 0;
   
        while (i<=4 && scores[i][0] != null) {
            String line = scores[i][0] + "-" + scores[i][1];
            
            writer.println(line);
            i++;    
        }
    }
    
    public void close() {
        writer.close();
    }
}
