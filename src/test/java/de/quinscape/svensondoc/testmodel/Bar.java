package de.quinscape.svensondoc.testmodel;

import java.util.Map;

/**
 * Bar model
 */
public class Bar
{
    private Map<String,BarValue> barValues;


    /**
     * Map of bar values
     */
    public Map<String, BarValue> getBarValues()
    {
        return barValues;
    }


    public void setBarValues(Map<String, BarValue> barValues)
    {
        this.barValues = barValues;
    }
}
