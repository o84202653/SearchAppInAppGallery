package com.oliverbotello.searchappinappgallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String APP_GALLERY_PACKAGE = "com.huawei.appmarket";
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

        ApplicationInfo appInfo = findPackageName(packageName);

        if (appInfo == null) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.app_is_not_installed),
                    Toast.LENGTH_LONG
            ).show();
            goToAppGallery(packageName);
            // go to appGallery
            return;
        }

        //get package info
        imgvwIcon.setImageDrawable(appInfo.loadIcon(getPackageManager()));
        txtvwAppName.setText(appInfo.loadLabel(getPackageManager()));
        txtvwAppPackageName.setText(appInfo.packageName);
    }

    private ApplicationInfo findPackageName(String packageName) {
        try {
            return getPackageManager().getPackageInfo(packageName, 0).applicationInfo;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void goToAppGallery(String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("appmarket://details?id=" + packageName));
        List<ResolveInfo> otherApps = getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo app : otherApps) {
            if (app.activityInfo.applicationInfo.packageName.equals(APP_GALLERY_PACKAGE)) {
                ComponentName psComponent = new ComponentName(app.activityInfo.applicationInfo.packageName, app.activityInfo.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setComponent(psComponent);
                startActivity(intent);

                break;
            }
        }
    }

    private boolean isPackageName(String packageName) {
        return packageName != null && !packageName.isEmpty();
    }

    @Override
    public void onClick(View v) {
        String packageName = edttxtPackageName.getText().toString();

        if (isPackageName(packageName))
            searchApp(packageName);
        else
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.insert_valid_package),
                    Toast.LENGTH_LONG
            ).show();
    }
}