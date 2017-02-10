/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.util.Comparator;

/**
 * Compares two nodes by X axis. Only used to find nodes that are the same.
 */
public class CoordinatesComparator implements Comparator<Node> {
@Override
    public int compare(Node a, Node b) {
        return a.getX_axis() < b.getX_axis() ? -1 : a.getX_axis() == b.getX_axis() 
                ? 0 : 1;
    }        
}
