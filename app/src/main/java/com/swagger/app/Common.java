package com.swagger.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class Common {
    public static boolean isDebugging = false;
    public static String imagePath = "";
    public static double Latitude;
    public static double Longitude;
    public static String SMS_ORIGIN="CTYLYF";

    //API Urls
    public static String baseUrl = "http://eventlog.ezoneindiaportal.com/api/";


    public static String userRegUrl= baseUrl+"RegistrationOTP";
    public static String validateOtpUserUrl= baseUrl+"UserRegistration";
    public static String validateOtpPartnerUrl= baseUrl+"PartnerRegistration";
    public static String loginUrl="http://eventlog.ezoneindiaportal.com/token";
    public static String serviceListUrl=baseUrl+"ProductCategory/GetAll";
    public static String partnerWiseProductInsert=baseUrl+"PartnerWiseProduct/Insert";

    //Firebase controls

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";



    // method to check internet connectivity
    public static boolean checkNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        try {
            if (context != null) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo[] netInfo = cm.getAllNetworkInfo();

                    if (netInfo != null) {
                        for (NetworkInfo ni : netInfo) {
                            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                                haveConnectedWifi = ni.isConnected();
                            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                                haveConnectedMobile = ni.isConnected();
                        }
                    }
                } else {
                    //could not fetch status assume true
                    haveConnectedWifi = true;
                    haveConnectedMobile = true;
                }
            } else {
                //could not fetch status assume true
                haveConnectedWifi = true;
                haveConnectedMobile = true;
            }
        } catch (Exception e) {
            haveConnectedWifi = false;
            haveConnectedMobile = false;
            Log.e("Network Error", "Error fecting network status");
        }

        return haveConnectedWifi || haveConnectedMobile;
    }


}
