package com.html.gmbrdilos.airhockey.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextResourceReader {

    //A method to read in text from a resource
    public static String readTextFileFromResource(Context context, // The Android context is required in order to access the resources.
                                                  int resourceId) {


        StringBuilder body = new StringBuilder();

        try {

            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {

                body.append(nextLine);
                body.append('\n');

            }
        }
        catch (IOException e) {

            throw new RuntimeException("Could not open resource: " + resourceId, e);

        }
        catch (Resources.NotFoundException nfe) {

            throw new RuntimeException("Resource not found: " + resourceId, nfe);

        }

        return body.toString();
    }
}
