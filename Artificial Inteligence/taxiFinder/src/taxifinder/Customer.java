/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

public class Customer {
    
    /**
     * @param X_axis is the longitude of the location of the customer
     * @param Y_axis is the latitude of the location of the customer
     */
    
    private final double X_axis;
    private final double Y_axis;

    public double getX_axis() {
        return X_axis;
    }

    public double getY_axis() {
        return Y_axis;
    }
    
    public Customer(double X_axis, double Y_axis) {
        this.X_axis = X_axis;
        this.Y_axis = Y_axis;
    }
    
    
}
