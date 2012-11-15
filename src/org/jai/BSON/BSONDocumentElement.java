package org.jai.BSON;

public class BSONDocumentElement {

    private final String name;
    private final Object value;
    private final short type;

    public BSONDocumentElement(String name, Object value, short type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }


    public short getType() {
        return type;
    }
}
