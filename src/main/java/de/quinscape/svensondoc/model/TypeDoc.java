package de.quinscape.svensondoc.model;

import java.util.List;

public class TypeDoc
{
    private final String name;
    private String description;
    private List<FieldDoc> fieldDocs;

    public TypeDoc(String name)
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public void setFieldDocs(List<FieldDoc> fieldDocs)
    {
        this.fieldDocs = fieldDocs;
    }


    public List<FieldDoc> getFieldDocs()
    {
        return fieldDocs;
    }
}
