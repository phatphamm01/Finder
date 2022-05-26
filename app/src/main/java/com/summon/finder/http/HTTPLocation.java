package com.summon.finder.http;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HTTPLocation {
    @GET("query")
    Call<LocationModel> getData(@Query("lat") double lat, @Query("lon") double lon);

    class LocationModel {
        @SerializedName("location")
        public String location;


        @SerializedName("locationDetail")
        public LocationDetail locationDetail;

        public class LocationDetail {
            @SerializedName("city")
            public String city;
            @SerializedName("continent")
            public String continent;
            @SerializedName("country")
            public String country;
            @SerializedName("country_code")
            public String country_code;
            @SerializedName("house_number")
            public String house_number;
            @SerializedName("road")
            public String road;
            @SerializedName("suburb")
            public String suburb;
        }
    }
}
