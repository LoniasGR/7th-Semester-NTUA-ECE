/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Leonidas
 */
public class FileHandler 
{
    
    private String pathToFile;
    private Scanner inputFile;
    /**
     * Create a new Scanner object to read the file.
     * @param name path to file.
     * @throws FileNotFoundException 
     */
    public FileHandler(String name) throws FileNotFoundException 
    {
        this.pathToFile = name;
        try {
        this.inputFile = new Scanner(new File(pathToFile));
        }
        catch (FileNotFoundException e) {
           System.err.println(e);
        }
    }
    /**
     * Close scanner.
     */
    public void close() {
        inputFile.close();
    }
    /**
     * Read the board file and return a 2-D array.
     * @return 2-D char array of the board. 
     */
    public char[][] ReadInput ()
    {
        char[][] fileArray = new char[22][19];
        String line;
        
        /* Assuming the input file has always the correct format.
           If not, more code needs to be added.
        */
        
        for(int i=0; i< 22; i++ ) {
            line = inputFile.nextLine();
            for (int j=0; j < 19; j++ ) {
                fileArray[i][j] = line.charAt(j);
            }
        }
        
    return fileArray; 
    }
    
    /**
     * Read highscores file and return a 2-D array. 5 highscores max supported.
     * @return String 2-D array consisting of name-score. 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public String[][] ReadHighscores () throws FileNotFoundException, IOException 
    {
        String[][] scores;
            scores = new String[5][2];
            String line;
            int counter = 0;
            while(inputFile.hasNext())
            {
                line = inputFile.next();
                String [] parts = line.split("-");
                scores[counter][0] = parts[0];
                scores[counter][1] = parts[1];
                counter++;
            }
        
        
        return scores;
    }
    
    
   
}
