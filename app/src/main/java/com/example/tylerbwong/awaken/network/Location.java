package com.example.tylerbwong.awaken.network;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map;

/**
 * @author Connor Wong
 */
public class Location {
   private String data;
   private JSONObject dataJson;
   private JSONParser parser;
   private Map locationData;

   private final static String DATA_URL = "http://api.db-ip.com/v2/9d5870706ebd1cfd8b0c679ca249110633efc9fc/";
   private final static String CITY_KEY = "city";
   private final static String STATE_KEY = "stateProv";
   private final static String COUNTRY_CODE_KEY = "countryCode";
   private final static String IP_KEY = "ipAddress";
   private final static String COUNTRY_NAME_KEY = "countryName";
   private final static String CONTINENT_NAME_KEY = "continentName";
   private final static String CONTINENT_CODE_KEY = "continentCode";

   private final static int DATA_LENGTH = 1024;

   public Location(String ipAddress) {
      try {
         InetAddress ip = InetAddress.getByName(ipAddress);
         ipAddress = convertIpByteArray(ip.getAddress());
         data = readUrl(DATA_URL + ipAddress);
         parser = new JSONParser();
         locationData = (Map) parser.parse(data);
         dataJson = new JSONObject(locationData);
      }
      catch (IOException | ParseException e) {
         e.printStackTrace();
      }
   }

   public String getCity() {
      return (String) dataJson.get(CITY_KEY);
   }

   public String getStateProv() {
      return (String) dataJson.get(STATE_KEY);
   }

   public String getCountryCode() {
      return (String) dataJson.get(COUNTRY_CODE_KEY);
   }

   public String getIpAddress() {
      return (String) dataJson.get(IP_KEY);
   }

   public String getCountryName() {
      return (String) dataJson.get(COUNTRY_NAME_KEY);
   }

   public String getContinentName() {
      return (String) dataJson.get(CONTINENT_NAME_KEY);
   }

   public String getContinentCode() {
      return (String) dataJson.get(CONTINENT_CODE_KEY);
   }

   private static String convertIpByteArray(byte[] ipAddress) {
      String result = "";

      for (int index = 0; index < ipAddress.length; index++) {
         result += ipAddress[index] & 0xFF;

         if (index < ipAddress.length - 1) {
            result += ".";
         }
      }

      return result;
   }

   private String readUrl(String urlString) {
      BufferedReader reader;
      String result = "";

      try {
         URL url = new URL(urlString);
         reader = new BufferedReader(new InputStreamReader(url.openStream()));
         StringBuffer buffer = new StringBuffer();
         int read;
         char[] chars = new char[DATA_LENGTH];
         while ((read = reader.read(chars)) != -1)
            buffer.append(chars, 0, read);

         result = buffer.toString();
      }
      catch (IOException e) {

      }

      return result;
   }
}