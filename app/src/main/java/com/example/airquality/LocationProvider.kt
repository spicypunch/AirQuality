package com.example.airquality

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import java.lang.Exception

class LocationProvider(val context: Context) {
    //Location는 위도, 경도, 고도와 같이 위치에 관련된 정보를 가지고 있는 클래스
    private var location: Location? = null

    //Location Manager는 시스템 위치 서비스에 접근을 제공하는 클래스
    private var locationManager: LocationManager? = null

    init {
        getLocation();
    }

    private fun getLocation(): Location? {
        try {
            //먼저 위치 시스템 서비스를 가져옴
            locationManager = context.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager

            var gpsLocation: Location? = null
            var networkLocation: Location? = null

            //GPS Provider와 Network Provider가 활성화되어 있는지 확인
            val isGPSEnabled: Boolean =
                locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled: Boolean =
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                //둘 다 사용 불가능한 상황이면 null 반환
                return null
            } else {
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                //ACCESS_COARSE_LOCATION보다 더 정밀한 위치 정보 얻기
                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                //도시 Block 단위의 정밀도의 위치 정보를 얻기
                //위 두 개 권한 없다면 null 반환
                if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
                    return null
                //네트워크를 통한 위치 파악이 가능한 경우에 위치를 가져옴
                if (isNetworkEnabled) {
                    networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                //GPS를 통한 위치 파악이 가능한 경우에 위치를 가져옴
                if (isGPSEnabled) {
                    gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                if (gpsLocation != null && networkLocation != null) {
                    //두 개 위치가 있다면 정확도 높은 것으로 선택됨
                    if (gpsLocation.accuracy > networkLocation.accuracy) {
                        location = gpsLocation
                        return gpsLocation
                    } else {
                        location = networkLocation
                        return networkLocation
                    }
                } else {
                    //가능한 위치 정보가 한 개만 있는 경우
                    if (gpsLocation != null) {
                        location = gpsLocation
                    }
                    if (networkLocation != null) {
                        location = networkLocation
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() //에러 출력
        }
        return location
    }
    //위도 정보를 가져오는 함수
    fun getLocationLatitude(): Double {
        return location?.latitude ?: 0.0 //null이면 0.0 반환
    }
    //경도 정보를 가져오는 함수
    fun getLocationLongitude(): Double {
        return location?.longitude ?: 0.0 //null이면 0.0 반환
    }
}