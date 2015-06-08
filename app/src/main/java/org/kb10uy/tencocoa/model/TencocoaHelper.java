package org.kb10uy.tencocoa.model;

import org.joda.time.DateTime;
import org.joda.time.Duration;

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
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kb10uy on 2015/06/02.
 */
public class TencocoaHelper {
    public static final char numberSuffixes[] = new char[]{' ', 'K', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'};

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

    public static String getCompressedNumberString(int num) {
        DecimalFormat df = new DecimalFormat("0.0");
        double tg = num;
        int si = 0;
        while (tg > 1000) {
            tg /= 1000.0;
            si++;
        }
        if ((int) tg == num) {
            df = new DecimalFormat("0");
        } else {
            df = new DecimalFormat("0.0");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(df.format(tg));
        sb.append(numberSuffixes[si]);
        return sb.toString();
    }

    public static String getRelativeTimeString(Date targetDate) {
        Duration d = new Duration(new DateTime(targetDate), new DateTime(new Date()));
        long relative = d.getStandardSeconds();
        if (relative < 20) return "now";
        if (relative < 60) return Long.toString(relative) + "s";
        if ((relative = d.getStandardMinutes()) < 60) return Long.toString(relative) + "m";
        if ((relative = d.getStandardHours()) < 24) return Long.toString(relative) + "h";
        relative = d.getStandardDays();
        return Long.toString(relative) + "d";
    }
}
