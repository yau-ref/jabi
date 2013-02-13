package org.jai.BSON;

import java.lang.reflect.Array;

public class BSONArray extends BSONDocument {

    private int i = 0;

    public BSONArray() {
    }

    public BSONArray(Object array) {
        if (array.getClass().isArray()) {
            int size = Array.getLength(array);
            for (int i = 0; i < size; i++) {
                add(Array.get(array, i));
            }
        } else {
            add(array);
        }
    }

    public BSONArray add(Object value) {
        add(Integer.toString(i++), value);
        return this;
    }

    public Object get(int i) {
        return super.get(String.valueOf(i));
    }
}
