package com.example.tylerbwong.awaken.network;

/**
 * Created by cdub on 6/24/16.
 */
public class WakeTest {
   public static void main(String[] args) {
      Wake test = new Wake("cdubthecoolcat.ddns.net", "00:24:E8:38:2C:67");
      test.sendPacket();
   }
}
