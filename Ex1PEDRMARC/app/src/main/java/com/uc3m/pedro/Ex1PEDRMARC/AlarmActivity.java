package com.uc3m.pedro.Ex1PEDRMARC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This Activity show an Alarm with sound and vibration when the device go out
 * the perimeter security. It's show a map to help the person.
 */
public class AlarmActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng center;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create broadcast to close the activity automatically.
        registerReceiver(broadcastReceiver, new IntentFilter(
                "com.uc3m.pedro.Ex1PEDRMARC.CLOSE_ALARM"));

        //allow the activity start with locked screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);




        //get the marker parameters
        Intent intent = getIntent();
        double latitudeCenter = intent.getDoubleExtra(
                "com.uc3m.pedro.Ex1PEDRMARC.ALARM_LATITUDE_EXTRA", 0
        );
        double longitudeCenter = intent.getDoubleExtra(
                "com.uc3m.pedro.Ex1PEDRMARC.ALARM_LONGITUDE_EXTRA", 0
        );
        center = new LatLng(latitudeCenter, longitudeCenter);

        //Set the layout with the map
        setContentView(R.layout.activity_alarm);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

                
        /*Button click listener: When we click on "Turn off the alarm"
         (button), the system stops the alarm if it's ringing
         */
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(
                        "com.uc3m.pedro.Ex1PEDRMARC.TURN_OFF_ALARM"));
            }
        });

    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        // Add a marker in the configured center and move the camera
        googleMap.addMarker(new MarkerOptions().position(center).title("Go here"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
