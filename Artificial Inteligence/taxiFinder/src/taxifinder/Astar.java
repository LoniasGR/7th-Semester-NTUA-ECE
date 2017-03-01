/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.util.ArrayList;
import java.util.Collections;

public class Astar {
    
    
    
    /**
     * Finds the Eukledian Distance between two nodes.
     * Uses EukledianDistance method.
     * @param a node 1
     * @param b node 2
     * @return Distance
     */
    public static double EukledianDistanceBetweenNodes (Node a, Node b) {
        return EukledianDistance(a.getX_axis(), a.getY_axis(), b.getX_axis(),
                b.getY_axis());
    }
    
    /**
     * Finds the Eukledian Distance between two points in the map.
     * @param X_1 point 1 X axis
     * @param Y_1 point 1 Y axis
     * @param X_2 point 2 X axis
     * @param Y_2 point 2 Y axis
     * @return eukledian distance
     */
    public static double EukledianDistance (double X_1, double Y_1, 
            double X_2, double Y_2) {
        return Math.sqrt(Math.pow(Y_1 - Y_2, 2.0) + Math.pow(X_1 - X_2, 2.0));
    }
    
    /**
     * Used to find the node of the map that is closest to the Client,
     * because the client may not always be on a node.
     * @param client our client
     * @param list list of nodes.
     * @return Node closest to client.
     */
    public static Node findNodeClosestToClient (Customer client, 
            ArrayList<Node> list) {
        double distance = Double.MAX_VALUE;
        Node result = null;
        for (Node node: list) {
            double currentDistance;
            currentDistance = EukledianDistance(node.getX_axis(),
                    node.getY_axis(), client.getX_axis(), client.getY_axis());
           if ( currentDistance < distance) {
               result = node;
               distance = currentDistance;              
           }
        }
        return result;
    }
    
     /**
     * Used to find the node of the map that is closest to the Client's 
     * destination, because it may not always be on a node.
     * @param client our client
     * @param list list of nodes.
     * @return Node closest to client.
     */
    public static Node findNodeClosestToClientDestination (Customer client, 
            ArrayList<Node> list) {
        double distance = Double.MAX_VALUE;
        Node result = null;
        for (Node node: list) {
            double currentDistance;
            currentDistance = EukledianDistance(node.getX_axis(),
                    node.getY_axis(), client.getDest_X_axis(), 
                    client.getDest_Y_axis());
           if ( currentDistance < distance) {
               result = node;
               distance = currentDistance;              
           }
        }
        return result;
    }
    
    /**
     * Used to find the node of the map that is closest to the taxi,
     * because the taxi may not always be on a node.
     * @param taxi our taxi
     * @param list list of map's nodes
     * @return Node closest to taxi.
     */
    public static Node findNodeClosestToTaxi (Taxi taxi, ArrayList<Node> list) {
        double distance = Double.MAX_VALUE;
        Node result = null;
        for (Node node: list) {
            double currentDistance;
            currentDistance = EukledianDistance(node.getX_axis(),
                    node.getY_axis(), taxi.getX_axis(), taxi.getY_axis());
           if ( currentDistance < distance) {
               result = node;
               distance = currentDistance;              
           }
        }
        return result;
    }
   
    
    
    
    /**
     * Finds nodes that are Equal in their position.
     * @param a node 1
     * @param b node 2
     * @return true if they are equal.
     */
    public static boolean FindEqualNodes (Node a, Node b) {
        return (a.getX_axis() == b.getX_axis() 
                && a.getY_axis() == b.getY_axis());
    } 
    
    
    
    
    /**
     * The algorithm used can be found on wikipedia
     * link: <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">
     * A* search algorithm</a>. The heuristic function used is the Eukledian 
     * distance between the nodes.
     * Camefrom is a variable inside Nodes, because it is easier to implement
     * than a list.
     * @param start is the algorithm's starting point
     * @param goal is the final point for the algorithm
     * @param graph the graph with all the nodes
     * @return 
     */
    public static Result AstarAlgorithm (Node start, Node goal,
            ArrayList<Node> graph, Heuristics heuristic, int time) {
        ArrayList<Node> openSet = new ArrayList<>();
        ArrayList<Node> closedSet = new ArrayList<>();
        Node current = start;
        openSet.add(start);
        current.SetGscore(0.0);
        current.SetFscore(Astar.EukledianDistanceBetweenNodes(start, goal));
        while(!openSet.isEmpty()) {
            Collections.sort(openSet, new fScoreComparator());
            current = openSet.get(0);
            if (FindEqualNodes(current, goal))
                return new Result(reconstructPath(current, start), 
                        current.getGscore());
            
            openSet.remove(current);
            closedSet.add(current);
            for (Node neighbour: current.getAdjacencyList()) {
                if (closedSet.contains(neighbour)) 
                    continue;
                double tentativeGScore = current.getGscore() + 
                    Astar.EukledianDistanceBetweenNodes
                        (current, neighbour);
                if (!openSet.contains(neighbour)) 
                    openSet.add(neighbour);
                else if (tentativeGScore >= neighbour.getGscore())
                    continue;
                neighbour.SetCamefrom(current);
                neighbour.SetGscore(tentativeGScore);
                double h = heuristic.returnHeuristic(current, neighbour, 
                                goal, time);
                if (h != Double.MAX_VALUE) 
                    neighbour.SetFscore(neighbour.getGscore() + h);
                else {
                    neighbour.SetFscore(h);
                }
            }
            
        }
    return  null;   
    }
    /** 
     * Bundled with A* from Wikipedia page.
     * Used to reconstruct the path of nodes from start to goal.
     * @param current current node
     * @param start taxi node
     * @return path of nodes
     */
        public static ArrayList<Node> reconstructPath 
        (Node current, Node start) {
            ArrayList<Node> result = new ArrayList<>();
            while ((!current.equals(start)) || current.getCameFrom()!= null) {
                Node temp = new Node(current);
                result.add(temp);
                current = current.getCameFrom();         
            }
            result.add(current);
            return result;
    }    
        /**
         * Clears came from for every node so that next time the reconstruct 
         * Path is used no interference occurs.
         * @param list list of nodes of the map.
         */
    public static void clearPaths(ArrayList<Node> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).SetCamefrom(null);
        }
    }  
    
    
}
