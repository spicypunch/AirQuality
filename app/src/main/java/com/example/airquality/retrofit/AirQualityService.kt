package com.example.airquality.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityService {
    @GET("nearest_city") //GET, POST, PUT, DELETE와 같은 HTTP 메서드 종류를 적어주고 상대 URL을 적어주었습니다
    fun getAirQualityData(@Query("lat") lat : String,
                          @Query("lon") lon : String,
                          @Query("key") key  : String) : Call<AirQualityResponse>
}