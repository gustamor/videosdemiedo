package net.laenredadera.full.peliculas.gratis.de.terror.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.tabs.TabLayout;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import net.laenredadera.full.peliculas.gratis.de.terror.Adapters.SearchableCardAdapter;
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
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener{

    public String TAG = "SearchFragment";
    private Realm realm;
    private int mCardlayoutRef;
    @SuppressWarnings("rawtypes")
    private RecyclerView.Adapter mAdapter;
    private RealmList<RealmMovieData> resultOfSearch;
    private SearchView searchView;
    private TabLayout tabs;
    private markAsFavorites fav;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.image_search_icon)
    ImageView searchIcon;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.text_search)
    TextView searchText;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.search_recyclerview)
    RecyclerView mRecyclerView;
    private Intent youtubePlayerIntent;
    private PopupWindow popupWindow;
    private boolean interstitalEnabled;
    private int clicksEnItemParaMostrarInter;
    private Activity activity;
    private Context context;
    private Handler handler;
    private InterstitialAd FANinterstitialAd;
    private static boolean hasClicked = false;
    private boolean shouldLoadAd = true;
    private InterstitialAdListener interstitialAdListener;
    private int triesToLoadInterstitial = 2;
    private boolean isShowingInterstitial;
    private MyApp app = new MyApp();
    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment getInstance() {
        return SearchFragment.this;
    }

    @Override
    public void onResume() {
        activity = requireActivity();
        context = requireContext();
        super.onResume();
        /*if (MyApp.isInterstitialAdEnabled()) {
            if ( MyApp.mediation().equals(InApp.IRONSOURCE) ||
                    MyApp.mediation().equals(InApp.FANIRON)) {
                loadIronsourceInterstitialListener();
            }
        }*/
        if (app.isIsComebackFromOtherApp()) {
            app.setItemHeaderCountClick(1);
            app.setGoToOtherApp(false);
           /* if (MyApp.isInterstitialAdEnabled())
                showIronInterstitialOnItem();*/
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        View v =  inflater.inflate(R.layout.fragment_search_dialog, container, false);
        ButterKnife.bind(this,v);
        interstitalEnabled = app.isInterstitialAdEnabled();
        youtubePlayerIntent = null;

        if (interstitalEnabled) {
            clicksEnItemParaMostrarInter = app.getClicksItemHeadersInterstitialAd();
            app.setPosterCountClick(0);
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

        setLayoutManagerToRecyclerview(v);
        searchView = requireActivity().findViewById(R.id.search_widget);
        searchView.setOnQueryTextListener(this);
        fav = new markAsFavorites();
        tabs = requireActivity().findViewById(R.id.tabs);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void makeAdapter(RealmList<RealmMovieData> videoList) {
        if (videoList.size() > 0) {
            mAdapter = new SearchableCardAdapter(videoList, mCardlayoutRef,
                    (position, YPos) -> {
                        searchView.clearFocus();
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
                        app.addItemHeaderOneClick();
                    },
                    (v, position) -> fav.manageSetAsFavorite(v),
                    (youtubeplayer,popupwindow) -> {
                        youtubePlayerIntent = youtubeplayer;
                        popupWindow = popupwindow;
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
                        app.addPosterOneClick();

                    });

        }
    }

    private RealmList<RealmMovieData> searchInRealm(String desire) {
        try {
            resultOfSearch = new SearchInDatabase(realm).getSearchResult(desire);
        } catch (Error e) {
            Log.d("VideolistFragment: ", e.toString());
        }
        return resultOfSearch;

    }

    private void setLayoutManagerToRecyclerview(View v) {
        RecyclerView recycler = v.findViewById(R.id.search_recyclerview);
        DrawableShapes horizontalSeparator = new DrawableShapes(getContext(), recycler);
        horizontalSeparator.addHorizontalDecorationToRecyclerView();
        RecyclerView.LayoutManager mLayoutManager = new SnappingLinearLayoutManager(this.getContext(), SnappingLinearLayoutManager.VERTICAL, false);
        mCardlayoutRef = getCardLayoutId();
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private int getCardLayoutId(){
        switch (app.getScreenSize()){
            case "normal":
                mCardlayoutRef = R.layout.video_card_expandable_item;
                break;
            case "large":
            case "xlarge":
                mCardlayoutRef = R.layout.video_card_expandable_search_item;

        }
        return mCardlayoutRef;
    }

    private void changePositionOfItemsField(RealmList<RealmMovieData> result){
        int size = result.size();
        realm.beginTransaction();
        for (int n=0; n < size; n++){
            assert result.get(n) != null;
            Objects.requireNonNull(result.get(n)).setAdapterPosition(n);
        }
        realm.commitTransaction();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 2) {
            RealmList<RealmMovieData> Main_List = searchInRealm(newText);
            try {
                if (Main_List.size() > 0) {
                    changePositionOfItemsField(Main_List);
                    makeAdapter(Main_List);
                    notifyAdapter();
                }
            } catch (Error e) {
                Log.d("SearchFragment:",e.toString());
            }
        } else {
            RealmList<RealmMovieData>  Main_List = new RealmList<>();
            makeAdapter(Main_List);
            mAdapter = null;
            mRecyclerView.setVisibility(View.INVISIBLE);
            tabs.setVisibility(View.VISIBLE);
            setIconVisibility();
        }

        return false;
    }

    public void onSwipeFragment() {
        if (mAdapter != null) {
            notifyAdapter();

        } else {
            setIconVisibility();
        }
    }

    private void notifyAdapter(){
        mAdapter.notifyDataSetChanged();
        searchIcon.setVisibility(View.GONE);
        searchText.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.removeAllViews();
        mRecyclerView.refreshDrawableState();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setIconVisibility() {
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(View.GONE);
        }
        try {
            searchIcon.setVisibility(View.VISIBLE);
            searchText.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    private boolean isItemInterstitialNeeded() {
        int clickItemHeaderClickCount = app.getItemHeaderClicks();
        int clickPosterCount = app.getItemHeaderClicks();
        try {

            if (clickItemHeaderClickCount > 1 && (clickItemHeaderClickCount % clicksEnItemParaMostrarInter == clicksEnItemParaMostrarInter - 1)) {
                return true;
            }
        } catch (Exception ignored) {
        }
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
        if (youtubePlayerIntent != null) {
            try {
                activity.startActivity(youtubePlayerIntent);
                activity.finish();

            } catch (IllegalStateException ise) {
                activity.startActivity(youtubePlayerIntent);
                activity.finish();

            }
            try {
                if (popupWindow != null) popupWindow.dismiss();
            } catch (NullPointerException ignored) {
            }

        } else {
            if (popupWindow != null) popupWindow.dismiss();

        }
    }

}


