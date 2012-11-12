package org.jai.BSON;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class BSONDocument implements Iterable<BSONDocumentElement> {

    private List<BSONDocumentElement> elementList;

    public BSONDocument() {
        this.elementList = new LinkedList<>();
    }

    public void add(String name, Object value) {
        if (exist(name)) {
            throw new IllegalArgumentException("Element already in list");
        }

        byte type = classificate(value);
        if (type < 0) {
            throw new IllegalArgumentException("Unsupported element type");
        }

        elementList.add(new BSONDocumentElement(name, value, type));
    }


    private byte classificate(Object n) {

        if (n == null) {
            return BSONElementTypes.NULL;
        }

        //ARRAY
        //BINARY


        if (n instanceof Integer || n instanceof Short || n instanceof Byte) {
            return BSONElementTypes.INT32;
        }

        if (n instanceof Long || n instanceof AtomicLong) {
            return BSONElementTypes.INT64;
        }

        if (n instanceof Float || n instanceof Double) {
            return BSONElementTypes.FLOATING_POINT;
        }

        if (n instanceof Boolean) {
            return BSONElementTypes.BOOLEAN;
        }

        if (n instanceof String || n instanceof Character) {
            return BSONElementTypes.STRING;
        }

        if (n instanceof BSONDocument) {
            return BSONElementTypes.DOCUMENT;
        }

        if (n instanceof Date) {
            return BSONElementTypes.DATE;
        }

        return -1;
    }

    private BSONDocumentElement find(String name) {
        for (BSONDocumentElement p : elementList) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public Object get(String name) {
        BSONDocumentElement p = find(name);
        return p.getValue();
    }

    public boolean exist(String name) {
        return find(name) != null;
    }

    public int size() {
        return elementList.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        elementList = new LinkedList<>();
    }

    public void remove(String name) {
        BSONDocumentElement p = find(name);
        if (p != null) {
            elementList.remove(p);
        }
    }

    @Override
    public Iterator<BSONDocumentElement> iterator() {
        return elementList.iterator();
    }

}