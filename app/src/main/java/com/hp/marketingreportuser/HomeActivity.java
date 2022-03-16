package com.hp.marketingreportuser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hp.marketingreportuser.databinding.ActivityHomeBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements LocationListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private AppBarConfiguration mAppBarConfiguration;
    public ActivityHomeBinding binding;
    public String name, email, mobNo, verificationDoc, routeAssign, dob, pwd;
    private TextView txtViewUserName;
    NavController navController;
    FusedLocationProviderClient fusedLocationProviderClient;
    public String ad = null,currdate;
    int mYear;
    viewModel viewModel;
    List<String> storeNames;
    LocationManager locationManager;
    MenuItem menuItem,menuItem1;
    SearchView searchView;
    BottomNavigationView bottomNavigation;
    MaterialButton btnTryAgain;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLocationProviderEnable();
        viewModel = new ViewModelProvider(HomeActivity.this).get(viewModel.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigation = binding.appBarHome.bottomNavigation;
        setSupportActionBar(binding.appBarHome.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.HomeFragment, R.id.UserTimelineFragment,R.id.AddUserTaskFragment,R.id.StoresFragment,R.id.MyProfileFragment,R.id.SettingsFragment)
                .setOpenableLayout(drawer)
                .build();
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_home);
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        SharedPreferences sharedPreferences = getSharedPreferences("profile", MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("authentication", MODE_PRIVATE);
        if (sharedPreferences.contains("name") && !sharedPreferences.getString("name", "").equals("")) {
            name = sharedPreferences.getString("name", "");
            mobNo = sharedPreferences.getString("mobNo", "");
            Log.v("mobNo",mobNo);
            email = sharedPreferences.getString("email", "");
            verificationDoc = sharedPreferences.getString("verificationDoc", "");
            dob = sharedPreferences.getString("dob", "");
            routeAssign = sharedPreferences.getString("routeAssign", "");
            View headerView = navigationView.getHeaderView(0);
            txtViewUserName = headerView.findViewById(R.id.txtViewUserName);
            txtViewUserName.setText(name.toUpperCase(Locale.ROOT));
        } else if (sharedPreferences1.contains("mobNo") && !sharedPreferences1.getString("mobNo", "").equals("")) {
            mobNo = sharedPreferences1.getString("mobNo", "");
            firebaseFirestore.collection("marketingPerson").document(mobNo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", documentSnapshot.getString("name"));
                        editor.putString("mobNo", mobNo);
                        editor.putString("email", documentSnapshot.getString("email"));
                        editor.putString("verificationDoc", documentSnapshot.getString("verificationDoc"));
                        final Calendar c = Calendar.getInstance();
                        Date dateRepresentations = c.getTime();
                        Timestamp timestamp = new Timestamp(dateRepresentations);
                        String[] dateParams = timestamp.toDate().toString().split(" ");
                        editor.putString("dob", dateParams[2]+" "+dateParams[1]+" "+dateParams[5]);
                        editor.putString("routeAssign", documentSnapshot.getString("routeAssign"));
                        editor.apply();
                        name = sharedPreferences.getString("name", "");
                        mobNo = sharedPreferences.getString("mobNo", "");
                        email = sharedPreferences.getString("email", "");
                        verificationDoc = sharedPreferences.getString("verificationDoc", "");
                        dob = sharedPreferences.getString("dob", "");
                        routeAssign = sharedPreferences.getString("routeAssign", "");
                    } else {
                        Toast.makeText(HomeActivity.this, "No Data Found", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, "No Data Found", Toast.LENGTH_LONG).show();

                }
            });
        }
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        String mMonth = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH).substring(0, 3);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        currdate = mDay + " " + mMonth + " " + mYear;
        loadData(String.valueOf(mYear));
        binding.appBarHome.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_home).navigate(R.id.HomeFragment);
                    break;
                case R.id.timeline:
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_home).navigate(R.id.UserTimelineFragment);
                    break;
                case R.id.add:
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_home).navigate(R.id.AddUserTaskFragment);
                    break;
                case R.id.store:
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_home).navigate(R.id.StoresFragment);
                    break;
                case R.id.myprofile:
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_home).navigate(R.id.MyProfileFragment);
                    break;
            }
            return true;
        });
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                switch (navDestination.getId()) {
                    case R.id.HomeFragment:
                        chkInternetSpeed();
                        binding.appBarHome.bottomNavigation.setVisibility(View.VISIBLE);
                        bottomNavigation.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case R.id.UserTimelineFragment:
                        chkInternetSpeed();
                        binding.appBarHome.bottomNavigation.setVisibility(View.VISIBLE);
                        bottomNavigation.getMenu().findItem(R.id.timeline).setChecked(true);
                        break;
                    case R.id.AddUserTaskFragment:
                        chkInternetSpeed();
                        binding.appBarHome.bottomNavigation.setVisibility(View.VISIBLE);
                        bottomNavigation.getMenu().findItem(R.id.add).setChecked(true);
                        break;
                    case R.id.StoresFragment:
                        chkInternetSpeed();
                        binding.appBarHome.bottomNavigation.setVisibility(View.VISIBLE);
                        bottomNavigation.getMenu().findItem(R.id.store).setChecked(true);
                        break;
                    case R.id.MyProfileFragment:
                        chkInternetSpeed();
                        binding.appBarHome.bottomNavigation.setVisibility(View.VISIBLE);
                        bottomNavigation.getMenu().findItem(R.id.myprofile).setChecked(true);
                        break;
                    default:
                        binding.appBarHome.bottomNavigation.setVisibility(View.GONE);
                }
            }
        });
        btnTryAgain = findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkInternetSpeed();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        getLocation();
                    }
                }
            }
    );

    public void loadData(String year) {
        firebaseFirestore.collection("dailyReport").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                storeNames =  new ArrayList<>();
                int totalVisits = 0, todayVisits = 0;
                float Jan = 0, Feb = 0, Mar = 0, Apr = 0, May = 0, Jun = 0, Jul = 0, Aug = 0, Sep = 0, Oct = 0, Nov = 0, Dec = 0;

                for (DocumentSnapshot documentSnapshot : list) {
                    if(documentSnapshot.getString("salesmanMobNo").contains(mobNo)){
                        if(!storeNames.contains(documentSnapshot.getString("storeName"))){
                            storeNames.add(documentSnapshot.getString("storeName"));
                        }
                        Timestamp timestamp = (Timestamp) documentSnapshot.get("date");
                        String[] dateParams = timestamp.toDate().toString().split(" ");
                        String taskDate = dateParams[2] + " " + dateParams[1] + " " + dateParams[5];
                        String chkDate = timestamp.toDate().toString();
                        if (chkDate.contains("Jan") && chkDate.contains(year)) {
                            Jan++;
                        } else if (chkDate.contains("Feb") && chkDate.contains(year)) {
                            Feb++;
                        } else if (chkDate.contains("Mar") && chkDate.contains(year)) {
                            Mar++;
                        } else if (chkDate.contains("Apr") && chkDate.contains(year)) {
                            Apr++;
                        } else if (chkDate.contains("May") && chkDate.contains(year)) {
                            May++;
                        } else if (chkDate.contains("Jun") && chkDate.contains(year)) {
                            Jun++;
                        } else if (chkDate.contains("Jul") && chkDate.contains(year)) {
                            Jul++;
                        } else if (chkDate.contains("Aug") && chkDate.contains(year)) {
                            Aug++;
                        } else if (chkDate.contains("Sep") && chkDate.contains(year)) {
                            Sep++;
                        } else if (chkDate.contains("Oct") && chkDate.contains(year)) {
                            Oct++;
                        } else if (chkDate.contains("Nov") && chkDate.contains(year)) {
                            Nov++;
                        } else if (chkDate.contains("Dec") && chkDate.contains(year)) {
                            Dec++;
                        }
                        if (taskDate.contains(currdate)) {
                            todayVisits++;
                        }
                        totalVisits++;
                    }
                }
                HashMap<String, Float> monthlyDataCount = new HashMap<String, Float>();
                monthlyDataCount.put("Jan", Jan);
                monthlyDataCount.put("Feb", Feb);
                monthlyDataCount.put("Mar", Mar);
                monthlyDataCount.put("Apr", Apr);
                monthlyDataCount.put("May", May);
                monthlyDataCount.put("Jun", Jun);
                monthlyDataCount.put("Jul", Jul);
                monthlyDataCount.put("Aug", Aug);
                monthlyDataCount.put("Sep", Sep);
                monthlyDataCount.put("Oct", Oct);
                monthlyDataCount.put("Nov", Nov);
                monthlyDataCount.put("Dec", Dec);
                viewModel.setMonthlyDataCount(monthlyDataCount);
                viewModel.setTodayVisits(todayVisits);
                viewModel.setTotalVisits(totalVisits);
                viewModel.setTotalStores(storeNames.size());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(getResources().getColor(R.color.black));
        editText.setHintTextColor(getResources().getColor(R.color.black));
        ImageView imageView1 = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        imageView1.setImageDrawable(getDrawable(R.drawable.close));
        imageView1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        menuItem1 = menu.findItem(R.id.action_notification);
        SharedPreferences sharedPreferences = getSharedPreferences("msgRecieved", Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(sharedPreferences.getAll().isEmpty()){
                    menuItem1.setIcon(R.drawable.notification_icon);
                }else{
                    menuItem1.setIcon(R.drawable.notification_active_icon);
                }
            }
        });
        Map<String, ?> notifications = new HashMap<>();
        notifications =  sharedPreferences.getAll();
        if(notifications.isEmpty()){
            menuItem1.setIcon(R.drawable.notification_icon);
        }else{
            menuItem1.setIcon(R.drawable.notification_active_icon);
        }
        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment_content_home).navigate(R.id.NotificationFragment);
                return true;
            }
        });
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                switch (navDestination.getId()) {
                    case R.id.UserTimelineFragment:
                    case R.id.StoresFragment:
                        menuItem.setVisible(true);
                        menuItem1.setVisible(false);
                        break;
                    case R.id.HomeFragment:
                        menuItem.setVisible(false);
                        menuItem1.setVisible(true);
                        break;
                    default:
                        menuItem.setVisible(false);
                        menuItem1.setVisible(false);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    boolean isLocationPermitted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //getLocation();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Allow Location Permission")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Location Service is require to add user task's current location")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermission();
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        }
    }

    public boolean isLocationProviderEnable(){
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this,R.style.AlertDialog);
            alert.setTitle("Location Service Disabled")
                    .setIcon(getDrawable(R.drawable.alert))
                    .setMessage("Please turn on Location Service and try again.");
            alert.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activityResultLauncher.launch(intent);
                }
            }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alert.show().dismiss();
                    AlertDialog.Builder alertRationale = new AlertDialog.Builder(HomeActivity.this, R.style.AlertDialog);
                    alertRationale.setTitle("Location Service")
                            .setMessage("Location Service is require to add user task's current location")
                            .setIcon(getDrawable(R.drawable.alert))
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    activityResultLauncher.launch(intent);
                                }
                            }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertRationale.show().dismiss();
                        }
                    });
                    alertRationale.show();
                }
            });
            alert.show();
            return false;
        }else{
            return true;
        }
    }

    @SuppressLint({"ServiceCast", "MissingPermission"})
    public void getLocation() {
        final Location[] returnLocation = {null};
        // if(isLocationProviderOn())
        if (isLocationPermitted() && isLocationProviderEnable()) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        returnLocation[0] = location;
                        Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            ad = addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getLocality() + " " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getCountryCode() + " " + addresses.get(0).getPostalCode() + " " + addresses.get(0).getLongitude() + " " + addresses.get(0).getLatitude();
                            Toast.makeText(HomeActivity.this, ad, Toast.LENGTH_SHORT).show();
                            viewModel.setItem(ad);
                            viewModel.setLongitude(addresses.get(0).getLongitude());
                            viewModel.setLatitude(addresses.get(0).getLatitude());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            requestPermission();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
        Toast.makeText(this, "provider", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        Toast.makeText(this, "provider", Toast.LENGTH_SHORT).show();
    }

   /* public static String getConnectionStatus(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null || !info.isConnected()) {
            return "NO_CONNECTIVITY";
        } else if (getInternetStatus(info.getType(), info.getSubtype(), context) == 3) {
            return "GOOD_STRENGTH";
        } else if (getInternetStatus(info.getType(), info.getSubtype(), context) >= 2) {
            return "FAIR_STRENGTH";
        } else {
            Toast.makeText(context, "Network strength is poor, Data may be not loaded on Realtime", Toast.LENGTH_LONG).show();
            return "POOR_STRENGTH";
        }
    }

    @SuppressLint("MissingPermission")
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    public static int getInternetStatus(int type, int subType, Context context) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int numberOfLevels = 5;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            if (level < 2) {
                return 2; //Fair
            } else {
                return 3; //Good
            }
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return 1; //Poor
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return 2; //Fair
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return 3; //Good
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return 0; //No Connection
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }*/

    public void chkInternetSpeed() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        /*if (getConnectionStatus(this).equals("NO_CONNECTIVITY")) {
            findViewById(R.id.error).setVisibility(View.VISIBLE);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondary));
        }else{
            findViewById(R.id.error).setVisibility(View.GONE);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        }*/
        if(networkChkClass.chkInternetSpeed(this)){
            findViewById(R.id.error).setVisibility(View.GONE);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        }else{
            findViewById(R.id.error).setVisibility(View.VISIBLE);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.secondary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chkInternetSpeed();
    }


}