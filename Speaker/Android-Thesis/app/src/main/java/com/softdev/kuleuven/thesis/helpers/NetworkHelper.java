package com.softdev.kuleuven.thesis.helpers;

import android.util.Log;

import java.net.InetAddress;

/**
 * NetworkHelper Thread to check internetConnection
 */

public class NetworkHelper implements Runnable {
   private volatile Boolean connected = false;
   private static final String TAG = "BootActivity";

    @Override
   public void run() {
       try  {
           InetAddress address = InetAddress.getByName("www.google.com");
           Log.d(TAG, "isInternetAvailable: TRUE ");
           //noinspection EqualsBetweenInconvertibleTypes
           connected = !address.equals("");

       } catch (Exception e) {
           Log.e(TAG, "ERROR isInternetAvailable: ",e );
       }
   }

   public Boolean getValue() {
       return connected;
   }
}
