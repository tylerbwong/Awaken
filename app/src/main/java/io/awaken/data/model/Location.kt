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
    @SerializedName("country_name")
    val countryName: String? = null
}
