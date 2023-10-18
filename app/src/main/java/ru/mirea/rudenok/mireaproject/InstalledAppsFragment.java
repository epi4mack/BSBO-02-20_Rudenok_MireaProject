package ru.mirea.rudenok.mireaproject;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class InstalledAppsFragment extends Fragment {

    public InstalledAppsFragment() {
        // Required empty public constructor
    }

    public static InstalledAppsFragment newInstance(String param1, String param2) {
        InstalledAppsFragment fragment = new InstalledAppsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_installed_apps, container, false);

        ListView listView = view.findViewById(R.id.appsView);

        PackageManager packageManager = requireActivity().getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        List<String> installed_apps = new ArrayList<>();
        for (ApplicationInfo appInfo : installedApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                installed_apps.add(appInfo.loadLabel(packageManager).toString());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, installed_apps);
        listView.setAdapter(adapter);

        return view;
    }

}