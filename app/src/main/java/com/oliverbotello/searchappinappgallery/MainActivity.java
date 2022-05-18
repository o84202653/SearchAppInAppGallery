package com.oliverbotello.searchappinappgallery;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String URI_INTENT_APP_GALLERY = "appmarket://details?id=";
    private static final String APP_GALLERY_PACKAGE = "com.huawei.appmarket";
    // UI Components
    private AppCompatEditText edttxtPackageName;
    private AppCompatImageView imgvwIcon;
    private AppCompatTextView txtvwAppName;
    private AppCompatTextView txtvwAppPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        edttxtPackageName = findViewById(R.id.edttxt_packagename);
        imgvwIcon = findViewById(R.id.imgvw_app_icon);
        txtvwAppName = findViewById(R.id.txtvw_app_name);
        txtvwAppPackageName = findViewById(R.id.txtvw_app_packagename);

        findViewById(R.id.btn_search_app).setOnClickListener(this);
    }

    private void searchApp(String packageName) {
        ApplicationInfo appInfo = findPackageName(packageName); // Get package info

        if (appInfo == null) { // If app is not installed
            showMessage(R.string.app_is_not_installed);
            goToAppGallery(packageName);

            return;
        }

        // Set package info
        imgvwIcon.setImageDrawable(appInfo.loadIcon(getPackageManager()));
        txtvwAppName.setText(appInfo.loadLabel(getPackageManager()));
        txtvwAppPackageName.setText(appInfo.packageName);
    }

    private ApplicationInfo findPackageName(String packageName) {
        try {
            // Try to get App Info
            return getPackageManager().getPackageInfo(packageName, 0).applicationInfo;
        } catch (PackageManager.NameNotFoundException e) {
            return null; // Return null if app is not installed
        }
    }

    private void goToAppGallery(String packageName) {
        // Init intent
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(URI_INTENT_APP_GALLERY + packageName)
        );
        // Get list of resolvers info
        List<ResolveInfo> otherApps = getPackageManager().queryIntentActivities(intent, 0);

        // Search for AppGallery
        for (ResolveInfo app : otherApps) {
            // If resolver is for AppGallery
            if (app.activityInfo.applicationInfo.packageName.equals(APP_GALLERY_PACKAGE)) {
                // Get Info
                ComponentName psComponent = new ComponentName(
                        app.activityInfo.applicationInfo.packageName,
                        app.activityInfo.name
                );
                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                );
                intent.setComponent(psComponent);
                startActivity(intent); // Init AppGallery

                return;
            }
        }

        // If AppGallery is not installed
        showMessage(R.string.appgallery_not_installed);
    }

    private boolean isPackageName(String packageName) {
        return packageName != null && !packageName.isEmpty(); // Verify if text is not empty
    }

    private void showMessage(@StringRes int id) {
        Toast.makeText(getApplicationContext(), getString(id), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        String packageName = edttxtPackageName.getText().toString(); // Get package name

        if (isPackageName(packageName)) // If text is a package name (structure)
            searchApp(packageName);
        else // If text is not a package name
            showMessage(R.string.insert_valid_package);
    }
}