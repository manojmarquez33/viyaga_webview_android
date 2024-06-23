package com.viyaga.shoppe.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.viyaga.shoppe.AboutUs;
import com.viyaga.shoppe.Configs;
import com.viyaga.shoppe.R;
import com.viyaga.shoppe.Tools;
import com.downloader.PRDownloader;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.Nullable;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private static final int RC_APP_UPDATE = 100;
    private AdvancedWebView webView;
    private Configs configs;
    private ContentLoadingProgressBar loading;
    private SwipeRefreshLayout refresh;
    private String currentUrl;
    private DrawerLayout drawerLayout;
    private NavigationView navigation;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private RelativeLayout connectionError;
    private ActionBar bar;
    private TextView errorText;
    private View headerView;
    private ProgressBar circleLoading;
    private AppUpdateManager mAppUpdateManager;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUpdateManager mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, MainActivity.this
                                , RC_APP_UPDATE);

                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        PRDownloader.initialize(getApplicationContext());

        configs = new Configs(this);


        toolbar = findViewById(R.id.appBar);
        if (configs.isFullscreen()) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }

        webView = findViewById(R.id.web);
        loading = findViewById(R.id.loading);
        circleLoading = findViewById(R.id.circular_loading);
        refresh = findViewById(R.id.refresh);

        connectionError = findViewById(R.id.connection);
        ImageView errorChar = connectionError.findViewById(R.id.character);
        errorChar.setImageDrawable(getResources().getDrawable(configs.getErrorCharacter()));
        TextView errorMessage = connectionError.findViewById(R.id.message);
        errorMessage.setText(configs.getErrorMessage());

        errorText = connectionError.findViewById(R.id.error);
        errorText.setText(configs.getErrorTitle());

        drawerLayout = findViewById(R.id.drawer);
        navigation = findViewById(R.id.navigation);


        headerView = navigation.inflateHeaderView(R.layout.drawer_header);

        TextView drawerTitle = headerView.findViewById(R.id.drawer_title);
        TextView drawerDesc = headerView.findViewById(R.id.drawer_description);
        ImageView drawerIcon = headerView.findViewById(R.id.drawer_icon);
        drawerTitle.setText(configs.getSlidingTitle());
        drawerTitle.setTextColor(getResources().getColor(configs.getSlidingTitleColor()));
        drawerDesc.setText(configs.getSlidingDesc());
        drawerDesc.setTextColor(getResources().getColor(configs.getSlidingDescColor()));
        drawerIcon.setImageResource(configs.getSlidingIcon());

        setupToolbar();
        setupNavigationDrawer();

        refresh.setOnRefreshListener(() -> {
            webView.reload();
            refresh.setRefreshing(false);
        });

        loadWebPage(configs.getWebPages().get(0).getPageUrlAddress());


    }


    private void setupNavigationDrawer() {

        setNavigationTheme(configs.getColor());

        Menu menu = navigation.getMenu();

        MenuItem affiliate = menu.add("Affiliate");
        affiliate.setIcon(R.drawable.ic_person);


        SubMenu pages = menu.addSubMenu("Pages");

        for (int i = 0; i < configs.getWebPages().size(); i++) {
            int id = configs.getWebPages().get(i).getId();
            int icon = configs.getWebPages().get(i).getIcon();
            String title = configs.getWebPages().get(i).getTitle();
            pages.add(1, id, 0, title);
            pages.getItem(i).setIcon(icon);
            pages.getItem(i).setCheckable(true);


        }
        pages.getItem(0).setChecked(true);

        SubMenu info = menu.addSubMenu("Info");

        MenuItem aboutus = menu.add("About Us");
        aboutus.setIcon(R.drawable.ic_about);

        for (int i = 0; i < configs.getInfoPages().size(); i++) {
            int id = configs.getInfoPages().get(i).getId();
            int icon = configs.getInfoPages().get(i).getIcon();
            String title = configs.getInfoPages().get(i).getTitle();
            info.add(2, id, 0, title);
            info.getItem(i).setIcon(icon);
            info.getItem(i).setCheckable(true);
        }


        MenuItem share = menu.add("Share");
        share.setIcon(R.drawable.ic_share);
        MenuItem rate = menu.add("Rate us");
        rate.setIcon(R.drawable.ic_star);



        navigation.setNavigationItemSelectedListener(item -> {
            pages.getItem(0).setChecked(false);
            navigation.setCheckedItem(item.getItemId());
            switch (item.getGroupId()) {
                case 1:
                    loadWebPage(configs.getWebPages().get(item.getItemId()).getPageUrlAddress());
                    bar.setTitle(configs.getWebPages().get(item.getItemId()).getTitle());
                    break;
                case 2:
                    if (Patterns.EMAIL_ADDRESS.matcher(configs.getInfoPages().get(item.getItemId()).getPageUrlAddress()).matches()) {
                        String email = configs.getInfoPages().get(item.getItemId()).getPageUrlAddress();
                        emailIntent(email);
                    } else if (configs.getInfoPages().get(item.getItemId()).getPageUrlAddress().contains("geo:")) {
                        String location = configs.getInfoPages().get(item.getItemId()).getPageUrlAddress();
                        locationIntent(location);
                    } else if (configs.getInfoPages().get(item.getItemId()).getPageUrlAddress().contains("tel:")) {

                        Dexter.withContext(MainActivity.this)
                                .withPermission(Manifest.permission.CALL_PHONE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        String phoneNumber = configs.getInfoPages().get(item.getItemId()).getPageUrlAddress();
                                        phoneCallIntent(phoneNumber);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    }
                    break;
            }

            if (item.getTitle().equals("Share")) {
                share();
                Log.d("TAG", "onNavigationItemSelected: " + item.getItemId());
            } else if (item.getTitle().equals("Rate us")) {
                rateUs();
                Log.d("TAG", "onNavigationItemSelected: " + item.getItemId());
            } else if (item.getTitle().equals("About Us")) {
                aboutus();
                Log.d("TAG", "onNavigationItemSelected: " + item.getItemId());
            } else if (item.getTitle().equals("Affiliate")) {
                affiliate();
                Log.d("TAG", "onNavigationItemSelected: " + item.getItemId());
            } else   if (isDrawerOpen()) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            return true;
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerOpened(view);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        };

        mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(configs.getToolBarIconColor()));
    }

    private void affiliate() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://viyaga.com/login-register.php"));
        startActivity(browserIntent);

    }


    private void aboutus() {
        Intent intent = new Intent(MainActivity.this, AboutUs.class) ;
        startActivity(intent);

    }


    private void phoneCallIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
        startActivity(intent);
    }

    private void locationIntent(String location) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(location));
            startActivity(intent);
        } catch (NullPointerException npe) {
            Toast.makeText(MainActivity.this, "Sorry No Location Available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void emailIntent(String email) {
        try {
            String uri = "mailto:" + email;
            Log.d("TAG", "emailIntent: " + uri);
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
            intent.putExtra(Intent.EXTRA_TEXT, "Hi there, ");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setNavigationTheme(int c) {
        int color = getResources().getColor(c);
        loading.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        circleLoading.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        refresh.setColorSchemeColors(color);
        errorText.setTextColor(color);

        headerView.setBackgroundColor(color);

        navigation.setItemBackground(getItemBackground(c));

        navigation.setItemTextColor(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
                new int[]{color, getResources().getColor(R.color.gray)}));
        navigation.setItemIconTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
                new int[]{color, getResources().getColor(R.color.gray)}));

        getWindow().setStatusBarColor(color);
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
    private Drawable getItemBackground(int color) {
        Drawable bg;
        switch (color) {
            case R.color.blue:
                bg = getResources().getDrawable(R.drawable.blue_background);
                break;
            case R.color.green:
                bg = getResources().getDrawable(R.drawable.green_background);
                break;
            case R.color.red:
                bg = getResources().getDrawable(R.drawable.red_background);
                break;
            case R.color.yellow:
                bg = getResources().getDrawable(R.drawable.yellow_background);
                break;
            case R.color.pink:
                bg = getResources().getDrawable(R.drawable.pink_background);
                break;
            case R.color.orange:
                bg = getResources().getDrawable(R.drawable.orange_background);
                break;
            case R.color.purple:
                bg = getResources().getDrawable(R.drawable.purple_background);
                break;
            case R.color.teal:
                bg = getResources().getDrawable(R.drawable.teal_background);
                break;
            case R.color.blue_sky:
                bg = getResources().getDrawable(R.drawable.blue_sky_background);
                break;
            default:
                bg = getResources().getDrawable(R.drawable.default_background);
        }
        return bg;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebPage(String currentUrl) {
        webView.setListener(this, this);
        webView.setMixedContentAllowed(false);
        webView.loadUrl(currentUrl);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setTitle(configs.getWebPages().get(0).getTitle());
        }


        Tools.changeToolbarFont(this, toolbar, configs.getTitleStyle());
        toolbar.setTitleTextColor(getResources().getColor(configs.getTitleColor()));
        toolbar.setBackgroundColor(getResources().getColor(configs.getToolBarColor()));
    }

    private boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                share();
                break;
            case R.id.rate:
                rateUs();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void share() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, configs.getShareText() + "https://play.google.com/store/apps/details?id=com.viyaga.shoppe ");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            e.printStackTrace();
        }
    }

    private void rateUs() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.rate_us_view);

        RatingBar ratingBar = dialog.findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (rating >= 3) {
                try {
                    Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    dialog.dismiss();
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void showLoading() {
        if (configs.getLoadingStyle() == 0) {
            loading.setVisibility(View.VISIBLE);
        } else if (configs.getLoadingStyle() == 1) {
            circleLoading.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        if (configs.getLoadingStyle() == 0) {
            loading.setVisibility(View.INVISIBLE);
        } else if (configs.getLoadingStyle() == 1) {
            circleLoading.setVisibility(View.INVISIBLE);
        }
    }

    private void showConnectionError() {
        webView.setVisibility(View.INVISIBLE);
        connectionError.setVisibility(View.VISIBLE);
    }

    private void hideConnectionError() {
        webView.setVisibility(View.VISIBLE);
        connectionError.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void handleError() {
        //nada
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (!url.contains("file:///android_asset")) {
            if (isNetworkAvailable()) {
                showLoading();
                hideConnectionError();
                if (url != null) {
                    currentUrl = url;
                }
            } else {
                showConnectionError();
            }
        } else {
            hideConnectionError();
        }
    }

    @Override
    public void onPageFinished(String url) {
        webView.loadUrl("javascript:(function() { " +
                "var head = document.getElementsByTagName('header')[0];"
                + "head.parentNode.removeChild(head);" +
                "})()");
        hideLoading();
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        handleError();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }


    @Override
    public void onExternalPageRequest(String url) {
        //nada
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop()
    {
        if(mAppUpdateManager!=null) mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private InstallStateUpdatedListener installStateUpdatedListener =new InstallStateUpdatedListener()
    {
        @Override
        public void onStateUpdate(InstallState state)
        {
            if(state.installStatus() == InstallStatus.DOWNLOADED)
            {
                showCompletedUpdate();
            }
        }
    };

    private void showCompletedUpdate()
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"New app is ready!",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mAppUpdateManager.completeUpdate();
            }
        });
        snackbar.show();

    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    /* we can check without requestCode == RC_APP_UPDATE because
    we known exactly there is only requestCode from  startUpdateFlowForResult() */
        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK) {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}