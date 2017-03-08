/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author Leonidas Avdelas
 */


public class Game {
    
    Timer timer;
    
    public AIMovement AI;
    
    private boolean speedup;
    private int cookies;
    private long speedupCookies;
    private boolean BigCookie;
    public final GameGraphics gg;
    
    public int vel_x;
    public int vel_y;

    private int pacmanDir_x;
    private int pacmanDir_y;
    
    private int pacman_x_prev;
    private int pacman_y_prev;
    
    public int score;
    public int highscore;
    public int collisions;
    public int collisionsCalculated;
    public int lives;
    
    private double PACMAN_SPEED;
    private double PACMAN_NORMAL_SPEED;
    
    private final Thread logicThread;
    
    public Game(GameGraphics gg)
    {        
        this.gg = gg;
        
        logicThread = new Thread() {
            @Override
            public void run(){
                Initialize();
              
                
                GameGraphics.gameState = GameGraphics.GameState.PLAYING;

                try {
                    UpdateGame();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Game.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        };
        logicThread.start();
    }
    
    
   /**
     * Set variables and objects for the game.
     */
    public void Initialize()
    {
        
        
        speedup = false;
        BigCookie = false;
        
        PACMAN_NORMAL_SPEED = 1;
        PACMAN_SPEED = 1;

                
        cookies = gg.findNumberOfCookies(gg.board);
        System.out.println("You have to eat " + cookies +" cookies.");
        speedupCookies = (int) Math.floor(cookies*0.6);
        vel_x = 0;
        vel_y = 0;
        
        gg.pacDir_x = 0;
        gg.pacDir_y = 0;
        
        score = 0;
        
        if (gg.highscores[0][1]!= null) {
        highscore = Integer.parseInt(gg.highscores[0][1]);
        }
        else 
            highscore = 0;
        
        collisions = 0;
        collisionsCalculated = 0;
        lives = 3;
        
        pacman_x_prev = gg.pacman_Coords.getX_pos();
        pacman_y_prev = gg.pacman_Coords.getY_pos();
        
        AI = new AIMovement(this);   
    }
    
 
    
    /**
     * Restart game - reset some variables.
     */
    public void reset()
    {
        AI = new AIMovement(this);
        vel_x = 0;
        vel_y = 0;
        pacmanDir_x = 0;
        pacmanDir_y = 0;
        
    }
    
    
    
    public void UpdateGame() throws InterruptedException
    {   
        while(true) {
            checkLives();
            if (GameGraphics.gameState != GameGraphics.GameState.DEATH &&
                    GameGraphics.gameState != GameGraphics.GameState.GAMEOVER &&
                    GameGraphics.gameState != GameGraphics.GameState.WIN &&
                    GameGraphics.gameState != GameGraphics.GameState.NEXTLVL &&
                    GameGraphics.gameState != GameGraphics.GameState.WAIT) {
                moveGhosts();
                movePacman();
                checkCollisions();
                checkCookies();
                Thread.sleep(10);
            }
            else {
                Thread.sleep(50);
            }

        }
    }
    
    public int checkForHighscore() {
        int i = 0;
        if (gg.highscores[0][1] == null) {
            return 0;
        }
        while ( i<=4 && gg.highscores[i][1] != null) {
            if (score > Integer.parseInt(gg.highscores[i][1]))
                return i;
            i++;
        }
        if(gg.highscores[i][1] == null)
            return i;
        else
            return -1;
            
    }
    
    public void addHighscore (int position, String name) throws FileNotFoundException, 
            UnsupportedEncodingException{
        
        for(int k=3;k>= position; k--) {
            System.arraycopy(gg.highscores[k], 0, gg.highscores[k+1], 0, 2);
        }
        
        gg.highscores[position][0] = name;
        gg.highscores[position][1] = Integer.toString(score);
        
        outputHandler oh = new outputHandler("highscores/highscores.txt");
        oh.writeToFile(gg.highscores);
        oh.close();
        
    }
    
    private void checkLives() {
        if (lives == 0) 
            GameGraphics.gameState = GameGraphics.GameState.GAMEOVER;
    }
    
    private void checkCollisions() {
        if (collisions == 0) ;
        else {
            switch(collisions){
                case 1: 
                    if (collisionsCalculated == 0) {
                        score+= 200;
                        collisionsCalculated++;
                    }
                    break;
                case 2:
                    switch (collisionsCalculated) {
                        case 0:
                            score+=600;
                            collisionsCalculated+=2;
                            break;
                        case 1:
                            score+=400;
                            collisionsCalculated+=1;
                            break;
                        default:
                            break;
                    }
                    break;
                case 3:
                     switch (collisionsCalculated) {
                        case 0:
                            score+=1400;
                            collisionsCalculated+=3;
                            break;
                        case 1:
                            score+=1200;
                            collisionsCalculated+=2;
                            break;
                        case 2:
                            score+=800;
                            collisionsCalculated+=1;
                        default:
                            break;
                    }
                     break;
                case 4:
                    switch (collisionsCalculated) {
                        case 0:
                            score+=3000;
                            collisionsCalculated+=4;
                            break;
                        case 1:
                            score+=2800;
                            collisionsCalculated+=3;
                            break;
                        case 2:
                            score+=2400;
                            collisionsCalculated+=2;
                        case 3:
                            score+=1600;
                            collisionsCalculated+=1;
                        default:
                            break;
                    }
                     break;
            }
        }
    }
    
    private void checkCookies() {
        if(cookies == speedupCookies) {
            if (speedup == false) {
            PACMAN_NORMAL_SPEED = PACMAN_NORMAL_SPEED*1.3;
            
            for (Ghost ghost : AI.ghosts) {
                ghost.GHOST_NORMAL_SPEED = ghost.GHOST_NORMAL_SPEED*1.3;
                if (!ghost.isScared()) {
                    ghost.GHOST_SPEED = ghost.GHOST_NORMAL_SPEED;
                }
            }
            System.out.println("Speedup! Normal speed now is " + 
                    PACMAN_NORMAL_SPEED);
            speedup = true;
            }
        }
        
        if(cookies == 0) {
            System.out.println("GAME OVER. YOU WON!");
            GameGraphics.gameState = GameGraphics.GameState.WIN;
        }
    }
    
    private void bigCookie () {
        PACMAN_SPEED = PACMAN_NORMAL_SPEED*1.2;
        AI.getScared();
        AI.changeDirection();
        gg.scared = true;
        
        if (BigCookie == true) {
            timer.restart();
        }
        else {
            BigCookie = true;

            for (Ghost ghost : AI.ghosts) {
                    ghost.GHOST_SPEED = ghost.GHOST_SPEED*0.9;
            }
        }
        timer = new Timer(7000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                BigCookie = false;
                collisions = 0;
                collisionsCalculated = 0;
                PACMAN_SPEED = PACMAN_NORMAL_SPEED;
                gg.scared = false;
                AI.calm();
                for (Ghost ghost : AI.ghosts) {
                ghost.GHOST_SPEED = ghost.GHOST_NORMAL_SPEED;
        }
                
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void moveGhosts() {
        AI.moveGhosts();
    }
    
    private void movePacman() {
        
       int pacman_x = gg.pacman_Coords.getX_pos();
       int pacman_y = gg.pacman_Coords.getY_pos();
        
        if (pacmanDir_x == -vel_x && pacmanDir_y == -vel_y) {
            pacmanDir_x = vel_x;
            pacmanDir_y = vel_y;
            gg.pacDir_x = pacmanDir_x;
            gg.pacDir_y = pacmanDir_y;
        }
        
        if (pacman_x % gg.BLOCK_SIZE == 0 && 
                pacman_y % gg.BLOCK_SIZE == 0) {
            int x = pacman_x/gg.BLOCK_SIZE;
            int y = pacman_y/gg.BLOCK_SIZE;
            System.out.println("pacman is at: " + x + ", " + y);
            if (gg.board[y][x] == '.' || gg.board[y][x] == 'O') {
                cookies--;
                
                if (gg.board[y][x] == '.')
                    score += 10;
                if (gg.board[y][x] == 'O') {
                    score += 50;
                    bigCookie();
                }
                gg.board[y][x] = ' ';
                
            
                
              
            }
            if (x == 0 && gg.board[y][0] == ' '
                && gg.board[y][18] == ' ') {
                
                pacman_x = gg.BLOCK_SIZE*18;
                x = 18;
            }
            else if (x == 18 && 
                gg.board[y][18] == ' '
                && gg.board[y][0] == ' ') {
                pacman_x = 0; 
                x = 0;
            }
            
            
            if (vel_x !=0 || vel_y != 0) {
                if ((vel_x == 1 && gg.board[y][x+1] != '#' 
                        && gg.board[y][x+1] != '-' )
                        || (vel_x == -1 && gg.board[y][x-1] != '#' 
                        && gg.board[y][x-1] != '-')
                        || (vel_y == 1 && gg.board[y+1][x] != '#' 
                                && gg.board[y+1][x] !='-')
                        || (vel_y == -1 && gg.board[y-1][x] != '#'
                                && gg.board[y-1][x] != '-')) {
                    System.out.println("VEL_Y = " + vel_y);
                    pacmanDir_x = vel_x;
                    pacmanDir_y = vel_y;
                    gg.pacDir_x = pacmanDir_x;
                    gg.pacDir_y = pacmanDir_y;
                    }                    
                }
            if ((vel_x == 1 && (gg.board[y][x+1] == '#' 
                             || gg.board[y][x+1] == '-' ))
                        || (vel_x == -1 && (gg.board[y][x-1] == '#' 
                            || gg.board[y][x-1] == '-'))
                        || (vel_y == 1 && (gg.board[y+1][x] == '#' 
                            || gg.board[y+1][x] == '-'))
                        || (vel_y == -1 && (gg.board[y-1][x] == '#'
                            || gg.board[y-1][x] == '-'))) {
                
                if ((pacmanDir_x == 1 && gg.board[y][x+1] != '#' 
                             && gg.board[y][x+1] != '-' )
                    || (pacmanDir_x == -1 && gg.board[y][x-1] != '#' 
                            && gg.board[y][x-1] != '-')
                        || (pacmanDir_y == 1 && gg.board[y+1][x] != '#' 
                                && gg.board[y+1][x] !='-')
                        || (pacmanDir_y == -1 && gg.board[y-1][x] != '#'
                                && gg.board[y-1][x] != '-')) ;
                else {
                System.out.println("NO");
                pacmanDir_x = 0;
                pacmanDir_y = 0;
                gg.pacDir_x = 0;
                gg.pacDir_y = 0;
                }
            }
        }
        if(PACMAN_NORMAL_SPEED > PACMAN_SPEED)
            PACMAN_SPEED = PACMAN_NORMAL_SPEED;
        double speed = PACMAN_SPEED;
        pacman_x = (int) Math.round(pacman_x + speed * pacmanDir_x);
        pacman_y = (int) Math.round(pacman_y + speed * pacmanDir_y);
        System.out.println("Pacman speed is: " + speed);
        System.out.println("New pacman position is: " + pacman_x
                + ", " + pacman_y);
        gg.pacman_Coords.setCoords(pacman_x, pacman_y);
    }
    
    public void closeGame () {
        logicThread.interrupt();
    }
}