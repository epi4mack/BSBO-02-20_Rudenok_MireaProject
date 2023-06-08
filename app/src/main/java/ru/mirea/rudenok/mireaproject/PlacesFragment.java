package ru.mirea.rudenok.mireaproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import ru.mirea.rudenok.mireaproject.databinding.FragmentPlacesBinding;

public class PlacesFragment extends Fragment {
    private FragmentPlacesBinding binding;
    private View root;
    private MapView mapView = null;
    private MyLocationNewOverlay locationNewOverlay;
    private static final int REQUEST_CODE_PERMISSION = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlacesBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        Configuration.getInstance().load(root.getContext().getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(root.getContext().getApplicationContext()));

        mapView = binding.mapView;
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(root.getContext().getApplicationContext(), new InternalCompassOrientationProvider(root.getContext().getApplicationContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);


        int cOARSE_LOCATION = ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int fINE_LOCATION = ContextCompat.checkSelfPermission(root.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (cOARSE_LOCATION == PackageManager.PERMISSION_GRANTED || fINE_LOCATION == PackageManager.PERMISSION_GRANTED) {
            setMyLocation();
        }

        Marker m1 = new Marker(mapView);
        m1.setPosition(new GeoPoint(55.802766,37.755572));
        m1.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Вкусно и точка\nФастфуд\nЩёлковское шоссе 2/1с1").setCancelable(false).setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
        mapView.getOverlays().add(m1);
        m1.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m1.setTitle("Вкусно и точка");

        Marker m2 = new Marker(mapView);
        m2.setPosition(new GeoPoint(55.810856, 37.800331));
        m2.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Натахтари\nКафе грузинской кухни\nЩёлковское шоссе, 75").setCancelable(false).setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
        mapView.getOverlays().add(m2);
        m2.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m2.setTitle("Натахтари");

        Marker m3 = new Marker(mapView);
        m3.setPosition(new GeoPoint(55.800008, 37.741064));
        m3.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Додо Пицца\nПиццерия\nБольшая Черкизовская ул., 30Б").setCancelable(false).setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
        mapView.getOverlays().add(m3);
        m3.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        m3.setTitle("Додо Пицца");

        return root;
    }

    protected void setMyLocation()
    {
        locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(root.getContext().getApplicationContext()), mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(this.locationNewOverlay);
        locationNewOverlay.runOnFirstFix(new Runnable() {
            public void run() {

                try {
                    double latitude = locationNewOverlay.getMyLocation().getLatitude();
                    double longitude = locationNewOverlay.getMyLocation().getLongitude();
                    Log.d("coord", String.valueOf(latitude));
                    Log.d("coord", String.valueOf(longitude));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IMapController mapController = mapView.getController();
                            mapController.setZoom(15.0);
                            GeoPoint startPoint = new GeoPoint(latitude, longitude);
                            mapController.setCenter(startPoint);
                        }
                    });
                }
                catch (Exception e) {}
            }
        });
    }
}