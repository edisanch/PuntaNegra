package com.example.stivi.puntanegra;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation ;
    public boolean locloc = false;
    ArrayList<LatLng> MarkerPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Add a marker in Sydney and move the camera

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker mark) {



                    // Already two locations
                   // if (MarkerPoints.size() > 1) {
                    //    MarkerPoints.clear();
                    //    mMap.clear();
                   // }

                    // Adding new item to the ArrayList
                   // MarkerPoints.add(point);

                    // Creating MarkerOptions
                   // MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                   // options.position(point);

                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED.
                     */
                  /*  if (MarkerPoints.size() == 1) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (MarkerPoints.size() == 2) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
*/

                    // Add new marker to the Google Map Android API V2
                    //mMap.addMarker(options);

                    // Checks, whether start and end locations are captured
                  //  if (MarkerPoints.size() >= 2) {

                LatLng origin = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                LatLng dest = mark.getPosition();

                /*
                        LatLng origin = MarkerPoints.get(0);
                        LatLng dest = MarkerPoints.get(1);*/

                        // Getting URL to the Google Directions API
                        String url = getUrl(origin, dest);
                        Log.d("onMapClick", url.toString());
                        FetchUrl FetchUrl = new FetchUrl();

                        // Start downloading json data from Google Directions API
                        FetchUrl.execute(url);
                        //move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                       // mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                  //  }








               /* if(arg0.getTitle().equals("MyHome")) // if marker source is clicked
                    Toast.makeText(MainActivity.this, arg0.getTitle(), Toast.LENGTH_SHORT).show();// display toast*/
                return true;
            }

        });


        int height = 100;
        int width = 100;

        ;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.puntanegraico, null) ;
        //BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.puntanegraico) ;
       //BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.marker);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        Marker perth= mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-2.1233857, -79.9004497))
                .title("MINI MARKET PIERITO")
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
                );
        Marker perth1= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1272666, -79.9054713))
                        .title("LICORERA HAS LA VACA")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth2= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1422681, -79.8966948))
                        .title("LICORERA EL BOTELLÓN")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth3= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1697625, -79.897733))
                        .title("ECONOMARKET CENTRÓPOLIS")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth4= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-1.0268377, -79.4689835))
                        .title("VENTAS CORP DISTRIBUIDORA")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth5= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1651747,	-79.9207737))
                        .title("MINI MARKET RICOS")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth6= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.174768	,-79.9056525))
                        .title("DESPENSA URDESA")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth7= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1696812,	-79.9099482))
                        .title("JUNIORS MARKET")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth8= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.2011701,	-79.8858717))
                        .title("COMERCIAL ANDRESITO")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth9= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.2012563	,-79.88638))
                        .title("SELECT DRINKS")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth10= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-1.7986284	,-79.5339113))
                        .title("LICORERA ROMANOS")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth11= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1206607,	-79.9005785))
                        .title("LICORERA LA BARATITA")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth12= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1906227,	-79.8818245))
                        .title("ECONOMARKET LA MERCED")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth13= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1958917	,-79.8881455))
                        .title("LA REPRESA DE LOS LICORES")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth14= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1889624	,-79.8943523))
                        .title("ECONOMARKET HURTADO")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth15= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.0563895	,-79.9194502))
                        .title("JUMBO MARKET")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );

        Marker perth16= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1907072	,-79.8827524))
                        .title("ECONOMARKET P. YCAZA")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth17= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1955807	,-79.8969005))
                        .title("AVÍCOLA EL RANCHO")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth18= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1938397	,-79.8814415))
                        .title("ECONOMARKET PICHINCHA")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth19= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.0377117,	-79.8706795))
                        .title("MINI MARKET AREVALOS")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );

        Marker perth21= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1289624	,-79.8646127))
                        .title("NELSON MARKET")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );
        Marker perth22= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-2.1742447,	-79.9072015))
                        .title("STOP MARKET")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntanegraico))
        );




    }
    protected void onStart() {
       // mGoogleApiClient.connect();
        super.onStart();
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {

               // mMap.clear();

                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        /*mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
        mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));*/
        locloc = mLastLocation != null;
        LatLng pos = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

    }
    public void onLocationChanged(Location location) {
        // New location has now been determined
        // String msg = "Updated Location: " +
        //      Double.toString(location.getLatitude()) + "," +
        //   Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        mLastLocation.setLatitude(location.getLatitude());
        mLastLocation.setLongitude(location.getLongitude());

        //origen = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
       // pos.setCenter(origen);
        //MarkerOrigen.setPosition(origen);
        //  fitZoomAndPositionToMapByMarkers();
        //  centerMap(origen);
        //dibujarCamino();

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String no= "no";
    }




    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

}
