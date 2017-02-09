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
public class TimeAndAmount {
    private final int startingTime;
    private final int endingTime;
    private final String amount;

    public TimeAndAmount (String values) {
        String [] info = values.split("-|=");
        String [] startingTimeSplit = info[0].split(":");
        this.startingTime = Integer.parseInt(startingTimeSplit[0]) * 100 +
                Integer.parseInt(startingTimeSplit[1]);
        String [] endingTimeSplit = info[1].split(":");
        this.endingTime = Integer.parseInt(endingTimeSplit[0]) * 100 +
                Integer.parseInt(endingTimeSplit[1]);
        this.amount = info[2];
    }
    
    
    public int getStartingTime() {
        return startingTime;
    }

    public int getEndingTime() {
        return endingTime;
    }

    public String getAmount() {
        return amount;
    }
    
}
