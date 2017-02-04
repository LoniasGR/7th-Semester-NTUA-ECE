/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

/**
 *
 * @author Leonidas Avdelas
 */

import java.awt.Graphics;
import java.awt.Point;

/**
 * Actual game.
 * 
 * @author www.gametutorial.net
 */

public class Game {


    public Game()
    {
        GameGraphics.gameState = GameGraphics.GameState.STARTING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                // Sets variables and objects for the game.
                Initialize();
                // Load game files (images, sounds, ...)
                LoadContent();
                
                GameGraphics.gameState = GameGraphics.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    private void Initialize()
    {

    }
    
    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent()
    {
    
    }    
    
    
    /**
     * Restart game - reset some variables.
     */
    public void RestartGame()
    {
        
    }
    
    
    /**
     * Update game logic.
     * 
     * @param gameTime gameTime of the game.
     * @param mousePosition current mouse position.
     */
    public void UpdateGame(long gameTime, Point mousePosition)
    {
        
    }
    
    /**
     * Draw the game to the screen.
     * 
     * @param g Graphics
     * @param mousePosition current mouse position.
     */
    public void Draw(Graphics g, Point mousePosition)
    {
        
    }
}
