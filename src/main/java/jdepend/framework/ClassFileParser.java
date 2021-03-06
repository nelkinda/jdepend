package jdepend.framework;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;
import static jdepend.framework.ConstantPoolTags.*;

/**
 * The <code>ClassFileParser</code> class is responsible for
 * parsing a Java class file to create a <code>JavaClass</code>
 * instance.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class ClassFileParser extends AbstractParser {

    public static final int JAVA_MAGIC = 0xCAFEBABE;

    public static final char CLASS_DESCRIPTOR = 'L';
    public static final Logger logger = getLogger(ClassFileParser.class.getPackageName());

    private String fileName;
    private String className;
    private String superClassName;
    private String[] interfaceNames;
    private boolean isAbstract;
    private JavaClass javaClass;
    private Constant[] constantPool;
    private FieldOrMethodInfo[] fields;
    private FieldOrMethodInfo[] methods;
    private AttributeInfo[] attributes;
    private DataInputStream in;


    public ClassFileParser() {
        this(new PackageFilter());
    }

    public ClassFileParser(final PackageFilter filter) {
        super(filter);
        reset();
    }

    /**
     * Test main.
     */
    public static void main(final String... args) {
        try {
            if (args.length <= 0) {
                logger.warning("usage: ClassFileParser <class-file>");
                System.exit(0);
            }

            final ClassFileParser parser = new ClassFileParser();
            parser.parse(new File(args[0]));
            logger.info(parser.toString());
        } catch (final Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private static String[] descriptorToTypes(final String descriptor) {
        int typesCount = 0;
        for (int index = 0; index < descriptor.length(); index++) {
            if (descriptor.charAt(index) == ';') {
                typesCount++;
            }
        }

        final String[] types = new String[typesCount];

        int typeIndex = 0;
        for (int index = 0; index < descriptor.length(); index++) {

            final int startIndex = descriptor.indexOf(CLASS_DESCRIPTOR, index);
            if (startIndex < 0) {
                break;
            }

            index = descriptor.indexOf(';', startIndex + 1);
            types[typeIndex++] = descriptor.substring(startIndex + 1, index);
        }

        return types;
    }

    /**
     * Resets the parser back to its vanilla state.
     * Must be called prior to actually parsing a class.
     */
    private void reset() {
        className = null;
        superClassName = null;
        interfaceNames = new String[0];
        isAbstract = false;

        javaClass = null;
        constantPool = new Constant[1];
        fields = new FieldOrMethodInfo[0];
        methods = new FieldOrMethodInfo[0];
        attributes = new AttributeInfo[0];
    }

    /**
     * Parses the class from the specified file.
     * Registered parser listeners are informed that the resulting
     * <code>JavaClass</code> was parsed.
     *
     * @throws IOException in case of I/O problems parsing the file.
     */
    public JavaClass parse(final File classFile) throws IOException {
        this.fileName = classFile.getCanonicalPath();

        logger.fine("\nParsing " + fileName + "...");

        try (InputStream in = new BufferedInputStream(Files.newInputStream(classFile.toPath()))) {
            return parse(in);
        }
    }

    /**
     * Parses the class from the specified {@link InputStream}.
     *
     * @param inputStream InputStream from which to parse the class.
     * @return {@code JavaClass} from parsing {@code inputStream}.
     * @throws IOException in case of I/O problems parsing the {@link InputStream}.
     */
    @Override
    public JavaClass parse(final InputStream inputStream) throws IOException {
        reset();

        javaClass = new JavaClass("Unknown");

        in = new DataInputStream(inputStream);

        final int magic = parseMagic();

        final int minorVersion = parseMinorVersion();
        final int majorVersion = parseMajorVersion();

        constantPool = parseConstantPool();

        parseAccessFlags();

        className = parseClassName();

        superClassName = parseSuperClassName();

        interfaceNames = parseInterfaces();

        fields = parseFields();

        methods = parseMethods();

        parseAttributes();

        addClassConstantReferences();

        addAnnotationsReferences();

        onParsedJavaClass(javaClass);

        return javaClass;
    }

    private int parseMagic() throws IOException {
        final int magic = in.readInt();
        if (magic != JAVA_MAGIC) {
            throw new IOException("Invalid class file: " + fileName);
        }

        return magic;
    }

    private int parseMinorVersion() throws IOException {
        return in.readUnsignedShort();
    }

    private int parseMajorVersion() throws IOException {
        return in.readUnsignedShort();
    }

    private Constant[] parseConstantPool() throws IOException {
        final int constantPoolSize = in.readUnsignedShort();
        final Constant[] pool = new Constant[constantPoolSize];

        for (int i = 1; i < constantPoolSize; i += getEntrySize(pool[i])) {
            pool[i] = parseNextConstant();
        }

        return pool;
    }

    private int getEntrySize(final Constant constant) {
        // TODO use a type map which is easier to extend for large arrays coming in one of the next JDK versions.
        return isDoubleSizeEntry(constant) ? 2 : 1;
    }

    private boolean isDoubleSizeEntry(final Constant constant) {
        return constant.getTag() == CONSTANT_DOUBLE || constant.getTag() == CONSTANT_LONG;
    }

    private void parseAccessFlags() throws IOException {
        final int accessFlags = in.readUnsignedShort();

        final boolean isAbstractClass = (accessFlags & AccessModifiers.ACC_ABSTRACT) != 0;
        final boolean isInterface = (accessFlags & AccessModifiers.ACC_INTERFACE) != 0;

        this.isAbstract = isAbstractClass || isInterface;
        javaClass.isAbstract(this.isAbstract);

        logger.fine("Parser: abstract = " + this.isAbstract);
    }

    private String parseClassName() throws IOException {
        final int entryIndex = in.readUnsignedShort();
        final String className = getClassConstantName(entryIndex);
        javaClass.setClassName(className);
        javaClass.setPackageName(getPackageName(className));

        logger.fine("Parser: class name = " + className);
        logger.fine("Parser: package name = " + getPackageName(className));

        return className;
    }

    private String parseSuperClassName() throws IOException {
        final int entryIndex = in.readUnsignedShort();
        final String superClassName = getClassConstantName(entryIndex);
        addImport(getPackageName(superClassName));

        logger.fine("Parser: super class name = " + superClassName);

        return superClassName;
    }

    private String[] parseInterfaces() throws IOException {
        final int interfacesCount = in.readUnsignedShort();
        final String[] interfaceNames = new String[interfacesCount];
        for (int i = 0; i < interfacesCount; i++) {
            final int entryIndex = in.readUnsignedShort();
            interfaceNames[i] = getClassConstantName(entryIndex);
            addImport(getPackageName(interfaceNames[i]));

            logger.fine("Parser: interface = " + interfaceNames[i]);
        }

        return interfaceNames;
    }

    private FieldOrMethodInfo[] parseFields() throws IOException {
        final int fieldsCount = in.readUnsignedShort();
        final FieldOrMethodInfo[] fields = new FieldOrMethodInfo[fieldsCount];
        for (int i = 0; i < fieldsCount; i++) {
            fields[i] = parseFieldOrMethodInfo();
            final String descriptor = toUtf8(fields[i].getDescriptorIndex());
            logger.fine("Parser: field descriptor = " + descriptor);
            final String[] types = descriptorToTypes(descriptor);
            for (final String type : types) {
                addImport(getPackageName(type));
                logger.fine("Parser: field type = " + type);
            }
        }

        return fields;
    }

    private FieldOrMethodInfo[] parseMethods() throws IOException {
        final int methodsCount = in.readUnsignedShort();
        final FieldOrMethodInfo[] methods = new FieldOrMethodInfo[methodsCount];
        for (int i = 0; i < methodsCount; i++) {
            methods[i] = parseFieldOrMethodInfo();
            final String descriptor = toUtf8(methods[i].getDescriptorIndex());
            logger.fine("Parser: method descriptor = " + descriptor);
            final String[] types = descriptorToTypes(descriptor);
            for (final String type : types) {
                if (type.length() > 0) {
                    addImport(getPackageName(type));
                    logger.fine("Parser: method type = " + type);
                }
            }
        }
        return methods;
    }

    private Constant parseNextConstant() throws IOException {
        final byte tag = in.readByte();

        switch (tag) {
        case CONSTANT_CLASS:
        case CONSTANT_STRING:
        case CONSTANT_METHOD_TYPE:
            return new Constant(tag, in.readUnsignedShort());
        case CONSTANT_FIELD:
        case CONSTANT_METHOD:
        case CONSTANT_INTERFACEMETHOD:
        case CONSTANT_NAMEANDTYPE:
        case CONSTANT_INVOKEDYNAMIC:
            return new Constant(tag, in.readUnsignedShort(), in.readUnsignedShort());
        case CONSTANT_INTEGER:
            return new Constant(tag, (Object) in.readInt());
        case CONSTANT_FLOAT:
            return new Constant(tag, in.readFloat());
        case CONSTANT_LONG:
            return new Constant(tag, in.readLong());
        case CONSTANT_DOUBLE:
            return new Constant(tag, in.readDouble());
        case CONSTANT_UTF8:
            return new Constant(tag, in.readUTF());
        case CONSTANT_METHOD_HANDLE:
            return new Constant(tag, in.readByte(), in.readUnsignedShort());
        default:
            throw new IOException("Unknown constant: " + tag);
        }
    }

    private FieldOrMethodInfo parseFieldOrMethodInfo() throws IOException {
        final FieldOrMethodInfo result = new FieldOrMethodInfo(
                in.readUnsignedShort(), in.readUnsignedShort(), in
                .readUnsignedShort());

        final int attributesCount = in.readUnsignedShort();
        for (int a = 0; a < attributesCount; a++) {
            final AttributeInfo attribute = parseAttribute();
            if ("RuntimeVisibleAnnotations".equals(attribute.name)) {
                result.runtimeVisibleAnnotations = attribute;
            }
        }

        return result;
    }

    private void parseAttributes() throws IOException {
        final int attributesCount = in.readUnsignedShort();
        attributes = new AttributeInfo[attributesCount];

        for (int i = 0; i < attributesCount; i++) {
            attributes[i] = parseAttribute();

            // Section 4.7.7 of VM Spec - Class File Format
            if ("SourceFile".equals(attributes[i].getName())) {
                final byte[] b = attributes[i].getValue();
                final int b0 = b[0] < 0 ? b[0] + 256 : b[0];
                final int b1 = b[1] < 0 ? b[1] + 256 : b[1];
                final int pe = b0 * 256 + b1;

                final String descriptor = toUtf8(pe);
                javaClass.setSourceFile(descriptor);
            }
        }
    }

    private AttributeInfo parseAttribute() throws IOException {
        final int nameIndex = in.readUnsignedShort();
        final String name = toUtf8(nameIndex);

        final int attributeLength = in.readInt();
        final byte[] value = new byte[attributeLength];
        for (int b = 0; b < attributeLength; b++) {
            value[b] = in.readByte();
        }

        return new AttributeInfo(name, value);
    }

    private Constant getConstantPoolEntry(final int entryIndex) throws IOException {
        if (entryIndex < 0 || entryIndex >= constantPool.length) {
            throw new IOException("Illegal constant pool index : " + entryIndex);
        }
        return constantPool[entryIndex];
    }

    private void addClassConstantReferences() throws IOException {
        for (int j = 1; j < constantPool.length; j += getEntrySize(constantPool[j])) {
            if (constantPool[j].getTag() == CONSTANT_CLASS) {
                final String name = toUtf8(constantPool[j].getNameIndex());
                addImport(getPackageName(name));

                logger.fine("Parser: class type = " + slashesToDots(name));
            }
        }
    }

    private void addAnnotationsReferences() throws IOException {
        addAttributeAnnotationReferences();
        addFieldAnnotationReferences();
        addMethodAnnotationReferences();
    }

    private void addAttributeAnnotationReferences() throws IOException {
        // TODO Understand why the first one is skipped.
        for (int j = 1; j < attributes.length; j++) {
            if ("RuntimeVisibleAnnotations".equals(attributes[j].name)) {
                addAnnotationReferences(attributes[j]);
            }
        }
    }

    private void addFieldAnnotationReferences() throws IOException {
        // TODO Understand why the first one is skipped.
        for (int j = 1; j < fields.length; j++) {
            if (fields[j].runtimeVisibleAnnotations != null) {
                addAnnotationReferences(fields[j].runtimeVisibleAnnotations);
            }
        }
    }

    private void addMethodAnnotationReferences() throws IOException {
        // TODO Understand why the first one is skipped.
        for (int j = 1; j < methods.length; j++) {
            if (methods[j].runtimeVisibleAnnotations != null) {
                addAnnotationReferences(methods[j].runtimeVisibleAnnotations);
            }
        }
    }

    private void addAnnotationReferences(final AttributeInfo annotation) throws IOException {
        // JVM Spec 4.8.15
        final byte[] data = annotation.value;
        final int numAnnotations = u2(data, 0);
        final int annotationIndex = 2;
        addAnnotationReferences(data, annotationIndex, numAnnotations);
    }

    private int addAnnotationReferences(final byte[] data, int index, final int numAnnotations) throws IOException {
        int visitedAnnotations = 0;
        while (visitedAnnotations < numAnnotations) {
            final int typeIndex = u2(data, index);
            final int numElementValuePairs = u2(data, index += 2);
            addImport(getPackageName(toUtf8(typeIndex).substring(1)));
            int visitedElementValuePairs = 0;
            index += 2;
            while (visitedElementValuePairs < numElementValuePairs) {
                index = addAnnotationElementValueReferences(data, index + 2);
                visitedElementValuePairs++;
            }
            visitedAnnotations++;
        }
        return index;
    }

    private int addAnnotationElementValueReferences(final byte[] data, int index) throws IOException {
        final byte tag = data[index];
        index += 1;
        switch (tag) {
        case 'B': case 'C': case 'D': case 'F': case 'I': case 'J': case 'S': case 'Z': case 's':
            index += 2;
            break;

        case 'e':
            final int enumTypeIndex = u2(data, index);
            addImport(getPackageName(toUtf8(enumTypeIndex).substring(1)));
            index += 4;
            break;

        case 'c':
            final int classInfoIndex = u2(data, index);
            addImport(getPackageName(toUtf8(classInfoIndex).substring(1)));
            index += 2;
            break;

        case '@':
            index = addAnnotationReferences(data, index, 1);
            break;

        case '[':
            final int numValues = u2(data, index);
            index = index + 2;
            for (int i = 0; i < numValues; i++) {
                index = addAnnotationElementValueReferences(data, index);
            }
            break;
        default:
            logger.warning("Unexpected class file component tag: '" + (char) tag + "'");
        }
        return index;
    }

    private int u2(final byte[] data, final int index) {
        return data[index] << 8 & 0xFF00 | data[index + 1] & 0xFF;
    }

    private String getClassConstantName(final int entryIndex) throws IOException {
        final Constant entry = getConstantPoolEntry(entryIndex);
        if (entry == null) {
            return "";
        }
        return slashesToDots(toUtf8(entry.getNameIndex()));
    }

    private String toUtf8(final int entryIndex) throws IOException {
        final Constant entry = getConstantPoolEntry(entryIndex);
        if (entry.getTag() == CONSTANT_UTF8) {
            return (String) entry.getValue();
        }
        throw new IOException("Constant pool entry is not a UTF8 type: " + entryIndex);
    }

    private void addImport(final String importPackage) {
        if (importPackage != null && getFilter().accept(importPackage)) {
            javaClass.addImportedPackage(new JavaPackage(importPackage));
        }
    }

    private String slashesToDots(final String s) {
        return s.replace('/', '.');
    }

    private String getPackageName(String s) {
        if (s.length() > 0 && s.charAt(0) == '[') {
            final String[] types = descriptorToTypes(s);
            if (types.length == 0) {
                return null; // primitives
            }

            s = types[0];
        }

        s = slashesToDots(s);
        final int index = s.lastIndexOf('.');
        if (index > 0) {
            return s.substring(0, index);
        }

        return "Default";
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation.
     */
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        try {
            s.append("\n").append(className).append(":\n");

            s.append("\nConstants:\n");
            for (int i = 1; i < constantPool.length; i++) {
                final Constant entry = getConstantPoolEntry(i);
                s.append("    ").append(i).append(". ").append(entry.toString()).append("\n");
                if (isDoubleSizeEntry(entry)) {
                    i++;
                }
            }

            s.append("\nClass Name: ").append(className).append("\n");
            s.append("Super Name: ").append(superClassName).append("\n\n");

            s.append(interfaceNames.length).append(" interfaces\n");
            for (final String interfaceName : interfaceNames) {
                s.append("    ").append(interfaceName).append("\n");
            }

            s.append("\n").append(fields.length).append(" fields\n");
            for (final FieldOrMethodInfo field : fields) {
                s.append(field.toString()).append("\n");
            }

            s.append("\n").append(methods.length).append(" methods\n");
            for (final FieldOrMethodInfo method : methods) {
                s.append(method.toString()).append("\n");
            }

            s.append("\nDependencies:\n");
            for (final JavaPackage jPackage : javaClass.getImportedPackages()) {
                s.append("    ").append(jPackage.getName()).append("\n");
            }

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return s.toString();
    }

    class FieldOrMethodInfo {
        private final int accessFlags;
        private final int nameIndex;
        private final int descriptorIndex;
        private AttributeInfo runtimeVisibleAnnotations;

        FieldOrMethodInfo(final int accessFlags, final int nameIndex, final int descriptorIndex) {
            this.accessFlags = accessFlags;
            this.nameIndex = nameIndex;
            this.descriptorIndex = descriptorIndex;
        }

        int accessFlags() {
            return accessFlags;
        }

        int getNameIndex() {
            return nameIndex;
        }

        int getDescriptorIndex() {
            return descriptorIndex;
        }

        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder();
            try {
                s
                        .append("\n    name (#")
                        .append(getNameIndex())
                        .append(") = ")
                        .append(toUtf8(getNameIndex()));
                s
                        .append("\n    signature (#")
                        .append(getDescriptorIndex())
                        .append(") = ")
                        .append(toUtf8(getDescriptorIndex()));
                final String[] types = descriptorToTypes(toUtf8(getDescriptorIndex()));
                for (final String type : types) {
                    s.append("\n        type = ").append(type);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return s.toString();
        }
    }
}
