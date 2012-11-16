package org.jai.BSON;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import static org.jai.BSON.BSONElementTypes.*;

public class BSONDecoder {

    private static final byte NULLBYTE = 0x00;

    public static BSONDocument decode(ByteBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("Null buffer");
        }

        BSONDocument document = new BSONDocument();

        int startPosition = buffer.position();
        int size = buffer.getInt();
        int currentPosition = buffer.position();


        short type;
        Object value;
        String name;
        while (currentPosition - startPosition < size - 1) {
            type = buffer.get();
            name = readName(buffer);
            if (type == 0x05) {
                int pos = buffer.position();
                buffer.getInt();
                type = (short) ((type << 8) + (short) buffer.get());
                buffer.position(pos);
            } else {
                type <<=  8;
            }
            value = readValue(buffer, type);
            currentPosition = buffer.position();
            document.add(name, value);
        }

        if (buffer.get() != NULLBYTE) {
            throw new IllegalStateException("End of document expected");
        }
        return document;
    }


    private static Object readValue(ByteBuffer buffer, short type) {
        switch (type) {
            case NULL:
                return null;
            case BOOLEAN:
                return buffer.get();
            case DATE:
                return new Date(buffer.getLong());
            case INT32:
                return buffer.getInt();
            case INT64:
                return buffer.getLong();
            case FLOATING_POINT:
                return buffer.getDouble();
            case STRING:
                return readString(buffer);
            case DOCUMENT:
            case ARRAY:
                return decode(buffer);
            case BINARY_GENERIC:
            case BINARY_FUNCTION:
            case BINARY_UUID:
            case BINARY_MD5:
                int size = buffer.getInt();
                buffer.get();
                byte[] b = new byte[size];
                buffer.get(b, 0, b.length);
                return  ByteBuffer.wrap(b);
            default:
                throw new IllegalArgumentException("Unsupported element type: " + type);
        }
    }

    private static String readName(ByteBuffer buffer) {
        ByteArrayOutputStream stringOStream = new ByteArrayOutputStream();
        byte temp;
        while ((temp = buffer.get()) != NULLBYTE) {
            stringOStream.write(temp);
        }
        return stringOStream.toString();
    }

    private static String readString(ByteBuffer buffer) {
        int length = buffer.getInt() - 1;
        byte[] stringBytes = new byte[length];
        buffer.get(stringBytes, 0, length);
        buffer.get();
        return new String(stringBytes);
    }

}
