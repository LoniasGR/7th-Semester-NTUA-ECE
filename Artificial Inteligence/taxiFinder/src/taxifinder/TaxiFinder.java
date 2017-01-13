/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class TaxiFinder {
/**
 * Main class of the program. There are two functions that create the edges
 * of the graph for faster traversal. Main just binds all classes together.
 */    
    private static ArrayList<Taxi> taxis;
    private static Customer client;
    private static ArrayList<Node> nodeList;
    private static Node customerNode;
    private static Node taxiNode;
    private static ArrayList<Result> results;
    private static Result bestResult;
    private static Taxi mostSuitableTaxi;
    /**
     * Main starts by calling the inputHandler class methods, then creates
     * the graph using TaxiFinder class methods and finally using Astar class 
     * methods and OutputHandler class methods finds the correct solution.
     * @param args the command line arguments <b>(NOT USED)</b>.
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, 
            Exception {
        bestResult = new Result(null, Double.MAX_VALUE);
        mostSuitableTaxi = null;
        inputHandler handler;
        handler = new inputHandler("taxis.csv");
        taxis = handler.getTaxisPosition();
        handler = new inputHandler("client.csv");
        client = handler.getCustomerPosition();
        handler = new inputHandler("nodes.csv");
        nodeList = handler.createGraph();
        findSameStreetNodes(nodeList);
        ArrayList<Node> nodeList2 = new ArrayList<>(nodeList);
        Collections.sort(nodeList2, new CoordinatesComparator());
        findIdenticalNodes(nodeList2);         
        results = new ArrayList<>();
        customerNode = 
                Astar.findNodeClosestToClient(client, nodeList);
        for (Taxi taxi: taxis) {
            Result currentResult;
            taxiNode = Astar.findNodeClosestToTaxi(taxi, nodeList);
            currentResult = Astar.AstarAlgorithm
                    (taxiNode, customerNode, nodeList);
            Astar.clearPaths(nodeList);
            
            if (!(currentResult == null)) {
                results.add(currentResult);
                if (bestResult.getDistance() > currentResult.getDistance()) {
                    bestResult = currentResult;
                    mostSuitableTaxi = taxi;
                }
            }
        }
        if (results.isEmpty()) {
            System.out.println("No path to client found!");
        }
        else {
            results.remove(bestResult);
            outputHandler.writeToFile(bestResult, results);
        }
    }

/**
 * Finds the nodes that are in the same street and are neighbours.
 * The concept is that if a node follows another in a file and they
 * are from the same street, then they are neighbours.
 * @param list is the list of the nodes.
 */
public static void findSameStreetNodes (ArrayList<Node> list) {
        Node tempNode;
        Node tempNode2 = list.get(0);
        for(int i = 1; i < list.size() ; i++ ) {
            tempNode = list.get(i);
            if (tempNode2.getId() == tempNode.getId()) {
                tempNode2.AddToAdjacencyList(tempNode);
                tempNode.AddToAdjacencyList(tempNode2);
                tempNode2 = tempNode;
            }
            else {
                tempNode2 = tempNode;
            }
        }
    }
    /**
     * Finds the nodes that are the same (street crossings etc).
     * The nodes are sorted by X axis so we can reduce the time this 
     * process takes from O(n^2) to O(n*log(n)).
     * @param list is a sorted by X axis list of all the nodes. 
     */
    static void findIdenticalNodes (ArrayList<Node> list) {
        ArrayList<Node> tempList = new ArrayList<>();
        Node tempNode;
        for (int i = 0; i < list.size(); i++) {
            tempNode = list.get(i);
            if (tempList.isEmpty()) {
                tempList.add(tempNode);
            }
            else {
               Node tempNode2;
               tempNode2 = tempList.get(tempList.size()-1);
               if (tempNode2.getX_axis() == tempNode.getX_axis() &&
                       tempNode2.getY_axis() == tempNode.getY_axis()) {
                   tempList.add(tempNode);
               }
               else {
                       Node tempNode3 = tempList.get(0);
                       ArrayList<Node> adjacentNodes =
                               tempNode3.getAdjacencyList();
                       for(int k = 1; k < tempList.size(); k++) {
                           Node tempNode4 = tempList.get(k);
                           adjacentNodes.addAll(tempNode4.getAdjacencyList());
                       }
                       for(int k = 0; k < tempList.size(); k++) {
                           Node tempNode4 = tempList.get(k);
                           tempNode4.RecreateAdjacencyList(adjacentNodes);
                       }
                       tempList.clear();
                       tempList.add(tempNode);
                }
            }
        }
    }
}