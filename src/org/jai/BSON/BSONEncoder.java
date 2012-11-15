package org.jai.BSON;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;

import static org.jai.BSON.BSONElementTypes.*;

public class BSONEncoder {
    private static final byte NULLBYTE = 0x00;
    private static final Charset STRINGS_CHARSET = Charset.forName("UTF-8");

    public static ByteBuffer encode(BSONDocument document) {
        return ByteBuffer.wrap(getDocumentBytes(document));
    }

    private static byte[] getDocumentBytes(BSONDocument document) {
        ByteArrayOutputStream documentOStream = new ByteArrayOutputStream();

        putInt32(documentOStream, 0);

        for (BSONDocumentElement element : document) {
            putElement(documentOStream, element);
        }

        documentOStream.write(NULLBYTE);

        int documentSize = documentOStream.size();

        byte[] documentBytes = documentOStream.toByteArray();
        documentBytes[0] = (byte) (0xFFL & (documentSize >> 24));
        documentBytes[1] = (byte) (0xFFL & (documentSize >> 16));
        documentBytes[2] = (byte) (0xFFL & (documentSize >> 8));
        documentBytes[3] = (byte) (0xFFL & (documentSize));

        return documentBytes;
    }

    private static void putDocument(ByteArrayOutputStream documentOStream, BSONDocument document) {
        byte[] documentBytes = getDocumentBytes(document);
        documentOStream.write(documentBytes, 0, documentBytes.length);
    }

    private static void putString(ByteArrayOutputStream documentOStream, String v) {
        byte[] bytes = v.getBytes(STRINGS_CHARSET);
        putInt32(documentOStream, bytes.length + 1);
        documentOStream.write(bytes, 0, bytes.length);
        documentOStream.write(NULLBYTE);
    }

    private static void putByte(ByteArrayOutputStream documentOStream, byte b) {
        documentOStream.write(b);
    }

    private static void putInt32(ByteArrayOutputStream documentOStream, int i) {
        documentOStream.write((byte) (0xFFL & (i >> 24)));
        documentOStream.write((byte) (0xFFL & (i >> 16)));
        documentOStream.write((byte) (0xFFL & (i >> 8)));
        documentOStream.write((byte) (0xFFL & (i)));
    }

    private static void putInt64(ByteArrayOutputStream documentOStream, long l) {
        documentOStream.write((byte) (0xFFL & (l >> 56)));
        documentOStream.write((byte) (0xFFL & (l >> 48)));
        documentOStream.write((byte) (0xFFL & (l >> 40)));
        documentOStream.write((byte) (0xFFL & (l >> 32)));
        documentOStream.write((byte) (0xFFL & (l >> 24)));
        documentOStream.write((byte) (0xFFL & (l >> 16)));
        documentOStream.write((byte) (0xFFL & (l >> 8)));
        documentOStream.write((byte) (0xFFL & (l)));
    }

    private static void putFloatingPoint(ByteArrayOutputStream documentOStream, double d) {
        putInt64(documentOStream, Double.doubleToRawLongBits(d));
    }

    private static void putBinary(ByteArrayOutputStream documentOStream, short type, ByteBuffer b) {
        putInt32(documentOStream, b.limit());
        putElementSubType(documentOStream, type);
        byte[] bytes = b.array();
        documentOStream.write(bytes, 0, bytes.length);
    }

    private static void putElement(ByteArrayOutputStream documentOStream, BSONDocumentElement e) {
        putElementType(documentOStream, e.getType());
        putElementName(documentOStream, e.getName());
        putElementValue(documentOStream, e.getValue(), e.getType());
    }

    private static void putElementName(ByteArrayOutputStream documentOStream, String v) {
        byte[] bytes = v.getBytes(STRINGS_CHARSET);
        documentOStream.write(bytes, 0, bytes.length);
        documentOStream.write(NULLBYTE);
    }

    private static void putElementType(ByteArrayOutputStream documentOStream, short type) {
        putByte(documentOStream, (byte) (0xFFL & type >> 8));
    }

    private static void putElementSubType(ByteArrayOutputStream documentOStream, short type) {
        putByte(documentOStream, (byte) (0xFFL & type));
    }

    private static void putElementValue(ByteArrayOutputStream documentOStream, Object value, short type) {
        switch (type) {
            case NULL:
                putByte(documentOStream, (byte) (0xFFL & NULL));
                break;
            case BOOLEAN:
                putByte(documentOStream, (byte) ((Boolean) value ? 0x01 : 0x00));
                break;
            case DATE:
                putInt64(documentOStream, ((Date) value).getTime());
                break;
            case INT32:
                putInt32(documentOStream, (Integer) value);
                break;
            case INT64:
                putInt64(documentOStream, (Long) value);
                break;
            case FLOATING_POINT:
                putFloatingPoint(documentOStream, (Double) value);
                break;
            case STRING:
                putString(documentOStream, (String) value);
                break;
            case DOCUMENT:
            case ARRAY:
                putDocument(documentOStream, (BSONDocument) value);
                break;
            case BINARY_GENERIC:
            case BINARY_FUNCTION:
            case BINARY_UUID:
            case BINARY_MD5:
                putBinary(documentOStream, type, (ByteBuffer) value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported element type");
        }
    }
}