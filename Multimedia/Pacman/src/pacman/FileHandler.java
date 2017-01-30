/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author Leonidas
 */
public class FileHandler 
{
    
    private String pathToFile;
    private Scanner inputFile;
    
    public FileHandler(String name) throws FileNotFoundException 
    {
        this.pathToFile = name;
        try {
        this.inputFile = new Scanner(new File(pathToFile));
        }
        catch (FileNotFoundException e) {
           /*Needs to be implemented! 
            Ideas:
                1) Exit the game if the file is not found.
                2) Ask again for a file.
                3) Pick a default file.
            */
        }
    }
    
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
   
}
