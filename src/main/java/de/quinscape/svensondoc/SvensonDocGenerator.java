package de.quinscape.svensondoc;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.utils.SourceRoot;
import de.quinscape.svensondoc.model.DocumentConfig;
import de.quinscape.svensondoc.model.FieldDoc;
import de.quinscape.svensondoc.model.SvensonDocConfig;
import de.quinscape.svensondoc.model.TypeDoc;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.JSONParser;
import org.svenson.TypeAnalyzer;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.JSONPropertyInfo;
import org.svenson.info.JavaObjectPropertyInfo;
import org.svenson.info.JavaObjectSupport;
import org.svenson.info.ObjectSupport;
import org.svenson.tokenize.InputStreamSource;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.beans.Introspector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;

@Command(
    name = "svenson-docgen"
)
public class SvensonDocGenerator
    implements Callable<Integer>
{
    private final static Logger log = LoggerFactory.getLogger(SvensonDocGenerator.class);

    private static final String MD_SUFFIX = ".md";
    private static final String REFERENCE_MAGIC = "*";

    @Option(names = {"-c", "--config"}, description = "path to the json config file", required = true)
    private String configPath;

    @Option(names = {"-o", "--output"}, description = "Output path for the markdown files", required = true)
    private String outputPath;

    @Option(names = {"-s", "--source"}, description = "Java source directory", required = true)
    private String sourceDir;

    @Option(names = {"-i", "--snippets"}, description = "Markdown snippets directory")
    private String snippetsDir;

    private final ObjectSupport objectSupport = new JavaObjectSupport();

    @Override
    public Integer call() throws Exception
    {
        final File inputPath = new File(this.configPath);
        final SvensonDocConfig cfg = loadConfig(inputPath);

        final File outputPath = new File(this.outputPath);
        if (!outputPath.exists() || !outputPath.isDirectory())
        {
            throw new SvensonDocGeneratorException("output must be an existing directory.");
        }

        final File sourceDir = new File(this.sourceDir);
        if (!sourceDir.exists() || !sourceDir.isDirectory())
        {
            throw new SvensonDocGeneratorException("Invalid source directory: " + sourceDir.getAbsolutePath());
        }

        SourceRoot sourceRoot = new SourceRoot(sourceDir.toPath());

        final Map<String, TypeDoc> typeDocs = extract(sourceRoot, cfg.getPackages());

        //log.info("TYPEDOCS:\n{}", JSON.formatJSON(JSON.defaultJSON().forValue(typeDocs)));

        final File snippetsDir;
        if (this.snippetsDir != null)
        {
            snippetsDir = new File(this.snippetsDir);
            if (!snippetsDir.exists() || !snippetsDir.isDirectory())
            {
                throw new SvensonDocGeneratorException(
                    "Snippets directoy must be an existing directory: " + snippetsDir.getAbsolutePath()
                );
            }
        }
        else
        {
            snippetsDir = null;
        }

        final Path referencePath;
        if (cfg.isLinkReference())
        {
            String referenceDoc = null;
            for (DocumentConfig document : cfg.getDocuments())
            {
                for (String file : document.getContent())
                {
                    if (file.equals(REFERENCE_MAGIC))
                    {
                        referenceDoc =  document.getName();
                        // don't break, take last one found
                    }
                }
            }

            if (referenceDoc == null)
            {
                throw new SvensonDocGeneratorException("Cannot link references without reference. Include the magic reference name ( = \"*\" ) in our of your documents.");
            }

            referencePath = Paths.get(outputPath.getAbsolutePath(), referenceDoc);
        }
        else
        {
            referencePath = null;
        }

        for (DocumentConfig document : cfg.getDocuments())
        {
            StringBuilder buf = new StringBuilder();

            int level = 0;

            for (String file : document.getContent())
            {
                if (file.endsWith(MD_SUFFIX))
                {
                    final File snippetFile = new File(snippetsDir, file);

                    if (!snippetFile.exists() || !snippetFile.isFile())
                    {
                        throw new SvensonDocGeneratorException("Invalid Snippet file: " + snippetFile
                            .getAbsolutePath());
                    }


                    int start = buf.length();

                    buf.append(
                        FileUtils.readFileToString(
                            snippetFile,
                            StandardCharsets.UTF_8
                        )
                    );

                    level = updateLevel(file, buf, start);


                }
                else
                {


                    final String relativeReferencePath;

                    if (referencePath != null && new File(outputPath, document.getName()).getAbsolutePath().equals(referencePath.toString()))
                    {
                        relativeReferencePath = "";
                    }
                    else
                    {
                        relativeReferencePath = referencePath != null ? outputPath.toPath()
                            .relativize(referencePath)
                            .toString() : null;
                    }

                    final RenderContext ctx = new RenderContext(relativeReferencePath, level + 1);
                    if (file.equals(REFERENCE_MAGIC))
                    {
                        for (Map.Entry<String, TypeDoc> e : typeDocs.entrySet())
                        {
                            final String className = e.getKey();
                            final Class<?> cls = Class.forName(className);
                            if (Enum.class.isAssignableFrom(cls))
                            {
                                renderEnum(typeDocs, cls, buf, cfg, ctx);
                            }
                            else
                            {
                                renderPojo(typeDocs, cls, buf, cfg, ctx);
                            }
                        }
                    }
                    else
                    {
                        final Class<?> cls = Class.forName(file);
                        if (Enum.class.isAssignableFrom(cls))
                        {
                            renderEnum(typeDocs, cls, buf, cfg, ctx);
                        }
                        else
                        {
                            renderPojo(typeDocs, cls, buf, cfg, ctx);
                        }
                    }
                }

                FileUtils.writeStringToFile(
                    new File(outputPath, document.getName()),
                    buf.toString(),
                    StandardCharsets.UTF_8
                );
            }
        }

        System.out.println(cfg.getDocuments().size() + " document(s) generated.");

        return 0;
    }


    private int updateLevel(String info, StringBuilder buf, int start)
    {
        final int length = buf.length();

        boolean startOfLine = true;
        int level = 0;
        int currentLevel = 0;
        for (int i = start; i < length; i++)
        {
            final char c = buf.charAt(i);
            if (startOfLine)
            {
                if (c == '#')
                {
                    currentLevel++;
                }
                else
                {
                    if (currentLevel > 0)
                    {
                        level = currentLevel + 1;
                    }
                    currentLevel = 0;
                    startOfLine = false;
                }
            }
            else
            {
                if (c == '\n')
                {
                    startOfLine = true;
                }
            }
        }

        log.debug("{}: Update level to {}", info, start);
        return level;
    }


    private String findFieldDocs(TypeDoc typeDoc, String name)
    {
        for (FieldDoc fieldDoc : typeDoc.getFieldDocs())
        {
            if (fieldDoc.getName().equals(name))
            {
                return fieldDoc.getDescription();
            }
        }

        return "";
    }


    private String describe(
        Class<?> type,
        Class<?> typeHint,
        Type type1,
        SvensonDocConfig cfg,
        RenderContext ctx,
        boolean isTitle
    )
    {
        String hint;
        if (typeHint != null)
        {
            hint = simplify(typeHint.getName(), cfg, ctx, isTitle);
        }
        else if (type1 instanceof ParameterizedType)
        {
            final Type typeArg = ((ParameterizedType) type1).getActualTypeArguments()[Map.class.isAssignableFrom(type) ? 1: 0];
            hint = simplify(typeArg.getTypeName(), cfg, ctx, isTitle);
        }
        else
        {
            hint = "?";
        }
        if (List.class.isAssignableFrom(type))
        {
            return "List of " + hint;
        }
        else if (Set.class.isAssignableFrom(type))
        {
            return "Set of " + hint;
        }
        else if (Map.class.isAssignableFrom(type))
        {
            return "Map of " + hint;
        }

        return simplify(type.getName(), cfg, ctx, isTitle);
    }


    private String simplify(
        String name,
        SvensonDocConfig cfg,
        RenderContext ctx,
        boolean isTitle
    )
    {

        final String cutoff = name.substring(name.lastIndexOf('.') + 1);
        if (name.startsWith("java.util.List<"))
        {
            final String result = "List&lt;" + shorten(
                cutoff.replace(">", "&gt;")
                , cfg, ctx, isTitle
            );
            log.debug("simplify List<> {} => {}", name, result);
            return result;
        }
        else if (name.startsWith("java.util.Map<java.lang.String,"))
        {
            final String result = "Map&lt;String," + shorten(cutoff.replace(">", "&gt;"), cfg, ctx, isTitle);
            log.debug("simplify Map<> {} => {}", name, result);
            return result;
        }
        else if (name.startsWith("java.util.Set<"))
        {
            final String result = "Set&lt;" + shorten(cutoff.replace(">", "&gt;"), cfg, ctx, isTitle);
            log.debug("simplify Set<> {} => {}", name, result);
            return result;
        }
        else if (name.startsWith("java"))
        {
            final String result = cutoff;
            log.debug("simplify java {} => {}", name, result);
            return result;
        }
        return shorten(name, cfg, ctx, isTitle);
    }


    private String shorten(
        String s,
        SvensonDocConfig cfg,
        RenderContext ctx,
        boolean isTitle
    )
    {
        if (isTitle && !cfg.isShortenTitle())
        {
            return s;
        }
        else if (!isTitle && !cfg.isShortenTypes())
        {
            return linkRef(s, s, cfg, ctx, isTitle);
        }

        for (String pkg : cfg.getPackages())
        {
            if (s.startsWith(pkg) && s.charAt(pkg.length()) == '.')
            {
                final String name = s.substring(pkg.length() + 1);
                return linkRef(name, s, cfg, ctx, isTitle);
            }
        }
        return s;
    }


    private String linkRef(
        String name,
        String full,
        SvensonDocConfig cfg,
        RenderContext ctx,
        boolean isTitle
    )
    {
        if (!cfg.isLinkReference() || isTitle)
        {
            return name;
        }

        String anchor = anchorize(cfg.isShortenTitle() ? name : full);


        return "[" + name + "](" + ctx.getReference() + "#" + anchor + ")";
    }


    private String anchorize(String s)
    {
        return s.toLowerCase().replace(".", "");
    }


    private void renderEnum(
        Map<String, TypeDoc> typeDocs,
        Class<?> cls,
        StringBuilder buf,
        SvensonDocConfig cfg,
        RenderContext ctx
    )
    {
        heading(buf, ctx.getLevel());

        buf.append(" ").append( describe(cls, null, cls, cfg, ctx, true)).append("\n\n");

        final TypeDoc typeDoc = typeDocs.get(cls.getName());
        if (typeDoc == null)
        {
            buf.append("*No documentation*\n");
        }
        else
        {
            buf.append("name | description\n");
            buf.append("-----|------------\n");

            typeDoc.getFieldDocs().forEach(fd -> {
                buf.append(fd.getName()).append(" | ").append(fd.getDescription()).append("\n");
            });
        }
    }


    private void heading(StringBuilder buf, int headingLevel)
    {
        for (int i=0; i < headingLevel; i++)
        {
            buf.append('#');
        }
    }


    private void renderPojo(
        Map<String, TypeDoc> typeDocs,
        Class<?> cls,
        StringBuilder buff,
        SvensonDocConfig cfg,
        RenderContext ctx
    )
    {
        heading(buff, ctx.getLevel());
        final String className = cls.getName();
        buff.append(" ").append(describe(cls, null, cls, cfg, ctx, true)).append("\n\n");


        final TypeDoc typeDoc = typeDocs.get(className);
        if (typeDoc == null)
        {
            buff.append("*No documentation*\n");
            return;
        }

        final String typeDescription = typeDoc.getDescription();
        if (typeDescription != null)
        {
            buff.append(typeDescription).append("\n\n");
        }

        final JSONClassInfo info = TypeAnalyzer.getClassInfo(objectSupport, cls);

        buff.append("name | type | description \n");
        buff.append("-----|------|-------------\n");

        List<Prop> props = new ArrayList<>();

        for (JSONPropertyInfo propertyInfo : info.getPropertyInfos())
        {
            if (propertyInfo.isIgnore())
            {
                continue;
            }

            final Method getterMethod = ((JavaObjectPropertyInfo) propertyInfo).getGetterMethod();
            final Method setterMethod = ((JavaObjectPropertyInfo) propertyInfo).getSetterMethod();
            Type type;
            if (getterMethod != null)
            {
                type = getterMethod.getGenericReturnType();
            }
            else
            {
                type = setterMethod.getGenericParameterTypes()[0];
            }


            final String name = propertyInfo.getJsonName();
            String description = typeDoc == null ? "" : findFieldDocs(typeDoc, name);
            final String typeDesc = describe(propertyInfo.getType(), propertyInfo.getTypeHint(), type, cfg, ctx, false);

            props.add(
                new Prop(name, typeDesc, description)
            );
        }

        //props.sort(Comparator.comparing(Prop::getName));


        for (Prop prop : props)
        {
            buff.append(prop.getName()).append(" | ")
                .append(prop.getTypeDesc()).append(" | ")
                .append(prop.getDescription().replace('\n', ' ')).append("\n");
        }
    }

    public Map<String, TypeDoc> extract(SourceRoot sourceRoot, Set<String> basePackages) throws IOException
    {
        final Map<String,TypeDoc> docs = new TreeMap<>();
        for (String pkg : basePackages)
        {
            sourceRoot.parse(pkg, (localPath, absolutePath, result) -> {

                if (result.isSuccessful())
                {
                    extract(result, docs);
                }

                return SourceRoot.Callback.Result.DONT_SAVE;
            });
        }

        return docs;
    }


    private void extract(
        ParseResult<CompilationUnit> result,
        Map<String, TypeDoc> docs
    )
    {

        final CompilationUnit unit = result.getResult().get();

        if (unit.getTypes().size() > 0)
        {
            TypeDeclaration<?> typeDecl = unit.getType(0);

            extractPojoDocumentation(typeDecl, docs);
        }
    }

    static String cleanDescription(String text)
    {
        return text.replaceAll("\\{@link\\s*(.*?)}", "$1");
    }


    private void extractPojoDocumentation(
        TypeDeclaration<?> typeDecl,
        Map<String, TypeDoc> docs
    )
    {
        final Optional<String> fullyQualifiedName = typeDecl.getFullyQualifiedName();
        if (!fullyQualifiedName.isPresent())
        {
            return;
        }

        final TypeDoc typeDoc = new TypeDoc(fullyQualifiedName.get());
        if (typeDecl.getJavadoc().isPresent())
        {
            final String description = cleanDescription(typeDecl.getJavadoc().get().getDescription().toText());
            typeDoc.setDescription(description);
        }

        final List<FieldDoc> fieldDocs = new ArrayList<>();
        if (typeDecl.isEnumDeclaration())
        {
            EnumDeclaration enumDeclaration = (EnumDeclaration) typeDecl;

            for (EnumConstantDeclaration decl : enumDeclaration.getEntries())
            {
                if (decl.getJavadoc().isPresent())
                {
                    final String description = decl.getJavadoc().get().getDescription().toText();

                    fieldDocs.add(
                        new FieldDoc(decl.getName().getIdentifier(), description)
                    );
                }
            }
        }
        else
        {
            final Map<String, String> fieldMap = new HashMap<>();

            for (MethodDeclaration method : typeDecl.getMethods())
            {
                final String methodName = method.getName().getIdentifier();

                final NodeList<Parameter> params = method.getParameters();

                final Optional<Javadoc> javadoc = method.getJavadoc();
                if (javadoc.isPresent())
                {
                    final String methodJavaDoc = cleanDescription(javadoc.get().getDescription().toText());
                    if (methodName.startsWith("get") && params.size() == 0)
                    {
                        final String propertyName = Introspector.decapitalize(methodName.substring(3));
                        fieldMap.put(propertyName, methodJavaDoc);
                    }
                    else if (methodName.startsWith("is") && params.size() == 0)
                    {
                        final String propertyName = Introspector.decapitalize(methodName.substring(2));
                        fieldMap.put(propertyName, methodJavaDoc);
                    }
                    else if (methodName.startsWith("set") && method.getType().isVoidType())
                    {
                        final String propertyName = Introspector.decapitalize(methodName.substring(3));
                        fieldMap.put(propertyName, methodJavaDoc);
                    }
                }
            }

            for (Map.Entry<String, String> e : fieldMap.entrySet())
            {
                final String fieldName = e.getKey();
                final String description = e.getValue();

                fieldDocs.add(
                    new FieldDoc(fieldName, description)
                );

            }

            if (typeDoc.getDescription() == null && fieldDocs.size() == 0)
            {
                return;
            }
        }

        typeDoc.setFieldDocs(fieldDocs);
        docs.put(typeDoc.getName(), typeDoc);
    }


    private SvensonDocConfig loadConfig(File inputPath) throws FileNotFoundException
    {
        return JSONParser.defaultJSONParser().parse(
            SvensonDocConfig.class,
            new InputStreamSource(
                new FileInputStream(
                    inputPath
                ),
                true
            )
        );
    }


    public static void main(String[] args)
    {
        new CommandLine(new SvensonDocGenerator()).execute(args);

    }


    public static class Prop
    {
        private final String name;

        private final String typeDesc;

        private final String description;


        public Prop(String name, String typeDesc, String description)
        {
            this.name = name;
            this.typeDesc = typeDesc;
            this.description = description;
        }


        public String getName()
        {
            return name;
        }


        public String getTypeDesc()
        {
            return typeDesc;
        }


        public String getDescription()
        {
            return description;
        }
    }

    private static class RenderContext
    {

        private final String reference;

        private final int level;

        public RenderContext(String reference, int level)
        {
            log.info("RenderContext: reference = {}, level = {}", reference, level);

            this.reference = reference;
            this.level = level;
        }


        public String getReference()
        {
            return reference;
        }


        public int getLevel()
        {
            return level;
        }
    }
}
