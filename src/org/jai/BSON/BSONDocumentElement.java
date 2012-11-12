package org.jai.BSON;

public class BSONDocumentElement {

    private String name;
    private Object value;
    private byte type;

    public BSONDocumentElement(String name, Object value, byte type) {
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


    public byte getType() {
        return type;
    }
}
