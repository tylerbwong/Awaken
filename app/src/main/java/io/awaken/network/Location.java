package io.awaken.network;

import com.google.gson.annotations.SerializedName;

/**
 * @author Connor Wong
 */
public class Location {
    @SerializedName("city")
    private String city;
    @SerializedName("region_code")
    private String regionCode;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("ip")
    private String ip;
    @SerializedName("country_name")
    private String countryName;
    @SerializedName("continent_name")
    private String continentName;
    @SerializedName("continent_code")
    private String continentCode;

    public String getCity() {
        return city;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getIp() {
        return ip;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getContinentName() {
        return continentName;
    }

    public String getContinentCode() {
        return continentCode;
    }
}