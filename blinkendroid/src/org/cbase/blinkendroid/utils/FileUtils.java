package org.cbase.blinkendroid.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.cbase.blinkendroid.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

/**
 * Utilities for creating and manipulating files on the devices sdcard.
 * @author ben
 *
 */
public class FileUtils {
    
    
    /**
     * Stores an image to the devices sdcard.
     * @param mContext The calling context.
     * @param imageData The image-data.
     * @param quality The images quality.
     * @param expName The name of the taken image.
     * @return true if everything went okay.
     */
    public static boolean storeByteImage(Context mContext, byte[] imageData,
	    int quality, String expName) {
	
	File outputFile = null;
	File outputFolder = new File(Environment.getExternalStorageDirectory() +"/data/blinkendroid/");
	
	FileOutputStream fileOutputStream = null;
	String fileName = expName;

	    try {
		//Create required folders if nonexistent.
		outputFolder.mkdirs();
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 5;

		Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
			imageData.length, options);
		outputFile = new File( outputFolder + "/"+ fileName + ".jpg");
		
		if (!outputFile.exists())
		    outputFile.createNewFile();
		
		fileOutputStream = new FileOutputStream(outputFile);
		BufferedOutputStream bos = new BufferedOutputStream(
			fileOutputStream);

		myImage.compress(CompressFormat.JPEG, quality, bos);

		bos.flush();
		bos.close();
		Log.d(Constants.LOG_TAG, "Wrote image to "
			+ outputFile.getAbsolutePath());
	    } catch (FileNotFoundException e) {
		Log.e(Constants.LOG_TAG, e.getMessage());
		e.printStackTrace();
	    } catch (IOException e) {
		Log.e(Constants.LOG_TAG, e.getMessage());
		e.printStackTrace();
	    }
	return true;
    }
    
}