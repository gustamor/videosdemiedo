package net.laenredadera.full.peliculas.gratis.de.terror.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import net.laenredadera.full.peliculas.gratis.de.terror.API;
import net.laenredadera.full.peliculas.gratis.de.terror.Adapters.VideoCardAdapter;
import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.InApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Interfaces.MovieDataService;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.ApplicationPojo;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmFavorites;
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
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar mProgressbar;
    private DrawableShapes horizontalSeparator;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.image_orderby)
    ImageView orderBy;
    private boolean interstitalEnabled;
    private int clickItemHeaderClickCount;
    private int clicksEnItemParaMostrarInter;
    private Intent youtubePlayerIntent;
    private PopupWindow popupWindow;
    private static long lastRefresh;
    private Context context;
    private boolean isShowingInterstitial;
    private int tries = 2;
    private Activity activity;
    private Handler handler;
    private static boolean hasClicked = false;
    private boolean shouldLoadAd = true;
    private InterstitialAdListener interstitialAdListener;
    private InterstitialAd FANinterstitialAd;

    private int triesToLoadInterstitial = 2;
    private MyApp app = new MyApp();
    public VideoListFragment() {
        // Required empty public constructor
    }

    private int getTimeoutSwipe() {
        return 1000;
    }

    @Override
    public void onResume() {
        super.onResume();
        context = requireContext();
        activity = requireActivity();
        if (app.isIsComebackFromOtherApp()) {
            app.setItemHeaderCountClick(1);
            app.setGoToOtherApp(false);
        }
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        setLayoutManager();
        super.onConfigurationChanged(newConfig);
    }

    public VideoListFragment getInstance() {
        return VideoListFragment.this;
    }

    public void notifyAdapter(){
        new AsyncGetMetadata().execute();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onSwipeFragment() {
        try {
            if (mAdapter != null && mRecyclerView != null )
                try {
                    Objects.requireNonNull(mRecyclerView.getAdapter()).notifyDataSetChanged();
                } catch (NullPointerException ignored) {}

        } catch (Error e){
        }
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.image_orderby)
    public void changeSortedType() {
        if (app.getAdapterOrdered() == null || app.getAdapterOrdered().equals("asc")) {
            app.setAdapterOrdered("desc");
        } else {
            app.setAdapterOrdered("asc");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_video_list, container, false);
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
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        mProgressbar = v.findViewById(R.id.progressBar);
        mProgressbar.setProgress(0);
        clicksEnItemParaMostrarInter = app.getClicksItemHeadersInterstitialAd();

        setLayoutManagerToRecyclerview(v);
        if (isNetworkAvailable()) {
            new AsyncGetMetadata().execute();
            lastRefresh = System.currentTimeMillis();
            startRefreshLayoutOnVideoList();
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

    private void startRefreshLayoutOnVideoList(){
        swipeRefreshLayout.setOnRefreshListener(
                () -> new Handler().postDelayed(
                        () -> {
                            long now = System.currentTimeMillis();

                            if (now - lastRefresh > (60*1000)) {
                                new AsyncGetMetadata().execute();
                                lastRefresh = System.currentTimeMillis();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }, getTimeoutSwipe()));
    }

    private void setLayoutManagerToRecyclerview(View v) {
        RecyclerView recycler = v.findViewById(R.id.videoRecyclerView);
        mRecyclerView = recycler.findViewById(R.id.videoRecyclerView);
        horizontalSeparator =  new DrawableShapes(getContext(),mRecyclerView);
        horizontalSeparator.addHorizontalDecorationToRecyclerView();
        mRecyclerView.setItemViewCacheSize(15);
        mLayoutManager = new SnappingLinearLayoutManager(this.getContext(),  SnappingLinearLayoutManager.VERTICAL,false);
        mLayoutManager.supportsPredictiveItemAnimations();
        setLayoutManager();
    }

    private void setLayoutManager() {
        int numberOfColumns = 2;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                if (MyApp.getScreenOrientation(requireActivity()) == Configuration.ORIENTATION_LANDSCAPE)
                    numberOfColumns = 3;
                horizontalSeparator.addHorizontalDecorationToRecyclerView();
                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                mRecyclerView.setLayoutManager(mLayoutManager);
            default:
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class AsyncGetMetadata extends AsyncTask<Void, Integer, Void> implements MovieDataService {

        private final Realm realm;
        private final MovieDataService service;
        private final SearchInDatabase db;
        private final markAsFavorites fav;
        public AsyncGetMetadata() {
            API api = new API();
            service = api.getApi().create(MovieDataService.class);
            realm = Realm.getDefaultInstance();
            this.getVideos();
            fav = new markAsFavorites();
            db = new SearchInDatabase(realm);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressbar.setProgress(values[0]);
        }

        private void createNewMoviedata(RealmMovieData movie) {
            if (movie != null) {
                realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(movie));
            }
        }

        private void setBeingSavedToTrue(RealmMovieData movie) {
            realm.executeTransaction(realm -> movie.setIsBeingSaved(true));
        }

        private void setBeingSavedToFalse(RealmMovieData movie) {
            realm.executeTransaction(realm -> movie.setIsBeingSaved(false));
        }

        private void makeAdapter(RealmList<RealmMovieData> videoList) {
            int mCardlayoutRef = R.layout.video_card_expandable_item;
            if (videoList.size() > 0) {
                mAdapter = new VideoCardAdapter(videoList, mCardlayoutRef,
                        (position, YPos) -> {
                            mRecyclerView.smoothScrollToPosition(position);
                            try {
                                if (app.isItemClickEnabled() == true) {
                                    if (interstitalEnabled){
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
                            app.addItemHeaderOneClick();
                        },

                        (video, position) -> fav.manageSetAsFavorite(video),
                        (youtubeplayer,popupwindow) -> {

                            youtubePlayerIntent = youtubeplayer;
                            popupWindow = popupwindow;
                            if (app.isItemClickEnabled() == true) {
                                nextActivityIfYoutubeIntentNotNulled();
                                        if (app.mediation().equals(InApp.IRONSOURCE)) {
                                           showIronInterstitialOnPoster();
                                            loadIronsourceInterstitialListener();
                                        } else if (app.mediation().equals(InApp.FACEBOOK)) {
                                            interstitialFANAdListener();
                                             showFANInterstitial("poster");
                                        } else if (app.mediation().equals(InApp.FANIRON)) {
                                            //Primero trata de mostar FAN. Si no lo hace, intenta mostar Ironsource, previamente cargado
                                            interstitialFANAdListener();
                                         showFANInterstitial("poster");
                                        }
                            } else {
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
                    if (tries++ < triesToMakeAnAdapter) {
                        makeAdapter(videoList);
                    }
                }
                mProgressbar.setVisibility(View.GONE);
            }
        }

        @Override
        public final Call<ApplicationPojo> getVideos() {
            Call<ApplicationPojo> movieApiCall = service.getVideos();
            movieApiCall.enqueue(new Callback<ApplicationPojo>() {
                @Override
                public void onResponse(@NonNull Call<ApplicationPojo> call,
                                       @NonNull Response<ApplicationPojo> response) {
                    ApplicationPojo ApiApplicationResponse = response.body();

                    RealmList<RealmMovieData> moviedataList = Objects.requireNonNull(ApiApplicationResponse).getPeliculas();
                /*  ArrayList<RealmMovieData> moviedataList = new ArrayList<>();
                moviedataList.add(apiResponse);*/
                    if (moviedataList != null && moviedataList.size() > 0) {
                        setImagesUris(moviedataList);
                    } else {
                        Toast.makeText(getContext(),
                                app.getAppContext().getResources()
                                        .getString(R.string.empty_textview), Toast
                                        .LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApplicationPojo> call, @NonNull Throwable t) {
                    Log.d("HTTP_ failure: ", t.toString());
                }
            });
            return null;
        }

        private void setImagesUris(RealmList<RealmMovieData> videoList) {
            //   AsyncDownloadImagesForCache();
            for (RealmMovieData movie : videoList) {
                if (movie != null) {
                    setBeingSavedToTrue(movie);
                    createNewMoviedata(movie);
                }
            }
            deleteOldMovies();
            deleteOldFavoritesMovies();
            RealmList<RealmMovieData> videolist = db.getArrayListFromRealm();
            fav.setAsFavorites();
            if (videolist.size() > 0) {
                try {
                    makeAdapter(videolist);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(),
                        app.getAppContext().getResources()
                                .getString(R.string.empty_textview), Toast.LENGTH_LONG)
                        .show();
            }
        }

        private void deleteOldFavoritesMovies() {
            try {
                realm.beginTransaction();
                RealmResults<RealmMovieData> movies = realm.where(RealmMovieData.class)
                        .equalTo("enabled",0)
                        .findAll();
                for (RealmMovieData m: movies){
                    realm.where(RealmFavorites.class)
                            .equalTo("link", m.getLink())
                            .findAll()
                            .deleteAllFromRealm();
                }
                realm.commitTransaction();
            } catch(Exception ignored) {}

        }

        private void deleteOldMovies() {
            try {
                realm.beginTransaction();
                RealmResults<RealmMovieData> toDelete = realm.where(RealmMovieData.class)
                        .equalTo("isBeingSaved", false)
                        .findAll();
                RealmResults<RealmMovieData> disabledToDelete = realm.where(RealmMovieData.class)
                        .equalTo("enabled", 0)
                        .findAll();

                toDelete.deleteAllFromRealm();
                disabledToDelete.deleteAllFromRealm();
            realm.commitTransaction();
            } catch(Exception ignored) {}
        }
   }

    private boolean isItemInterstitialNeeded() {
        clickItemHeaderClickCount = app.getItemHeaderClicks();
        try {
            if (clickItemHeaderClickCount == 0 && app.isIsAdsFirstClickEnabled()) {
                return true;
            }
            if (clickItemHeaderClickCount != 1 && (clickItemHeaderClickCount % clicksEnItemParaMostrarInter == clicksEnItemParaMostrarInter - 1)) {
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void showFANInterstitial(String cardLayoutClickOn){
        if (FANinterstitialAd != null && FANinterstitialAd.isAdLoaded()) {
            FANinterstitialAd.show();
            interstitialFANAdListener();
            requestFANInterstitialAd();
            shouldLoadAd = false;
        } else {
            if (app.mediation().equals(InApp.FANIRON)) {
                if (cardLayoutClickOn.equals("item")) {
                    showIronInterstitialOnItem();
                    loadIronsourceInterstitialListener();
                } else  if (cardLayoutClickOn.equals("poster")) {
                    showIronInterstitialOnPoster();
                    loadIronsourceInterstitialListener();
                }
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
                    if (popupWindow != null)   popupWindow.dismiss();

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
            setInterstitialAdListener();
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
                    Log.d("IRON ERROR:", error.getErrorMessage());
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

    private void nextActivityIfYoutubeIntentNotNulled() {
        if (youtubePlayerIntent != null) {
            try {
                context.startActivity(youtubePlayerIntent);
                activity.finish();

            } catch (IllegalStateException ise) {
                context.startActivity(youtubePlayerIntent);
                activity.finish();

            }
            try {
                if (popupWindow != null) popupWindow.dismiss();
            } catch (NullPointerException ignored) {
            }

        } else {
            if (popupWindow != null) popupWindow.dismiss();
            if (app.isInterstitialAdEnabled()) {
                if (isShowingInterstitial) IronSource.loadInterstitial();
            }
        }
    }
}
