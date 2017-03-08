/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class is used to determine which way a ghost should move.
 * @author Leonidas Avdelas
 */
public class AIMovement {
    //right , down, up, left
    private final Integer [] directions;
    
    
    private final Game game;
    private final GameGraphics gg;
    public ArrayList<Ghost> ghosts;
    
    /**
     * AIMovement constructor.
     * Get information about each ghost.
     * @param game Game Object.
     */
    public AIMovement (Game game) {
        this.game = game;
        this.gg = game.gg;
                
        ghosts = new ArrayList<>();
        
        directions = new Integer [4];
        
        for (Coordinates ghost: gg.ghost_Coords) {
            ghosts.add(new Ghost(ghost.getX_pos(),ghost.getY_pos()));

        }
    }
    
    /**
     * If ghost has reached the cave, then it can calm down.
     */
    public void calm () {
                
        for (Ghost ghost : ghosts) {
            ghost.calm ();
            
            int x = ghost.getX_pos();
            int y = ghost.getY_pos();
            if(ghost.isRunning()) {
                if (gg.board[y/gg.BLOCK_SIZE][x/gg.BLOCK_SIZE] == 'F') {
                    ghost.reached();
                }           
            }
        }
    }
    
    /**
     * Make all ghosts scared.
     */
    public void getScared () {
        for (Ghost ghost : ghosts) {
            ghost.getScared ();
        }
    }
    
    /** 
     * When the ghosts get scared, they change direction.
     */
    public void changeDirection () {
        for (Ghost ghost : ghosts) {
            ghost.setVel_x(- ghost.getVel_x());
            ghost.setVel_y(- ghost.getVel_y());
        }
    }
    
    /**
     * Checks for collision between ghost and pacman.
     * @param ghost ghost to check.
     * @return true if ghost collides with pacman, false else
     */
    private boolean checkForCollision (Ghost ghost) {
        int x = 3*ghost.getX_pos()/gg.BLOCK_SIZE;
        int y = 3*ghost.getY_pos()/gg.BLOCK_SIZE;
        
        int pacx = 3*gg.pacman_Coords.getX_pos()/gg.BLOCK_SIZE;
        int pacy = 3*gg.pacman_Coords.getY_pos()/gg.BLOCK_SIZE;
        
        int block_size = gg.BLOCK_SIZE;
        
        
        if (x == pacx && y == pacy && !ghost.isScared()) {
            GameGraphics.gameState = GameGraphics.GameState.DEATH;
            
            
        }
        
        if (x == pacx && y == pacy && ghost.isScared()) {
            ghost.run();
            game.collisions++;
            goToCave(ghost);
            return true;
        }
        return false;        
    }
    
    /**
     * Main function use to decide the movement of each ghost.
     */
    public void moveGhosts () {
        for (Ghost ghost : ghosts) {
            
            int x = ghost.getX_pos();
            int y = ghost.getY_pos();
            int block_size = gg.BLOCK_SIZE;
            
            
            checkForCollision(ghost);
            
            if (!ghost.isRunning()) { 
                          
            if (x % block_size == 0 && y % block_size == 0) {
                int x_arr = x/block_size;
                int y_arr = y/block_size;
                System.out.println("In a block: " + x_arr + ", " + y_arr);
  
                if (gg.board[y_arr][x_arr] == 'F') {
                   exitEntrance(ghost); 
                }
                else if (x == 0 && gg.board[y_arr][x_arr] == ' '
                && gg.board[y_arr][18] == ' ') {
                    
                x = block_size*18;
                }
                else if (x/block_size == 18 && 
                gg.board[y_arr][18] == ' '
                && gg.board[y_arr][0] == ' ') {
                    
                x = 0;        
                }
                
                else {
                checkForCrossing(ghost);                
                }
            }
             
            x =  (int) Math.round(x + ghost.GHOST_SPEED * ghost.getVel_x());
            y = (int) Math.round(y + ghost.GHOST_SPEED * ghost.getVel_y());

        ghost.setX(x);
        ghost.setY(y);
       
        System.out.println("Ghost speed is: " + ghost.GHOST_SPEED);
        System.out.println("New ghost position is: " + x
            + ", " + y);
        }
        }
    }
    
