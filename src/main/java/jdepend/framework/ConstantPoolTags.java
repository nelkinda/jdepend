package jdepend.framework;

/**
 * Tags for constant pool entries.
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se14/jvms14.pdf">The Java® Virtual Machine Specification,
 *      Table 4.4-B. Constant pool tags (by tag)</a>
 */
public class ConstantPoolTags {
    public static final int CONSTANT_UTF8 = 1;
    public static final int CONSTANT_INTEGER = 3;
    public static final int CONSTANT_FLOAT = 4;
    public static final int CONSTANT_LONG = 5;
    public static final int CONSTANT_DOUBLE = 6;
    public static final int CONSTANT_CLASS = 7;
    public static final int CONSTANT_STRING = 8;
    public static final int CONSTANT_FIELD = 9;
    public static final int CONSTANT_METHOD = 10;
    public static final int CONSTANT_INTERFACEMETHOD = 11;
    public static final int CONSTANT_NAMEANDTYPE = 12;
    public static final int CONSTANT_METHOD_HANDLE = 15;
    public static final int CONSTANT_METHOD_TYPE = 16;
    public static final int CONSTANT_DYNAMIC = 17;
    public static final int CONSTANT_INVOKEDYNAMIC = 18;
    public static final int CONSTANT_MODULE = 19;
    public static final int CONSTANT_PACKAGE = 20;
}
