/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.util.ArrayList;

/**
 *Result class provides us a simple way to gather the results, such as the
 * best distance for every taxi, as well as the nodes that need to be passed.
 */
public class Result {
    private final  ArrayList<Node> path;
    private double distance;
    private Taxi taxi;
    
    public Result (ArrayList<Node> path, double distance) {
        this.path = path;
        this.distance = distance;
    }
    
    public void addToList (Node node) {
        this.path.add(node);
    }
    
    public void addToDistance (double distance) {
        this.distance = distance;
    }
    
    public double getDistance () {
        return this.distance;
    }
    
    public int getPathNumberOfNodes () {
        return this.path.size();
    }
    
    public Node getPathNode (int i) {
        return this.path.get(i);
    }
    
    public void addTaxi (Taxi t) {
        this.taxi = t;
    }
    
    public Taxi getTaxi () {
        return this.taxi;
    }
}
