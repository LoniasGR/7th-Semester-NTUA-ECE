/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.io.File;

/**
 *
 * @author Leonidas Avdelas
 */
public class FolderReader {
    
    private final File folder;
    
    public FolderReader (String path) {
        folder = new File(path);
    }
    
    public String[] getFiles () {
        File[] listOfFiles = folder.listFiles();
        String [] filenames = new String[listOfFiles.length];
        
        for (int i = 0; i <listOfFiles.length; i++) {
            filenames[i] = listOfFiles[i].getName();
            System.out.println(filenames[i]);
            }
        return filenames;
        }        
}
