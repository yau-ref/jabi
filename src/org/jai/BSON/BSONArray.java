package org.jai.BSON;

public class BSONArray extends BSONDocument {

    private int i = 0;

    public BSONArray add(Object value) {
        add(Integer.toString(i++), value);
        return this;
    }
}
