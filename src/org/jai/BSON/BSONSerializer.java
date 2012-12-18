package org.jai.BSON;

import java.lang.reflect.Field;
import java.util.Date;

public class BSONSerializer {
    public static BSONDocument serialize(Object o) {
        if (o == null) {
            return null;
        }

        BSONDocument serializedObject = new BSONDocument();
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                BSONSerializable annotation = field.getAnnotation(BSONSerializable.class);
                if (annotation != null) {
                    String fieldName = annotation.name().equals("") ? field.getName() : annotation.name();
                    Object fieldValue = null;

                    field.setAccessible(true);

                    if (field.getType().equals(null)) {
                        return null;
                    }
                    if (field.getType().equals(int.class)) {
                        fieldValue = field.getInt(o);
                    } else if (field.getType().equals(boolean.class)) {
                        fieldValue = field.getBoolean(o);
                    } else if (field.getType().equals(byte.class)) {
                        fieldValue = field.getByte(o);
                    } else if (field.getType().equals(char.class)) {
                        fieldValue = field.getChar(o);
                    } else if (field.getType().equals(double.class)) {
                        fieldValue = field.getDouble(o);
                    } else if (field.getType().equals(short.class)) {
                        fieldValue = field.getShort(o);
                    } else if (field.getType().equals(long.class)) {
                        fieldValue = field.getLong(o);
                    } else if (field.getType().equals(float.class)) {
                        fieldValue = field.getFloat(o);
                    } else if (field.getType().equals(String.class) ||
                            field.getType().equals(Integer.class) ||
                            field.getType().equals(Boolean.class
                            ) ||
                            field.getType().equals(Byte.class) ||
                            field.getType().equals(Character.class) ||
                            field.getType().equals(Double.class) ||
                            field.getType().equals(Short.class) ||
                            field.getType().equals(Long.class) ||
                            field.getType().equals(Float.class) ||
                            field.getType().equals(Date.class) ||
                            field.getType().equals(BSONArray.class) ||
                            field.getType().equals(BSONDocument.class)) {

                        fieldValue = field.get(o);
                    } else {
                        fieldValue = serialize(field.get(o));
                    }

                    field.setAccessible(false);

                    serializedObject.add(fieldName, fieldValue);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return serializedObject;
    }
}
