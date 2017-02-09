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
     * @param time is given in a military format like 1430 or 420.
     */
    
    private final double X_axis;
    private final double Y_axis;
    private final double dest_X_axis;
    private final double dest_Y_axis;
    private final int time;
    private final int persons;
    private final String language;
    private final int luggage;

    public int getTime() {
        return time;
    }

    public int getPersons() {
        return persons;
    }

    public String getLanguage() {
        return language;
    }

    public int getLuggage() {
        return luggage;
    }

    public double getDest_X_axis() {
        return dest_X_axis;
    }

    public double getDest_Y_axis() {
        return dest_Y_axis;
    }

    public double getX_axis() {
        return X_axis;
    }

    public double getY_axis() {
        return Y_axis;
    }
    
    public Customer(double X_axis, double Y_axis,
            double dest_X_axis, double dest_Y_axis,
            String time, int persons, String language, int luggage) {
        this.X_axis = X_axis;
        this.Y_axis = Y_axis;
        this.dest_X_axis = dest_X_axis;
        this.dest_Y_axis = dest_Y_axis;
        String [] parts = time.split(":");
        this.time = Integer.parseInt(parts[0]) * 100 + 
                Integer.parseInt(parts[1]);
        this.persons = persons;
        this.language = language;
        this.luggage = luggage;
    }
    
    public String translateInfoToProlog () {
        String s = "client("+X_axis+","+Y_axis+","+dest_X_axis+","+dest_Y_axis+
                ","+time+","+persons+","+language+","+luggage+")";
        
        return s;
    }
    
    
}
