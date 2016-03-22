package com.uc3m.pedro.Ex1PEDRMARC;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * This Activity is the launcher activity. We can start to use the app using a password. We can
 * set up a new perimeter security, turn off it and turn off the alarm.
 */

public class Welcome extends AppCompatActivity {
    //variable PASSWORD
    private static final String PASSWORD = "1234"; //<=== change the password here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        Button buttonAlarm = (Button) findViewById(R.id.buttonAlarm);
        Button buttonSetup = (Button) findViewById(R.id.buttonSetup);
        Button buttonService = (Button) findViewById(R.id.buttonService);

        /*Button click listener: When we click on "Turn off alamr"
         (buttonAlarm), the system stops the alarm if it's ringing
         */
        buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Turn off the alarm
                sendBroadcast(new Intent(
                        "com.uc3m.pedro.Ex1PEDRMARC.TURN_OFF_ALARM"));

            }
        });

        /*Button click listener: When we click on "Set Up a new perimeter security"
         (buttonSetup), the system stops the Control Service if it's running
         */
        buttonSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the new layout
                //Inflate the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View view = LayoutInflater
                        .from(Welcome.this)
                        .inflate(R.layout.dialog_password, null);
                //Create the new dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(Welcome.this);
                //Set the layout
                dialog.setView(view);
                //Add accept action button:
                dialog.setNeutralButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get String object from dialog layout text field
                        final String pass =
                                ((EditText) view.findViewById(R.id.editTextPassword))
                                        .getText()
                                        .toString();

                        if (pass.equals(PASSWORD)) {
                            Intent intentSetup = new Intent(
                                    Welcome.this,
                                    SetupActivity.class);
                            startActivity(intentSetup);
                            finish();
                        } else {
                            Toast.makeText(
                                    Welcome.this,
                                    "Incorrect password",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });
                dialog.show();
            }
        });

        /*Button click listener: When we click on "Turn off the current perimeter security"
         (buttonService), and introduce the password, we go to the Setup Activity
         */
        buttonService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Get the new layout
                //Inflate the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View view = LayoutInflater
                        .from(Welcome.this)
                        .inflate(R.layout.dialog_password, null);
                //Create the new dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(Welcome.this);
                //Set the layout
                dialog.setView(view);
                //Add accept action button:
                dialog.setNeutralButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get String object from dialog layout text field
                        final String pass =
                                ((EditText) view.findViewById(R.id.editTextPassword))
                                        .getText()
                                        .toString();

                        if (pass.equals(PASSWORD)) {
                            Intent intentService = new Intent(
                                    Welcome.this,
                                    ControlService.class);
                            //destroy the Service each time the SetupActivity is opened.
                            stopService(intentService);


                        } else {
                            Toast.makeText(
                                    Welcome.this,
                                    "Incorrect password",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });
                dialog.show();
            }
        });
    }
}




