package org.jai.BSON;

import java.lang.reflect.Array;
import java.util.List;

public class BSONArray extends BSONDocument {

    private int i = 0;

    public BSONArray() {
    }

    public BSONArray(Object array) {
        if (array instanceof List) {
            List<Object> list = (List<Object>) array;
            for (Object o : list) {
                add(o);
            }
        } else if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
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
