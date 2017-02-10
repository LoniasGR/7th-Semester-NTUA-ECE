/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPFunctor;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;
import java.io.IOException;


/**
 *
 * @author Leonidas Avdelas
 */
public class Heuristics {
    
    private JIPEngine jip;
    private JIPTermParser Tparser;
    private JIPFunctor functor;
    private JIPQuery jipQuery;
    private JIPTerm term;
    
    public Heuristics () throws JIPSyntaxErrorException, IOException {
        jip = new JIPEngine();
        Tparser = jip.getTermParser();
       
        try {
        jip.consultFile("heuristics.pro");
        }
        catch (IOException e) {
            System.err.print("heuristics.pro: " + e);
        }
        catch (JIPSyntaxErrorException e) {
            System.err.println("heuristics.pro: " + e);       
        }
    }
 
    public void AddAssertion (String s) {
        
        jipQuery = 
                jip.openSynchronousQuery(Tparser.parseTerm("assert("+s+")."));
        jipQuery.nextSolution();
    }
    
    public boolean checkCompatibility (Taxi taxi, Customer client) {
        String s = "compatible(" +taxi.translateInfoToProlog() + 
                "," + client.translateInfoToProlog()+").";
        
        jipQuery = jip.openSynchronousQuery(Tparser.parseTerm(s));
        
        return jipQuery.nextSolution() != null;   
    }
    
    public int returnHeuristic (Node current, Node neighbour, 
            Node goal, int time) {
        int Id1 = current.getId();
        int Id2 = neighbour.getId();
        double X1 = current.getX_axis();
        double X2 = neighbour.getX_axis();
        double Y2 = neighbour.getY_axis();
        double Xdst = goal.getX_axis();
        double Ydst = goal.getY_axis();
        
        String s = "returnHeuristic(" + Id1 + "," + Id2 + "," + X1 + "," + X2 + 
                "," + Y2 + "," + Xdst + "," + Ydst + ",A).";
        
        jipQuery = jip.openSynchronousQuery(Tparser.parseTerm(s));
        JIPTerm str = jipQuery.nextSolution();
        System.out.println(str);
        
        return 1;
    }
}
