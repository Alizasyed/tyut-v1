package com.lumos.durshaltyut;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.skyfishjy.library.RippleBackground;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

public class student_main extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    //labels

    private String[] grades;

    DatabaseReference currfent_booking;
    private GoogleMap mMap;
    private Spinner gradeSpinner;
    private TextView gradetv;
    private TextView mRequest, dummyfield;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    String sName = "Student";
    private List<AutocompletePrediction> predictionList;
    //current
    private Location mLastKnownLocation;
    // private Location selectedLocation;
    //private LocationRequest mLocationRequest;
    //for updating users req
    private LocationCallback locationCallback;

    FirebaseAuth mAuth;
    String sNumber;
    String selected_topic;
    String selected_location;
    String selected_subject;

    TextView topic;
    TextView subject;
    private ImageButton imgbutton;

    Fragment fragment = null;

    private ImageButton tyutit;
    //view relted

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnBook;
    private Drawer result = null;


    //ripple
    private RippleBackground rippleBg;
    private final float DEFAULT_ZOOM = 18;


    //bottom shee
    private BottomAppBar mBottomAppBar;
    private LinearLayout mBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    String user_type;
    private String selected_grade;
    LatLng currentMarkerLocation;
    private LatLng req_location;

    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    //map
    TextView name;
    GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        Resources grade_res = getResources();
        grades = grade_res.getStringArray(R.array.gradelist);

        Resources qual_res = getResources();
        String[] qual = qual_res.getStringArray(R.array.qual_List);

        Bundle bundle = getIntent().getExtras();
        sName = bundle.getString("name");

        name = findViewById(R.id.name);
        name.setText(sName);

        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(student_main.this, student_main.class);
                    startActivity(intent);
                    finish();
                }
            }


        };


//        subject = findViewById(R.id.subject_text_view);
        topic = findViewById(R.id.topic_text_view);
        mRequest = findViewById(R.id.request_text);
        //bottomsheet

        mBottomSheet = findViewById(R.id.bottomsheet);
        // mBottomAppBar = findViewById(R.id.btm_app_bar);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        tyutit = findViewById(R.id.tyutit);
        gradeSpinner = findViewById(R.id.grade_spinner);
        SearchableSpinner subject_spinner = findViewById(R.id.sub_spinner);


        gradetv = findViewById(R.id.grade_text_view);
//this is our bottom panel

        //ArrayList

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainlayout);
        coordinatorLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if ((mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) || (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED)) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }


            }

        });
//geofire


        //Set Adapter
        //Spinner
        gradeSpinner.setAdapter(new ArrayAdapter<>(student_main.this, android.R.layout.simple_spinner_dropdown_item, grades));
        subject_spinner.setAdapter(new ArrayAdapter<>(student_main.this, android.R.layout.simple_spinner_dropdown_item, qual));
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "Please Select ", Toast.LENGTH_SHORT).show();
                    //empty tv
                    gradetv.setText("");

                } else {
                    sNumber = parent.getItemAtPosition(position).toString();
                    //selected value


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "Please Select ", Toast.LENGTH_SHORT).show();
                    //empty tv
                    gradetv.setText("");

                } else {
                    sNumber = parent.getItemAtPosition(position).toString();
                    //selected value


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        tyutit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
//
//            }
//        });


//        mBottomAppBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
//
//            }
//        });


        imgbutton = findViewById(R.id.mainimg);

        result = new DrawerBuilder().withActivity(this).build();

        //Create the drawer

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.header)
                .inflateMenu(R.menu.drawer)

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(student_main.this, ((Nameable) drawerItem).getName().getText(student_main.this), Toast.LENGTH_SHORT).show();
                        }

                        switch (position) {
                            case 1:
                                //  Toast.makeText(student_main.this, "Item one clicked", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(student_main.this, profileactivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                //  Toast.makeText(student_main.this, "Item two  clicked", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(student_main.this, history.class);
                                startActivity(i);
                                break;

                            case 3:
                                //     Toast.makeText(student_main.this, "Item three  clicked", Toast.LENGTH_SHORT).show();
                                Intent i3 = new Intent(student_main.this, catelog.class);
                                startActivity(i3);
                                break;


                            case 4:
                                //     Toast.makeText(student_main.this, "Item three  clicked", Toast.LENGTH_SHORT).show();
                                Intent i4 = new Intent(student_main.this, wallet.class);
                                startActivity(i4);
                                break;

                            case 8:
                                FirebaseAuth.getInstance().signOut();
                                Intent i5 = new Intent(student_main.this, login.class);
                                i5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i5);
                        }
                        return false;
                    }
                }).build();


        //drawer stuff

        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.openDrawer();
            }
        });


        btnBook = findViewById(R.id.btn_book);
        materialSearchBar = findViewById(R.id.searchBar);

        rippleBg = findViewById(R.id.ripple_bg);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(student_main.this);
        Places.initialize(student_main.this, "AIzaSyD7QMz0THVDmCLe6ROG3o8xvNlZa5m8OuY");
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

