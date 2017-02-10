/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

/**
 *
 * @author Leonidas Avdelas
 */
public class Line {

  
    private final int line_id;
    private double lineStartX;
    private double lineStartY;
    private double lineEndX;
    private double lineEndY;
    private final String highway;
    private final String name;
    private final boolean oneway;
    private final boolean oneway_direction;
    private final boolean lit;
    private final int lanes;
    private final int maxspeed;
    private final String railway;
    private final String boundary;
    private final boolean access;
    private final String natural;
    private final String barrier;
    private final boolean tunnel;
    private final boolean bridge;
    private final boolean incline;
    private final String waterway;
    private final boolean busway;
    private final boolean toll;
    
    private Traffic traffic;

 
     public Line(int line_id, String highway, String name, boolean oneway,
             boolean oneway_direction, boolean lit, int lanes, int maxspeed,
             String railway, String boundary, boolean access, String natural,
             String barrier, boolean tunnel, boolean bridge, boolean incline,
             String waterway, boolean busway, boolean toll) {
         
        this.line_id = line_id;
        this.highway = highway;
        this.name = name;
        this.oneway = oneway;
        this.oneway_direction = oneway_direction;
        this.lit = lit;
        this.lanes = lanes;
        this.maxspeed = maxspeed;
        this.railway = railway;
        this.boundary = boundary;
        this.access = access;
        this.natural = natural;
        this.barrier = barrier;
        this.tunnel = tunnel;
        this.bridge = bridge;
        this.incline = incline;
        this.waterway = waterway;
        this.busway = busway;
        this.toll = toll;
        
        this.traffic = null;
        
    }
     
    public void add_lineStart (double X, double Y) {
        this.lineStartX = X;
        this.lineStartY = Y;
    }
    
    public void add_lineEnd (double X, double Y) {
        this.lineEndX = X;
        this.lineEndY = Y;
    }
    
     public int getLine_id() {
        return line_id;
    }

    public String getHighway() {
        return highway;
    }

    public String getName() {
        return name;
    }

    public boolean isOneway() {
        return oneway;
    }

    public boolean isOneway_direction() {
        return oneway_direction;
    }

    public boolean isLit() {
        return lit;
    }

    public int getLanes() {
        return lanes;
    }

    public int getMaxspeed() {
        return maxspeed;
    }

    public String getRailway() {
        return railway;
    }

    public String getBoundary() {
        return boundary;
    }

    public boolean isAccess() {
        return access;
    }

    public String getNatural() {
        return natural;
    }

    public String getBarrier() {
        return barrier;
    }

    public boolean isTunnel() {
        return tunnel;
    }

    public boolean isBridge() {
        return bridge;
    }

    public boolean isIncline() {
        return incline;
    }

    public String getWaterway() {
        return waterway;
    }

    public boolean isBusway() {
        return busway;
    }

    public boolean isToll() {
        return toll;
    }
    
    public boolean isHighway() {
        return !this.highway.equals("");
    }
    
    public void addTrafficInfo (Traffic t) {
        this.traffic = t;
    }
}