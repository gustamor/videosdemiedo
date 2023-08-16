package net.laenredadera.full.peliculas.gratis.de.terror.App;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import androidx.multidex.MultiDex;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import com.getkeepsafe.relinker.MissingLibraryException;
import com.onesignal.OneSignal;

public class MyApp extends Application {
    private static boolean mainAdEnabled;
    private static boolean isComebackFromOtherApp;
    private static boolean isAdsFirstClickEnabled;
    private static int clicksItemHeaderInterstitialAd;
    private static boolean interstitialEnabled;
    private static int countItemHeaderClick;
    private static int countPosterClick;
    private static int visit;
    private static boolean isItemClickEnabled;
    private static int numberOfVisitsToShowRating;
    private static int getDrawerHeaderRandomOffset;
    private static String imageNavigationName;
    private static String imageFavoritesName;
    private static boolean isRatingHidden;
    private static boolean castEnabled;
    private static int PLAYER_INTER_FREQUENCY;
    private static boolean auxuno;
    private static String jwt;
    private static SharedPreferences sharedPreferences;
    private static Context context;
    public  static String orderedBy;
    private static boolean bannerAdEnabled;
    private String GP_Audiences_Player_BannerId;
    private  boolean isFANEnabled;
    private com.facebook.ads.AdView bannerMainAdAudiencesId;
    private String GP_Audiences_Main_IntestitialId_SP;
    private String GP_Audiences_Main_IntestitialId;
    private static String mediation;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm realm;
        try {
            Realm.init(getApplicationContext());
            setUpRealConfig();
            realm = Realm.getDefaultInstance();
            realm.close();
        } catch (MissingLibraryException mle) {
            try {
                Realm.init(getApplicationContext());
                setUpRealConfig();
                realm = Realm.getDefaultInstance();
                realm.close();
            } catch (Exception e) {
                System.exit(1);
            }
        }

        mainAdEnabled = false;
        isComebackFromOtherApp = false;
        isAdsFirstClickEnabled = false;
        countItemHeaderClick = 0;
        countPosterClick = 0;
        this.orderedBy = "desc";
        context = getApplicationContext();

