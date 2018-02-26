package ca.mcgill.ecse321.treeple;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final float DEFAULT_ZOOM = 20;
    private static final double DEFAULT_RADIUS = 50;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private ArrayList<Marker> markers = new ArrayList<>();

    private View popupView;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_maps);

        mFusedLocationProviderClient = getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_clear_markers) {
            clearMarkers();
        } else if (item.getItemId() == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        getLocationPermission();
        startLocationUpdates();
        updateLocationUI();
        getDeviceLocation();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                markers.add(marker);
                CameraUpdate centerCam = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
                mMap.animateCamera(centerCam, 400, null);

                LinearLayout mapsLayout = (LinearLayout) findViewById(R.id.map_layout);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                popupView = inflater.inflate(R.layout.new_tree_popup, null);

                Spinner landSpinner = (Spinner) popupView.findViewById(R.id.land_spinner);
                Spinner statusSpinner = (Spinner) popupView.findViewById(R.id.status_spinner);
                Spinner ownershipSpinner = (Spinner) popupView.findViewById(R.id.ownership_spinner);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;

                TextView coords = (TextView) popupView.findViewById(R.id.tree_coords);
                LatLng markerPos = marker.getPosition();
                Double latitude = markerPos.latitude;
                Double longitude = markerPos.longitude;

                coords.setText(latitude + " " + longitude);

                popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(mapsLayout, Gravity.CENTER, 0, 0);

                ArrayAdapter<CharSequence> landAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.land_enum, R.layout.spinner_layout);
                landAdapter.setDropDownViewResource(R.layout.spinner_layout);
                landSpinner.setAdapter(landAdapter);

                ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.status_enum, R.layout.spinner_layout);
                statusAdapter.setDropDownViewResource(R.layout.spinner_layout);
                statusSpinner.setAdapter(statusAdapter);

                ArrayAdapter<CharSequence> ownershipAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.ownership_enum, R.layout.spinner_layout);
                ownershipAdapter.setDropDownViewResource(R.layout.spinner_layout);
                ownershipSpinner.setAdapter(ownershipAdapter);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO: Open popupwindow containing cutDown method, and tree info
                LinearLayout mapsLayout = (LinearLayout) findViewById(R.id.map_layout);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                popupView = inflater.inflate(R.layout.marker_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;

                TextView coords = (TextView) popupView.findViewById(R.id.tree_coords);
                coords.setText(marker.getPosition().toString());

                popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(mapsLayout, Gravity.CENTER, 0, 0);

                return true;
            }
        });
    }

    public void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Set the map's camera position to the current location of the device.
                            onLocationChanged(location);
                            mLastKnownLocation = location;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MapActivity", "Error getting location");
                                e.printStackTrace();
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void clearMarkers() {
        mMap.clear();
        markers.clear();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        long UPDATE_INTERVAL = 10 * 1000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        long FASTEST_INTERVAL = 2000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        }, Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
    }

    private Bundle getDateFromLabel(String text) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split("-");

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        if (comps.length == 3) {
            day = Integer.parseInt(comps[0]);
            month = Integer.parseInt(comps[1]);
            year = Integer.parseInt(comps[2]);
        }

        rtn.putInt("day", day);
        rtn.putInt("month", month);
        rtn.putInt("year", year);

        return rtn;
    }

    public void showDatePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText().toString());
        args.putInt("id", v.getId());

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "tree_date_planted");
    }

    @SuppressLint("DefaultLocale")
    public void setDate(int d, int m, int y) {
        TextView tv = (TextView) popupView.findViewById(R.id.tree_date_planted);
        tv.setText(String.format("%02d-%02d-%04d", d, m + 1, y));
    }

    public void plantTree(View view) throws JSONException {
        // TODO: Need to add Volley get request here
        TextView currentView = popupView.findViewById(R.id.tree_height);
        int height = Integer.parseInt(currentView.getText().toString());

        currentView = popupView.findViewById(R.id.tree_diameter);
        int diameter = Integer.parseInt(currentView.getText().toString());

        currentView = popupView.findViewById(R.id.tree_date_planted);
        String datePlanted = currentView.getText().toString();

        Spinner currentSpinner = popupView.findViewById(R.id.land_spinner);
        String land = currentSpinner.getSelectedItem().toString();

        currentSpinner = popupView.findViewById(R.id.status_spinner);
        String status = currentSpinner.getSelectedItem().toString();

        currentSpinner = popupView.findViewById(R.id.ownership_spinner);
        String ownership = currentSpinner.getSelectedItem().toString();

        currentView = popupView.findViewById(R.id.tree_species);
        String species = currentView.getText().toString();

        currentView = popupView.findViewById(R.id.tree_municipality);
        String municipality = currentView.getText().toString();

        currentView = popupView.findViewById(R.id.tree_coords);
        String[] latlng = currentView.getText().toString().split(" ");
        Double latitude = Double.parseDouble(latlng[0]);
        Double longitude = Double.parseDouble(latlng[1]);

        JSONObject treeObj = new JSONObject();
        treeObj.put("height", height);
        treeObj.put("diameter", diameter);
        treeObj.put("datePlanted", datePlanted);
        treeObj.put("land", land);
        treeObj.put("status", status);
        treeObj.put("ownership", ownership);
        treeObj.put("species", species);
        treeObj.put("latitude", latitude);
        treeObj.put("longitude", longitude);
        treeObj.put("municipality", municipality);

        System.out.println("TEST: " + treeObj);

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST, VolleyController.DEFAULT_BASE_URL + "/newtree/", treeObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("TEST: " + "JSONRESPONSE " + response.toString());
                popupWindow.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("TEST: " + "ErrRESPONSE " + error);
            }
        });

        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(jsonReq);
    }

    public void cutDownTree(View view) throws JSONException {
        // TODO add cut method, get tree ids
        JSONObject treeObj = new JSONObject();
        int treeId = 0;
        treeObj.put("treeId", treeId);

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE, VolleyController.DEFAULT_BASE_URL + "/deletetree/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                popupWindow.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(jsonReq);
    }

}
