package ca.mcgill.ecse321.treeple;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;

    private PlaceDetectionClient mPlaceDetectionClient;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(0, 0);
    private static final float DEFAULT_ZOOM = 18;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Place> places = new ArrayList<>();
    private long checkInTime;
    private long checkOutTime;
    private boolean checkedIn = false;
    private boolean checkedOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_maps);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mFusedLocationProviderClient = getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Ignore running outside AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_clear_markers) {
            clearMarkers();
        } else if (item.getItemId() == R.id.option_search_place) {
            createAutocompleteIntent();
        } else if (item.getItemId() == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        getLocationPermission();
        startLocationUpdates();
        updateLocationUI();
        getDeviceLocation();
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

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

    public void createAutocompleteIntent() {

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("CA")
                .setTypeFilter(TYPE_FILTER_ESTABLISHMENT)
                .build();

        LatLng center = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        double radiusDegrees = 0.001;
        LatLng northEast = new LatLng(center.latitude + radiusDegrees, center.longitude + radiusDegrees);
        LatLng southWest = new LatLng(center.latitude - radiusDegrees, center.longitude - radiusDegrees);
        LatLngBounds bounds = LatLngBounds.builder()
                .include(northEast)
                .include(southWest)
                .build();

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter).setBoundsBias(bounds).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng placeLatLng = place.getLatLng();
                places.add(place);

                final Marker placeMarker = mMap.addMarker(new MarkerOptions().title(place.getName().toString()).position(placeLatLng).snippet(place.getId()));
                markers.add(placeMarker);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, DEFAULT_ZOOM));
                final PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.option_search_place));
                popupMenu.getMenuInflater().inflate(R.menu.place_context_menu, popupMenu.getMenu());

                mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.equals(placeMarker)) {
                            popupMenu.getMenu().findItem(R.id.place_name).setTitle(place.getName());
                            popupMenu.getMenu().findItem(R.id.place_address).setTitle(place.getAddress());
                            popupMenu.getMenu().findItem(R.id.place_wait).setTitle((Integer.toString(getWaitTime(place))));
                            popupMenu.show();
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getItemId() == R.id.option_checkin) {
                                        checkIn(place);
                                    } else if (item.getItemId() == R.id.option_checkout) {
                                        checkOut(place);
                                    } else if (item.getItemId() == R.id.option_add_favorites) {
                                        addToFavorites(place);
                                    } else {
                                        return false;
                                    }
                                    return true;
                                }
                            });
                            return true;
                        } else {
                            int i = 0;
                            for (Marker existingMarker : markers) {
                                if (marker.equals(existingMarker)) {
                                    final Place existingPlace = places.get(i);
                                    popupMenu.getMenu().findItem(R.id.place_name).setTitle(existingPlace.getName());
                                    popupMenu.getMenu().findItem(R.id.place_address).setTitle(existingPlace.getAddress());
                                    popupMenu.getMenu().findItem(R.id.place_wait).setTitle(Integer.toString(getWaitTime(existingPlace)));
                                    popupMenu.show();
                                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            if (item.getItemId() == R.id.option_checkin) {
                                                checkIn(existingPlace);
                                            } else if (item.getItemId() == R.id.option_checkout) {
                                                checkOut(existingPlace);
                                            } else if (item.getItemId() == R.id.option_add_favorites) {
                                                addToFavorites(existingPlace);
                                            } else {
                                                return false;
                                            }
                                            return true;
                                        }
                                    });
                                    return true;
                                }
                                i++;
                            }
                            return false;
                        }
                    }
                });

            }
        }
    }

    public void clearMarkers() {
        mMap.clear();
        markers.clear();
        places.clear();
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

    public void checkIn(final Place place) {
        if (!checkedIn) {
            HttpUtils.get(String.format("/locations/%s", place.getId()), new RequestParams(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    String placeName = replace(place.getName().toString());
                    String placeAddress = replace(place.getAddress().toString());
                    HttpUtils.post(String.format("/locations/%s?name=%s&strtNum=%s&address=%s", place.getId(), placeName, "1", placeAddress), new RequestParams(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable1, JSONObject errorResponse) {
                            System.out.println(statusCode);
                        }
                    });
                }
            });

            checkInTime = Calendar.getInstance().getTimeInMillis() / 1000;
            String checkInTimeString = Long.toString(checkInTime);
            HttpUtils.patch(String.format("/locations/checkIn/%s/?username=%s&checkIn=%s/", place.getId(), getUsername(), checkInTimeString), new RequestParams(), new JsonHttpResponseHandler());
            checkedIn = true;
            checkedOut = false;

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Already checked in!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void checkOut(final Place place) {
        if (!checkedOut) {
            checkOutTime = Calendar.getInstance().getTimeInMillis() / 1000;
            String checkOutTimeString = Long.toString(checkInTime);
            checkedIn = false;
            checkedOut = true;
            HttpUtils.patch(String.format("/locations/checkOut/%s/?username=%s&checkOut=%s/", place.getId(), getUsername(), checkOutTimeString), new RequestParams(), new JsonHttpResponseHandler());
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Already checked out!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void addToFavorites(Place place) {
        
    }

    public int getWaitTime(final Place place) {
        final int[] waitTime = new int[1];
        waitTime[0] = -1;

        HttpUtils.get(String.format("/locations/%s", place.getId()), new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    waitTime[0] = response.getInt("qTime");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String placeName = replace(place.getName().toString());
                String placeAddress = replace(place.getAddress().toString());
                placeAddress = placeAddress.replaceAll(",", "%2C");
                System.out.println("Place Name: " + placeName);
                System.out.println("Place Address: " + placeAddress);
                HttpUtils.post(String.format("/locations/%s/?name=%s&strtNum=%s&address=%s", place.getId(), placeName, "1", placeAddress), new RequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable1, JSONObject errorResponse) {
                        System.out.println(statusCode);
                    }
                });
            }
        });
        return waitTime[0];
    }

    public String getUsername() {
        if (RegisterActivity.username != null) {
            return RegisterActivity.username;
        } else if (LoginActivity.username != null) {
            return LoginActivity.username;
        } else {
            System.out.println("USER NULL");
            return "testUser";
        }

    }

    public String replace(String str) {
        String[] words = str.split(" ");
        StringBuilder sentence = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; ++i) {
            sentence.append("%20");
            sentence.append(words[i]);
        }

        return sentence.toString();
    }
}
