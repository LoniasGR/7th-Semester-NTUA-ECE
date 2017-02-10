/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.util.Comparator;

/**
 *
 * @author Leonidas
 */
public class ResultByDistanceComparator implements Comparator<Result>{
    @Override
    public int compare(Result a, Result b)
    {
        return a.getDistance() < b.getDistance() ? 
           -1 : a.getDistance() == b.getDistance() ? 0 : 1;
    }
}
