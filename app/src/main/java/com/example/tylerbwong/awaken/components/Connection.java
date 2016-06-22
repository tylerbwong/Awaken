package com.example.tylerbwong.awaken.components;

/**
 * @author Tyler Wong
 */
public class Connection {
   private String nickname;
   private String host;
   private String mac;
   private String portWol;
   private String portDev;
   private String city;
   private String state;
   private String country;
   private String date;

   public Connection(String nickname, String host, String mac, String portWol, String portDev,
                     String city, String state, String country, String date) {
      this.nickname = nickname;
      this.host = host;
      this.mac = mac;
      this.portWol = portWol;
      this.portDev = portDev;
      this.city = city;
      this.state = state;
      this.country = country;
      this.date = date;
   }

   public String getNickname() {
      return nickname;
   }

   public String getHost() {
      return host;
   }

   public String getMac() {
      return mac;
   }

   public String getPortWol() {
      return portWol;
   }

   public String getPortDev() {
      return portDev;
   }

   public String getCity() {
      return city;
   }

   public String getState() {
      return state;
   }

   public String getCountry() {
      return country;
   }

   public String getDate() {
      return date;
   }
}
