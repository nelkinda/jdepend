package jdepend.framework;

class Constant {
    private final byte tag;
    private final int nameIndex;
    private final int typeIndex;
    private Object value;

    Constant(final byte tag, final int nameIndex) {
        this(tag, nameIndex, -1);
    }

    Constant(final byte tag, final Object value) {
        this(tag, -1, -1);
        this.value = value;
    }

    Constant(final byte tag, final int nameIndex, final int typeIndex) {
        this.tag = tag;
        this.nameIndex = nameIndex;
        this.typeIndex = typeIndex;
        value = null;
    }

    byte getTag() {
        return tag;
    }

    int getNameIndex() {
        return nameIndex;
    }

    int getTypeIndex() {
        return typeIndex;
    }

    Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append("tag: ").append(getTag());
        if (getNameIndex() > -1) {
            s.append(" nameIndex: ").append(getNameIndex());
        }
        if (getTypeIndex() > -1) {
            s.append(" typeIndex: ").append(getTypeIndex());
        }
        if (getValue() != null) {
            s.append(" value: ").append(getValue());
        }
        return s.toString();
    }
}
