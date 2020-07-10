package de.quinscape.svensondoc.model;

import java.util.List;

public class DocumentConfig
{
    private String name;

    private List<String> content;


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public List<String> getContent()
    {
        return content;
    }


    public void setContent(List<String> content)
    {
        this.content = content;
    }
}
