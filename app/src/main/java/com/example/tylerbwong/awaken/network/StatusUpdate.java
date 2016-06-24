package com.example.tylerbwong.awaken.network;

import android.os.StrictMode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Tyler Wong
 */
public final class StatusUpdate {
   /**
    * How long the socket should wait for a response
    * before stopping.
    */
   private final static int TIMEOUT = 2000;

   /**
    * The device is running.
    */
   public final static String RUNNING = "running";

   /**
    * The device is not running.
    */
   public final static String INACTIVE = "not running";

   /**
    * Gets the current status of the device in question. The method
    * will attempt to establish a socket connection with the target
    * device. If no connection can be made, the device is considered
    * INACTIVE.
    *
    * @param host - the ip address, or name of the host
    * @param devicePort - the port that the network is forwarding
    * @return the current status of the machine RUNNING or INACTIVE
    */
   public static String getStatus(String host, int devicePort) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);

      String status = RUNNING;

      try {
         InetSocketAddress address = new InetSocketAddress(host, devicePort);
         Socket statusSocket = new Socket();
         statusSocket.connect(address, TIMEOUT);
      }
      catch (IOException e) {
         status = INACTIVE;
      }

      return status;
   }
}
