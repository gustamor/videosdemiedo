package net.laenredadera.full.peliculas.gratis.de.terror.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.FacebookSdk;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.InApp;
import net.laenredadera.full.peliculas.gratis.de.terror.R;
import net.laenredadera.full.peliculas.gratis.de.terror.Utils.AdvertisingActivities;

import java.util.List;

public class SplashPermissionsActivity extends AppCompatActivity {

    public String TAG = "SplashPermissionsActivity";

    private boolean isAdShowing;
    private final int secondsInSplashTimer = 10;
    private boolean adWereClosed;
    private InterstitialAd FANinterstitialAd;
    private int triesToLoadInter = 2;
    private InterstitialAdListener interstitialAdListener;
    private final boolean production = true;
    private final MyApp app = new MyApp();

    @SuppressWarnings("rawtypes")
    public Class getNextActivityClass() {
        return MainActivity.class;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_permissions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isNetworkAvailable();
        final int sdk = android.os.Build.VERSION.SDK_INT;
        try {
            if (sdk >= Build.VERSION_CODES.P) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (NullPointerException ignored) {}
        requestPermission();
        IronSource.getAdvertiserId(this);
        onRemoteConfig();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        nextActivity();
    }

   private void checkNetworkAvailability(){
        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashPermissionsActivity.this);
            builder.setTitle("Required network");
            builder.setMessage("This app require network connection. Please restart app with network up");
            builder.setNeutralButton("Confirm",
                    (dialogInterface, i) -> {
                        SplashPermissionsActivity.this.finish();
                        System.exit(0);
                    });
            builder.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashPermissionsActivity.this);
        builder.setTitle("Required Permissions");
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS",
                (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 101);
                });
        builder.setNegativeButton("Cancel",
                (dialog, which) -> {
                    dialog.cancel();
                    SplashPermissionsActivity.this.finish();
                    System.exit(0);
                });
        builder.show();
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            checkNetworkAvailability();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            openSettingsDialog();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).
                withErrorListener(
                        error -> Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    public void nextActivity(){
            Intent mainIntent = new Intent(SplashPermissionsActivity.this, getNextActivityClass());
            String packageName =  SplashPermissionsActivity.this.getPackageName();
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    |Intent.FLAG_ACTIVITY_NEW_TASK);
            mainIntent.setData(Uri.parse("package:" + packageName));
            SplashPermissionsActivity.this.startActivity(mainIntent);
            finish();
   }

  private void showFANInterstitial(){
      if (FANinterstitialAd != null && FANinterstitialAd.isAdLoaded() ) {
          FANinterstitialAd.show();
      } else {
          nextActivity();
      }
  }

    private void requestFANInterstitialAd() {
        try {
            FANinterstitialAd = new com.facebook.ads.InterstitialAd(this, app.getFANInterstitialSP()) ;
            interstitialFANAdListener();
            FANinterstitialAd.loadAd(FANinterstitialAd.buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build());
        } catch (Exception ignored) {}
    }

    private void interstitialFANAdListener(){
        try {
            interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) { }
                @Override
                public void onInterstitialDismissed(Ad ad) {
                    nextActivity();                }
                @Override
                public void onError(Ad ad, AdError adError) {
                    if (triesToLoadInter > 0) {
                        requestFANInterstitialAd();
                        triesToLoadInter -= 1;
                    }
                    else if (app.mediation().equals(InApp.FANIRON)) {
                        if (IronSource.isInterstitialReady()) {
                            isAdShowing = true;
                            IronSource.showInterstitial("DefaultInterstitial");
                        } else {
                            nextActivity();
                        }
                    } else {
                       nextActivity();
                    }
                }
                @Override
                public void onAdLoaded(Ad ad) {
                    try {
                        showFANInterstitial();
                    }catch (Exception e){
                        nextActivity();
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                    //nextActivity();
                }
                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };
        } catch (Exception ignored) {
            nextActivity();
        }

    }

    private void requestIronInterstitialAd() {
        setInterstitialAdListener();
    }

    private void setInterstitialAdListener() {
        try {
            IronSource.setInterstitialListener(new InterstitialListener() {
                @Override
                public void onInterstitialAdReady() {
                    try {
                        isAdShowing = true;
                        if (app.mediation().equals(InApp.IRONSOURCE)) {
                            IronSource.showInterstitial("DefaultInterstitial");
                        }
                    } catch (Exception e){
                        nextActivity();
                    }
                }

                @Override
                public void onInterstitialAdLoadFailed(IronSourceError error) {
                    if (app.mediation().equals(InApp.IRONSOURCE)) {
                        nextActivity();
                        isAdShowing = false;
                    }
                }

                @Override
                public void onInterstitialAdOpened() {
                }

                @Override
                public void onInterstitialAdClosed() {
                    isAdShowing = false;
                    adWereClosed = true;
                    nextActivity();
                }

                @Override
                public void onInterstitialAdShowFailed(IronSourceError error)
                {
                    adWereClosed = true;
                    nextActivity();
                }

                @Override
                public void onInterstitialAdClicked(){
                }

                @Override
                public void onInterstitialAdShowSucceeded() {
                }

            });
            IronSource.loadInterstitial();

        } catch (Exception e) {
            if (app.mediation().equals(InApp.IRONSOURCE)
                    || app.mediation().equals(InApp.FANIRON) ) {
                nextActivity();

            }
        }
    }

    private void loadIronSourceSDK() {
        IronSource.getAdvertiserId(this);
        String appKey = "103a8fa29";
        IronSource.init(this, appKey,IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.BANNER);
        IronSource.setMetaData("is_child_directed","false");
    }

    private void loadFacebookSDK() {
        FacebookSdk.fullyInitialize();
    }
    public static void sendPromotonialEmailIntent(final Context context, String email, String subject, String title, String message) {
    }

    void sendEmailToSupport() {
            String email ="riseagain.apps@gmail.com";
            String subject =  app.getAppContext().getResources().getString(R.string.firebase_crash_subject);
            String title = app.getAppContext().getResources().getString(R.string.app_name);
            String message = "\n\n\n\n------------\n" + getDeviceName();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        this.startActivity(Intent.createChooser(emailIntent, title));

    }
    public String getDeviceName() {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                return capitalize(model);
            } else {
                return capitalize(manufacturer) + " " + model;
            }
     }

     private String capitalize(String s) {
            if (s == null || s.length() == 0) {
                return "";
            }
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                return s;
            } else {
                return Character.toUpperCase(first) + s.substring(1);
            }
     }

    private void remoteConfigCrashedDialog() {
         new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCrashed))
                .setIcon(getResources().getDrawable(R.drawable.info))
                .setTitle(app.getAppContext().getResources().getString(R.string.without_config_title))
                .setMessage(app.getAppContext().getResources().getString(R.string.without_config_message))
                .setPositiveButton(app.getAppContext().getResources().getString(R.string.try_again), (dialog, which) ->
                        sendEmailToSupport())
                .setNegativeButton(app.getAppContext().getResources().getString(R.string.cancel), (dialog, which) -> {
                    dialog.cancel();
                    SplashPermissionsActivity.this.finish();
                    System.exit(0);
                }).show();

    }
    private void onRemoteConfig(){

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this,
                        task -> {
                        if (task.isSuccessful()) {
                                boolean interstitialEnabled = mFirebaseRemoteConfig.getBoolean("INTERSTITIAL_ENABLED");
                                boolean bannerEnabled = mFirebaseRemoteConfig.getBoolean("BANNER_FAN_ENABLED");
                                boolean isItemClickEnabled = mFirebaseRemoteConfig.getBoolean("ADS_ITEM_CLICK_ENABLED");
                                int clicksItemHeadersParaInters = (int) mFirebaseRemoteConfig.getLong("CADA_X_CLICKS_EN_ITEM");
                                int numVisitsToRate =  (int) mFirebaseRemoteConfig.getLong("NUM_VISITS_TO_RATE");
                                int drawerHeaderRandomOffset = (int) mFirebaseRemoteConfig.getLong("DRAWER_IMAGE_RANDOM_OFFSET");
                                String imageNavigation = mFirebaseRemoteConfig.getString("IMAGE_NAVIGATION");
                                String imageFavorites = mFirebaseRemoteConfig.getString("IMAGE_FAVORITES");
                                boolean castEnabled = mFirebaseRemoteConfig.getBoolean("CAST_ENABLED");
                                boolean auxuno = mFirebaseRemoteConfig.getBoolean("AUXUNO");
                                String inAppAdsNetwork = mFirebaseRemoteConfig.getString("ADS_NETWORK");
                                app.setMediation(inAppAdsNetwork);
                                boolean adsFirstButtonClickEnabled = mFirebaseRemoteConfig.getBoolean("FIRST_CLICK");
                                boolean isFANInterstitialSPEnabled = mFirebaseRemoteConfig.getBoolean("INTERSTITIAL_SP_ENABLED");

                                if (inAppAdsNetwork.equals(InApp.IRONSOURCE)) {
                                    loadIronSourceSDK();
                                } else if (inAppAdsNetwork.equals(InApp.FACEBOOK)){
                                    loadFacebookSDK();
                                    boolean isFANEnabled = mFirebaseRemoteConfig.getBoolean("FAN_PROVIDEER");
                                    String GP_Audiences_Banner = mFirebaseRemoteConfig.getString("FAN_NATIVE_BANNER");
                                    String GP_Audiences_Interstitial = mFirebaseRemoteConfig.getString("FAN_INTERSTITIAL_ID");
                                    String GP_Audiences_Interstitial_SP = mFirebaseRemoteConfig.getString("INTERSTITIAL_S");
                                    app.setFANBanner(GP_Audiences_Banner);
                                    app.setFANInterstitial(GP_Audiences_Interstitial);
                                    app.setFANInterstitialSP(GP_Audiences_Interstitial_SP);
                                    app.setIsFANEnabled(isFANEnabled);
                                } else if (inAppAdsNetwork.equals(InApp.FANIRON)) {
                                    loadIronSourceSDK();
                                    loadFacebookSDK();
                                    boolean isFANEnabled = mFirebaseRemoteConfig.getBoolean("FAN_PROVIDEER");
                                    String GP_Audiences_Banner = mFirebaseRemoteConfig.getString("FAN_NATIVE_BANNER");
                                    String GP_Audiences_Interstitial = mFirebaseRemoteConfig.getString("FAN_INTERSTITIAL_ID");
                                    String GP_Audiences_Interstitial_SP = mFirebaseRemoteConfig.getString("INTERSTITIAL_S");
                                    app.setFANBanner(GP_Audiences_Banner);
                                    app.setFANInterstitial(GP_Audiences_Interstitial);
                                    app.setFANInterstitialSP(GP_Audiences_Interstitial_SP);
                                    app.setIsFANEnabled(isFANEnabled);
                                }

                                int playerInterfreq = (int) mFirebaseRemoteConfig.getLong("PLAYER_INTER_FREQ");
                                String jwt = mFirebaseRemoteConfig.getString("JWT");
                                String endpoint = mFirebaseRemoteConfig.getString("BACKEND");
                                app.setJwt(jwt);
                                app.setAuxUno(auxuno);
                                app.setInterstitialAdEnabled(interstitialEnabled);
                                app.setBannerAdEnabled(bannerEnabled);
                                app.setItemClickEnabled(isItemClickEnabled);
                                app.setIsAdsFirstClickEnabled(adsFirstButtonClickEnabled);
                                app.setIsInterSPEnabled(isFANInterstitialSPEnabled);
                                app.setNumberOfVisitsToShowRating(numVisitsToRate);
                                app.setClicksItemHeadersInterstitialAd(clicksItemHeadersParaInters);
                                app.setDrawerHeaderRandomOffset(drawerHeaderRandomOffset);
                                app.setImageNavigationName(imageNavigation);
                                app.setImageFavoritesName(imageFavorites);
                                app.setIsCastEnabled(castEnabled);
                                app.setPlayerInterstitialAdFrequency(playerInterfreq);
                                app.setMainAdEnabled(false);
                                app.setBannerAdEnabled(false);
                               if ( app.isInterstitialAdEnabled() == production) {
                                   if (inAppAdsNetwork.equals(InApp.IRONSOURCE)) {
                                       loadIronSourceSDK();
                                       requestIronInterstitialAd();
                                   } else if   (inAppAdsNetwork.equals(InApp.FACEBOOK))  {
                                       loadFacebookSDK();
                                       requestFANInterstitialAd();
                                   } else if (inAppAdsNetwork.equals(InApp.FANIRON))  {
                                       loadIronSourceSDK();
                                       loadFacebookSDK();
                                       requestIronInterstitialAd();
                                       requestFANInterstitialAd();
                                   }
                               } else {
                                    nextActivity();
                                }
                            } else {
                                remoteConfigCrashedDialog();
                            }
                        });
    }
}

