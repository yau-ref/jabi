package org.jai.BSON;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.jai.BSON.BSONElementTypes.*;

public class BSONDocument implements Iterable<BSONDocumentElement> {

    private List<BSONDocumentElement> elementList;

    public BSONDocument() {
        this.elementList = new LinkedList<>();
    }

    private short classify(Object n) {

        if (n == null) {
            return NULL;
        }
        if (n instanceof Integer || n instanceof Short || n instanceof Byte) {
            return INT32;
        }

        if (n instanceof Long || n instanceof AtomicLong) {
            return INT64;
        }

        if (n instanceof Float || n instanceof Double) {
            return FLOATING_POINT;
        }

        if (n instanceof Boolean) {
            return BOOLEAN;
        }

        if (n instanceof String || n instanceof Character) {
            return STRING;
        }

        if (n instanceof BSONDocument /* || is array */) {
            return DOCUMENT;
        }

        if (n instanceof Date) {
            return DATE;
        }

        if (n instanceof ByteBuffer) {//Any binary data
            return BINARY_GENERIC;
        }

        return -1;
    }

    public BSONDocument add(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("Element name couldn't be null");
        }

        if (exist(name)) {
            throw new IllegalArgumentException("Element already in list");
        }

        short type = classify(value);

        if (type < 0) {
            throw new IllegalArgumentException("Unsupported element type");
        }

        elementList.add(new BSONDocumentElement(name, value, type));

        return this;
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
        if (p == null) {
            throw new IllegalArgumentException("Document does not contains element: " + name);
        }
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

    public BSONDocument clear() {
        elementList = new LinkedList<>();
        return this;
    }

    public BSONDocument remove(String name) {
        BSONDocumentElement p = find(name);
        if (p != null) {
            elementList.remove(p);
        }
        return this;
    }

    @Override
    public Iterator<BSONDocumentElement> iterator() {
        return elementList.iterator();
    }

}