    /** 
     * Check if ghost is in acrossing and has more than one ways to go.
     * @param ghost ghost we are checking.
     */
    private void checkForCrossing (Ghost ghost) {
        for (int i=0; i < 4; i++)
            directions[i] = 0;
        
        int x = ghost.getX_pos()/gg.BLOCK_SIZE;
        int y = ghost.getY_pos()/gg.BLOCK_SIZE;
        if (ghost.getVel_x() == 1) {
            if(gg.board[y-1][x] != '#' && gg.board[y-1][x] != '-')
                directions[3]++;
            if(gg.board[y+1][x] != '#' && gg.board[y+1][x] != '-')
                directions[1]++;
            if(gg.board[y][x+1] != '#' && gg.board[y][x+1] != '-')
                directions[0]++;
            if (directions[3] == 0 && directions[1] == 0 && directions[0] == 0)
                directions[2]++;
        }
        if (ghost.getVel_x() == -1) {
            if(gg.board[y+1][x] != '#' && gg.board[y+1][x] != '-')
                directions[1]++;
            if(gg.board[y-1][x] != '#' && gg.board[y-1][x] != '-')
                directions[3]++;
            if(gg.board[y][x-1] != '#' && gg.board[y][x-1] != '-')
                directions[2]++;
            if (directions[1] == 0 && directions[3] == 0 && directions[2] == 0)
                directions[0]++;
        }
        if (ghost.getVel_y() == 1) {
            if (gg.board[y][x+1] != '#' && gg.board[y][x+1] != '-')
                directions[0]++;
            if(gg.board[y][x-1] != '#' && gg.board[y][x-1] != '-')
                directions[2]++;
            if(gg.board[y+1][x] !='#' && gg.board[y+1][x] !='-')
                directions[1]++;
             if (directions[1] == 0 && directions[0] == 0 && directions[2] == 0)
                directions[3]++;
        }        
        if (ghost.getVel_y() == -1) {
            if (gg.board[y][x+1] != '#' && gg.board[y][x+1] != '-')
                directions[0]++;
            if(gg.board[y][x-1] != '#' && gg.board[y][x-1] != '-')
                directions[2]++;
            if(gg.board[y-1][x] !='#' && gg.board[y-1][x] !='-')
                directions[3]++;
             if (directions[0] == 0 && directions[3] == 0 && directions[2] == 0)
                directions[1]++;
        }
        
        int total_directions = 0;
        
        for (int i = 0; i < 4; i++)
            total_directions += directions[i];
        
        
        if (total_directions >= 1) {
            if (checkForPacman (ghost, directions) && !ghost.isScared());
            else if(avoidPacman(ghost,directions) && ghost.isScared());
            else {
                int chosen_direction = pickRandom(total_directions);
                System.out.println("Chosen direction is:" + chosen_direction);

                for(int i =0; i < 4; i++) {
                    if (directions[i] == 1) {
                        chosen_direction--;
                        if (chosen_direction == 0) {
                            if(i == 0) {
                                ghost.setVel_x(1);
                                ghost.setVel_y(0);
                            }
                            if(i==1) {
                                ghost.setVel_x(0);
                                ghost.setVel_y(1);
                            }
                            if(i==2) {
                                ghost.setVel_x(-1);
                                ghost.setVel_y(0);
                            }
                            if(i==3) {
                                ghost.setVel_x(0);
                                ghost.setVel_y(-1);
                            }
                            break;
                        }
                    }
                }
            }   
        }
    }
    