        try {
            OneSignal.initWithContext(this);
            OneSignal.unsubscribeWhenNotificationsAreDisabled(false);
        } catch (Exception ignored){}
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        addOneVisitMoreToUser();
    }

    public void setIsCastEnabled(boolean state) {
        castEnabled = state;
    }
    public boolean isCastEnabled(){
        return castEnabled;
    }

    public void addOneVisitMoreToUser(){
        try {
            visit = sharedPreferences.getInt("numberOfVisit",0)  ;
            visit += 1;
            sharedPreferences.edit().putInt("numberOfVisit", visit);
            sharedPreferences.edit().apply();
        } catch (NullPointerException npe) {

        }

    }

    public int getNumberOfVisits(){
        return visit;
    }
    public void setNumberOfVisitsToShowRating(int visit){
       numberOfVisitsToShowRating = visit;
    }
    public int getNumberOfVisitsToShowRating() {
        return numberOfVisitsToShowRating;
    }
    public boolean isStartRatingDialogClicked(){
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean("isRatingDialogShowed", false);
        }
        return false;
    }
    public static void setRatingdDialogClicked(boolean state){
        try {
            sharedPreferences.edit().putBoolean("isRatingDialogShowed", state);
            sharedPreferences.edit().apply();
        } catch (NullPointerException npe) {

        }

    }
    public void setMediation(String inAppAdsNetwork) {
        mediation = inAppAdsNetwork;
    }
    public String mediation(){
        if (mediation == null) return " ";
        return mediation;
    }
    public int getPlayerInterstitialAdFrequency() {
        return PLAYER_INTER_FREQUENCY;
    }
    public void setPlayerInterstitialAdFrequency(int freq) {
        PLAYER_INTER_FREQUENCY = freq;
    }
    public void setMainAdEnabled(boolean state){
        mainAdEnabled = state;
    }
    public void setInterstitialAdEnabled(boolean state){
        interstitialEnabled = state;
    }
    public boolean isInterstitialAdEnabled(){
        return  interstitialEnabled;
    }
    public void setBannerAdEnabled(boolean state){
        bannerAdEnabled = state;
    }
    public boolean isBannerAdEnabled(){
        return bannerAdEnabled;
    }
    public static String getScreenSize() {
        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return "xlarge";
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                return "undefined";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "large";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "small";
            default:
                return "";
        }

    }

    public void setJwt(String jwtToken) {
        jwt = jwtToken;
    }
    public String  getJwt() {
        return jwt;
    }
    public String getAdapterOrdered(){
        return orderedBy;
    }
    public void setAdapterOrdered(String orderType){orderedBy = orderType;}
    public Context getAppContext() {
        return this.context;
    }
    public static int getScreenOrientation(Activity activity){
        return activity.getResources().getConfiguration().orientation;
    }

    // FAN
    public void setIsInterSPEnabled(boolean enabled){
    }

    public  void setItemClickEnabled(boolean enabled){
        isItemClickEnabled = enabled;
    }
    public static boolean isItemClickEnabled(){
        return isItemClickEnabled;
    }
    public void setAuxUno(boolean state) {
        auxuno = state;
    }
    public boolean auxUno() {
        return auxuno;
    }

    public void addOneClickToAds(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int clicks = getClicksInAds();
        editor.putInt("clickInAds", clicks + 1);
        editor.apply();
    }
    public void setClicksInAds(int clicks){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("clickInAds", clicks);
        editor.apply();
    }
    public int getClicksInAds(){
        return sharedPreferences.getInt("clickInAds",0);
    }
    public void setIsRatingMustBeHidden(boolean enabled){
        isRatingHidden = enabled;
    }
    public  boolean isRatingMustBeHidden(){
        return isRatingHidden;
    }
    public String getFANBannerId(){
        return GP_Audiences_Player_BannerId;
    }
    public void setFANBanner(String id) {
        if (id != null) {
            GP_Audiences_Player_BannerId = id;
        } else  {
            GP_Audiences_Player_BannerId = "";
        }
    }

    public void setFANInterstitialSP(String id) {
        if (id != null) {
            GP_Audiences_Main_IntestitialId_SP = id;
        } else  {
            GP_Audiences_Main_IntestitialId_SP = "";
        }
    }
    public String getFANInterstitialSP(){
        return GP_Audiences_Main_IntestitialId_SP;
    }
    public void setFANInterstitial(String id) {
        if (id != null) {
            GP_Audiences_Main_IntestitialId = id;
        } else  {
            GP_Audiences_Main_IntestitialId = "";
        }
    }
    public String getFANInterstitial(){
        return GP_Audiences_Main_IntestitialId;
    }
    public void requestMainFANBannerAd(Context context) {
        com.facebook.ads.AdSize size;
        switch (getScreenSize()) {
            case "normal":
                size = com.facebook.ads.AdSize.BANNER_HEIGHT_50;
                break;
            case "large":
                size = com.facebook.ads.AdSize.BANNER_HEIGHT_90;
                break;
            case "xlarge":
            default:
                size = com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250;
                break;
        }
        bannerMainAdAudiencesId = new com.facebook.ads.AdView(context, getFANBannerId(),size);
    }

    public com.facebook.ads.AdView getFANBannerAd() {
        if (bannerMainAdAudiencesId != null) return bannerMainAdAudiencesId;
        return null;
    }
    public void setIsFANEnabled(boolean enabled){
         isFANEnabled = enabled;
    }

    // END FAN
    public void setGoToOtherApp(boolean state){
        isComebackFromOtherApp = state;
    }
    public boolean isIsComebackFromOtherApp(){
        return isComebackFromOtherApp;
    }
    public void setIsAdsFirstClickEnabled(boolean state){
        isAdsFirstClickEnabled = state;
    }
    public boolean isIsAdsFirstClickEnabled(){
        return isAdsFirstClickEnabled;
    }
    public void setClicksItemHeadersInterstitialAd(int clicks) {
        clicksItemHeaderInterstitialAd = clicks;
    }
    public int getClicksItemHeadersInterstitialAd() {
        return clicksItemHeaderInterstitialAd;
    }
    public void addPosterOneClick(){
        countPosterClick++;
    }
    public void setPosterCountClick(int n){
        countPosterClick = n;
    }
    public int getPosterClicks(){
        return countPosterClick;
    }
    public void addItemHeaderOneClick(){
        countItemHeaderClick++;
    }
    public void setItemHeaderCountClick(int n){
        countItemHeaderClick = n;
    }

    public int getItemHeaderClicks(){
        return countItemHeaderClick;
    }

    public void setDrawerHeaderRandomOffset(int offset) {
        getDrawerHeaderRandomOffset = offset;
    }
    public int getDrawerHeaderRandomOffset() {
        return getDrawerHeaderRandomOffset;
    }

    public void setImageNavigationName(String imageName) {
         imageNavigationName = imageName;
    }

    public String getImageNavigationName(){ return imageNavigationName;}

    public void setImageFavoritesName(String imageName) {
        imageFavoritesName = imageName;
    }

    public  String getImageFavoritesName(){ return imageFavoritesName;}
    private void setUpRealConfig(){
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);
    }

}
