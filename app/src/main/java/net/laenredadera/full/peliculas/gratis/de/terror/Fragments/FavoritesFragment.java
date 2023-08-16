package net.laenredadera.full.peliculas.gratis.de.terror.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import net.laenredadera.full.peliculas.gratis.de.terror.Adapters.FavoriteCardAdapter;
import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.InApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;
import net.laenredadera.full.peliculas.gratis.de.terror.R;
import net.laenredadera.full.peliculas.gratis.de.terror.UI.DrawableShapes;
import net.laenredadera.full.peliculas.gratis.de.terror.UI.SnappingLinearLayoutManager;
import net.laenredadera.full.peliculas.gratis.de.terror.Utils.SearchInDatabase;
import net.laenredadera.full.peliculas.gratis.de.terror.Utils.markAsFavorites;
import java.util.Objects;
import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class FavoritesFragment extends Fragment {

    public String TAG = "FavoritesFragment";
    private Realm realm;
    private View v;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RealmList<RealmMovieData> videos;
    private SearchInDatabase db;
    private markAsFavorites fav;
    private DrawableShapes horizontalSeparator;
    private Intent youtubePlayerIntent;
    private PopupWindow popupWindow;
    @BindView(R.id.image_orderby)
    ImageView orderBy;
    private ImageView backgrFavorites;
    private boolean interstitalEnabled;
    private int clicksEnItemParaMostrarInter;
    private int clickItemHeaderClickCount;
    final String FANTASMA = "fantasma";
    final String FLOTANTES = "flotantes";
    private boolean isShowingInterstitial;
    private Activity activity;
    private int tries = 0;
    private Handler handler;
    private InterstitialAd FANinterstitialAd;
    private static boolean hasClicked = false;
    private boolean shouldLoadAd = true;
    private InterstitialAdListener interstitialAdListener;
    private int triesToLoadInterstitial = 2;
    private MyApp app = new MyApp();


    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        activity = requireActivity();
        super.onResume();
        if (app.isInterstitialAdEnabled()) {
            if ( app.mediation().equals(InApp.IRONSOURCE) ||
                    app.mediation().equals(InApp.FANIRON)) {
                loadIronsourceInterstitialListener();
            }
        }

        if (app.isIsComebackFromOtherApp()) {
            app.setItemHeaderCountClick(1);
            app.setGoToOtherApp(false);
            if (app.isInterstitialAdEnabled()){
                showIronInterstitialOnItem(); }
        }

        if (db == null) {
            db = new SearchInDatabase(realm);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        makeAdapter(db.getFavoritesListFromRealm());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        makeAdapter(db.getFavoritesListFromRealm());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        makeAdapter(db.getFavoritesListFromRealm());
        setLayoutManager();
    }


    public FavoritesFragment getInstance() {
        return FavoritesFragment.this;
    }

    public void notifyAdapter(){

        try {
            videos = db.getFavoritesListFromRealm();
            if (videos.size() > 0) {

                makeAdapter(videos);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
                if (mRecyclerView != null) {
                    mRecyclerView.removeAllViews();
                    mRecyclerView.refreshDrawableState();
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }
            swapBackgroundVisibility(videos.size());
        } catch (NullPointerException ignored) {}
    }

    public void onSwipeFragment() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        if (db == null) {
            db = new SearchInDatabase(realm);
        }
        notifyAdapter();
        swapBackgroundVisibility(db.getFavoritesListFromRealm().size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_favorites, container, false);
        backgrFavorites = v.findViewById(R.id.imageView_empty_favorites);
        realm = Realm.getDefaultInstance();
        db = new SearchInDatabase(realm);

        interstitalEnabled = app.isInterstitialAdEnabled();

        if (interstitalEnabled) {
            clicksEnItemParaMostrarInter = app.getClicksItemHeadersInterstitialAd();
            app.setPosterCountClick(0);
            clickItemHeaderClickCount = 0;
            app.setClicksInAds(0);

            if (app.mediation().equals(InApp.FACEBOOK)) {
                interstitialFANAdListener();
                requestFANInterstitialAd();
            } else if (app.mediation().equals(InApp.IRONSOURCE)) {
                loadIronsourceInterstitialListener();

            } else if  (app.mediation().equals(InApp.FANIRON)) {
                loadIronsourceInterstitialListener();
                interstitialFANAdListener();
                requestFANInterstitialAd();
            }
        }

        youtubePlayerIntent = null;
        fav = new markAsFavorites();

        setLayoutManagerToRecyclerview(v);
        setBackgroundImageSource();
        fav.setAsFavorites();
        videos = db.getFavoritesListFromRealm();
        swapBackgroundVisibility(videos.size());

        if (isNetworkAvailable()) {
            makeAdapter(videos);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Required network");
            builder.setMessage("This app require network connection. Please restart app with network up");
            builder.setNeutralButton("Confirm", (dialogInterface, i) -> {
                requireActivity().finish();
                System.exit(0);
            });
            builder.show();
        }

        return v;
    }

    @OnClick(R.id.image_orderby)
    public void changeSortedType() {
        if (app.getAdapterOrdered() == null || app.getAdapterOrdered().equals("asc")) {
            app.setAdapterOrdered("desc");
        } else {
            app.setAdapterOrdered("asc");
        }
    }

    private void swapBackgroundVisibility(int videosSize){
            try {
                if (videosSize == 0) {
                    try {
                        mRecyclerView.setVisibility(View.GONE);
                        backgrFavorites.setVisibility(View.VISIBLE);
                    } catch (NullPointerException npe) {
                        try {
                            backgrFavorites = v.findViewById(R.id.imageView_empty_favorites);
                            setLayoutManagerToRecyclerview(v);
                            mRecyclerView.setVisibility(View.GONE);
                            backgrFavorites.setVisibility(View.VISIBLE);
                        } catch (Exception ignored){}
                    }
                } else {
                    try {
                        backgrFavorites.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } catch (NullPointerException npe) {
                        try {
                            backgrFavorites = v.findViewById(R.id.imageView_empty_favorites);
                            setLayoutManagerToRecyclerview(v);
                            backgrFavorites.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } catch (Exception ignored){}
                    }
                }
            }  catch (Exception ignored) {

            }

    }
    private void setLayoutManagerToRecyclerview(View v) {
        try {
            mRecyclerView = v.findViewById(R.id.videoFavoritesRecyclerView);
        } catch (NullPointerException npe) {
            mRecyclerView = v.findViewById(R.id.videoFavoritesRecyclerView);
        }
        horizontalSeparator =  new DrawableShapes(getContext(),mRecyclerView);
        horizontalSeparator.addHorizontalDecorationToRecyclerView();
        mRecyclerView.setItemViewCacheSize(10);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mLayoutManager = new SnappingLinearLayoutManager(this.getContext(),  SnappingLinearLayoutManager.VERTICAL,false);
        setLayoutManager();

    }
    public void makeAdapter(RealmList<RealmMovieData> videoList) {
        int mCardlayoutRef = R.layout.video_card_expandable_item;

        if (videoList.size() > 0) {
            mAdapter = new FavoriteCardAdapter(videoList, mCardlayoutRef,
                    (position, YPos) -> {
                        mRecyclerView.smoothScrollToPosition(position);
                        try {
                            if (app.isItemClickEnabled()) {
                                if (interstitalEnabled) {
                                    if (isItemInterstitialNeeded()) {
                                        if (app.mediation().equals(InApp.IRONSOURCE)) {
                                            showIronInterstitialOnItem();
                                            loadIronsourceInterstitialListener();
                                        } else if (app.mediation().equals(InApp.FACEBOOK)) {
                                            interstitialFANAdListener();
                                            showFANInterstitial("item");
                                        }else if (app.mediation().equals(InApp.FANIRON)) {
                                            //Primero trata de mostar FAN. Si no lo hace, intenta mostar Ironsource, previamente cargado
                                            interstitialFANAdListener();
                                            showFANInterstitial("item");
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored){}

                        if (Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount() < 1) {
                            swapBackgroundVisibility(db.getFavoritesListFromRealm().size());
                        }
                        app.addItemHeaderOneClick();
                    },

                    (video, position) -> {
                        fav.manageSetAsFavorite(video);
                        swapBackgroundVisibility(db.getFavoritesListFromRealm().size());
                    },
                    (youtubeplayer,popupwindow) -> {
                        youtubePlayerIntent = youtubeplayer;
                        popupWindow = popupwindow;
                        if (app.isInterstitialAdEnabled()) {
                            if (app.mediation().equals(InApp.IRONSOURCE)) {
                                loadIronsourceInterstitialListener();
                                showIronInterstitialOnPoster();
                            } else if (app.mediation().equals(InApp.FACEBOOK)) {
                                interstitialFANAdListener();
                                showFANInterstitial("poster");
                            }else if (app.mediation().equals(InApp.FANIRON)) {
                                //Primero trata de mostar FAN. Si no lo hace, intenta mostar Ironsource, previamente cargado
                                interstitialFANAdListener();
                                showFANInterstitial("poster");
                            }
                        }
                        else {
                            nextActivityIfYoutubeIntentNotNulled();
                        }
                    });

            try {
                if (mRecyclerView != null) {
                    mRecyclerView.removeAllViews();
                    mRecyclerView.refreshDrawableState();
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            } catch (NullPointerException npe) {
                setLayoutManagerToRecyclerview(v);
                int triesToMakeAnAdapter = 2;
                if (tries++ < 2) {
                    makeAdapter(videoList);

                }
            }

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setLayoutManager() {
        int numberOfColumns = 2;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {

            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                if (MyApp.getScreenOrientation(requireActivity()) == Configuration.ORIENTATION_LANDSCAPE)
                    numberOfColumns = 3;
                horizontalSeparator =  new DrawableShapes(getContext(),mRecyclerView);
                horizontalSeparator.addHorizontalDecorationToRecyclerView();
                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL: case Configuration.SCREENLAYOUT_SIZE_SMALL:
                mRecyclerView.setLayoutManager(mLayoutManager);
                break;
            default:

                break;
        }
    }

    private void setBackgroundImageSource(){

        if (backgrFavorites == null) {
            try {
                backgrFavorites = v.findViewById(R.id.imageView_empty_favorites);
            } catch (Exception ignored) {}
        }

        if (app.getImageFavoritesName() != null && backgrFavorites != null) {
            switch (app.getImageFavoritesName()) {
                case FLOTANTES:
                    try {
                        Glide.with(app.getAppContext()).load(R.drawable.favorites).into(backgrFavorites);
                    } catch (NullPointerException ignored) {}
                    break;
                case FANTASMA:
                default:
                    try {
                        Glide.with(app.getAppContext()).load(R.drawable.fantasma).into(backgrFavorites);
                    } catch (NullPointerException ignored) {}
                    break;
            }
        }
    }

    private boolean isItemInterstitialNeeded() {
        clickItemHeaderClickCount = app.getItemHeaderClicks();
        if (app.isIsAdsFirstClickEnabled() && clickItemHeaderClickCount == 0 ) {
            return true;
        }
        try {
            if (clickItemHeaderClickCount > 1 && (clickItemHeaderClickCount % clicksEnItemParaMostrarInter == clicksEnItemParaMostrarInter - 1)) {
                return true;
            } else {
                if (app.isIsAdsFirstClickEnabled())
                    if (clickItemHeaderClickCount == 1 || clickItemHeaderClickCount == 0 ) {
                        return true;
                    }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void showFANInterstitial(String cardLayoutType){
        if (FANinterstitialAd != null && FANinterstitialAd.isAdLoaded()) {
            FANinterstitialAd.show();
            interstitialFANAdListener();
            requestFANInterstitialAd();
            shouldLoadAd = false;
        } else {
            if (cardLayoutType.equals("item")) {
                showIronInterstitialOnItem();
                loadIronsourceInterstitialListener();
            } else  if (cardLayoutType.equals("poster")) {
                loadIronsourceInterstitialListener();
                showIronInterstitialOnPoster();
            }

        }
    }

    private void requestFANInterstitialAd() {
        handler = new Handler();
        handler.postDelayed(() -> {
            try {
                FANinterstitialAd = new InterstitialAd(requireContext(),app.getFANInterstitial());
                FANinterstitialAd.loadAd( FANinterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
            } catch (Exception ignored) {}
        }, 0);
    }

    private void interstitialFANAdListener(){
        try {
            interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    app.addOneClickToAds();
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    if (popupWindow != null) {
                        try {
                            popupWindow.dismiss();
                        } catch (Exception ignored) {}
                    }
                    nextActivityIfYoutubeIntentNotNulled();
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    if (triesToLoadInterstitial > 0) {
                        shouldLoadAd = true;
                        FANinterstitialAd.loadAd( FANinterstitialAd.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build());
                        triesToLoadInterstitial -= 1;
                    } else {
                        if (app.mediation().equals(InApp.FANIRON)) {
                            loadIronsourceInterstitialListener();
                        } else {
                            nextActivityIfYoutubeIntentNotNulled();
                        }
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    try {
                        if (shouldLoadAd) {
                            shouldLoadAd = false;
                        }
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                    //    requestFANInterstitialAd();
                    if (!hasClicked) {
                        hasClicked = true;
                    } else {
                        nextActivityIfYoutubeIntentNotNulled();
                    }
                }
                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

        } catch (Exception e) {
            nextActivityIfYoutubeIntentNotNulled();
        }
    }

    private void showIronInterstitialOnPoster() {
        if (IronSource.isInterstitialReady() ) {
            IronSource.showInterstitial("DefaultInterstitial");
        } else {
            nextActivityIfYoutubeIntentNotNulled();
        }
    }

    private void showIronInterstitialOnItem() {
        if (IronSource.isInterstitialReady() ) {
            IronSource.showInterstitial("DefaultInterstitial");
        }
    }
    private void loadIronsourceInterstitialListener() {
        setInterstitialAdListener();
    }

    private void setInterstitialAdListener() {

        try {
            IronSource.setInterstitialListener(new InterstitialListener() {
                @Override
                public void onInterstitialAdReady() {
                    isShowingInterstitial = false;
                }

                @Override
                public void onInterstitialAdLoadFailed(IronSourceError error) {
                    isShowingInterstitial = false;
                    nextActivityIfYoutubeIntentNotNulled();
                }

                @Override
                public void onInterstitialAdOpened() {
                    isShowingInterstitial = true;
                }

                @Override
                public void onInterstitialAdClosed() {
                    isShowingInterstitial = false;
                    nextActivityIfYoutubeIntentNotNulled();

                }

                @Override
                public void onInterstitialAdShowFailed(IronSourceError error) {
                    Log.d("IRON ERROR:", error.getErrorMessage());
                    nextActivityIfYoutubeIntentNotNulled();
                }

                @Override
                public void onInterstitialAdClicked() {
                    isShowingInterstitial = true;

                }

                @Override
                public void onInterstitialAdShowSucceeded() {

                }
            });

            if (!IronSource.isInterstitialReady()) {
                IronSource.loadInterstitial();
            }

        } catch (Exception e) {
            nextActivityIfYoutubeIntentNotNulled();
        }
    }

    private void nextActivityIfYoutubeIntentNotNulled(){
        if (youtubePlayerIntent != null){
            try {
                activity.startActivity(youtubePlayerIntent);
                activity.finish();
            } catch (IllegalStateException ise) {
                activity.startActivity(youtubePlayerIntent);
                activity.finish();
            }
            try {
                if (popupWindow != null) popupWindow.dismiss();
            } catch (NullPointerException ignored) { }
        } else {
            if (popupWindow != null) popupWindow.dismiss();
            if (app.isInterstitialAdEnabled()) {
                if (isShowingInterstitial) IronSource.loadInterstitial();
            }
        }
    }

}
