/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Leonidas Avdelas
 */
public class GameGraphics extends GamePanel {
        
    /**
     * Size of each block inside the game.
     */
    public int BLOCK_SIZE = 24;
    
    /**
     * Width of the frame.
     */
    public static int frameWidth = 456;
    
    /**
     * Height of the frame.
     */
    public static int frameHeight = 528;
    
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
    private final int GAME_FPS = 25;
    
    /**
     * Pause between updates. It is in nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = NANOSEC_IN_SEC / GAME_FPS;

    
    /**
     * Possible states of the game
     */
    public static enum GameState
    {STARTING, MAIN_MENU, PLAYING, GAMEOVER, WIN, PAUSE, DEATH, 
    RESTART, NEXTLVL, WAIT}
    
    /**
     * Current state of the game
     */
    public static GameState gameState;
    
    /**
     * Elapsed game time in nanoseconds.
     */
    private long gameTime;
    
    /**
     * It is used for calculating elapsed time.
     */
    private long lastTime;
    
    /**
     * pacman x and y facing direction
     */
    public int pacDir_x;
    public int pacDir_y;
    
    
    // The actual game
    private Game game;
    
    //The GUI
    private final GUI gui;
    
    //the game's board
    public char[][] board = null;
    
    //colors for some parts of the game
    private final Color mazeColor = Color.BLUE;
    private final Color foodColor = Color.orange;
    private final Color gateColor = Color.GRAY; 
    
    //media tracker for images
    private MediaTracker images;
    
    //images for the game
    private Image pacmanIdle;
    private ImageIcon pacmanIcon;
    private Image[] pacmanUp;
    private Image[] pacmanDown;
    private Image[] pacmanRight;
    private Image[] pacmanLeft;
    private Image[] ghost;
    private Image[] ghostScared;
    
    private FolderReader fr;
    public String [] boards;
    private int currentBoard;
    
    private int pacmanAnimPos;
    private Integer [] ghostAnimPos;
    
    private int ghosts;
    public boolean scared;
    public Coordinates pacman_Coords;
    public ArrayList<Coordinates> ghost_Coords;
    
    public String highscores[][];
    
