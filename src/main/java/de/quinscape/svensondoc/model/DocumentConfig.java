package de.quinscape.svensondoc.model;

import java.util.List;

/**
 * Configuration for a single ouput markdown document.
 */
public class DocumentConfig
{
    private String name;

    private List<String> content;


    /**
     * Name of the markdown document including .md suffix
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
     * List of content. Can be either a snippet name ending with ".md" or a full-qualified class name.
     *
     * <p>
     *     A third special value is "*" which should be alone in a document called "reference.md" or so. Every known
     *     POJO type is inserted there. If you enable the `"linkReference"` option the reference will
     *     be cross-linked.
     * </p>
     *
     */
    public List<String> getContent()
    {
        return content;
    }


    public void setContent(List<String> content)
    {
        this.content = content;
    }
}
