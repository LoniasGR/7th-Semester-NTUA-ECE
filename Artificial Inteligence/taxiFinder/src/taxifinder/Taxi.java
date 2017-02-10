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
    private final boolean available;
    private final int min_capacity;
    private final int max_capacity;
    private final String [] languages;
    private final double rating;
    private final boolean long_distance;
    private final String type;

     /**
     * @param X_axis is the longitude of the taxi
     * @param Y_axis is the latitude of the taxi
     * @param id is the id of the taxi 
     * @param available is true if the taxi is available for customers
     * @param capacity min and max capacity depends on the type of the car etc
     * @param languages languages the driver speaks
     * @param rating rating of the driver
     * @param long_distance true if the taxi does distances above 30km
     * @param type of the taxi
     */
    
    public Taxi(double X_axis, double Y_axis, int id, boolean available,
            String capacity, String languages,
            double rating, boolean long_distance, String type) {
        this.X_axis = X_axis;
        this.Y_axis = Y_axis;
        this.id = id;
        this.available = available;
        String [] capacity_parts = capacity.split("-");
        this.min_capacity = Integer.parseInt(capacity_parts[0]);
        this.max_capacity = Integer.parseInt(capacity_parts[1]);      
        this.languages = languages.split("\\|");
        this.rating = rating;
        this.long_distance = long_distance;
        String [] type_etc = type.split("\\s");
        this.type = type_etc[0];
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
    
    public boolean isAvailable() {
        return available;
    }

    public int getMin_capacity() {
        return min_capacity;
    }

    public int getMax_capacity() {
        return max_capacity;
    }

    public String[] getLanguages() {
        return languages;
    }

    public double getRating() {
        return rating;
    }

    public boolean isLong_distance() {
        return long_distance;
    }

    public String getType() {
        return type;
    }
    
    public String translateInfoToProlog () {
        String s = "taxi(" + X_axis + "," + Y_axis + "," + id + "," + available
                + "," + min_capacity + "," + max_capacity + "," + 
                rating + "," + long_distance + "," + type +")";
        return s;
    }
    
}
