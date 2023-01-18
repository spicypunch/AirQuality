package com.example.airquality

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.airquality.databinding.ActivityMainBinding
import com.example.airquality.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityMapBinding

    private var mMap: GoogleMap? = null
    var currentLat: Double = 0.0
    var currentLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MainActivity에서 intent로 전달된 값을 가져옴
        currentLat = intent.getDoubleExtra("currentLat", 0.0)
        currentLng = intent.getDoubleExtra("currentLng", 0.0)

        /** 구글 맵 객체의 생명주기를 관리하는 supportMapFragmetn 객체를 mapFragment에 저장
         * getMapAsync는 mapFragment에 OnMapReadyCallback 인터페이스를 등록해줌
         * 지도가 준비되면 onMapReady() 함수가 자동으로 실행됨
         */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.btnCheckHere.setOnClickListener {
            mMap?.let {
                val intent = Intent()
                intent.putExtra("latitude", it.cameraPosition.target.latitude)
                intent.putExtra("longitude", it.cameraPosition.target.longitude)
                setResult(Activity.RESULT_OK, intent) // MainActivity에 정의해두었던 onActivityResult 함수가 실행됨
                finish()
            }
        }
    }

    // 지도가 준비되었을 때 실행되는 콜백
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.let {
            val currentLocation = LatLng(currentLat, currentLng)
            it.setMaxZoomPreference(20.0f) // 줌 최댓값 설정
            it.setMinZoomPreference(12.0f) // 줌 최솟값 설정
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
        }

        setMarker()

        binding.fabCurrentLocation.setOnClickListener {
            val locationProvider = LocationProvider(this@MapActivity)
            // 위도와 경도 정보를 가져옵니다.
            val latitude = locationProvider.getLocationLatitude()
            val longitude = locationProvider.getLocationLongitude()
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f))
            setMarker()
        }
    }

    private fun setMarker() {
        mMap?.let {
            it.clear() // 지도에 있는 마커를 먼저 삭제
            val markerOptions = MarkerOptions()
            markerOptions.position(it.cameraPosition.target) // 마커의 위치 설정
            markerOptions.title("마커 위치") // 마커의 이름 설정
            val marker = it.addMarker(markerOptions) // 지도에 마커를 추가하고, 마커 객체를 반환

            it.setOnCameraMoveListener {
                marker?.let { marker ->
                    marker.position = it.cameraPosition.target
                }
            }
        }
    }
}