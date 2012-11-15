package org.jai.BSON;

final class BSONElementTypes {
    public static final short FLOATING_POINT = 0x0100;
    public static final short STRING = 0x0200;
    public static final short DOCUMENT = 0x0300;
    public static final short ARRAY = 0x0400;
    public static final short BINARY_GENERIC = 0x0500;
    public static final short BINARY_FUNCTION = 0x0501;
    public static final short BINARY_UUID = 0x0504;
    public static final short BINARY_MD5 = 0x0505;
    public static final short BOOLEAN = 0x0800;
    public static final short DATE = 0x0900;
    public static final short NULL = 0x0A00;
    public static final short INT32 = 0x1000;
    public static final short INT64 = 0x1200;
}
