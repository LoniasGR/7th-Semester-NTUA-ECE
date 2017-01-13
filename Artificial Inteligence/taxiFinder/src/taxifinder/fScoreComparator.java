/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.util.Comparator;

public class fScoreComparator implements Comparator<Node> {
    /**
     * Compares two nodes' fScore.
     * It is used to sort an ArrayList by fScore.
     * @param a is the first of the two nodes we want to compare
     * @param b is the second of the two nodes we want to compare
     * @return 1 if a>b, 0 if a=b or -1 if b>a
     */
    @Override
    public int compare(Node a, Node b) {
    return a.getFscore()< b.getFscore() ? -1 : a.getFscore() == b.getFscore() 
            ? 0 : 1;
}    
    
}
