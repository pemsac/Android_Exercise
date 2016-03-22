package com.uc3m.pedro.Ex1PEDRMARC;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/*
This activity allows the user to set up the app working parameters such as
center currentLocation and maximum radius

This Activity uses Google API available on Google Play Services. It must be installed on the device
 */

public class SetupActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener {

    //Variables
    private GoogleMap mMap;
    private Marker mMarker;
    private Double radius;
    private Intent intentService;

    //methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //create the intent for the service
        intentService = new Intent(SetupActivity.this, ControlService.class);

        //create the buttons objects
        Button buttonOK = (Button) findViewById(R.id.buttonOK);
        Button buttonPosition = (Button) findViewById(R.id.buttonPosition);
        Button buttonManually = (Button) findViewById(R.id.buttonManually);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*Button click listener: When we click on "Set the marker on your current position"
         (buttonPosition), the system sets the marker on the current currentLocation if it's available
         */
        buttonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMap.getMyLocation() != null) {
                    //If currentLocation is available, set the marker on its coordinates
                    LatLng latLng = new LatLng(
                            mMap.getMyLocation().getLatitude(),
                            mMap.getMyLocation().getLongitude());
                    mMarker.setPosition(latLng);
                } else {
                    //If currentLocation is unavailable, show an alert toast
                    Toast.makeText(
                            SetupActivity.this,
                            "Unknown position. Remember to activate the currentLocation access ",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        //set the listener to update the draggable marker position
        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMarkerDragListener(this);


        /* Button click listener: When we click on "Set the marker manually" (buttonManually),
         the system show a dialog with two text fields to write the latitudeCenter and longitude

         */
        buttonManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                //Get the new layout
                //Inflate the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View view = LayoutInflater
                        .from(SetupActivity.this)
                        .inflate(R.layout.dialog_locationmanually, null);
                //Create the new dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(SetupActivity.this);
                //Set the layout
                dialog.setView(view);
                //Add accept action button:
                dialog.setNeutralButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get String objects from dialog layout text fields
                        String latitudeString =
                                ((EditText) view.findViewById(R.id.editTextLatitude))
                                        .getText()
                                        .toString();
                        String longitudeString =
                                ((EditText) view.findViewById(R.id.editTextLongitude))
                                        .getText()
                                        .toString();
                        //Check if these objects are null
                        if (latitudeString.isEmpty() || longitudeString.isEmpty()) {
                            Toast.makeText(
                                    SetupActivity.this,
                                    "Any text field can't be empty.",
                                    Toast.LENGTH_SHORT)
                                    .show();

                        } else {
                            //Get Values from the Text fields of the dialog layout
                            double latitude = Double.parseDouble(latitudeString);
                            double longitude = Double.parseDouble(longitudeString);

                            //check if they're valid values
                            if ((latitude <= 90 && latitude >= -90) && (longitude <= 180 && longitude >= -80)) {
                                //if the values are correct, set the new mMarker and finish the dialog
                                LatLng latLng = new LatLng(latitude, longitude);
                                mMarker.setPosition(latLng);
                                dialog.dismiss();
                            } else {
                                //else, show an Alert dialog
                                new AlertDialog.Builder(SetupActivity.this)
                                        .setTitle("Invalid values")
                                        .setMessage("You have to set the Latitude between -90 " +
                                                "and 90; and the Longitude between -180 and 180")
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //show the dialog
                dialog.show();
            }


        });


        /* Button click listener: When we click on "OK" (buttonOK), the system ask the perimeter
        security radius and start the localization control.
        */
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the new layout
                //Inflate the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View view = LayoutInflater
                        .from(SetupActivity.this)
                        .inflate(R.layout.dialog_radius, null);
                //Create the new dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(SetupActivity.this);
                //Set the layout
                dialog.setView(view);
                //Add accept action button:
                dialog.setNeutralButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Get String object from dialog layout text field
                                final String radiusString =
                                        ((EditText) view.findViewById(R.id.editTextRadius))
                                                .getText()
                                                .toString();

                                //Check if this object's null
                                if (radiusString.isEmpty()) {
                                    Toast.makeText(
                                            SetupActivity.this,
                                            "The text field can't be empty or 0 m.",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    //else finish the setup

                                    //show an advice
                                    new AlertDialog.Builder(SetupActivity.this)
                                            .setTitle("Setup complete")
                                            .setMessage("The system is ready to realize the currentLocation " +
                                                    "control. From now on the app is running in " +
                                                    "background with a persistent notification. Open " +
                                                    "again the app with your password to desactivate " +
                                                    "the cotrol")
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //pass raidus to double with a minimum of 1m
                                                    radius = Double.parseDouble(radiusString);
                                                    if (radius < 1) {
                                                        radius = 1.0;
                                                    }
                                                    //start the service sending the parameters
                                                    intentService.putExtra(
                                                            "com.uc3m.pedro.Ex1PEDRMARC.RADIUS_EXTRA",
                                                            radius);
                                                    intentService.putExtra(
                                                            "com.uc3m.pedro.Ex1PEDRMARC.LATITUDE_EXTRA",
                                                            mMarker.getPosition().latitude);
                                                    intentService.putExtra(
                                                            "com.uc3m.pedro.Ex1PEDRMARC.LONGITUDE_EXTRA",
                                                            mMarker.getPosition().longitude);
                                                    startService(intentService);
                                                    finish();


                                                }
                                            })
                                            .show();
                                }
                            }
                        }

                );
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }

                );
                dialog.show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        //destroy the Service each time the SetupActivity is opened.
        intentService.removeExtra("com.uc3m.pedro.Ex1PEDRMARC.LATITUDE_EXTRA");
        intentService.removeExtra("com.uc3m.pedro.Ex1PEDRMARC.LONGITUDE_EXTRA");
        intentService.removeExtra("com.uc3m.pedro.Ex1PEDRMARC.RADIUS_EXTRA");
        stopService(intentService);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Enable currentLocation on Map
        mMap.setMyLocationEnabled(true);
        //Enable zoom buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Set the mMarker default position in Leganes
        LatLng latLng = new LatLng(40.33245454451009,-3.7655024364296463);
        mMarker = mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mMarker.setPosition(marker.getPosition());
    }
}
