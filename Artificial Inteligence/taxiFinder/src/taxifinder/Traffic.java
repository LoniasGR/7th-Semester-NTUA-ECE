/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxifinder;

import java.util.ArrayList;

/**
 *
 * @author Leonidas Avdelas
 */
public class Traffic {
    
    private final int line_id;
    private final String name;
    private final ArrayList<TimeAndAmount> hoursAndTraffic;

    public Traffic (int line_id, String name, String timeAndAmountInfo) {
        this.line_id = line_id;
        this.name = name;
        if (timeAndAmountInfo.equals(""))
            hoursAndTraffic = null;
        else {
            hoursAndTraffic = new ArrayList<>();
            String [] individualInfo;
            individualInfo = timeAndAmountInfo.split("\\|");
            for (String individualInfo1 : individualInfo) {
                TimeAndAmount instance = new TimeAndAmount(individualInfo1);
                hoursAndTraffic.add(instance);
            }
        }
    }
    
    
    public int getLine_id() {
        return line_id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<TimeAndAmount> getHoursAndTraffic() {
        return hoursAndTraffic;
    }
    
    
}