//
//        //for search
//        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//            @Override
//            public void onSearchStateChanged(boolean enabled) {
//
//
//            }
//
//            @Override
//            public void onSearchConfirmed(CharSequence text) {
//
//                startSearch(text.toString(), true, null, true);
//            }
//
//
//            @Override
//            public void onButtonClicked(int buttonCode) {
//                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
//
//                    result.openDrawer();
//                    //opening or closing a navigation drawer
//                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
//
//                    materialSearchBar.closeSearch();
//                    materialSearchBar.clearSuggestions();
//                }
//
//            }
//        });
//
//
//        //to search
//        materialSearchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////main logic
//                final FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
//                        .setCountry("pk")
//                        .setTypeFilter(TypeFilter.ADDRESS)
//                        .setSessionToken(token)
//                        .setQuery(s.toString())
//                        .build();
//
//                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
//                        if (task.isSuccessful()) {
//                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
//                            if (predictionsResponse != null) {
//                                predictionList = predictionsResponse.getAutocompletePredictions();
//                                List<String> suggestionList = new ArrayList<>();
//                                for (int i = 0; i < predictionList.size(); i++) {
//                                    AutocompletePrediction prediction = predictionList.get(i);
//                                    suggestionList.add(prediction.getFullText(null).toString());
//                                }
//                                materialSearchBar.updateLastSuggestions(suggestionList);
//                                if (!materialSearchBar.isSuggestionsVisible()) {
//                                    materialSearchBar.showSuggestionsList();
//                                }
//                            }
//                        } else {
//                            Log.i("mytag", "predication fethcing task unsuccessful");
//                        }
//                    }
//                });
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
//            @Override
//            public void OnItemClickListener(int position, View v) {
//                //req places api to send us lat lang
//
//                if (position >= predictionList.size()) {
//                    return;
//                }
//
//                AutocompletePrediction selectedPredication = predictionList.get(position);
//                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
//                materialSearchBar.setText(suggestion);
//                //close keyboard
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        materialSearchBar.clearSuggestions();
//                    }
//                }, 1000);
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//
//                    String placeId = selectedPredication.getPlaceId();
//                    List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
//
//                    FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
//                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//                        @Override
//                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
//                            Place place = fetchPlaceResponse.getPlace();
//                            Log.i("mytag", "Place found: " + place.getName());
//                            LatLng latLngOfPlace = place.getLatLng();
//                            if (latLngOfPlace != null) {
//                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            if (e instanceof ApiException) {
//                                ApiException apiException = (ApiException) e;
//                                apiException.printStackTrace();
//                                int statusCode = apiException.getStatusCode();
//                                Log.i("mytag", "place not found: " + e.getMessage());
//                                Log.i("mytag", "status code: " + statusCode);
//                            }
//
//                        }
//                    });
//
//
//                }
//            }
//
//            @Override
//            public void OnItemDeleteListener(int position, View v) {
//
//            }
//        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                currentMarkerLocation = mMap.getCameraPosition().target;
                rippleBg.startRippleAnimation();
                //call your server then stop rippple
//                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                req_location = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
//                mRequest.setText("Looking for an educator for you.");
//                Toast.makeText(student_main.this, "post booking", Toast.LENGTH_SHORT).show();
//                selected_grade = sNumber;
//                selected_topic = topic.getText().toString();
//                selected_location = mLastKnownLocation.toString();
//                selected_subject = subject.getText().toString();
//                String contact = "000 000 000";
//                String name = "studentName";
                //  Booking booking = new Booking(name, selected_grade, contact, selected_topic, selected_subject, selected_location);
                Toast.makeText(getApplicationContext(), "Booking in process", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(student_main.this, postBooking.class);
                intent.putExtra("type", "student");
                // intent.putExtra("booking", booking);
//                intent.putExtra("name", name);
//                intent.putExtra("grade",selected_grade );
//                intent.putExtra("subject", selected_subject);
//                intent.putExtra("loc", selected_location);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        rippleBg.stopRippleAnimation();
                        //startActivity(new Intent(student_main.this, MainActivity.class));
                        // finish(); //if we dont want user tocome back here
                    }
                }, 3000);


                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //to stop the ripple


            }
        });

    }
    //onCreate ends


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    //drawer stuff

    private void addDrawer() {
    }

    //when map is ready and loaded
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        getDeviceLocation();

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

        //change loc position
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            ImageView btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            btnMyLocation.setImageResource(R.drawable.locationbutton);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(200, 180, 60, 60);

        }


        //check if GPS enabled and then req user to enable

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(student_main.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(student_main.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                getDeviceLocation();

            }
        });

        task.addOnFailureListener(student_main.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(student_main.this, 51);

                        //give user the option to enabmle gps
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchOpened())
                    materialSearchBar.closeSearch();
                return false;
            }
        });


        //new code
    }
//onmapready


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {
//gets last location
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                            } else { // last location is null
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                //new code
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                                //new code
                                locationCallback = new LocationCallback() { //executed when an updated location is recieved
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);

                                        if (locationResult == null) {
                                            return;
                                        }

                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);

                                    }
                                };

                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(student_main.this, "Unable to get last location", Toast.LENGTH_SHORT);
                        }

                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        getDeviceLocation();
        mLastKnownLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }
}

