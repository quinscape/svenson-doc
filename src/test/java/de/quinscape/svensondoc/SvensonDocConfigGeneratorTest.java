package de.quinscape.svensondoc;

import de.quinscape.svensondoc.model.SvensonDocConfig;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class SvensonDocConfigGeneratorTest
{
    private final static Logger log = LoggerFactory.getLogger(SvensonDocConfigGeneratorTest.class);


    @Test
    void testShortenNone() throws IOException
    {
        final Path tmp = Files.createTempDirectory("test-output");

        try
        {
            SvensonDocGenerator.main(new String[]{
                "--config",
                "src/test/java/de/quinscape/svensondoc/config-shorten-none.json",
                "--source",
                "src/test/java",
                "--output",
                tmp.toString(),
                "--snippets",
                "src/test/java/de/quinscape/svensondoc/snippets"
            });

            final String out = FileUtils.readFileToString(new File(tmp.toFile(), "out.md"), StandardCharsets.UTF_8);

            assertThat(out, is("# Snippet-Test\n" +
                "\n" +
                "Snippet-Content\n" +
                "\n" +
                "## Heading\n" +
                "### de.quinscape.svensondoc.testmodel.Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | de.quinscape.svensondoc.testmodel.BarValue | Embedde bar value\n" +
                "bars | List of de.quinscape.svensondoc.testmodel.Bar | List of bars in foo\n" +
                "num | String | Num value of foo\n"));

        }
        finally
        {
            FileUtils.deleteDirectory(tmp.toFile());
        }
    }


    @Test
    void testShortenAll() throws IOException
    {
        final Path tmp = Files.createTempDirectory("test-output");

        try
        {
            SvensonDocGenerator.main(new String[]{
                "--config",
                "src/test/java/de/quinscape/svensondoc/config-shorten-all.json",
                "--source",
                "src/test/java",
                "--output",
                tmp.toString(),
                "--snippets",
                "src/test/java/de/quinscape/svensondoc/snippets"
            });

            final String out = FileUtils.readFileToString(new File(tmp.toFile(), "out.md"), StandardCharsets.UTF_8);

            assertThat(out, is("# Snippet-Test\n" +
                "\n" +
                "Snippet-Content\n" +
                "\n" +
                "## Heading\n" +
                "### Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | BarValue | Embedde bar value\n" +
                "bars | List of Bar | List of bars in foo\n" +
                "num | String | Num value of foo\n"));

        }
        finally
        {
            FileUtils.deleteDirectory(tmp.toFile());
        }
    }


    @Test
    void testShortenTitle() throws IOException
    {
        final Path tmp = Files.createTempDirectory("test-output");

        try
        {
            SvensonDocGenerator.main(new String[]{
                "--config",
                "src/test/java/de/quinscape/svensondoc/config-shorten-title.json",
                "--source",
                "src/test/java",
                "--output",
                tmp.toString(),
                "--snippets",
                "src/test/java/de/quinscape/svensondoc/snippets"
            });

            final String out = FileUtils.readFileToString(new File(tmp.toFile(), "out.md"), StandardCharsets.UTF_8);

            assertThat(out, is("# Snippet-Test\n" +
                "\n" +
                "Snippet-Content\n" +
                "\n" +
                "## Heading\n" +
                "### Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | de.quinscape.svensondoc.testmodel.BarValue | Embedde bar value\n" +
                "bars | List of de.quinscape.svensondoc.testmodel.Bar | List of bars in foo\n" +
                "num | String | Num value of foo\n"));

        }
        finally
        {
            FileUtils.deleteDirectory(tmp.toFile());
        }
    }


    @Test
    void testReference() throws IOException
    {
        final Path tmp = Files.createTempDirectory("test-output");

        try
        {
            SvensonDocGenerator.main(new String[]{
                "--config",
                "src/test/java/de/quinscape/svensondoc/config-reference.json",
                "--source",
                "src/test/java",
                "--output",
                tmp.toString(),
                "--snippets",
                "src/test/java/de/quinscape/svensondoc/snippets"
            });

            final String out = FileUtils.readFileToString(new File(tmp.toFile(), "out.md"), StandardCharsets.UTF_8);

            assertThat(out, is("# Bar\n" +
                "\n" +
                "Bar model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "barValues | Map of BarValue | Map of bar values\n" +
                "# BarValue\n" +
                "\n" +
                "Mapped value within bar.\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of bar value\n" +
                "# Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | BarValue | Embedde bar value\n" +
                "bars | List of Bar | List of bars in foo\n" +
                "num | String | Num value of foo\n"));

        }
        finally
        {
            FileUtils.deleteDirectory(tmp.toFile());
        }
    }


    @Test
    void testLinkedReference() throws IOException
    {
        final Path tmp = Files.createTempDirectory("test-output");

        try
        {
            SvensonDocGenerator.main(new String[]{
                "--config",
                "src/test/java/de/quinscape/svensondoc/config-linked-reference.json",
                "--source",
                "src/test/java",
                "--output",
                tmp.toString(),
                "--snippets",
                "src/test/java/de/quinscape/svensondoc/snippets"
            });

            final String out = FileUtils.readFileToString(new File(tmp.toFile(), "out.md"), StandardCharsets.UTF_8);

            assertThat(out, is("# Snippet-Test\n" +
                "\n" +
                "Snippet-Content\n" +
                "\n" +
                "## Heading\n" +
                "### de.quinscape.svensondoc.testmodel.Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | [BarValue](reference.md#dequinscapesvensondoctestmodelbarvalue) | Embedde bar value\n" +
                "bars | List of [Bar](reference.md#dequinscapesvensondoctestmodelbar) | List of bars in foo\n" +
                "num | String | Num value of foo\n"));

            final String ref = FileUtils.readFileToString(new File(tmp.toFile(), "reference.md"), StandardCharsets.UTF_8);

            assertThat(ref, is("# de.quinscape.svensondoc.testmodel.Bar\n" +
                "\n" +
                "Bar model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "barValues | Map of [BarValue](#dequinscapesvensondoctestmodelbarvalue) | Map of bar values\n" +
                "# de.quinscape.svensondoc.testmodel.BarValue\n" +
                "\n" +
                "Mapped value within bar.\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of bar value\n" +
                "# de.quinscape.svensondoc.testmodel.Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | [BarValue](#dequinscapesvensondoctestmodelbarvalue) | Embedde bar value\n" +
                "bars | List of [Bar](#dequinscapesvensondoctestmodelbar) | List of bars in foo\n" +
                "num | String | Num value of foo\n"));

        }
        finally
        {
            FileUtils.deleteDirectory(tmp.toFile());
        }
    }


    @Test
    void testLinkedReferenceShortened() throws IOException
    {
        final Path tmp = Files.createTempDirectory("test-output");

        try
        {
            SvensonDocGenerator.main(new String[]{
                "--config",
                "src/test/java/de/quinscape/svensondoc/config-linked-reference-short.json",
                "--source",
                "src/test/java",
                "--output",
                tmp.toString(),
                "--snippets",
                "src/test/java/de/quinscape/svensondoc/snippets"
            });

            final String out = FileUtils.readFileToString(new File(tmp.toFile(), "out.md"), StandardCharsets.UTF_8);

            assertThat(out, is("# Snippet-Test\n" +
                "\n" +
                "Snippet-Content\n" +
                "\n" +
                "## Heading\n" +
                "### Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | [BarValue](reference.md#barvalue) | Embedde bar value\n" +
                "bars | List of [Bar](reference.md#bar) | List of bars in foo\n" +
                "num | String | Num value of foo\n"));


            final String ref = FileUtils.readFileToString(new File(tmp.toFile(), "reference.md"), StandardCharsets.UTF_8);

            assertThat(ref, is("# Bar\n" +
                "\n" +
                "Bar model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "barValues | Map of [BarValue](#barvalue) | Map of bar values\n" +
                "# BarValue\n" +
                "\n" +
                "Mapped value within bar.\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of bar value\n" +
                "# Foo\n" +
                "\n" +
                "Foo model\n" +
                "\n" +
                "name | type | description \n" +
                "-----|------|-------------\n" +
                "name | String | Name of foo\n" +
                "strings | List of String | \n" +
                "ints | Map of Integer | \n" +
                "value | [BarValue](#barvalue) | Embedde bar value\n" +
                "bars | List of [Bar](#bar) | List of bars in foo\n" +
                "num | String | Num value of foo\n"));
        }
        finally
        {
            FileUtils.deleteDirectory(tmp.toFile());
        }
    }
}
