package de.quinscape.svensondoc.model;

/**
 * Extracted Javadoc for one property
 */
public final class PropertyDoc
{
    private final String name;

    private final String description;


    public PropertyDoc(String name, String description)
    {

        this.name = name;
        this.description = description;
    }


    /**
     * Property name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Description extracted from Javadoc
     */
    public String getDescription()
    {
        return description;
    }
}
