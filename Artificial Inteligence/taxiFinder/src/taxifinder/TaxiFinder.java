/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaxiFinder {
/**
 * Main class of the program. There are two functions that create the edges
 * of the graph for faster traversal. Main just binds all classes together.
 */    
    private static ArrayList<Taxi> taxis;
    
    private static Customer client;
    
    private static ArrayList<Node> nodeList;
    
    private static ArrayList<Line> lineList;
    
    private static ArrayList<Traffic> trafficList;
    
    private static Node customerNode;
    private static Node taxiNode;
    private static ArrayList<Result> results;
    private static Result bestResult;
    private static Taxi mostSuitableTaxi;
    private static Heuristics heuristic;
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
        taxis = handler.getTaxisInformation();
        
        handler = new inputHandler("client.csv");
        client = handler.getCustomerInfo();
        
        handler = new inputHandler("nodes.csv");
        nodeList = handler.createGraph();
        
        handler = new inputHandler("lines.csv");
        lineList = handler.getLineInfo();
        //findLineStartAndEnd(lineList, nodeList);
        
        handler = new inputHandler("traffic.csv");
        trafficList = handler.getTrafficInfo();
        
        heuristic = new Heuristics();
        addInformationToProlog();
        
        connectTrafficWithLine(lineList, trafficList);
        
        findSameStreetNodes(nodeList, lineList);
        
       
        ArrayList<Node> nodeList2 = new ArrayList<>(nodeList);
        Collections.sort(nodeList2, new CoordinatesComparator());
        findIdenticalNodes(nodeList2); 
        
        results = new ArrayList<>();
        customerNode = 
                Astar.findNodeClosestToClient(client, nodeList);
        for (Taxi taxi: taxis) {
            if (heuristic.checkCompatibility(taxi, client)) {
                Result currentResult;
                taxiNode = Astar.findNodeClosestToTaxi(taxi, nodeList);
                currentResult = Astar.AstarAlgorithm
                                (taxiNode, customerNode, nodeList, heuristic,
                                        client.getTime());
                Astar.clearPaths(nodeList);

                if (!(currentResult == null)) {
                    currentResult.addTaxi(taxi);
                    results.add(currentResult);
                    if (bestResult.getDistance() > currentResult.getDistance())
                    {
                        bestResult = currentResult;
                        mostSuitableTaxi = taxi;
                    }
                }
            } else continue;
        }
        if (results.isEmpty()) {
            System.out.println("No path to client found!");
        }
        else {
            Collections.sort(results, new ResultByDistanceComparator());
            List<Result> bestResults = results.subList(0, 5);
            outputHandler.printbestResultsByDistance(bestResults);
            bestResults.remove(bestResult);
            outputHandler.writeToFile(bestResult, bestResults, "map1.kml");
            bestResults.add(bestResult);
            Collections.sort(bestResults, new ResultByTaxiRatingComparator());
            outputHandler.printbestResultsByRating(bestResults);
            bestResults.remove(bestResult);
            outputHandler.writeToFile(bestResult, bestResults, "map2.kml");
            bestResults.add(bestResult);
            
        }
    }

/**
 * Finds the nodes that are in the same street and are neighbors.
 * The concept is that if a node follows another in a file and they
 * are from the same street, then they are neighbors.
 * @param list is the list of the nodes.
 * @param lineList
 */
public static void findSameStreetNodes (ArrayList<Node> list,
        ArrayList<Line> lineList) {
        Node tempNode;
        Node tempNode2 = list.get(0);
        Line tempLine;
        
        for(int i = 1; i < list.size() ; i++ ) {
            tempNode = list.get(i);
            for (Line lineList1 : lineList) {
                if (tempNode.getLine_id() == lineList1.getLine_id()) {
                    tempLine = lineList1;
                    break;
                }
            }
            if (tempNode2.getLine_id() == tempNode.getLine_id()) {
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
               if (tempNode2.getId() == tempNode.getId()) {
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
    
    static void findLineStartAndEnd (ArrayList<Line> lList, 
            ArrayList<Node> nList) {
        for(Line line : lList) {
            int Lid = line.getLine_id();
            int iterator = 0;
            for (int i=0; i < nList.size(); i++) {
                Node temp = nList.get(i);
                if (temp.getLine_id() == Lid) {
                    iterator = i;
                    break;
                }
            }
            Node temp1 = nList.get(iterator);
            line.add_lineStart(temp1.getX_axis(), temp1.getY_axis());
            for(int i = iterator; i < nList.size(); i++) {
                Node temp = nList.get(i);
                if (temp.getLine_id() == Lid)
                temp.addLine(line);
                else {
                    iterator = i-1;
                    break;
                }
            }
            temp1 = nList.get(iterator);
            line.add_lineEnd(temp1.getX_axis(),temp1.getY_axis());
            
        }
    }
    
    static void connectTrafficWithLine (ArrayList<Line> lineList, 
            ArrayList<Traffic> trafficList) {
        
        for (Line lineList1 : lineList) {
            for (Traffic trafficList1: trafficList) {
                if (lineList1.getLine_id() == trafficList1.getLine_id()) {
                    lineList1.addTrafficInfo(trafficList1);
                    break;
                }
            }
        }    
    }
    
    static void addInformationToProlog () {
        for(Taxi taxi1 : taxis) {
            String [] languages = taxi1.getLanguages();
            int id = taxi1.getId();
             for (String language : languages) {
                 heuristic.AddAssertion("language("+taxi1.getId() +
                         "," + language+")");
             }
        }
        for(Traffic traffic : trafficList) {
            ArrayList<TimeAndAmount> hours = traffic.getHoursAndTraffic();
            if (hours == null)
                continue;
            else {
                for (TimeAndAmount hour : hours) {
                    int id = traffic.getLine_id();
                    int srtH = hour.getStartingTime();
                    int endH = hour.getEndingTime();
                    String vol = hour.getAmount();

                    heuristic.AddAssertion("traffic(" + id + "," + srtH + ","
                          + endH + "," + vol + ")");
                }
            }
        }
        
        for(Line line : lineList) {
            int id = line.getLine_id();
            boolean oneway = line.isOneway();
            boolean oneway_direction = line.isOneway_direction();
            
            heuristic.AddAssertion("oneway(" + id + "," + oneway + "," + 
                    oneway_direction + ")");
            heuristic.AddAssertion(("highway("+ id +"," + 
                    line.isHighway() +")"));
        }
        for (Node n : nodeList) {
            heuristic.AddAssertion("belongsIn(" + n.getId() + "," 
                    + n.getLine_id() + ")");
        }
    }
}