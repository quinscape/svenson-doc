package de.quinscape.svensondoc.model;

import java.util.List;

/**
 * Extracted Javadoc information.
 */
public class TypeDoc
{
    private final String name;
    private String description;
    private List<PropertyDoc> propertyDocs;

    public TypeDoc(String name)
    {
        this.name = name;
    }


    /**
     * Fully qualified type name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Description extracted from Javadoc.
     */
    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public void setPropertyDocs(List<PropertyDoc> propertyDocs)
    {
        this.propertyDocs = propertyDocs;
    }


    /**
     * List of property docs for the type.
     */
    public List<PropertyDoc> getPropertyDocs()
    {
        return propertyDocs;
    }
}
