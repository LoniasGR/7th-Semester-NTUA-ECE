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
    
    public void getScared() {
        this.scared = true;
    }
    
    public void calm () {
        this.scared = false;
    }
    
    public void reached () {
        this.running = false;
    }
    public void run () {
        this.running = true;
    }
    public boolean isRunning () {
        return running;
    }
    
    public boolean isScared () {
        return scared;
    }
    
    public int getX_pos() {
        return x_curr;
    }
    
    public int getY_pos() {
        return y_curr;
    }
    
    public int getVel_x() {
        return vel_x;
    }

    public int getVel_y() {
        return vel_y;
    }

    public int getX_prev() {
        return x_prev;
    }

    public int getY_prev() {
        return y_prev;
    }
    
    public void setX (int x) {
        this.x_curr = x;
    }
    
    public void setY (int y) {
        this.y_curr = y;
    }

    public void setVel_x(int vel_x) {
        this.vel_x = vel_x;
    }

    public void setVel_y(int vel_y) {
        this.vel_y = vel_y;
    }

    public void setX_prev(int x_prev) {
        this.x_prev = x_prev;
    }

    public void setY_prev(int y_prev) {
        this.y_prev = y_prev;
    }
    
    
}