    public Thread gameThread;
    private boolean started;
        
    
    public GameGraphics (GUI gui)
    {
        super();
        this.gui = gui;
        
        
        gameState = GameState.STARTING;
        
        //We start game in new thread.
        gameThread = new Thread() {
            @Override
            public void run(){
                try {
                    GameLoop();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GameGraphics.class.getName())
                            .log(Level.SEVERE, null, ex);
                } catch (InterruptedException | IOException ex) {
                    Logger.getLogger(GameGraphics.class.getName()).
                            log(Level.SEVERE, null, ex);
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
    private void Initialize() throws FileNotFoundException, IOException
    {
        started = false;
        fr = new FolderReader("boards");
        boards = fr.getFiles();
        currentBoard = 0;
        System.out.println(boards.length);
        
        FileHandler fh;
        
        fh = new FileHandler("highscores/highscores.txt");
        
        highscores = fh.ReadHighscores();
        
        System.out.println(highscores.length);
        
        fh.close();
        
        
        restart();
        
        addActionListenerForMenu();
        addActionListenerForHighScores();
        addActionListenerForStart();
        addActionListenerForLoad();
        
        gui.start.setActionCommand("Start");
        gui.mstart.setActionCommand("Start");
        gui.start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Start".equals(e.getActionCommand())) {
                    gui.start.setEnabled(false);
                    gui.mstart.setActionCommand("Restart");
                    newGame();
                }
                else if("Restart".equals(e.getActionCommand())) {
                    gui.start.setEnabled(false);
                    gameState = gameState.RESTART;
                }
            }
        });        
    }
    private void restart() {
        
       
            FileHandler fh = null;
        try {
            fh = new FileHandler("boards/" + boards[currentBoard]);
            board = fh.ReadInput();
        
        }
        catch (FileNotFoundException e){
            System.err.print(e.getMessage());
            System.exit(1);
        }
        
        fh.close();
        
        reset();
    }
    private void reset() {

        pacDir_x = 0;
        pacDir_y = 0;
        pacmanAnimPos = 2;
        pacman_Coords = findPacmanLocation(board);
//        System.out.println("Pacman Location is: " + pacman_Coords.getX_pos() +
//                ", " +pacman_Coords.getY_pos());
        
        ghosts = 4;
        scared = false;
        Coordinates coordinates = findGhostsLocation(board);
        ghost_Coords = new ArrayList<>();
        for (int i =0; i < ghosts; i++) {
            ghost_Coords.add(new Coordinates(coordinates));
        }
        ghostAnimPos = new Integer [ghosts];
        for(int i = 0; i < ghosts; i++) {
            ghostAnimPos[i] = 2;
        }
        
        System.out.println("There are " + ghosts + " ghosts.");
        for (Coordinates coords : ghost_Coords) {
        System.out.println("Location: " + coords.getX_pos() +", "
        + coords.getY_pos());
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
        
        pacmanUp = new Image[3];
        pacmanUp[0] = new ImageIcon("pacpics/PMup1.gif").getImage();
        images.addImage(pacmanUp[0], 0);
        pacmanUp[1] = new ImageIcon("pacpics/PMup2.gif").getImage();
        images.addImage(pacmanUp[1], 0);
        pacmanUp[2] = new ImageIcon("pacpics/PMup3.gif").getImage();
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
        
        pacmanIdle = new ImageIcon("pacpics/PM0.gif").getImage();
        images.addImage(pacmanIdle, 0);
        pacmanIcon = new ImageIcon("pacpics/PMright3.gif");
        
        ghost = new Image[2];
        ghost[0] = new ImageIcon("ghostpics/Ghost1.gif").getImage();
        images.addImage(ghost[0],0);
        ghost[1] = new ImageIcon("ghostpics/Ghost2.gif").getImage();
        images.addImage(ghost[1],0);
        
        ghostScared = new Image[2];
        ghostScared[0] = new ImageIcon("ghostpics/GhostScared1.gif").getImage();
        images.addImage(ghostScared[0], 0);
        ghostScared[1] = new ImageIcon("ghostpics/GhostScared2.gif").getImage();
        images.addImage(ghostScared[1], 0);
        
        try {
           images.waitForAll();
        }
        catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    
    }

    
    /**
     * In specific intervals of time (GAME_UPDATE_PERIOD) the game/logic 
     * is updated and then the game is drawn on the screen.
     */
    private void GameLoop() throws FileNotFoundException, InterruptedException, IOException
    {
        // This variables are used for calculating the time that defines 
        //for how long we should put threat to sleep to meet the GAME_FPS.
        long beginTime, timeTaken, timeLeft;
        
        boolean death = false;
        int leaderboard;
        String name;
        
        while(true)
        {
            beginTime = System.nanoTime();
            
            switch (gameState)
            {
                case PLAYING:
                    death = false;
                    gameTime += System.nanoTime() - lastTime;

                    
                    lastTime = System.nanoTime();
                    break;
                case WAIT:
                    break;
                case WIN:
                    System.out.println("currentBoard" + currentBoard);
                    if (currentBoard == (boards.length-1)) {
                    leaderboard = game.checkForHighscore();
                    if (leaderboard != -1) {
                   
                        name = gui.showHighscoreMessage(leaderboard);
                        try {
                            game.addHighscore(leaderboard, name);
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(GameGraphics.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    
                    int replay = gui.showVictoryMessage();
                    
                    if (replay == JOptionPane.YES_OPTION) {
                        System.out.println("Game Restarting");
                        gui.closeMessage();
                        gameState = GameState.RESTART;
                    }
                    if (replay == JOptionPane.NO_OPTION 
                            || replay == JOptionPane.CLOSED_OPTION)
                        System.exit(0);
                    }
                    else {
                        currentBoard++;
                        gameState = GameState.NEXTLVL;

                    }
                    break;
                    
                case NEXTLVL:
                    System.out.println("Next level!");
                    restart();
                    int lives = game.lives;
                    int score = game.score;
                    game.Initialize();
                    game.score = score;
                    game.lives = lives;
                    gameState = GameState.PLAYING; 
                    break;
                case GAMEOVER:
                   leaderboard = game.checkForHighscore();
                   System.out.println("Highscore! Position is " +leaderboard);

                   if (leaderboard != -1) {
                   
                        name = gui.showHighscoreMessage(leaderboard);
                        try {
                            game.addHighscore(leaderboard, name);
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(GameGraphics.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    int restart = gui.showLossMessage();
                    if (restart == JOptionPane.YES_OPTION) {
                        System.out.println("Game Restarting");
                        gui.closeMessage();
                        System.out.println("AHOI");
                        currentBoard = 0;
                        gameState = GameState.RESTART;
                    }
                    else if (restart == JOptionPane.NO_OPTION 
                            || restart == JOptionPane.CLOSED_OPTION)
                        System.exit(0);
                break;
                case RESTART:
                    System.out.println("11111 restarting!");
                    restart();
                    game.Initialize();
                    gameState = GameState.PLAYING; 
                    break;
                    
                case DEATH:
                    game.lives--;
                    reset();
                    game.reset();
                    gameState = GameState.PLAYING;
                    death = true;
                break;
                
                case MAIN_MENU:
                break;
                case STARTING:
                    // Sets variables and objects.
                    Initialize();
                    // Load files - images, sounds, ...
                    LoadContent();
                    this.gui.setVisible();
                    pacmanIcon.getImage().flush();
                    GUI.lives1.setIcon(pacmanIcon);
                    GUI.lives2.setIcon(pacmanIcon);
                    GUI.lives3.setIcon(pacmanIcon);

                    // When all things that are called above finished, 
                    //we change game status to main menu.
                    gameState = GameState.MAIN_MENU;
                break;
            }
            
            // Repaint the screen.
            if (!death)
                repaint();
            
            // Here we calculate the time that defines 
            //for how long we should put threat to sleep to meet the GAME_FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / NANOSEC_IN_MILLISEC; 
            // In milliseconds
            // If the time is less than 10 milliseconds, then we will put thread
            //to sleep for 10 millisecond so that some other 
            //thread can do some work.
            if (timeLeft < 10) 
                timeLeft = 10; //set a minimum
            try {
                 //Provides the necessary delay and also 
                 //yields control so that other thread can do work.
                 Thread.sleep(timeLeft);
            } catch (InterruptedException ex) { }
        }
    }
    
    
    private void drawGhosts0 (Graphics2D g2d) {
        if (scared == true) {
            drawGhostsScared1(g2d);
            System.out.println("Ghosts are scared now!");
        }
        else 
            drawGhosts1(g2d);
    }
    
    private void drawGhosts1(Graphics2D g2d) {
        int c = 0;
        for(Ghost ghost1 : game.AI.ghosts) {
            switch(ghostAnimPos[c]) {
                case 1:
                    g2d.drawImage(ghost[0], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);
                    break;
                case 2:
                    g2d.drawImage(ghost[1], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);               
                    break;
                default:
                    g2d.drawImage(ghost[0], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);
            }
            if(ghostAnimPos[c]++ == 3)
                ghostAnimPos[c] = 1;
        
            c++;
        }
    }
     private void drawGhostsScared1(Graphics2D g2d) {
        int c = 0;
        for(Ghost ghost1 : game.AI.ghosts) {
            switch(ghostAnimPos[c]) {
                case 1:
                case 2:
                    g2d.drawImage(ghostScared[0], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);
                    break;
                case 3:
                case 4:
                    g2d.drawImage(ghostScared[1], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);               
                    break;
                default:
                    g2d.drawImage(ghostScared[0], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);
            }
            if(ghostAnimPos[c]++ == 5)
                ghostAnimPos[c] = 1;
        
            c++;
        }
    }
     
     private void drawGhosts(Graphics2D g2d) {
        int c = 0;
        for(Coordinates ghost1 : ghost_Coords) {
            switch(ghostAnimPos[c]) {
                case 1:
                case 2:
                    g2d.drawImage(ghost[0], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);
                    break;
                case 3:
                case 4:
                    g2d.drawImage(ghost[1], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);               
                    break;
                default:
                    g2d.drawImage(ghost[0], ghost1.getX_pos(),ghost1.getY_pos(),
                        this);
            }
            if(ghostAnimPos[c]++ == 5)
                ghostAnimPos[c] = 1;
        
            c++;
        }
    }
    
    
    private void drawPacman (Graphics2D g2d) {
        if (pacDir_x == -1) 
            drawPacmanLeft(g2d);
        else if(pacDir_x == 1)
            drawPacmanRight(g2d);
        else if(pacDir_y == 1)
            drawPacmanDown(g2d);
        else if(pacDir_y == -1) 
            drawPacmanUp(g2d);   
        else 
            g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+ 1, 
                    pacman_Coords.getY_pos()+1,this);
        
        if (pacmanAnimPos++ == 5)
            pacmanAnimPos = 1;
    }
    private void drawPacmanDown(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacmanDown[0], pacman_Coords.getX_pos() + 1, 
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 2:
                g2d.drawImage(pacmanDown[1], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 3:
                g2d.drawImage(pacmanDown[2], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 4:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
                break;
            default:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
        }
    }
    private void drawPacmanUp(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacmanUp[0], pacman_Coords.getX_pos() + 1, 
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 2:
                g2d.drawImage(pacmanUp[1], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 3:
                g2d.drawImage(pacmanUp[2], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
                
            case 4:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
            default:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
        }
    }
    private void drawPacmanLeft(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacmanLeft[0], pacman_Coords.getX_pos() + 1, 
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 2:
                g2d.drawImage(pacmanLeft[1], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 3:
                g2d.drawImage(pacmanLeft[2], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
             case 4:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
            default:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
        }
    }
    
    private void drawPacmanRight(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacmanRight[0], pacman_Coords.getX_pos() + 1, 
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 2:
                g2d.drawImage(pacmanRight[1], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
            case 3:
                g2d.drawImage(pacmanRight[2], pacman_Coords.getX_pos() + 1,
                        pacman_Coords.getY_pos() + 1, this);
                break;
             case 4:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
            default:
                g2d.drawImage(pacmanIdle, pacman_Coords.getX_pos()+1,
                        pacman_Coords.getY_pos()+1,this);
        }
    }

    public void DrawMaze(Graphics2D g2d) {
        for (int y = 0; y < frameHeight; y+=BLOCK_SIZE) {
                    for (int x = 0; x < frameWidth; x+=BLOCK_SIZE) {
                
                g2d.setBackground(mazeColor);
                g2d.setStroke(new BasicStroke(2));
                
                System.out.print(
                        Character.toString(board[y/BLOCK_SIZE][x/BLOCK_SIZE]));
                switch(board[y/BLOCK_SIZE][x/BLOCK_SIZE]) {
                    
                case('#'):
                    g2d.setColor(mazeColor);
                    g2d.drawRect(x,y,BLOCK_SIZE,BLOCK_SIZE);
                    break;
                
                case('.') :
                    g2d.setColor(foodColor);
                    g2d.fillOval(x+12, y+12, BLOCK_SIZE/4, BLOCK_SIZE/4);
                    break;
                
                case('-') :
                    g2d.setColor(gateColor);
                    g2d.drawLine(x, y, x+BLOCK_SIZE, y);
                    break;
                
                case('O'):
                    g2d.setColor(foodColor);
                    g2d.fillOval(x+5,y+5,2*BLOCK_SIZE/3,2*BLOCK_SIZE/3);
                
                }  
            }
            System.out.println();
        }
    }
    private void printLives() {
        switch(game.lives) {
            case 0:
                GUI.lives1.setVisible(false);
                GUI.lives2.setVisible(false);
                GUI.lives3.setVisible(false);
                break;
            case 1:
                GUI.lives1.setVisible(true);
                GUI.lives2.setVisible(false);
                GUI.lives3.setVisible(false);
                break;
            case 2:
                GUI.lives1.setVisible(true);
                GUI.lives2.setVisible(true);
                GUI.lives3.setVisible(false);
                break;
            case 3:
                GUI.lives1.setVisible(true);
                GUI.lives2.setVisible(true);
                GUI.lives3.setVisible(true);
                break;     
        }      
    }
    private void printScore() {
        int score = game.score;
        int highscore = game.highscore;
        if (score > highscore) {
            game.highscore = score;
            highscore = score;
        }
        String sc = Integer.toString(score);
        String hsc = Integer.toString(highscore);
        GUI.score.setText(sc);
        GUI.highScore.setText(hsc);
        
    }
    /**
     * Draw the game to the screen. It is called through repaint()
     * method in GameLoop() method.
     * @param g2d
     */
    @Override
    public void Draw(Graphics2D g2d)
    {
        switch (gameState)
        {
            case PLAYING:
                printScore();
                printLives();
                DrawMaze(g2d);
                drawGhosts0(g2d);
                drawPacman(g2d);
            break;
            case GAMEOVER:
                DrawMaze(g2d);
                drawGhosts(g2d);
            break;
            case MAIN_MENU:
                DrawMaze(g2d);
                drawGhosts(g2d);
            break;
            case WAIT:
                DrawMaze (g2d);
                drawGhosts (g2d);
                drawPacman (g2d);
                
        }
    }
    
    
    /**
     * Starts new game.
     */
    private void newGame()
    {
        // We set gameTime to zero and lastTime 
        //to current time for later calculations.
        gameTime = 0;
        lastTime = System.nanoTime();
        
        started = true;
        game = new Game(this);    
        System.out.println("Game has started! Good Luck!");
    }
    
    
    /**
     * This method is called when keyboard key is released.
     * 
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e)
    {
        int key = e.getKeyCode();
        
        if(gameState == GameState.PLAYING) {
            switch (key) {
                case KeyEvent.VK_LEFT:
                    game.vel_x = -1;
                    game.vel_y = 0;
                    break;
                case KeyEvent.VK_RIGHT:
                    game.vel_x = 1;
                    game.vel_y = 0;
                    break;
                case KeyEvent.VK_UP:
                    game.vel_x = 0;
                    game.vel_y = -1;
                    break;
                case KeyEvent.VK_DOWN:
                    game.vel_x = 0;
                    game.vel_y = 1;
                    break;
                default:
                    break;
                    
            }
            System.out.println("New directions are: " + 
                    game.vel_x + ", " + game.vel_y);

            
        }
    }
    
    private void addActionListenerForMenu () {
        gui.exit.addActionListener((ActionEvent arg0) -> {
            System.exit(0);
        });
    }
    
    private void addActionListenerForHighScores () {
        gui.highscores.addActionListener((ActionEvent arg0) -> {
            gui.showHighscores(highscores);
        });
    }
    
    private void addActionListenerForStart () {
        gui.mstart.addActionListener ((ActionEvent arg0) -> {
            if ("Start".equals(arg0.getActionCommand())) {
                gui.mstart.setActionCommand("Restart");
                gui.start.setEnabled(false);
                newGame();
            }
            else if("Restart".equals(arg0.getActionCommand())) {
                gameState = GameState.RESTART;
            }
        });        
    }
    
    private void addActionListenerForLoad () {
        gui.load.addActionListener ((ActionEvent arg0) -> {
            String s = gui.chooseBoardMessage();
            int i;
            for(i = 0; i < boards.length; i++) {
                if (boards[i] == s)
                    break;
            }
            currentBoard = i;
            if (started) {
            gui.start.setEnabled(true);
            gui.start.setActionCommand("Restart");
            restart();
            gameState = GameState.WAIT;
            }
            else {
                newGame();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameGraphics.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                gameState = GameState.RESTART;
            }
        });
    }
    
    
   
}

