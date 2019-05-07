package com.example.android.sockets;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {


    public static final int STATUS_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set Up Button
        Button click = (Button)findViewById(R.id.button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        //Sets Up OnClick Listener For Button
        click.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //Activly Requests Acsess To Files (With Pop-Up)
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STATUS_CODE);
                } else {
                    //Runs When Granted Permisssion
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String pictureName = "image.jpg";
                    File imageFile = new File(pictureDirectory, pictureName);
                    Uri pictureUri = Uri.fromFile(imageFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                    startActivityForResult(intent, 100);
                    send sendcode = new send();
                    sendcode.execute();

                }

            }
        });


    }

    @Override
    //More permission Granting code
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STATUS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // TODO run your code
                } else {
                    // TODO show warning
                }
            }
        }
    }


}
//Main Actions - Asynchronous
class send extends AsyncTask<Void,Void,Void> {
    static Socket s; //Socket Variable
    @Override
    protected Void doInBackground(Void...params){
        try {
            s = new Socket("192.168.1.5",8000); //Connects to IP address - enter your IP here
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //Gets information about a said directory on your device - currently downloads
            File photoPath = new File(directory, "image.jpg");
            Log.d("photoPath", photoPath.getAbsolutePath());
            InputStream input = new FileInputStream(photoPath.getAbsolutePath()); //Gets the true path of your image
            Log.d("photoPath", photoPath.getAbsolutePath());
            try {
                try {
                    //Reads bytes (all together)
                    int bytesRead;
                    while ((bytesRead = input.read()) != -1) {
                        Log.d("bytesRead", "mona");
                        s.getOutputStream().write(bytesRead); //Writes bytes to output stream
                    }
                } finally {
                    //Flushes and closes socket
                    s.getOutputStream().flush();
                    s.close();


                    Socket s2 = new Socket("192.168.1.5",8000);
                    String receivedFromserver = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(s2.getInputStream(), "UTF-8"));
                    String readLine = "";
                    while ((readLine = in.readLine()) != null) {

                        receivedFromserver += readLine;
                    }
                    Log.d("receivedFromserver", receivedFromserver);
                    s2.close();
                }
            } finally {
                input.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
