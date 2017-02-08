/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class inputHandler {
    private String fileName;
    private Scanner scanner;
    
    public inputHandler(String fileName) throws FileNotFoundException {
        try {
                this.scanner = new Scanner(new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            switch (fileName) { 
                case "client.csv":
                    System.out.println("Client file not found!");
                    break; 
                case "taxis.csv":
                    System.out.println("Taxis file not found!");
                    break; 
                case "nodes.csv":
                    System.out.println("Nodes file not found!");
                    break;
                default:
                    break;
            }
                System.exit(1);
        }
    }
    
    public void closeScanner () {
        scanner.close();
    }
    
    public Customer getCustomerInfo() throws InputMismatchException, 
            NoSuchElementException {
        String number;
        double [] coords = new double[2];
        double [] dest_coords = new double[2];
        String time = null; 
        int persons = 0;
        String language = null;
        int luggage = 0;
        
        try {
                scanner.nextLine();
                scanner.useDelimiter(",|\n");
                number = scanner.next();
                coords[0] = Double.parseDouble(number);
                number = scanner.next();
                coords[1] = Double.parseDouble(number);
                number = scanner.next();
                dest_coords[0] = Double.parseDouble(number);
                number = scanner.next();
                dest_coords[1] = Double.parseDouble(number);
                number = scanner.next();
                time = number;
                number = scanner.next();
                persons = Integer.parseInt(number);
                number = scanner.next();
                language = number;
                number = scanner.next();
                luggage = Integer.parseInt(number);            
                 
        }
        catch (InputMismatchException e) {
                System.out.println("Incompatible Input File!");
                System.exit(1);
        }
        catch (NoSuchElementException ex) {
            System.out.println("Incomplete File");
            System.exit(1);
        } 
        Customer client = new Customer(coords[0], coords[1],
        dest_coords[0], dest_coords[1], time, persons, language, luggage);
        return client;
    }
    
    public ArrayList<Taxi> getTaxisPosition() throws Exception {
        String number;
        double coords[] = new double[2];
        ArrayList<Taxi> taxiList = new ArrayList<>();
        int id;
        scanner.nextLine();
        scanner.useDelimiter(",|\n");
        while (scanner.hasNextLine()) {
            number = scanner.next();
            coords[0] = Double.parseDouble(number);
            number = scanner.next();
            coords[1] = Double.parseDouble(number);
            number = scanner.next();
            try{
                id = Integer.parseInt(number);
                }
            catch (NumberFormatException e) {
                number = number.substring(0, number.length()-1);
                id = Integer.parseInt(number);
            }
            taxiList.add(new Taxi(coords[0], coords[1], id));
        }
        return taxiList;
    }
    public ArrayList<Node> createGraph() throws Exception {
       double[] coords = new double[2];
       ArrayList<Node> graph = new ArrayList<>();
       int id;
       String[] line = new String[4];
       String number;
       String streetName;
       scanner.nextLine();
       while (scanner.hasNextLine()) {
           number = scanner.nextLine();
           line = number.split("," ,4);
           number = line[0];
           coords[0] = Double.parseDouble(number);
           number = line[1];
           coords[1] = Double.parseDouble(number);
           number = line[2];
           try{
               id = Integer.parseInt(number);
            }
           catch (NumberFormatException e) { 
               number = number.substring(0, number.length()-1);
               id = Integer.parseInt(number);
            }
           streetName = line[3];
           graph.add(new Node(coords[0], coords[1], id, streetName));
        }
        return graph;
    }
}
