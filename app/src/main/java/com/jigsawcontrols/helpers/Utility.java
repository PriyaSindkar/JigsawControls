package com.jigsawcontrols.helpers;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Priya on 9/12/2015.
 */
public class Utility {

    public static String returnBas64Image(Bitmap thumbnail){
        //complete code to save image on server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String  encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return  encodedImage;
    }
}
