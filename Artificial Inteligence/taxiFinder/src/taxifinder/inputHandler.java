/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
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
                case "lines.csv":
                    System.out.println("Lines file not found!");
                    break;
                case "traffic.csv":
                    System.out.println("Traffic file not found");
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
                time = scanner.next();
                number = scanner.next();
                persons = Integer.parseInt(number);
                language = scanner.next();
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
    
    public ArrayList<Taxi> getTaxisInformation() throws Exception {
        String scannerResult;
        double coords[] = new double[2];
        ArrayList<Taxi> taxiList = new ArrayList<>();
        int id;
        boolean available;
        String capacity;
        String languages;
        double rating;
        boolean long_distance;
        String type;

        scanner.nextLine();
        scanner.useDelimiter(",|\n");
        while (scanner.hasNextLine()) {
            scannerResult = scanner.next();
            coords[0] = Double.parseDouble(scannerResult);
            
            scannerResult = scanner.next();
            coords[1] = Double.parseDouble(scannerResult);
            
            scannerResult = scanner.next();
            try{
                id = Integer.parseInt(scannerResult);
                }
            catch (NumberFormatException e) {
                scannerResult = 
                        scannerResult.substring(0, scannerResult.length()-1);
                id = Integer.parseInt(scannerResult);
            }
            
            scannerResult = scanner.next();
            available = scannerResult.equals("yes");
        
            capacity = scanner.next();

            languages = scanner.next();

            scannerResult = scanner.next();
            rating = Double.parseDouble(scannerResult);
          
            scannerResult = scanner.next();
            long_distance = scannerResult.equals("yes");

            type = scanner.next();  
            
            taxiList.add(new Taxi(coords[0], coords[1], id, available,
            capacity, languages, rating, long_distance, type));
        }
        return taxiList;
    }
    
    public ArrayList<Line> getLineInfo() {
        ArrayList<Line> lineList = new ArrayList<>();
        int line_id, lanes, maxspeed;
        String highway, name, railway, boundary, natural, barrier, waterway;
        boolean oneway, oneway_direction, lit, access, tunnel, bridge, incline, 
                busway, toll;
    
        String line;
        String [] line_prts;
    
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            line_prts = line.split(",", 18);
            try{
                   line_id = Integer.parseInt(line_prts[0]);
            }
            catch (NumberFormatException e) { 
               line_prts[0] = 
                       line_prts[0].substring(0, line_prts[0].length()-1);
               line_id = Integer.parseInt(line_prts[0]);
            }   
            
            highway = line_prts[1];

            name = line_prts[2];
                
            if (line_prts[3].equals("-1")) {
                oneway = true;
                oneway_direction = false;
            }
            else {
                if (line_prts[3].equals("yes")) 
                    oneway = oneway_direction = true;
                else {
                    oneway = false;
                    oneway_direction = false;
                }
            }
                
            lit = line_prts[4].equals("yes");
            
            if (line_prts[5].equals(""))
                lanes = 0;
            else {
                try{
                   lanes = Integer.parseInt(line_prts[5]);
                }
               catch (NumberFormatException e) { 
                   line_prts[5] = 
                           line_prts[5].substring(0, line_prts[5].length()-1);
                   lanes = Integer.parseInt(line_prts[5]);
                }   
            }
            
            if (line_prts[6].equals("")) 
                maxspeed = 0;
            else {
                try{
                    maxspeed = Integer.parseInt(line_prts[6]);
                }
                catch (NumberFormatException e) { 
                    line_prts[6] = 
                           line_prts[6].substring(0, line_prts[6].length()-1);
                    maxspeed = Integer.parseInt(line_prts[6]);
                }   
            }
            
            railway = line_prts[7];
            boundary = line_prts[8];
            access = line_prts[9].equals("yes");
            natural = line_prts[10];
            barrier = line_prts[11];
            tunnel = line_prts[12].equals("yes");
            bridge = line_prts[13].equals("yes");
            incline = line_prts[14].equals("up");
            waterway = line_prts[15];
            busway = line_prts[16].equals("yes");
            toll = line_prts[17].equals("yes");
            
            lineList.add(new Line(line_id, highway, name, oneway,
            oneway_direction, lit, lanes, maxspeed, railway, boundary, access,
            natural, barrier, tunnel, bridge, incline, waterway, busway, toll));     
        }
        return lineList;
    }
    
        public ArrayList<Traffic> getTrafficInfo () {
        ArrayList<Traffic> trafficInfo = new ArrayList<>();
        
        int line_id;
        String name;
        String other;
        
        String line;
        
        scanner.nextLine();
        
        while(scanner.hasNextLine()) {
            line = scanner.nextLine();
            String [] line_parts = line.split(",",3);

            try{
                    line_id = Integer.parseInt(line_parts[0]);
                }
                catch (NumberFormatException e) { 
                    line_parts[0] = 
                           line_parts[0].substring(0, line_parts[0].length()-1);
                    line_id = Integer.parseInt(line_parts[0]);
                }   
            if(line_parts.length != 2) {
                name = line_parts[1];
                other = line_parts[2];
            }
            else {
                name = "";
                other = "";
            }
            trafficInfo.add(new Traffic(line_id, name, other));
            
        }
        
        return trafficInfo;
    }
        
    public ArrayList<Node> createGraph() throws Exception {
        
       double[] coords = new double[2];
       ArrayList<Node> graph = new ArrayList<>();
       int id;
       int line_id;
       String[] line;
       String number;
       String streetName;
       
       scanner.nextLine();
       while (scanner.hasNextLine()) {
           number = scanner.nextLine();
           line = number.split(",", 5);
           
           coords[0] = Double.parseDouble(line[0]);
           coords[1] = Double.parseDouble(line[1]);
           
           try{
               line_id = Integer.parseInt(line[2]);
            }
           catch (NumberFormatException e) { 
               line[2] = line[2].substring(0, line[2].length()-1);
               line_id = Integer.parseInt(line[2]);
            }
           
           try{
               id = Integer.parseInt(line[3]);
            }
           catch (NumberFormatException e) { 
               line[3] = line[3].substring(0, line[3].length()-1);
               id = Integer.parseInt(line[3]);
            }
           streetName = line[4];
           graph.add(new Node(coords[0], coords[1],line_id, id, streetName));
        }
        return graph;
    }
}
