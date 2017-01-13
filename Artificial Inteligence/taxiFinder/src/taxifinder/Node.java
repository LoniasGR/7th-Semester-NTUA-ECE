/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.util.ArrayList;
    
public class Node {
    
    private final double X_axis;
    private final double Y_axis;
    private final int id;
    private final String streetName;
    private ArrayList<Node> adjacencyList;
    private double fscore;
    private double gscore;
    private Node cameFrom;
    
    /**
     * 
     * @param X_axis is the X axis position of the node
     * @param Y_axis is the Y axis position of the node
     * @param id is the node's ID
     * @param streetName is the street Name of the node (if it exists)
     */
    public Node(double X_axis, double Y_axis, int id, String streetName) {
        this.adjacencyList = new ArrayList<>();
        this.X_axis = X_axis;
        this.Y_axis = Y_axis;
        this.id = id;
        this.streetName = streetName;
        this.fscore = Double.MAX_VALUE;
        this.gscore = Double.MAX_VALUE;
    }
    
    /**
     * 
     * @param node is the node its properties we want to copy
     */
    public Node (Node node) {
        this.X_axis = node.getX_axis();
        this.Y_axis = node.getY_axis();
        this.id = node.getId();
        this.streetName = node.getStreetName();
    }
    
    public String getStreetName() {
        return streetName;
    }
    
    public Node getCameFrom() {
        return this.cameFrom;
    }
    public double getFscore() {
        return fscore;
    }
    
    public double getHscore() {
        return gscore;
    }
    public double getGscore() {
        return gscore;
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

    public String getStreetNames() {
        return streetName;
    }

    public ArrayList<Node> getAdjacencyList() {
        return adjacencyList;
    }  
    
    public void RecreateAdjacencyList(ArrayList<Node> list) {
        this.adjacencyList = list;
    }
    
    public void AddToAdjacencyList (Node node) {
        this.adjacencyList.add(node);
    }
    public void SetGscore (double gScore) {
        this.gscore = gScore;
    }
    
    public void SetFscore (double fScore) {
        this.fscore = fScore;
    }
    
    public void SetCamefrom (Node node) {
        this.cameFrom = node;
    }
    
   }
