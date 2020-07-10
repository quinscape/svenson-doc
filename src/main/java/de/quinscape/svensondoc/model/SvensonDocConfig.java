package de.quinscape.svensondoc.model;

import org.svenson.JSONTypeHint;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Root node for our JSON data
 */
public class SvensonDocConfig
{
    private List<DocumentConfig> documents;

    private Set<String> packages;

    private boolean shortenTypes;

    private boolean shortenTitle;

    private boolean linkReference;


    /**
     * List of document configuration defining the markout output documents and their contents.
     */
    @JSONTypeHint(DocumentConfig.class)
    public List<DocumentConfig> getDocuments()
    {
        if (documents == null)
        {
            return Collections.emptyList();
        }

        return documents;
    }


    public void setDocuments(List<DocumentConfig> documents)
    {
        this.documents = documents;
    }


    /**
     * Java packages with JSON POJOs to document.
     */
    public Set<String> getPackages()
    {
        if (packages == null)
        {
            return Collections.emptySet();
        }
        return packages;
    }


    public void setPackages(Set<String> packages)
    {
        this.packages = packages;
    }


    /**
     * Whether to shorten the type names in the property tables.
     */
    public boolean isShortenTypes()
    {
        return shortenTypes;
    }


    public void setShortenTypes(boolean shortenTypes)
    {
        this.shortenTypes = shortenTypes;
    }


    /**
     * Whether to shorten type titles
     */
    public boolean isShortenTitle()
    {
        return shortenTitle;
    }


    public void setShortenTitle(boolean shortenTitle)
    {
        this.shortenTitle = shortenTitle;
    }


    /**
     * If true, cross-link reference. For this to work you need to define a document "reference.md"
     * with a single content "*" to auto-generate the reference.
     */
    public boolean isLinkReference()
    {
        return linkReference;
    }


    public void setLinkReference(boolean linkReference)
    {
        this.linkReference = linkReference;
    }
}
