package org.jai.BSON;

public class BSONArray extends BSONDocument {

    private int i = 0;

    public BSONDocument add(Object value) {
        return add(Integer.toString(i++), value);
    }
}
