package com.roommate.find.common;

import android.graphics.Typeface;
import android.os.Environment;

import java.io.File;

public class AppConstants
{
	public static int DEVICE_DISPLAY_WIDTH;
	public static int DEVICE_DISPLAY_HEIGHT;

	public static final String GILL_SANS_TYPE_FACE			= "Gill Sans";
	public static final String MONTSERRAT_MEDIUM_TYPE_FACE  = "montserrat_medium";
	public static Typeface tfMedium;
	public static Typeface tfRegular;
	public static String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString() + File.separator;
	public static final String INTERNET_CHECK			 	 = "InternetCheck";

	public static final String Guest					 = "G";
	public static final String User	     				 = "U";
	public static final String Tutor					 = "T";
	//tables
    public static final String Table_Users				 = "Users";
}
