package com.example.tylerbwong.awaken.network;

import java.io.IOException;
import java.net.InetAddress;
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
      String status = RUNNING;

      try {
         InetAddress address = InetAddress.getByName(host);
         Socket statusSocket = new Socket(address, devicePort);
         statusSocket.setSoTimeout(TIMEOUT);
      }
      catch (IOException e) {
         status = INACTIVE;
      }

      return status;
   }
}
