/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Leonidas Avdelas
 */
public class GameGraphics extends GamePanel {
    
    /**
     * Width of the frame.
     */
    public static int frameWidth;
    /**
     * Height of the frame.
     */
    public static int frameHeight;
    
    /**
     * Time of one second in nanoseconds.
     * 1 second = 1 000 000 000 nanoseconds
     */
    public static final long NANOSEC_IN_SEC = 1000000000L;
    
    /**
     * Time of one millisecond in nanoseconds.
     * 1 millisecond = 1 000 000 nanoseconds
     */
    public static final long NANOSEC_IN_MILLISEC = 1000000L;
    
     /**
     * FPS - Frames per second
     * How many times per second the game should update?
     */
    private final int GAME_FPS = 60;
    
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = NANOSEC_IN_SEC / GAME_FPS;

    
    /**
     * Possible states of the game
     */
    public static enum GameState
    {STARTING, MAIN_MENU, PLAYING, GAMEOVER, WIN, PAUSE}
    
    /**
     * Current state of the game
     */
    public static GameState gameState;
    
    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    
    // It is used for calculating elapsed time.
    private long lastTime;
    
    // The actual game
    private Game game;
    
    private GUI gui;
    
    //the game's board
    private char[][] board = null;
    
    private MediaTracker images;
    
    private Image pacmanIdle;
    private Image[] pacmanUp;
    private Image[] pacmanDown;
    private Image[] pacmanRight;
    private Image[] pacmanLeft;
    private Image[] ghost;
    private Image[] ghostScared;
    
    
    public GameGraphics (GUI gui)
    {
        super();
        
        this.gui = gui;
        
        gameState = GameState.STARTING;
        
        //We start game in new thread.
        Thread gameThread = new Thread() {
            @Override
            public void run(){
                try {
                    GameLoop();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GameGraphics.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        };
        gameThread.start();
    }
    
     /**
     * Set variables and objects.
     * This method is intended to set the variables and 
     * objects for this class, variables and objects for the 
     * actual game can be set in Game.java.
     */
    private void Initialize() throws FileNotFoundException
    {
        FileHandler fh;
        try {
            fh = new FileHandler("boards/board1.txt");
            board = fh.ReadInput();
        
        }
        catch (FileNotFoundException e){
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Load files - images, sounds, ...
     * This method is intended to load files 
     * for this class, files for the actual game can be loaded in Game.java.
     */
    private void LoadContent()
    {
        images = new MediaTracker(this);
        
        ghost = new Image[2];
        ghost[0] = new ImageIcon("/ghostpics/Ghost1.gif").getImage();
        images.addImage(ghost[0],0);
        ghost[1] = new ImageIcon("/ghostpics/Ghost2.gif").getImage();
        images.addImage(ghost[1],0);
        
        ghostScared = new Image[2];
        ghostScared[0] = new ImageIcon("/ghostpics/GhostScared1.gif").getImage();
        images.addImage(ghostScared[0], 0);
        ghostScared[1] = new ImageIcon("/ghostpics/GhostScared2.gif").getImage();
        images.addImage(ghostScared[1], 0);
        
        pacmanUp = new Image[3];
        pacmanUp[0] = new ImageIcon("/pacpics/PMup1.gif").getImage();
        images.addImage(pacmanUp[0], 0);
        pacmanUp[1] = new ImageIcon("/pacpics/PMup2.gif").getImage();
        images.addImage(pacmanUp[1], 0);
        pacmanUp[2] = new ImageIcon("/pacpics/PMup3.gif").getImage();
        images.addImage(pacmanUp[2], 0);
        
        pacmanDown = new Image[3];
        pacmanDown[0] = new ImageIcon("pacpics/PMdown1.gif").getImage();
        images.addImage(pacmanDown[0], 0);
        pacmanDown[1] = new ImageIcon("pacpics/PMdown2.gif").getImage();
        images.addImage(pacmanDown[1], 0);
        pacmanDown[2] = new ImageIcon("pacpics/PMdown3.gif").getImage();
        images.addImage(pacmanDown[2], 0); 
        
        pacmanRight = new Image[3];
        pacmanRight[0] = new ImageIcon("pacpics/PMright1.gif").getImage();
        images.addImage(pacmanRight[0], 0);
        pacmanRight[1] = new ImageIcon("pacpics/PMright2.gif").getImage();
        images.addImage(pacmanRight[1], 0);
        pacmanRight[2] = new ImageIcon("pacpics/PMright3.gif").getImage();
        images.addImage(pacmanRight[2], 0);        
        
        pacmanLeft = new Image[3];
        pacmanLeft[0] = new ImageIcon("pacpics/PMleft1.gif").getImage();
        images.addImage(pacmanLeft[0], 0);
        pacmanLeft[1] = new ImageIcon("pacpics/PMleft2.gif").getImage();
        images.addImage(pacmanLeft[1], 0);
        pacmanLeft[2] = new ImageIcon("pacpics/PMleft3.gif").getImage();
        images.addImage(pacmanLeft[2], 0);
        
        pacmanIdle = new ImageIcon("pacpics/PM.gif").getImage();
        images.addImage(pacmanIdle, 0);
        
        try {
           images.waitForAll();
        }
        catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    
    }
    
    private void createTerrain () 
    {
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 19; j++) {
                System.out.print(Character.toString(board[i][j]));
                this.add(new JLabel(Character.toString(board[i][j])));
            }
            System.out.println();
        }
    }
    
    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic 
     * is updated and then the game is drawn on the screen.
     */
    private void GameLoop() throws FileNotFoundException
    {
        // This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;
        
        while(true)
        {
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;
                    
                    
                    lastTime = System.nanoTime();
                break;
                case GAMEOVER:
                    //...
                break;
                case MAIN_MENU:
                break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();
                    
                    //Creating the Terrain...
                    createTerrain();
                    
                    this.gui.setVisible();


                    // When all things that are called above finished, we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                break;
            }
            
            // Repaint the screen.
            repaint();
            
            // Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / NANOSEC_IN_MILLISEC; // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
            if (timeLeft < 10) 
                timeLeft = 10; //set a minimum
            try {
                 //Provides the necessary delay and also yields control so that other thread can do work.
                 Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }
    
    /**
     * Draw the game to the screen. It is called through repaint() method in GameLoop() method.
     * @param g2d
     */
    @Override
    public void Draw(Graphics2D g2d)
    {
        switch (gameState)
        {
            case PLAYING:
            break;
            case GAMEOVER:
                //...
            break;
            case MAIN_MENU:
                //...
            break;

        }
    }
    
    
    /**
     * Starts new game.
     */
    private void newGame()
    {
        // We set gameTime to zero and lastTime to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        
        game = new Game();
    }
    
    
    /**
     * This method is called when keyboard key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        
    }
    
   
}

