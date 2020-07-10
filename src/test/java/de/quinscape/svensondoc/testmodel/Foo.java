package de.quinscape.svensondoc.testmodel;

import java.util.List;
import java.util.Map;

/**
 * Foo model
 */
public class Foo
{
    private String name;

    private String num;

    private List<Bar> bars;

    private List<String> strings;

    private Map<String, Integer> ints;

    private BarValue value;

    /**
     * Name of foo
     */
    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Num value of foo
     */
    public String getNum()
    {
        return num;
    }


    public void setNum(String num)
    {
        this.num = num;
    }


    /**
     * List of bars in foo
     */
    public List<Bar> getBars()
    {
        return bars;
    }


    public void setBars(List<Bar> bars)
    {
        this.bars = bars;
    }


    public List<String> getStrings()
    {
        return strings;
    }


    public void setStrings(List<String> strings)
    {
        this.strings = strings;
    }


    public Map<String, Integer> getInts()
    {
        return ints;
    }


    public void setInts(Map<String, Integer> ints)
    {
        this.ints = ints;
    }


    /**
     * Embedde bar value
     */
    public BarValue getValue()
    {
        return value;
    }


    public void setValue(BarValue value)
    {
        this.value = value;
    }
}
