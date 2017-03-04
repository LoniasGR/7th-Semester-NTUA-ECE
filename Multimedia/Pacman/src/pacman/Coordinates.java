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
public class Coordinates {

   
    private int x_pos;
    private int y_pos;
    
    public Coordinates(int x_pos,int y_pos) {
        this.x_pos = x_pos;
        this.y_pos = y_pos;                
    }
    
    public Coordinates (Coordinates coords) {
        this.x_pos = coords.getX_pos();
        this.y_pos = coords.getY_pos();
    }
    
     public int getX_pos() {
        return x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }
    
    public void setCoords (int x, int y) {
        this.x_pos = x;
        this.y_pos = y;
    }
}
