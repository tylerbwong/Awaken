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
    * Gets the current status of the device in question. The method
    * will attempt to establish a socket connection with the target
    * device. If no connection can be made, the device is considered
    * INACTIVE.
    *
    * @param host - the name of the host/ip address
    * @param devicePort - the port that the router forwards to your device
    * @return true if the current status of the machine is RUNNING or false if the current status is
    * INACTIVE
    */
   public static boolean getStatus(String host, int devicePort) {
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
}
