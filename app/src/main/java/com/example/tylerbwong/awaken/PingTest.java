package com.example.tylerbwong.awaken;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by cdub on 6/20/16.
 */
public class PingTest {
   public static void main(String[] args) {
      try {
         Socket socket = new Socket("mark-i.ddns.net", 31415);
         System.out.println("Server up.");
      }
      catch (IOException e) {
         System.out.println("Server down.");
      }
   }
}
