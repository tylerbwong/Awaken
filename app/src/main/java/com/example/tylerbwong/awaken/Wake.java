package com.example.tylerbwong.awaken;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Connor Wong
 */
public class Wake {

   private String mIp;
   private String mMac;
   private int mPort = 7;

   private final static int MAC_LENGTH = 6;
   private final static int UDP_MULTIPLIER = 16;
   private final static String MAC_DELIMIT = "(\\:|\\-)";
   private final static String INVALID_MAC = "Invalid MAC Address";

   public Wake(String ip, String mac) {
      this.mIp = ip;
      this.mMac = mac;
   }

   public Wake(String ip, String mac, int port) {
      this.mIp = ip;
      this.mMac = mac;
      this.mPort = port;
   }

   private byte[] getMacBytes(String macStr) {
      byte[] bytes = new byte[MAC_LENGTH];
      String[] mac = macStr.split(MAC_DELIMIT);

      if (mac.length != MAC_LENGTH) {
         throw new IllegalArgumentException(INVALID_MAC);
      }

      try {
         for (int index = 0; index < MAC_LENGTH; index++) {
            bytes[index] = (byte) Integer.parseInt(mac[index], UDP_MULTIPLIER);
         }
      }
      catch (NumberFormatException e) {
         throw new IllegalArgumentException(INVALID_MAC);
      }
      return bytes;
   }

   public void sendPacket() {
      try {
         byte[] macBytes = getMacBytes(mMac);
         byte[] bytes = new byte[MAC_LENGTH + UDP_MULTIPLIER * macBytes.length];

         for (int index = 0; index < MAC_LENGTH; index++) {
            bytes[index] = (byte) 0xff;
         }

         for (int index = MAC_LENGTH; index < bytes.length; index += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, index, macBytes.length);
         }

         InetAddress address = InetAddress.getByName(mIp);
         DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, mPort);
         DatagramSocket socket = new DatagramSocket();

         socket.send(packet);
         socket.close();
      }
      catch (Exception e) {

      }
   }

   public String getIp() {
      return mIp;
   }

   public String getMac() {
      return mMac;
   }

   public int getPort() {
      return mPort;
   }
}
