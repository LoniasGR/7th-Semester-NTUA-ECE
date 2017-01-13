/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class outputHandler {
    /**
     * Writes to a KML file all the taxi routes 
     * Optimal route is written in blue, while all the others in red
     * @param bestResult is the best taxi route for the customer
     * @param results are all the other taxi routes
     * @throws FileNotFoundException
     * TODO: add more parameters to the writing method.
     */
    
    public static void writeToFile (Result bestResult,ArrayList<Result> results) 
            throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream ("map.kml"));
        System.setOut(out);
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\t<kml xmlns=\"http://earth.google.com/kml/2.1\">\n" +
                "\t<Document>\n" +
                "\t\t<name>Taxi Routes</name>\n" +
                "\t\t<Style id=\"blue\">\n" +
                "\t\t\t<LineStyle>\n" +
                "\t\t\t\t<color>50C87800</color>\n" +
                "\t\t\t\t<width>4</width>\n" +
                "\t\t\t</LineStyle>\n" +
                "\t\t</Style>\n" +
                "\t\t<Style id=\"red\">\n" +
                "\t\t\t<LineStyle>\n" +
                "\t\t\t\t<color>ff0000ff</color>\n" +
                "\t\t\t\t<width>4</width>\n" +
                "\t\t\t</LineStyle>\n" +
                "\t\t</Style>" +
                "\t\t<Placemark>\n" +
                "\t\t\t<name>Taxi 1 </name>\n" +
                "\t\t\t<styleUrl>#blue</styleUrl>\n" +
                "\t\t\t<LineString>\n" +
                "\t\t\t\t<altitudeMode>relative</altitudeMode>\n" +
                "\t\t\t\t<coordinates>");
         for (int i = 0; i < bestResult.getPathNumberOfNodes(); i++) {
                System.out.println("\t\t\t\t\t" 
                        + bestResult.getPathNode(i).getX_axis() 
                        + "," + bestResult.getPathNode(i).getY_axis()+ 
                        "," + bestResult.getPathNode(i).getId());
            }
            System.out.println("\t\t\t\t</coordinates>\n" +
                    "\t\t\t</LineString>\n" +
                    "\t\t</Placemark>");
            int i = 2;
            for (Result result: results) {
                System.out.println("\t\t<Placemark>\n" +
                "\t\t\t<name>Taxi " + i + "</name>\n" +
                "\t\t\t<styleUrl>#red</styleUrl>\n" +
                "\t\t\t<LineString>\n" +
                "\t\t\t\t<altitudeMode>relative</altitudeMode>\n" +
                "\t\t\t\t<coordinates>");
                i++;
         for (int j = 0; j < result.getPathNumberOfNodes(); j++) {
                System.out.println("\t\t\t\t\t" 
                        + result.getPathNode(j).getX_axis() 
                        + "," + result.getPathNode(j).getY_axis()+ 
                        "," + result.getPathNode(j).getId());
            }
         System.out.println("\t\t\t\t</coordinates>\n" +
                    "\t\t\t</LineString>\n" +
                    "\t\t</Placemark>");    
            }
            System.out.println("\t</Document>\n" +
                "</kml>");
            
                
        
        
        
    }
   
}