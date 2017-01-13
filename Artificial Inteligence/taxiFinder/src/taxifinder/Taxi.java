/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

public class Taxi {
    
    private final double X_axis;
    private final double Y_axis;
    private final int id;  
    
     /**
     * @param X_axis is the longitude of the taxi
     * @param Y_axis is the latitude of the taxi
     * @param id is the id of the taxi 
     */
    
    public Taxi(double X_axis, double Y_axis, int id) {
        this.X_axis = X_axis;
        this.Y_axis = Y_axis;
        this.id = id;
    }
    public double getX_axis() {
        return X_axis;
    }

    public double getY_axis() {
        return Y_axis;
    }

    public int getId() {
        return id;
    }    
}
