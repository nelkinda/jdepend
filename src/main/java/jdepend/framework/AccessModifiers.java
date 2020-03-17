package jdepend.framework;

/**
 * Access Modifiers.
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se14/jvms14.pdf">The Java® Virtual Machine Specification,
 *      Table 4.1-B. Class access and property modifiers</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se14/jvms14.pdf">The Java® Virtual Machine Specification,
 *      Table 4.5-A. Field access and property modifiers</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se14/jvms14.pdf">The Java® Virtual Machine Specification,
 *      Table 4.6-A. Method access and property modifiers</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se14/jvms14.pdf">The Java® Virtual Machine Specification,
 *      Table 4.7.6-A. Nested class access and property modifiers</a>
 */
public class AccessModifiers {
    public static final int ACC_PUBLIC = 0x0001;
    public static final int ACC_PRIVATE = 0x0002;
    public static final int ACC_PROTECTED = 0x0004;
    public static final int ACC_STATIC = 0x0008;
    public static final int ACC_FINAL = 0x0010;
    public static final int ACC_SUPER = 0x0020;
    public static final int ACC_SYNCHRONIZED = 0x0020;
    public static final int ACC_OPEN = 0x0020;
    public static final int ACC_TRANSITIVE = 0x0020;
    public static final int ACC_VOLATILE = 0x0040;
    public static final int ACC_BRIDGE = 0x0040;
    public static final int ACC_STATIC_PHASE = 0x0040;
    public static final int ACC_TRANSIENT = 0x0080;
    public static final int ACC_VARARGS = 0x0080;
    public static final int ACC_NATIVE = 0x100;
    public static final int ACC_INTERFACE = 0x200;
    public static final int ACC_ABSTRACT = 0x400;
    public static final int ACC_STRICT = 0x800;
    public static final int ACC_SYNTHETIC = 0x1000;
    public static final int ACC_ANNOTATION = 0x2000;
    public static final int ACC_ENUM = 0x4000;
    public static final int ACC_MODULE = 0x8000;
    public static final int ACC_MANDATED = 0x8000;
}
