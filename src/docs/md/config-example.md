## Example config

```json 
{
    "documents": [
        {
            "name": "readme.md",
            "content" : [
                "readme-start.md",
                "de.quinscape.svensondoc.model.SvensonDocConfig",
                "config-example.md"
            ]
        },
        {
            "name": "docs/reference.md",
            "content" : [
                "*"
            ]
        }
    ],

    "packages": [
        "de.quinscape.svensondoc.model"
    ],

    "shortenTitle": true,
    "shortenTypes": true,
    "linkReference" : true
}
```

The `svenson-doc.json` to generate this readme and the reference linked to it.
