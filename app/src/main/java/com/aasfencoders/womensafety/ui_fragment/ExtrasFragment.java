package com.aasfencoders.womensafety.ui_fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aasfencoders.womensafety.MainActivity;
import com.aasfencoders.womensafety.R;
import com.aasfencoders.womensafety.ShowPolice;
import com.aasfencoders.womensafety.utilities.CheckNetworkConnection;
import com.aasfencoders.womensafety.utilities.NetworkDialog;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ExtrasFragment extends Fragment {

    private RadioGroup radioGroup;
    private SharedPreferences sharedPreferences;
    private Button showPolice;
    LocationManager locationManager;
    LocationListener locationListener;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    checkGPS();
                    startShowPolice();
                    Log.i("############","1");
                }
            }
        }
    }

    private void getPermission() {

        boolean state = CheckNetworkConnection.checkNetwork(getContext());
        if (state) {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT < 23) {
                startShowPolice();
                Log.i("############","3");
            } else {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    checkGPS();
                }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startShowPolice();
                    Log.i("############","2");
                }
            }

        } else {
            NetworkDialog.showNetworkDialog(getContext());
        }

    }

    private void checkGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void startShowPolice() {
        Intent intent = new Intent(getContext() , ShowPolice.class);
        startActivity(intent);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extra,container,false);
        radioGroup = view.findViewById(R.id.sim_radio_group);
        if(getContext() != null){
            sharedPreferences = getContext().getSharedPreferences(getString(R.string.package_name), Context.MODE_PRIVATE);
        }

        String checked = sharedPreferences.getString(getString(R.string.SIM), getString(R.string.SIMNO));

        showPolice = view.findViewById(R.id.showPolice);

        showPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
            }
        });

        if(checked.equals(getString(R.string.SIM1))){
            radioGroup.check(R.id.sim1);
        }else if(checked.equals(getString(R.string.SIM2))){
            radioGroup.check(R.id.sim2);
        }else{
            radioGroup.check(R.id.sim_No);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.sim1:
                        sharedPreferences.edit().putString(getString(R.string.SIM), getString(R.string.SIM1)).apply();
                        break;
                    case R.id.sim2:
                        sharedPreferences.edit().putString(getString(R.string.SIM), getString(R.string.SIM2)).apply();
                        break;
                    case R.id.sim_No:
                        sharedPreferences.edit().putString(getString(R.string.SIM), getString(R.string.SIMNO)).apply();
                        break;
                }
            }
        });

        return view;
    }
}
