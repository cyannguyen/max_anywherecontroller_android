package com.ibm.maximo.pushnotification;

import android.net.ParseException;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class  MaximoUtil {

    public static byte[] getByteArrayFromInputStream(InputStream inputStream) throws java.io.IOException
    {
        BufferedInputStream bufferedinputstream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        byte readbyte[] = new byte[2048];
        while(i != -1)
        {
            try
            {
                i = bufferedinputstream.read(readbyte);
            }
            catch(EOFException ex)
            {
                i = -1;
            }
            if(i > 0)
                byteArrayOutputStream.write(readbyte, 0, i);
        }
        byte[] responseData = byteArrayOutputStream.toByteArray();
        if(bufferedinputstream != null)
            bufferedinputstream.close();
        if(byteArrayOutputStream != null)
            byteArrayOutputStream.close();

        return responseData;
    }


}
