package com.example.tylerbwong.awaken.network;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Pair;

import com.example.tylerbwong.awaken.interfaces.AsyncResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Tyler Wong
 */
public final class StatusUpdate extends AsyncTask<Pair<String, Integer>, Void, Boolean> {
   /**
    * How long the socket should wait for a response
    * before stopping.
    */
   private final static int TIMEOUT = 500;

   /**
    * The device is running.
    */
   public final static boolean RUNNING = true;

   /**
    * The device is not running.
    */
   public final static boolean INACTIVE = false;

   /**
    * The async response.
    */
   public AsyncResponse delegate = null;

   /**
    * Gets the current status of the device in question. The method
    * will attempt to establish a socket connection with the target
    * device. If no connection can be made, the device is considered
    * INACTIVE.
    *
    * @param params - a variable amount of pairs of two arguments, host name and port number
    * @return true if the current status of the machine is RUNNING or false if the current status is
    * INACTIVE
    */
   @Override
   protected Boolean doInBackground(Pair<String, Integer>... params) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);

      boolean status = RUNNING;

      try {
         InetSocketAddress address = new InetSocketAddress(params[0].first, params[0].second);
         Socket statusSocket = new Socket();
         statusSocket.connect(address, TIMEOUT);
         statusSocket.close();
      }
      catch (IOException e) {
         status = INACTIVE;
      }

      return status;
   }

   public static boolean refreshStatus(String host, int devicePort) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);

      boolean status = RUNNING;

      try {
         InetSocketAddress address = new InetSocketAddress(host, devicePort);
         Socket statusSocket = new Socket();
         statusSocket.connect(address, TIMEOUT);
         statusSocket.close();
      }
      catch (IOException e) {
         status = INACTIVE;
      }

      return status;
   }

   @Override
   protected void onPostExecute(Boolean result) {
      delegate.onTaskResult(result);
   }
}
