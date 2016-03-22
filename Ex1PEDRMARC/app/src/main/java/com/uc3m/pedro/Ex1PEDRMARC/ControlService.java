package com.uc3m.pedro.Ex1PEDRMARC;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class ControlService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    //variables
    private double radius, latitudeCenter, longitudeCenter;
    private LatLng latLngCenter, latLngCurrent;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private Thread threadControl;
    private Ringtone alert;
    private Vibrator vibration;

    //constructor
    public ControlService() {
    }

    //methods
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Start the location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this);

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Get SetupActivity.class parameters
        intent.getExtras();
        longitudeCenter =  intent.getDoubleExtra(
                "com.uc3m.pedro.Ex1PEDRMARC.LONGITUDE_EXTRA", 0);
        latitudeCenter = intent.getDoubleExtra(
                "com.uc3m.pedro.Ex1PEDRMARC.LATITUDE_EXTRA", 0);
        radius = intent.getDoubleExtra(
                "com.uc3m.pedro.Ex1PEDRMARC.RADIUS_EXTRA", 1);
        latLngCenter = new LatLng(latitudeCenter, longitudeCenter);


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Create broadcast to turn off the alarm from the AlarmActivity.
        registerReceiver(broadcastReceiver, new IntentFilter(
                "com.uc3m.pedro.Ex1PEDRMARC.TURN_OFF_ALARM"));

        //Set the service on foreground:
        //Create the notification
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_map)
                .setContentTitle("Ex1PEDRMARC is running")
                .setContentText("Starting service...");
        // Create Intent to start the app when we click on the notification
        Intent notificationIntent = new Intent(ControlService.this, Welcome.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder.setContentIntent(pendingIntent);
        // start the service on foreground
        startForeground(1, mBuilder.build());
        //start notificatio manager to update it
        mNotificationManager = (
                NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //Setup the Location service (from Google API)
        //create a new google api client and connect it
        mGoogleApiClient = new GoogleApiClient.Builder(ControlService.this)
                .addConnectionCallbacks(ControlService.this)
                .addOnConnectionFailedListener(ControlService.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


        //create the Location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(7500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Prepare vibration and alarm
        vibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Uri uriAlert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        alert = RingtoneManager.getRingtone(getApplicationContext(), uriAlert);



        //main thread in loop:
        threadControl = new Thread() {

            public void run() {

                //variables
                Location lastLocation = null;
                boolean alarmStarted = false;
                while (threadControl.isAlive()) {
                    //check if the location has changed
                    if (lastLocation!=currentLocation && currentLocation!=null){
                        //save the locationn
                        lastLocation=currentLocation;

                        //distance from the center:
                        latLngCurrent = new LatLng(
                                currentLocation.getLatitude(),
                                currentLocation.getLongitude()
                        );
                        double distance = getDistanceFromLatLngInKm(latLngCenter,latLngCurrent);

                        //messages and alerts
                        if(distance < 0.5*radius){
                            if(alarmStarted){
                                sendBroadcast(new Intent(
                                        "com.uc3m.pedro.Ex1PEDRMARC.CLOSE_ALARM"));
                                vibration.cancel();
                                alert.stop();
                                alarmStarted=false;
                            }

                            int margin = (int) radius - (int) distance;
                            mBuilder.setContentText(
                                    "Safe!. Your're at " + margin + "m from the perimeter")
                                    .setSmallIcon(android.R.drawable.ic_dialog_map);

                        }
                        else if(0.5*radius <= distance && distance < 0.85*radius){
                            if(alarmStarted){
                                sendBroadcast(new Intent(
                                        "com.uc3m.pedro.Ex1PEDRMARC.CLOSE_ALARM"));
                                vibration.cancel();
                                alert.stop();
                                alarmStarted=false;
                            }

                            int margin = (int) radius - (int) distance;
                            mBuilder.setContentText(
                                    "Be careful!. Your're only at " + margin + "m from the perimeter")
                                    .setSmallIcon(android.R.drawable.ic_dialog_map);
                        }
                        else if(0.85*radius <= distance && distance < radius){
                            if(alarmStarted){
                                sendBroadcast(new Intent(
                                        "com.uc3m.pedro.Ex1PEDRMARC.CLOSE_ALARM"));
                                vibration.cancel();
                                alert.stop();
                                alarmStarted=false;
                            }

                            int margin = (int) radius - (int) distance;
                            mBuilder.setContentText(
                                    "Caution!. Your're very close to the perimeter (" + margin + "m)")
                                    .setSmallIcon(android.R.drawable.ic_dialog_info);
                        }
                        else{
                            mBuilder.setContentText(
                                    "YOU'RE OUT THE SECURITY PERIMETER!.")
                                    .setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel);

                            if (!alarmStarted){
                                alarmStarted=true;
                                Intent intentAlarm = new Intent(
                                        ControlService.this,
                                        AlarmActivity.class);

                                intentAlarm.putExtra(
                                        "com.uc3m.pedro.Ex1PEDRMARC.ALARM_LATITUDE_EXTRA",
                                        latitudeCenter);
                                intentAlarm.putExtra(
                                        "com.uc3m.pedro.Ex1PEDRMARC.ALARM_LONGITUDE_EXTRA",
                                        longitudeCenter);

                                //Sound alarm with max volume
                                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                mAudioManager.setStreamVolume(
                                        AudioManager.STREAM_RING,
                                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                                        AudioManager.FLAG_PLAY_SOUND);
                                alert.play();

                                //vibrate

                                vibration.vibrate(new long[]{0, 1000, 1000}, 0);

                                intentAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intentAlarm);
                            }
                        }
                    }
                    else if(currentLocation==null){
                        mBuilder.setContentText(
                                "Location unavailable... check your location access is enable ")
                                .setSmallIcon(android.R.drawable.ic_dialog_alert);
                    }


                    mNotificationManager.notify(1, mBuilder.build());
                    try {
                        sleep(10000);
                    }
                    catch (InterruptedException e){
                        break;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        threadControl.start();

    }

    @Override
    public void onDestroy(){
        vibration.cancel();
        alert.stop();

        threadControl.interrupt();
        mGoogleApiClient.disconnect();
        stopForeground(true);
        mNotificationManager.cancel(1);
    }


    //Math methods (source: http://droidjava.com/get-distance-in-km-from-2-latlng-coordinates/)
    private static double getDistanceFromLatLngInKm(LatLng c1, LatLng c2) {
        int R = 6371000; // Radius of the earth in m

        double lat1 = c1.latitude;
        double lat2 = c2.latitude;

        double lon1 = c1.longitude;
        double lon2 = c2.longitude;

        double dLat = deg2rad(lat2-lat1);
        double dLon = deg2rad(lon2-lon1);

        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private static double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            vibration.cancel();
            alert.stop();
        }
    };
}
