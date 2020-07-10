package de.quinscape.svensondoc.model;

public final class FieldDoc
{
    private final String name;

    private final String description;


    public FieldDoc(String name, String description)
    {

        this.name = name;
        this.description = description;
    }


    public String getName()
    {
        return name;
    }


    public String getDescription()
    {
        return description;
    }
}
