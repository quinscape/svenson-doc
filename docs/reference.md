# DocumentConfig

Configuration for a single ouput markdown document.

property | type | description 
---------|------|-------------
name | String | Name of the markdown document including .md suffix
content | List of String | List of content. Can be either a snippet name ending with ".md" or a full-qualified class name.  <p>     A third special value is "*" which should be alone in a document called "reference.md" or so. Every known     POJO type is inserted there. If you enable the `"linkReference"` option the reference will     be cross-linked. </p>
# PropertyDoc

Extracted Javadoc for one property

property | type | description 
---------|------|-------------
name | String | Property name
description | String | Description extracted from Javadoc
# SvensonDocConfig

Root node for our JSON data

property | type | description 
---------|------|-------------
shortenTitle | boolean | Whether to shorten type titles
linkReference | boolean | If true, cross-link reference. For this to work you need to define a document "reference.md" with a single content "*" to auto-generate the reference.
packages | Set of String | Java packages with JSON POJOs to document.
documents | List of [DocumentConfig](#documentconfig) | List of document configuration defining the markout output documents and their contents.
shortenTypes | boolean | Whether to shorten the type names in the property tables.
# TypeDoc

Extracted Javadoc information.

property | type | description 
---------|------|-------------
name | String | Fully qualified type name
propertyDocs | List of [PropertyDoc](#propertydoc) | List of property docs for the type.
description | String | Description extracted from Javadoc.
