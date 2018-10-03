package io.awaken.data.model

import com.google.gson.annotations.SerializedName

/**
 * @author Connor Wong
 */
class Location {
    @SerializedName("city")
    val city: String? = null
    @SerializedName("region_code")
    val regionCode: String? = null
    @SerializedName("country_code")
    val countryCode: String? = null
    @SerializedName("ip")
    val ip: String? = null
    @SerializedName("country_name")
    val countryName: String? = null
    @SerializedName("continent_name")
    val continentName: String? = null
    @SerializedName("continent_code")
    val continentCode: String? = null
}
