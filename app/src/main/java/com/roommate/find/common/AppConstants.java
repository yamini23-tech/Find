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

	public static int selectedTab						 = 1;
	public static final String Guest					 = "G";
	public static final String User	     				 = "U";
	public static final String User_Type     			 = "UserType";
	public static String LoggedIn_User_Type     	     = "LoggedInUserType";
	//tables
	public static final String Table_Users				 = "Users";
	public static final String Table_Guests				 = "Guests";
	public static final String Table_Posts				 = "Posts";
	public static final String Table_Events				 = "Events";
	public static final String Table_Meetings			 = "Meetings";
	public static final String Table_Favourite  		 = "Favourite";
	public static final String Table_Rating      		 = "Rating";
	public static final String Table_Posts_Images		 = "PostImages";
	public static final String Table_Support    		 = "Support";

	public static final String Profiles_Storage_Path 	 = "Profiles/";
	public static final String Posts_Storage_Path 	 	 = "Posts/";
}
