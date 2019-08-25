
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.CommonFunctions;
import com.example.myapplication.Value;


public class MainActivity extends AppCompatActivity {

    int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    LocationManager locationManager;

    double longitudeBest, latitudeBest;

    EditText BuslineIdText;
    EditText RouteTypeText;


    Value userResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BuslineIdText = (EditText) findViewById(R.id.userId);
        RouteTypeText = (EditText) findViewById(R.id.topK);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }


    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void submitRequest(View view) {

        if (CommonFunctions.isStringNullOrEmpty(BuslineIdText.getText().toString())) {
            Toast.makeText(this, "You must select the bus line id first.", Toast.LENGTH_LONG).show();
            return;
        } else if (CommonFunctions.isStringNullOrEmpty(RouteTypeText.getText().toString())) {
            Toast.makeText(this, "You must select the route type first.", Toast.LENGTH_LONG).show();
            return;
        }


        new SendAsyncRequest(this).execute();

    }

    /*
     * Location functionality based on : https://github.com/obaro/SimpleLocationApp/blob/master/app/src/main/java/com/sample/foo/simplelocationapp/MainActivity.java
     */

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    /*public void toggleBestUpdates(View view) {
        if (!checkLocation())
            return;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true) != null ? locationManager.getBestProvider(criteria, true) : locationManager.getAllProviders().get(0);
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] arrayOfPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, arrayOfPermissions, MY_PERMISSIONS_REQUEST_LOCATION);
                Toast.makeText(this, "Allow gps permissions and try again !", Toast.LENGTH_LONG).show();
            } else {
                locationManager.requestLocationUpdates(provider, 2 * 60 * 1000, 10, locationListenerBest);
                Toast.makeText(this, "Calculcating location... \n(Best Provider is " + provider + ")", Toast.LENGTH_LONG).show();
            }
        }
    }*/
    /*private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueNetwork.setText(longitudeBest + "");
                    latitudeValueNetwork.setText(latitudeBest + "");
                    Toast.makeText(MainActivity.this, "Updated location !!", Toast.LENGTH_SHORT).show();
                    locationManager.removeUpdates(locationListenerBest);
                }
            });
        }*/

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    /*
     * Location functionality based on : https://github.com/obaro/SimpleLocationApp/blob/master/app/src/main/java/com/sample/foo/simplelocationapp/MainActivity.java
     */


    public class SendAsyncRequest extends AsyncTask<Void, Void, String> {
        ProgressDialog mProgressDialog;
        Context context;

        public SendAsyncRequest(Context context) {
            this.context = context;

            String Bu = Bus.getText().toString());
            int k = Integer.parseInt(topKEditText.getText().toString());

            userRequest = new UserRequest(userId, k, range, lon, lat);
        }

        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(context, "", "Please wait, sending request...");
        }

        protected String doInBackground(Void... params) {
            try {
                sendRequest();
                return "OK";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "ERROR";
        }

        protected void onPostExecute(String result) {
            if (result.equals("OK")) {
                mProgressDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("userRequest", userRequest);
                intent.putExtra("userResponse", userResponse);
                startActivity(intent);
            } else {
                mProgressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("An unexpected error occured!");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void sendRequest() throws Exception {
        String ipPort = masterIpPort.getText().toString().trim();

        String master_ip = ipPort.split(":")[0];
        int master_port = Integer.parseInt(ipPort.split(":")[1]);

        Connection connectionToMaster = new Connection(master_ip, master_port);

        System.out.println(String.format("\nSending request to master on %s:%s", master_ip, master_port));

        connectionToMaster.send(userRequest);

        UserResponse response = (UserResponse) connectionToMaster.receive();

        userResponse = response;

        System.out.println(String.format("\nReceived response from master on %s:%s\n", master_ip, master_port));

        System.out.println(response);

        connectionToMaster.close();
    }

}
