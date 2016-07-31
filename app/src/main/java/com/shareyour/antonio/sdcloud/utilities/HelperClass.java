package com.shareyour.antonio.sdcloud.utilities;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by antonio on 19/06/15.
 */
public class HelperClass {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public static String formatDate(DateTime data){

        return formatter.format(new Date(data.getValue()));
    }


}
