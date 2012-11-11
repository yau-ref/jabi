package BSON;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import static BSON.BSONElementTypes.*;

public class BSONDecoder {

    private static byte NULLBYTE = 0x00;

    public static BSONDocument decode(ByteBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("Null buffer");
        }

        BSONDocument document = new BSONDocument();

        int startPosition = buffer.position();
        int size = buffer.getInt();
        int currentPosition = buffer.position();


        byte type;
        Object value;
        String name;
        while (currentPosition - startPosition < size - 1) {
            type = buffer.get();
            name = readName(buffer);
            value = readValue(buffer, type);
            currentPosition = buffer.position();
            document.add(name, value);
        }

        if (buffer.get() != NULLBYTE) {
            //TODO: Create special exception class for situation below
            throw new IllegalStateException("End of document expected");
        }
        return document;
    }


    private static Object readValue(ByteBuffer buffer, byte type) {
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
            case BINARY:
                break;
            default:
                throw new IllegalArgumentException("Unsupported element type: " + type);
        }

        return null;
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