    /**
     * If ghost is scared, it tries to avoid pacman. So if pacman is 3 or less
     * blocks close it runs away.
     * @param ghost current ghost.
     * @param directions possible directions the ghost can move.
     * @return true if ghost has to avoid pacman, else false.
     */
    private boolean avoidPacman(Ghost ghost, Integer [] directions) {
        int pacx = gg.pacman_Coords.getX_pos()/gg.BLOCK_SIZE;
        int pacy = gg.pacman_Coords.getY_pos()/gg.BLOCK_SIZE;
        
        int x = ghost.getX_pos()/gg.BLOCK_SIZE;
        int y = ghost.getY_pos()/gg.BLOCK_SIZE;
        
        
        if (Math.abs(pacx - x) <= 3) {
            if (directions[0] == 1 && pacx > x && gg.board[y][x-1] != '#' && 
                    gg.board[y][x-1] !='-') {
                    ghost.setVel_x(-1);
                    ghost.setVel_y(0);
                    return true;
            }
            if (directions[2] == 1 && pacx < x && gg.board[y][x+1] != '#' && 
                    gg.board[y][x+1] !='-') {
                    ghost.setVel_x(1);
                    ghost.setVel_y(0);  
                    return true;
            }
        }
        else if(Math.abs(pacy - y) <= 3) {
            if(directions[1] == 1 && pacy > y && gg.board[y-1][x] != '#' &&
                    gg.board[y-1][x] !='-') {
                    ghost.setVel_y(-1);
                    ghost.setVel_x(0);
                    return true;
            }
            if(directions[3] == 1 && pacy < y && gg.board[y+1][x] != '#' &&
                    gg.board[y+1][x] !='-') {
                    ghost.setVel_y(1);
                    ghost.setVel_x(0);
                    return true;            
            }
        }
        return false;
    }
    
    /**
     * The opposite of avoid pacman. Checks if pacman is close and moves towards
     * it.
     * @param ghost current ghost.
     * @param directions possible movement directions of ghost
     * @return true if ghost can move towards pacman, else false.
     */
    private boolean checkForPacman (Ghost ghost, Integer [] directions) {
        int pacx = gg.pacman_Coords.getX_pos()/gg.BLOCK_SIZE;
        int pacy = gg.pacman_Coords.getY_pos()/gg.BLOCK_SIZE;
        
        int x = ghost.getX_pos()/gg.BLOCK_SIZE;
        int y = ghost.getY_pos()/gg.BLOCK_SIZE;
        
        if (Math.abs(pacx - x) <= 3) {
            if (directions[0] == 1 && pacx > x) {
                    ghost.setVel_x(1);
                    ghost.setVel_y(0);
                    return true;
            }
            if (directions[2] == 1 && pacx < x) {
                    ghost.setVel_x(-1);
                    ghost.setVel_y(0);  
                    return true;
            }
        }
        else if(Math.abs(pacy - y) <= 3) {
            if(directions[1] == 1 && pacy > y) {
                    ghost.setVel_y(1);
                    ghost.setVel_x(0);
                    return true;
            }
            if(directions[3] == 1 && pacy < y) {
                    ghost.setVel_y(-1);
                    ghost.setVel_x(0);
                    return true;            
            }
        }
    return false;
    }
    
    /**
     * If ghost is inside the cave, it has to move towards the entrance.
     * This function decides how the movement will be done.
     * @param ghost current ghost.
     */
    private void exitEntrance (Ghost ghost) {
        int x = ghost.getX_pos();
        int y = ghost.getY_pos();

        Coordinates entrance = gg.findEntrance(gg.board);
        if (x != entrance.getX_pos()) {
            if (x > entrance.getX_pos()) {
                ghost.setVel_x(-1);
                ghost.setVel_y(0);
            }
            else {
                ghost.setVel_x(1);
                ghost.setVel_y(0);
            }
        }
        else {
            if (y > entrance.getY_pos()) {
                ghost.setVel_x(0);
                ghost.setVel_y(-1);
            }
            else {
                ghost.setVel_x(0);
                ghost.setVel_y(1);
            }
        }
    }
    
    /** 
     * Teleport ghost to cave if it gets eaten.
     * @param ghost current ghost.
     */
    private void goToCave (Ghost ghost) {
        Coordinates coord = gg.findGhostsLocation(gg.board);
        ghost.setX(coord.getX_pos());
        ghost.setY(coord.getY_pos());
    }
    
    /**
     * Random number picker. Used to decide the direction the ghost will follow.
     * @param maxVal maximum value for the random choice.
     * @return random number between 1 and maxVal+1.
     */
    private int pickRandom (int maxVal) {
        Random rand = new Random();
        int value = rand.nextInt(maxVal)+1;
        return value;
    }
}
        