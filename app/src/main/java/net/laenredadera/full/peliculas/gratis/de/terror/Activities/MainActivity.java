package net.laenredadera.full.peliculas.gratis.de.terror.Activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.ironsource.mediationsdk.IronSource;
import net.laenredadera.full.peliculas.gratis.de.terror.Adapters.PageAdapter;
import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Fragments.FavoritesFragment;
import net.laenredadera.full.peliculas.gratis.de.terror.Fragments.SearchFragment;
import net.laenredadera.full.peliculas.gratis.de.terror.Fragments.VideoListFragment;
import net.laenredadera.full.peliculas.gratis.de.terror.InApp;
import net.laenredadera.full.peliculas.gratis.de.terror.R;
import net.laenredadera.full.peliculas.gratis.de.terror.Utils.AdvertisingActivities;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import static net.laenredadera.full.peliculas.gratis.de.terror.R.drawable.favicon;
import static net.laenredadera.full.peliculas.gratis.de.terror.R.drawable.ic_home_white_30dp;
import static net.laenredadera.full.peliculas.gratis.de.terror.R.drawable.searchicon;

public class MainActivity extends AppCompatActivity {
    private PageAdapter pagerAdapter;
    private Realm realm;
    AlertDialog.Builder dialog;
    private NativeBannerAd nativeBannerAd;
    final ArrayList<Fragment>  videoFragments = new ArrayList<>();
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.navview)
    NavigationView mNavigationView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.pager)
    ViewPager mPager;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.image_orderby)
    ImageView orderBy;

    private FavoritesFragment favoritesFragment;
    private VideoListFragment videoListFragment;
    private SearchFragment searchDialogFragment;
    private ImageView imageViewbackArrow;
    private SearchView searchView;
    private boolean mReturningWithResult = false;
    final String REZA = "reza";
    final String MONSTERS = "elvira";
    final String MID_TIO_ONE = "midTioOne";
    final String MID_TIO_DEMON = "midTiodDemon";

    LinearLayout nativeBannerAdContainer;
    private MyApp app = new MyApp();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            pagerAdapter.removeFragment(favoritesFragment);
            pagerAdapter.removeFragment(videoListFragment);
            pagerAdapter.removeFragment(searchDialogFragment);
        } catch(Exception ignored) {}
    }

    @Override
    protected void onResume() {
        switch (app.getScreenSize()){
            case "normal":
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                break;
            case "large":
            case "xlarge":
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
        }
        super.onResume();
         if (app.mediation().equals(InApp.FANIRON)
                || app.mediation().equals(InApp.IRONSOURCE)) {
             IronSource.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (app.mediation().equals(InApp.IRONSOURCE)
        || app.mediation().equals(InApp.FANIRON) ) {
            IronSource.onPause(this);
        }
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mReturningWithResult = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mReturningWithResult) {
            fragmentTransactions();
        }
        mReturningWithResult = false;
    }


    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setDrawerImageSource();
        nativeBannerAdContainer = findViewById(R.id.native_banner_ad_container);
        //  bannerContainer =  findViewById(R.id.banner_ad_container);
        if (app.mediation().equals(InApp.IRONSOURCE)
                || app.mediation().equals(InApp.FANIRON) ) {
            if (app.isBannerAdEnabled()) {
                app.requestMainFANBannerAd(this);
                loadFanNativeBanner();
            } else {
                nativeBannerAdContainer.setVisibility(View.GONE);
            }
        } else {
            nativeBannerAdContainer.setVisibility(View.GONE);
        }

        mToolbar.setTitleTextAppearance(this, R.style.MonotonTextAppearance);
        mToolbar.setTitle(R.string.app_name_toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        realm = Realm.getDefaultInstance();
        orderBy.setVisibility(View.VISIBLE);
        try {
            fragmentTransactions();
        } catch (IllegalStateException ise) {
            onPostResume();
        }

        realm = Realm.getDefaultInstance();
        Menu menu = mNavigationView.getMenu();

        mNavigationView.setNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.share_rate_app: shareRateThisApp(); break;
                        case R.id.suggest_film_menu: suggestFilmByEmail(); break;
                        case R.id.share_twitter: shareByTwitter(); break;
                        case R.id.share_facebook:shareByFacebook(); break;
                        case R.id.share_email: shareByEmail(); break;
                        case R.id.share_others:shareByOthers(); break;
                        case R.id.share_moreapps: shareMoreApps(); break;
                        case R.id.menu_about_us: openAboutUsActivity(); break;
                    }
                    return true;
                });

        if (app.getNumberOfVisits() < app.getNumberOfVisitsToShowRating()) {
            MenuItem target = menu.findItem(R.id.share_rate_app);
            target.setVisible(false);
        } else if ( !app.isRatingMustBeHidden()) {
            MenuItem target = menu.findItem(R.id.share_rate_app);
            target.setVisible(true);
        } else if (app.isStartRatingDialogClicked()) {
            MenuItem target = menu.findItem(R.id.share_rate_app);
            target.setVisible(false);
        }

        setToolbar();
        setTabLayout();
        slideToMainFragment();
        whenSwipeFragment();
    }

    private void fragmentTransactions(){
        videoFragments.add(new FavoritesFragment().getInstance());
        videoFragments.add(new VideoListFragment().getInstance());
        videoFragments.add(new SearchFragment().getInstance());
        favoritesFragment = (FavoritesFragment) videoFragments.get(0);
        videoListFragment = (VideoListFragment) videoFragments.get(1);
        searchDialogFragment = (SearchFragment) videoFragments.get(2);
        mPager.offsetLeftAndRight(3);
        pagerAdapter = new PageAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(favoritesFragment);
        pagerAdapter.addFragment(videoListFragment);
        pagerAdapter.addFragment(searchDialogFragment);
        mPager.setAdapter(pagerAdapter);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @SuppressLint("NonConstantResourceId")
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_order_ascending_date:
                    app.setAdapterOrdered("asc");
                    if (mPager.getCurrentItem() == 0){
                        favoritesFragment = (FavoritesFragment) videoFragments.get(0);
                        favoritesFragment.notifyAdapter();
                    } else {
                        videoListFragment.notifyAdapter();
                    }
                    Objects.requireNonNull(mPager.getAdapter()).notifyDataSetChanged();
                    return true;
                case R.id.menu_order_descending_date:
                    app.setAdapterOrdered("desc");
                    if (mPager.getCurrentItem() == 0){
                        favoritesFragment.notifyAdapter();
                    } else {
                        videoListFragment.notifyAdapter();
                    }
                   Objects.requireNonNull(mPager.getAdapter()).notifyDataSetChanged();

                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void setToolbar() {
        imageViewbackArrow = findViewById(R.id.image_backarrow_tab);
        searchView = findViewById(R.id.search_widget);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_32dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setTabLayout(){
        tabLayout.addTab(tabLayout.newTab().setIcon(favicon));
        tabLayout.addTab(tabLayout.newTab().setIcon(ic_home_white_30dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(searchicon));
        tabLayout.setTabTextColors(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(tabSelectedListenerOnTouchItem);
        tabLayout.selectTab(tabLayout.getTabAt(1));

    }
    final TabLayout.OnTabSelectedListener tabSelectedListenerOnTouchItem = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition(), true);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };

    private void suggestFilmByEmail(){
        String email ="riseagain.apps@gmail.com";
        String subject = app.getAppContext().getResources().getString(R.string.suggest_film_subject);
        String title = app.getAppContext().getResources().getString(R.string.app_name);
        String message = app.getAppContext().getResources().getString(R.string.suggest_film_message);
        AdvertisingActivities.sendSuggestionEmailIntent(MainActivity.this,email,subject,title,message);
    }

    private void shareByEmail(){
        String email ="";
        String subject = app.getAppContext().getResources().getString(R.string.app_name);
        String title = app.getAppContext().getResources().getString(R.string.app_name);
        String message = app.getAppContext().getResources().getString(R.string.promotionalMessage)
                + app.getAppContext().getResources().getString(R.string.installDeeplink);
        AdvertisingActivities.sendPromotonialEmailIntent(MainActivity.this,email,subject,title,message);
    }

    private void shareByTwitter(){
        AdvertisingActivities.sendPromotionalTweetIntent(MainActivity.this, MainActivity.this.getString(R.string.facebookQuote));
    }

    private void shareByFacebook(){
        AdvertisingActivities.sendPromotonialFacebookIntent(MainActivity.this,MainActivity.this.getString(R.string.GP_Link));
    }

    private void shareByOthers(){
        String quote;
        quote = getString(R.string.facebookQuote);
        String appUrl = app.getAppContext().getResources().getString(R.string.installDeeplink);
        String message = quote;
        StringBuilder text = new StringBuilder(message)
                .append("\n")
                .append(appUrl);
        String titleIntent = getResources().getText(R.string.send_to).toString();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, titleIntent));
    }

    private void shareMoreApps(){
        String marketUrl = this.getString(R.string.GP_AriseApps);
        AdvertisingActivities.openGooglePlayMarketActiviyURL(this,marketUrl,this.getString(R.string.GP_AriseApps));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void shareRateThisApp(){
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(app.getAppContext().getResources().getString(R.string.GP_Link))));
        }
        app.setRatingdDialogClicked(true);
    }

    private void slideToMainFragment(){
        mPager.setCurrentItem(1,true);
    }

    private void whenSwipeFragment() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
                switch (position) {
                    case 0: // Favorites fragment
                        mToolbar.setTitle(R.string.favorites);
                        mToolbar.setTitleTextAppearance(getApplicationContext(),R.style.DanceScriptTextAppearance);
                        imageViewbackArrow.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                        orderBy.setVisibility(View.VISIBLE);
                        favoritesFragment.onSwipeFragment();
                        break;
                    case 1:  // Main Fragment
                        mToolbar.setTitle(R.string.app_name_toolbar);
                        mToolbar.setTitleTextAppearance(getApplicationContext(), R.style.MonotonTextAppearance);
                        imageViewbackArrow.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        orderBy.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                        videoListFragment.onSwipeFragment();
                        break;
                    case 2: // Search Fragment
                        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        searchView.setVisibility(View.VISIBLE);
                        searchView.setIconifiedByDefault(true);
                        searchView.setIconified(false);
                        searchView.requestFocus();
                        searchView.setQueryHint(getText(R.string.search_hint));
                        orderBy.setVisibility(View.GONE);
                        imageViewbackArrow.setVisibility(View.VISIBLE);
                        imageViewbackArrow.setOnClickListener(v -> slideToMainFragment());
                        searchDialogFragment.onSwipeFragment();
                        break;
                }

                Objects.requireNonNull(mPager.getAdapter()).notifyDataSetChanged();

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void openAboutUsActivity() {
        Intent aboutIntent = new Intent(MainActivity.this, AboutUsActivity.class);
        MainActivity.this.startActivity(aboutIntent);
        MainActivity.this.finish();
    }

    private void openWallpapersScary(){
        String scaryMarketUrl = "market://details?id=com.nuevocurso.wallpaperhorror.wallpapersickness.wallpaper.scary.hd.blue";
        AdvertisingActivities.openGooglePlayMarketActiviyURL(this,scaryMarketUrl);
    }

    @Override
    public void onBackPressed() {
        if ((app.getNumberOfVisits() >= app.getNumberOfVisitsToShowRating())) {
            if (app.isStartRatingDialogClicked()) {
                if (app.isRatingMustBeHidden()) {
                    notShowRagingInDialog();
                } else {
                    showRatingInDialog();
                }
            } else {
                showRatingInDialog();
            }
        } else {
            notShowRagingInDialog();
        }

        dialog.show();
    }

    private void showRatingInDialog() {
        String weburl = app.getAppContext().getResources().getString(R.string.GP_Link);
        dialog = new AlertDialog.Builder(this)
                .setMessage(app.getAppContext().getResources().getString(R.string.dontleaveus))
                .setPositiveButton(app.getAppContext().getResources().getString(R.string.yes), (dialog, which) -> {
                    app.addOneVisitMoreToUser();
                    //   super.onBackPressed();
                    finish();
                })
                .setNegativeButton(app.getAppContext().getResources().getString(R.string.no), null)
                .setNeutralButton(app.getAppContext().getResources().getString(R.string.rate_app), (dialog, which) -> {
                    AdvertisingActivities.openGooglePlayMarketActiviyURL(this, weburl);
                    app.setRatingdDialogClicked(true);
                });
    }

    private void notShowRagingInDialog() {
        dialog = new AlertDialog.Builder(this)
                .setMessage(app.getAppContext().getResources().getString(R.string.dontleaveus))
                .setPositiveButton(app.getAppContext().getResources().getString(R.string.yes), (dialog, which) -> {
                    //  super.onBackPressed();
                    app.addOneVisitMoreToUser();
                    finish();
                })
                .setNegativeButton(app.getAppContext().getResources().getString(R.string.no), null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int randomNumber(){
        int min = 0;
        int max = 100;
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    private void setDrawerImageSource(){
        View header = mNavigationView.getHeaderView(0);
        ImageView image = header.findViewById(R.id.imgHeader);
        if (app.getDrawerHeaderRandomOffset() < 101) {
            if (randomNumber() < app.getDrawerHeaderRandomOffset()) {
                image.setBackground(ContextCompat.getDrawable(this, R.drawable.drawerdemon));
            } else {
                image.setBackground(ContextCompat.getDrawable(this, R.drawable.monsters));
            }
        } else {
            switch (app.getImageNavigationName()) {
                case MID_TIO_ONE:
                    image.setBackground(ContextCompat.getDrawable(this, R.drawable.drawertio));
                    break;
                case MID_TIO_DEMON:
                    image.setBackground(ContextCompat.getDrawable(this, R.drawable.drawerdemon));
                    break;
                case REZA:
                    image.setBackground(ContextCompat.getDrawable(this, R.drawable.reza2));
                    break;
                case MONSTERS:
                default:
                    image.setBackground(ContextCompat.getDrawable(this, R.drawable.monsters));
                    break;
            }
        }
    }

    private void loadFanNativeBanner() {

        nativeBannerAd = new NativeBannerAd(this, app.getFANBannerId());
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets.
            }
            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                if (nativeBannerAdContainer.getVisibility() != View.GONE) {
                    nativeBannerAdContainer.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                if (nativeBannerAdContainer.getVisibility() != View.VISIBLE) {
                    nativeBannerAdContainer.setVisibility(View.VISIBLE);
                }
                showAdWithDelay();
            }
            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
            }
            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
            }
        };

        nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .build());
    }

    private void showAdWithDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(nativeBannerAd == null || !nativeBannerAd.isAdLoaded()) {
                return;
            }
            if(nativeBannerAd.isAdInvalidated()) {
                return;
            }
            inflateAdTemplate(nativeBannerAd);
        }, 1*1000); // Show the ad after 2 secpnds
    }

    private void inflateAdTemplate(NativeBannerAd nativeBannerAd) {
        NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                .setBackgroundColor(Color.BLACK)
                .setTitleTextColor(Color.WHITE)
                .setDescriptionTextColor(Color.LTGRAY)
                .setButtonColor(Color.WHITE)
                .setButtonTextColor(Color.BLACK);
        View adView = NativeBannerAdView.render(MainActivity.this, nativeBannerAd, NativeBannerAdView.Type.HEIGHT_50,viewAttributes);
        nativeBannerAdContainer.addView(adView);
    }



}
