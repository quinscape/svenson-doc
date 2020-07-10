# Svenson-doc

svenson-doc is a markdown documentation generator for svenson JSON classes.

It can be used as maven dependency but there's also a "uber" jar.

## Usage with maven

Here's an example how to start the executable with exec:java 

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.5.0</version>
                <executions>
                    <execution>
                        <id>generate-json-docs</id>
                        <goals>
                            <goal>java</goal>
                        </goals>
                        <phase>prepare-package</phase>
                    </execution>
                </executions>
                <configuration>
                    <mainClass>de.quinscape.svensondoc.SvensonDocGenerator</mainClass>
                    <arguments>

                        <argument>--config</argument>
                        <argument>${project.basedir}/svenson-doc.json</argument>

                        <!-- Documentation for the standard Automaton logic implementations -->
                        <argument>--source</argument>
                        <argument>${project.basedir}/src/main/java</argument>

                        <argument>--output</argument>
                        <argument>${project.basedir}</argument>

                        <argument>--snippets</argument>
                        <argument>${project.basedir}/src/docs/md</argument>
                    </arguments>
                </configuration>
            </plugin>

        </plugins>
    </build>

```                                                         
Here we configure svenson-doc to use a configuration file in the project root called `svenson-doc.json`. We configure
the normal maven java source path to extra Javadoc from and we set the output directory to project
base directory, which is useful if you want to auto-generate the project readme.

The snippets directory contains hand-written markdown snippets that are mixed
within the autogenerated and optionally cross-linked JSON centric POJO documentation.  

## Configuration

The configuration also happens via JSON