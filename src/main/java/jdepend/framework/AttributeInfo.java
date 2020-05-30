package jdepend.framework;

class AttributeInfo {
    final String name;
    final byte[] value;

    AttributeInfo(final String name, final byte[] value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public byte[] getValue() {
        return this.value;
    }
}
