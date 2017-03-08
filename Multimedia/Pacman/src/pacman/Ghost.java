/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

/**
 * Ghost class to hold information for each ghost.
 * @author Leonidas Avdelas
 */
public class Ghost {

    public double GHOST_SPEED;
    public double GHOST_NORMAL_SPEED;
   
    private int vel_x;
    private int vel_y;
    
    private int x_prev;
    private int y_prev;
    
    private int x_curr;
    private int y_curr;
    
    private boolean scared;
    private boolean running;
    
    /**
     * Constructor for ghost class.
     * All variables of ghost class are initialized to default.
     * @param x x-axis position of the ghost.
     * @param y y-axis position of the ghost.
     */
    public Ghost(int x,int  y) {
        this.x_prev = x;
        this.y_prev = y;
        this.x_curr = x;
        this.y_curr = y;
        this.GHOST_SPEED = 1;
        this.GHOST_NORMAL_SPEED = 1;
        this.scared = false;
        this.running = false;
    }
    
    /** 
     * Make ghost scared, when pacman eats big cookie.
     */
    public void getScared() {
        this.scared = true;
    }
    
    /**
     * Calm ghost. Happens 7 seconds after it gets scared, unless reseted.
     */
    public void calm () {
        this.scared = false;
    }
    
    /**
     * If ghost reaches its starting point it's not running away anymore.
     */
    public void reached () {
        this.running = false;
    }
    
    /**
     * If ghost gets eaten by pacman while scared, it starts running away.
     */
    public void run () {
        this.running = true;
    }
    
    /**
     * Answer to whether the ghost is running.
     * @return ghost running situation.
     */
    public boolean isRunning () {
        return running;
    }
    
    /** 
     * Answer to whether ghost is scared.
     * @return ghost scared situation.
     */
    public boolean isScared () {
        return scared;
    }
    
    /**
     * Return x-axis position of ghost.
     * @return x-axis position.
     */
    public int getX_pos() {
        return x_curr;
    }
    
    /**
     * Return y-axis position of ghost.
     * @return y-axis position.
     */
    public int getY_pos() {
        return y_curr;
    }
    
    /**
     * Return x-axis velocity of ghost.
     * @return x-axis velocity.
     */
    public int getVel_x() {
        return vel_x;
    }

    /**
     * Return y-axis velocity of ghost.
     * @return y-axis velocity.
     */
    public int getVel_y() {
        return vel_y;
    }

    /**
     * Return previous x-axis position of ghost.
     * @return x-axis position.
     */
    public int getX_prev() {
        return x_prev;
    }

    /**
     * Return previous y-axis position of ghost.
     * @return y-axis position.
     */
    public int getY_prev() {
        return y_prev;
    }
    
    /**
     * Set new x-axis position for ghost.
     * @param x new x-axis position.
     */
    public void setX (int x) {
        this.x_curr = x;
    }
    
    /**
     * Set new y-axis position for ghost.
     * @param y  new y-axis position.
     */
    public void setY (int y) {
        this.y_curr = y;
    }

    /**
     * Set new x-axis velocity for ghost.
     * @param vel_x new x-axis velocity.
     */
    public void setVel_x(int vel_x) {
        this.vel_x = vel_x;
    }

    /**
     * Set new y-axis velocity for ghost.
     * @param vel_y new y-axis velocity.
     */
    public void setVel_y(int vel_y) {
        this.vel_y = vel_y;
    }

    /**
     * Set new x-axis previous position for ghost.
     * @param x_prev previous x-axis position.
     */
    public void setX_prev(int x_prev) {
        this.x_prev = x_prev;
    }

    /**
     * Set new y-axis previous position for ghost.
     * @param y_prev previous y-axis position.
     */
    public void setY_prev(int y_prev) {
        this.y_prev = y_prev;
    }
    
    
}
