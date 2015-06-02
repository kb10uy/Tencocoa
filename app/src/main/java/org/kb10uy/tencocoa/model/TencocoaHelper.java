package org.kb10uy.tencocoa.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/**
 * Created by kb10uy on 2015/06/02.
 */
public class TencocoaHelper {
    public static boolean serializeObjectToFile(Serializable object, FileOutputStream output) {
        try {
            ObjectOutputStream serializer = new ObjectOutputStream(output);
            serializer.writeObject(object);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> T deserializeObjectFromFile(FileInputStream input) {
        try {
            ObjectInputStream deserializer = new ObjectInputStream(input);
            T value = (T) deserializer.readObject();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
