package com.teamgogoal.utils;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.extern.slf4j.Slf4j;
import tech.gusavila92.apache.commons.codec.binary.Hex;

public class GzipUtils {

    private static final int BUFFER_SIZE = 32;

    public static String compress(String content) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(content.length());
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(content.getBytes());
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();
            return Hex.encodeHexString(compressed);
//            return new String(compressed, StandardCharsets.UTF_8);
        } catch(Exception e) {
            Log.e("GzipUtils", "compress failure:" + e.toString());
            return "";
        }
    }

    public static String decompress(String content) {
        try {
            byte[] compressed =new BigInteger(content,16).toByteArray();
//            byte[] compressed = content.getBytes("utf-8");
            ByteArrayInputStream is = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
            StringBuilder string = new StringBuilder();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                string.append(new String(data, 0, bytesRead));
            }
            gis.close();
            is.close();
            return string.toString();
        } catch(Exception e) {
            Log.e("GzipUtils", "decompress failure:" + e.toString());
            return "";
        }
    }

    public static void main(String[] args) {
        String s = "1122334455";

        String a = compress(s);

        System.out.println(decompress(a));
    }
}
