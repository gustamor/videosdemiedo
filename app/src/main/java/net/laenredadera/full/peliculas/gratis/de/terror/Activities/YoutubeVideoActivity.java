package net.laenredadera.full.peliculas.gratis.de.terror.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdViewAttributes;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils.PlayServicesUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;

import net.laenredadera.full.peliculas.gratis.de.terror.Adapters.YoutubeActivityVideoCardAdapter;
import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Chromecast.MediaRouteButtonUtils;
import net.laenredadera.full.peliculas.gratis.de.terror.Chromecast.NotificationManager;
import net.laenredadera.full.peliculas.gratis.de.terror.Chromecast.PlaybackControllerBroadcastReceiver;
import net.laenredadera.full.peliculas.gratis.de.terror.Chromecast.YouTubePlayersManager;
import net.laenredadera.full.peliculas.gratis.de.terror.InApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmFavorites;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;
import net.laenredadera.full.peliculas.gratis.de.terror.R;
import net.laenredadera.full.peliculas.gratis.de.terror.UI.DrawableShapes;
import net.laenredadera.full.peliculas.gratis.de.terror.Utils.NetworkUtils;
import net.laenredadera.full.peliculas.gratis.de.terror.Utils.SearchInDatabase;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

@SuppressWarnings("ALL")
public class YoutubeVideoActivity extends AppCompatActivity  implements YouTubePlayersManager.LocalYouTubePlayerInitListener, ChromecastConnectionListener {

    public String TAG = "YoutubeVideoPlayerActivity";
    private boolean fullscreen;
    private Realm realm;
    private SearchInDatabase db;
    private int mCardlayoutRef;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.youtubeVideoRecyclerView)
    RecyclerView mRecyclerView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.YouTubePlayer)
    YouTubePlayerView youTubePlayerView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cast_title)
    TextView videoTitle_view;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cast_movie_image)
    AppCompatImageView videoIcon_view;

    private DrawableShapes horizontalSeparator;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer activePlayer = null;
    private String YoutubeVideoUrl;
    private int ListPosition;
    private float videoCurrentSecond;

    private boolean adIsDisplaying;
    private int clicksParaMostrarInter;
    private View chromeCastControlsRoot;
    private YouTubePlayersManager youTubePlayersManager;
    private boolean connectedToChromeCast = false;
    private PlaybackControllerBroadcastReceiver playbackControllerBroadcastReceiver;
    private NotificationManager notificationManager;
    private androidx.mediarouter.app.MediaRouteButton  mediaRouteButton;
    private final int googlePlayServicesAvailabilityRequestCode = 1;
    private String videoTitle;
    private String videoIconUrl;
    private boolean isPlaying;
    private FrameLayout bannerContainer;
    private IronSourceBannerLayout banner;
    private boolean isAdIterstitialEnabled;
    private Dialog popupWindow;
    private com.facebook.ads.AdView adFANBanner;
    private InterstitialAdListener interstitialAdListener;
    private InterstitialAd FANinterstitialAd;
    private boolean shouldLoadAd;
    private boolean shouldShowAd;
    private int triesToLoadInterstitial = 2;
    LinearLayout nativeBannerAdContainer;
    private NativeBannerAd nativeBannerAd;

    private boolean interstitalEnabled;
    private Handler handler;
    private static boolean hasClicked = false;
    private int clicksEnItemParaMostrarInter;
    private int clickItemHeaderClickCount;
    private MyApp app = new MyApp();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (youTubePlayerView.isActivated()) {
            youTubePlayerView.release();
        }
        if (adFANBanner != null) adFANBanner = null;
        if (activePlayer != null) activePlayer = null;
        if (mAdapter != null) mAdapter = null;
        if (mRecyclerView != null) mRecyclerView = null;
        YoutubeVideoActivity.this.finish();
    }
 
    @Override
    protected void onResume() {
        if (MyApp.getScreenOrientation(YoutubeVideoActivity.this) == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().getDecorView()
                    .setSystemUiVisibility( View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (activePlayer != null) {
            activePlayer.seekTo(videoCurrentSecond);
            activePlayer.play();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (activePlayer != null) {
            activePlayer.pause();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activePlayer != null) {
            activePlayer.pause();
        }
        IronSource.onPause(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putFloat("currentSeconds", videoCurrentSecond);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        videoCurrentSecond = savedInstanceState.getFloat("currentSeconds");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (connectedToChromeCast){
            disconnectChromecast();
        } else {
            if (fullscreen) {
                youTubePlayerView.exitFullScreen();
                getResources().getConfiguration().orientation = Configuration.ORIENTATION_PORTRAIT;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else {
               // boolean backPressed = true;
                youTubePlayerView.release();
                connectedToChromeCast = false;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);
                getResources().getConfiguration().orientation = Configuration.ORIENTATION_PORTRAIT;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video);
        ButterKnife.bind(this);
        nativeBannerAdContainer = (LinearLayout) findViewById(R.id.native_banner_ad_container);
        interstitalEnabled = app.isInterstitialAdEnabled();

        if (app.isBannerAdEnabled()) {
            if (app.mediation().equals(InApp.FACEBOOK)) {
                app.requestMainFANBannerAd(this);
                loadFanNativeBanner();
            } else if (app.mediation().equals(InApp.IRONSOURCE)) {
        } else {
               nativeBannerAdContainer.setVisibility(View.GONE);
        }

        }
        if (interstitalEnabled) {
            clicksParaMostrarInter =  app.getPlayerInterstitialAdFrequency();
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

        connectedToChromeCast = false;
        youTubePlayerView = findViewById(R.id.YouTubePlayer);
        chromeCastControlsRoot = findViewById(R.id.chromecast_controls_root);
      //  favorite = new markAsFavorites();
        if (app.isCastEnabled()){
            getLifecycle().addObserver(youTubePlayerView);
            notificationManager = new NotificationManager(this, YoutubeVideoActivity.class);
            youTubePlayersManager = new YouTubePlayersManager(this, youTubePlayerView, chromeCastControlsRoot, notificationManager, getLifecycle());
            mediaRouteButton =   MediaRouteButtonUtils.initMediaRouteButton(this);
            registerBroadcastReceiver();
            PlayServicesUtils.checkGooglePlayServicesAvailability(this, googlePlayServicesAvailabilityRequestCode, this::initChromeCast);
        }

        fullscreen = false;
        realm = Realm.getDefaultInstance();
        db = new SearchInDatabase(realm);

        setAllPositions();
        getExtras();

        try {
            populateAdapter();
        } catch (Exception ignored) {
            System.exit(1);
        }
        if (app.isCastEnabled()) updateUi(false);
    }

    private void getExtras(){
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                YoutubeVideoUrl = extras.getString("YoutubeVideoUrl");
                try {
                    ListPosition = findPositionByURL(YoutubeVideoUrl);
                } catch (IllegalArgumentException iae) {
                    this.onBackPressed();
                }
                if (extras.getString("position") != null) {
                    ListPosition = extras.getInt("position");
                }
                if (extras.getString("ID") != null) {
                    String videoID = extras.getString("ID");
                }
                if (extras.getString("Title") != null) {
                    videoTitle = extras.getString("Title");
                }
                if (extras.getString("Year") != null) {
                    String videoYear = extras.getString("Year");
                }
                if (extras.getString("Icon") != null) {
                    videoIconUrl = extras.getString("Icon");
                }
            }
        }

    }

    private void disconnectChromecast(){
        if (app.isCastEnabled()) {
            notificationManager.dismissNotification();
            youTubePlayersManager.togglePlayback();
            connectedToChromeCast = false;
            Intent stopCastSessionImplicitIntent = new Intent(PlaybackControllerBroadcastReceiver.STOP_CAST_SESSION);
            playbackControllerBroadcastReceiver.onReceive(YoutubeVideoActivity.this,stopCastSessionImplicitIntent);
        }
    }

    @Override
    public void onChromecastConnecting() {
    }

    @Override
    public void onChromecastConnected(@NonNull ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        connectedToChromeCast = true;
        updateUi(true);
        notificationManager.showNotification();
    }

    @Override
    public void onChromecastDisconnected() {
        connectedToChromeCast = false;
        updateUi(false);
        notificationManager.dismissNotification();
    }

    @Override
    public void onLocalYouTubePlayerInit() {
        if(connectedToChromeCast)
            return;
       MediaRouteButtonUtils.addMediaRouteButtonToPlayerUi(
                mediaRouteButton, android.R.color.white,
                null, localPlayerUiMediaRouteButtonContainer
        );
    }

    private void registerBroadcastReceiver() {
        playbackControllerBroadcastReceiver = new PlaybackControllerBroadcastReceiver(youTubePlayersManager::togglePlayback);
        IntentFilter filter = new IntentFilter(PlaybackControllerBroadcastReceiver.TOGGLE_PLAYBACK);
        filter.addAction(PlaybackControllerBroadcastReceiver.STOP_CAST_SESSION);
        filter.addAction(PlaybackControllerBroadcastReceiver.DISCONNECT_CAST_SESSION);
        getApplicationContext().registerReceiver(playbackControllerBroadcastReceiver, filter);
    }

    private void updateUi(boolean isConnected) {
        YoutubeVideoActivity.MediaRouteButtonContainer disabledContainer = isConnected ? localPlayerUiMediaRouteButtonContainer : chromecastPlayerUiMediaRouteButtonContainer;
        YoutubeVideoActivity.MediaRouteButtonContainer enabledContainer = isConnected ? chromecastPlayerUiMediaRouteButtonContainer : localPlayerUiMediaRouteButtonContainer;
        int mediaRouteButtonColor = isConnected ? android.R.color.black : android.R.color.white;
        // the media route button has a single instance.
        // therefore it has to be moved from the local YouTube player Ui to the chromecast YouTube player Ui, and vice versa.
       MediaRouteButtonUtils.addMediaRouteButtonToPlayerUi(
                mediaRouteButton, mediaRouteButtonColor,
                disabledContainer, enabledContainer
        );

        youTubePlayerView.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        if (chromeCastControlsRoot == null) {
            chromeCastControlsRoot = findViewById(R.id.chromecast_controls_root);
        }
        chromeCastControlsRoot.setVisibility(isConnected ? View.VISIBLE : View.GONE);
        videoTitle_view.setText(videoTitle);
        try {
            if (!this.isDestroyed()) {
                Glide.with(this).load(videoIconUrl).centerCrop().into(videoIcon_view);
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (app.isCastEnabled()) {
            if (requestCode == googlePlayServicesAvailabilityRequestCode)
                PlayServicesUtils.checkGooglePlayServicesAvailability(this, googlePlayServicesAvailabilityRequestCode, this::initChromeCast);
        }

    }

    private void initChromeCast() {
        new ChromecastYouTubePlayerContext(
                CastContext.getSharedInstance(this).getSessionManager(),
                this, playbackControllerBroadcastReceiver, youTubePlayersManager
        );
    }

    private void setAllPositions() {

        realm.beginTransaction();
        RealmResults<RealmMovieData> result_ = db.getResultsFromRealm();
        int n = 0;
        for (RealmMovieData v: result_) {
            try {
                v.setAdapterPosition(n++);

            } catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
        realm.commitTransaction();

    }


    private void populateAdapter() {
        NetworkUtils netutils = new NetworkUtils();
        final String youtubeID = getVideoId(YoutubeVideoUrl);
        setLayoutManagerToRecyclerview();
        RealmList<RealmMovieData> videos = db.getArrayListFromRealm();
        mAdapter = makeAdapter(videos);
        setAdapter();
        if (netutils.isNetworkAvailable()) {
            loadVideoInPlayerViewAndInitialize(youtubeID);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(YoutubeVideoActivity.this);

            builder.setTitle("Problemas de red");
            builder.setMessage("Por favor, conecta la red o intenta más tarde");
            builder.setNeutralButton("Confirm", (dialogInterface, i) -> {
                YoutubeVideoActivity.this.finish();
                System.exit(0);
            });
            builder.show();
        }
    }

    private void setLayoutManagerToRecyclerview() {
        horizontalSeparator =  new DrawableShapes(getBaseContext(),mRecyclerView);
        horizontalSeparator.addHorizontalDecorationToRecyclerView();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mLayoutManager = new LinearLayoutManager(YoutubeVideoActivity.this,  LinearLayoutManager.VERTICAL,false);
        mCardlayoutRef = R.layout.video_card_expandable_item;
        setLayoutManager();

    }

    public void setLayoutManager() {
        int numberOfColumns = 2;
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                horizontalSeparator =  new DrawableShapes(getBaseContext(),mRecyclerView);
                horizontalSeparator.addHorizontalDecorationToRecyclerView();
                mRecyclerView.setLayoutManager(new GridLayoutManager(YoutubeVideoActivity.this, numberOfColumns));
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                mRecyclerView.setLayoutManager(mLayoutManager);
                break;
            default:
                break;
        }
    }

    private int findPositionByURL(String youtubeVideoUrl){
        try {
            RealmMovieData o = realm.where(RealmMovieData.class)
                    .equalTo("enabled",1)
                    .equalTo("link",youtubeVideoUrl)
                    .findFirst();
            assert o != null;
            return o.getAdapterPosition();
        } catch (NullPointerException npe) {
            this.onBackPressed();
        }
        return 0;
    }

    //Function: makeAdapter
    //Produces: an RecyclyerView.Adapter

    private RecyclerView.Adapter makeAdapter(RealmList<RealmMovieData> videoList) {
        int expanded = ListPosition;
        RecyclerView.Adapter adapter;
        adapter = null;
        if (!videoList.isEmpty()) {
            adapter = new YoutubeActivityVideoCardAdapter(expanded, videoList, mCardlayoutRef,
                    (position, YPos) -> mRecyclerView.scrollToPosition(position),
                    video -> {
                        String id = getVideoId(video.getLink());
                        videoTitle = video.getTitle();
                        videoIconUrl = video.getIcon();
                        try {
                            activePlayer.pause();
                            activePlayer.seekTo(0);
                            activePlayer.loadVideo(id, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (interstitalEnabled) {
                            if (showInterstitialIfNeeded()) {
                                if (app.mediation().equals(InApp.IRONSOURCE)) {
                                    showIronInterstitialOnPoster();
                                    loadIronsourceInterstitialListener();
                                } else if (app.mediation().equals(InApp.FACEBOOK)) {
                                    interstitialFANAdListener();
                                    showFANInterstitial("poster");
                                }else if (app.mediation().equals(InApp.FANIRON)) {
                                    interstitialFANAdListener();
                                    showFANInterstitial("poster");
                                }
                            }
                        }
                    },

                    state -> {
                        if (state) {
                            activePlayer.pause();
                        } else {
                            activePlayer.play();
                        }
                    },
                    this::manageSetAsFavorite);
        }
        return adapter;
    }

    private void manageSetAsFavorite(RealmMovieData video){
        RealmFavorites fav = realm.where(RealmFavorites.class).equalTo("link",video.getLink()).findFirst();
        realm.beginTransaction();

        if (fav != null) {
            Uri l1 = Uri.parse(fav.getLink());
            Uri l2 = Uri.parse(video.getLink());
            if (l1.equals(l2)) {
                video.setFavorite(false);// Swap If video is in favorites
                fav.deleteFromRealm();
            } else {
                video.setFavorite(true); // Swap If video is not in favorites
                RealmFavorites favorite = new RealmFavorites(video.getLink());
                realm.copyToRealmOrUpdate(favorite);
            }
        } else {
            video.setFavorite(true);
            RealmFavorites favorite = new RealmFavorites(video.getLink());
            realm.copyToRealmOrUpdate(favorite);
        }
        realm.commitTransaction();

    }

    //Function: setAdapter()
    //Produces: attach an RecyclerView.Adapter to a RecyclerView
    private void setAdapter() {
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(ListPosition);
    }

    private String getVideoId(String youtubeVideoUrl) {
        String expression = "(vi/|v=|/v/|youtu\\.be/|/embed/)";
        String[] link = youtubeVideoUrl.split(expression);
        if (link[1] != null) {
            return link[1];
        }
        return "";
    }

    //Function loadVideoInPlayerView
    //Produces: void - YoutubePlayerView

    private void loadVideoInPlayerViewAndInitialize(final String videoID) {
        final float zero = 0;
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .rel(0)
                .ivLoadPolicy(3)
                .ccLoadPolicy(1)
                .controls(0)
                .build();

        youTubePlayerView.enableBackgroundPlayback(false);
        getLifecycle().addObserver(youTubePlayerView);

         youTubePlayerView.initialize(new YouTubePlayerListener() {
            @Override
            public void onReady(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                activePlayer = youTubePlayer;

              if (app.isCastEnabled()) {
                  disconnectChromecast();
                  connectedToChromeCast = false;
                  updateUi(connectedToChromeCast);
              }
                YouTubePlayerUtils.loadOrCueVideo(activePlayer,getLifecycle(),videoID,zero);

            }

            @Override
            public void onStateChange(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState playerState) {
                switch (playerState) {
                    case PLAYING:
                        isPlaying = true;
                        break;
                    case ENDED:
                        isPlaying = false;
                        if (app.isCastEnabled()) disconnectChromecast();
                        break;
                    case PAUSED:
                        isPlaying = false;
                        break;
                    case VIDEO_CUED:
                }
            }
            @Override
            public void onCurrentSecond(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, float seconds) {
                videoCurrentSecond = seconds;
            }
            @Override
            public void onPlaybackQualityChange(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {
            }

            @Override
            public void onPlaybackRateChange(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackRate playbackRate) {
            }

            @Override
            public void onError(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError playerError) {
            }

            @Override
            public void onVideoDuration(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, float v) {
            }

            @Override
            public void onVideoLoadedFraction(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, float v) {
            }

            @Override
            public void onVideoId(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, @NotNull String s) {
            }

            @Override
            public void onApiChange(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
            }

        },false,iFramePlayerOptions);//, iFramePlayerOptions);

        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                getWindow().getDecorView()
                        .setSystemUiVisibility( View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                fullscreen = true;
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                fullscreen = false;
            }
        });
    }
/*

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
*/


    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubePlayerView.enterFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            youTubePlayerView.exitFullScreen();
        }
    }

    private boolean showInterstitialIfNeeded(){
        int clickCount = app.getPosterClicks();
        try {

            if (clickCount > 1 && (clickCount % clicksParaMostrarInter == clicksParaMostrarInter - 1 )){
                return true;
            }
        } catch (Exception ignored) { }
        app.addPosterOneClick();
        return false;

    }

    private void togglePlayPauseVideo() {
        try {
            if (activePlayer != null)
            {
                if (adIsDisplaying)
                    activePlayer.pause();
                else
                    activePlayer.play();
            }

        } catch (NullPointerException ignored) {}

    }

    private final MediaRouteButtonContainer chromecastPlayerUiMediaRouteButtonContainer = new MediaRouteButtonContainer() {
        public void addMediaRouteButton(MediaRouteButton mediaRouteButton) { youTubePlayersManager.getChromecastUiController().addView(mediaRouteButton); }
        public void removeMediaRouteButton(MediaRouteButton mediaRouteButton) { youTubePlayersManager.getChromecastUiController().removeView(mediaRouteButton); }
    };

    private final MediaRouteButtonContainer localPlayerUiMediaRouteButtonContainer = new MediaRouteButtonContainer() {
        public void addMediaRouteButton(MediaRouteButton mediaRouteButton) { youTubePlayerView.getPlayerUiController().addView(mediaRouteButton); }
        public void removeMediaRouteButton(MediaRouteButton mediaRouteButton) { youTubePlayerView.getPlayerUiController().removeView(mediaRouteButton); }
    };

    public interface MediaRouteButtonContainer {
        void addMediaRouteButton(MediaRouteButton mediaRouteButton);
        void removeMediaRouteButton(MediaRouteButton mediaRouteButton);
    }


    private void showFANInterstitial(String cardLayoutType){
        if (FANinterstitialAd != null && FANinterstitialAd.isAdLoaded()) {
            FANinterstitialAd.show();
            interstitialFANAdListener();
            requestFANInterstitialAd();
            shouldLoadAd = false;
            app.addOneClickToAds();

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


    private void showIronInterstitialOnPoster() {
        if (IronSource.isInterstitialReady() ) {
            IronSource.showInterstitial("DefaultInterstitial");
        }
    }
    private void showIronInterstitialOnItem() {
        IronSource.showInterstitial("DefaultInterstitial");
    }

    private void loadIronsourceInterstitialListener() {
        try {
            Handler handler = new Handler();
            handler.postDelayed(() -> setInterstitialAdListener(), 0);
        } catch (RuntimeException  rte) {
            setInterstitialAdListener();
        }
    }

    private void requestFANInterstitialAd() {
        handler = new Handler();
        handler.postDelayed(() -> {
            try {
                FANinterstitialAd = new InterstitialAd(this,app.getFANInterstitial());
                FANinterstitialAd.loadAd( FANinterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
            } catch (Exception ignored) {}
        }, 0);
    }

    private void interstitialFANAdListener(){
        if (app.mediation().equals(InApp.FACEBOOK)) {
            try {
                interstitialAdListener = new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        adIsDisplaying = true;
                        if (popupWindow != null) popupWindow.dismiss();
                        togglePlayPauseVideo();
                    }
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        if (shouldLoadAd) {
                            if (!FANinterstitialAd.isAdLoaded()) FANinterstitialAd.loadAd();
                            shouldLoadAd = true;
                        } else {
                            FANinterstitialAd.destroy();
                            shouldShowAd = false;
                            adIsDisplaying = false;
                            if (popupWindow != null) popupWindow.dismiss();
                            togglePlayPauseVideo();
                        }
                    }
                    @Override
                    public void onError(Ad ad, AdError adError) {
                        if (triesToLoadInterstitial > 0) {
                            FANinterstitialAd.loadAd(FANinterstitialAd.buildLoadAdConfig()
                                    .withAdListener(interstitialAdListener)
                                    .build());
                            triesToLoadInterstitial -= 1;
                        }  else if (app.mediation().equals(InApp.FANIRON)) {
                            loadIronsourceInterstitialListener();
                        }
                        else {
                            FANinterstitialAd.destroy();
                            adIsDisplaying = false;
                            if (popupWindow != null) popupWindow.dismiss();
                            togglePlayPauseVideo();
                            shouldShowAd = true;
                        }
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        try{
                            if (shouldShowAd) {
                                adIsDisplaying = true;
                            }
                        } catch (Exception e) {
                            togglePlayPauseVideo();
                        }
                        if (popupWindow != null) popupWindow.dismiss();
                    }
                    @Override
                    public void onAdClicked(Ad ad) {
                        adIsDisplaying = true;
                        togglePlayPauseVideo();
                        if (popupWindow != null) popupWindow.dismiss();
                        app.addOneClickToAds();
                    }
                    @Override
                    public void onLoggingImpression(Ad ad) {
                    }
                };

            } catch (Exception e) {
                adIsDisplaying = false;
                if (popupWindow != null) popupWindow.dismiss();
                togglePlayPauseVideo();
            }
        }
    }

    private void setInterstitialAdListener() {
        if (app.mediation().equals(InApp.IRONSOURCE)
                || app.mediation().equals(InApp.FANIRON)) {
            try {
                IronSource.setInterstitialListener(new InterstitialListener() {

                    @Override
                    public void onInterstitialAdReady() {
                        adIsDisplaying = false;
                        togglePlayPauseVideo();
                    }

                    @Override
                    public void onInterstitialAdLoadFailed(IronSourceError error) {
                        adIsDisplaying = false;
                        if (popupWindow != null) popupWindow.dismiss();
                        togglePlayPauseVideo();
                    }

                    @Override
                    public void onInterstitialAdOpened() {
                        if (popupWindow != null) popupWindow.dismiss();
                        togglePlayPauseVideo();

                    }

                    @Override
                    public void onInterstitialAdClosed() {
                        adIsDisplaying = false;
                        if (popupWindow != null) popupWindow.dismiss();
                        togglePlayPauseVideo();
                        //  requestInterstitialAd();

                    }

                    @Override
                    public void onInterstitialAdShowFailed(IronSourceError error) {
                        if (popupWindow != null) popupWindow.dismiss();
                        adIsDisplaying = false;
                        togglePlayPauseVideo();

                    }

                    @Override
                    public void onInterstitialAdClicked() {
                        adIsDisplaying = true;
                        togglePlayPauseVideo();
                        if (popupWindow != null) popupWindow.dismiss();
                        app.addOneClickToAds();

                    }

                    @Override
                    public void onInterstitialAdShowSucceeded() {
                    }
                });
                if (!IronSource.isInterstitialReady()) {
                    IronSource.loadInterstitial();
                }
            } catch (Exception e) {
                adIsDisplaying = false;
                if (popupWindow != null) popupWindow.dismiss();
                togglePlayPauseVideo();
            }
        }
    }

    private void showAdWithDelay() {
        //Native Banner
        if (app.mediation().equals(InApp.FACEBOOK)){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(nativeBannerAd == null || !nativeBannerAd.isAdLoaded()) {
                        return;
                    }
                    if(nativeBannerAd.isAdInvalidated()) {
                        return;
                    }
                    inflateAdTemplate(nativeBannerAd);
                }
            }, 1*1000); // S
        }
    }

    private void loadFanNativeBanner() {
        if (app.mediation().equals(InApp.FACEBOOK)){

            nativeBannerAd = new NativeBannerAd(this, app.getFANBannerId());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
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
            // load the ad
            nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig()
                    .withAdListener(nativeAdListener)
                    .build());
        }

    }
    private void inflateAdTemplate(NativeBannerAd nativeBannerAd) {
        NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                .setBackgroundColor(Color.BLACK)
                .setTitleTextColor(Color.WHITE)
                .setDescriptionTextColor(Color.LTGRAY)
                .setButtonColor(Color.WHITE)
                .setButtonTextColor(Color.BLACK);
        View adView = NativeBannerAdView.render(YoutubeVideoActivity.this, nativeBannerAd, NativeBannerAdView.Type.HEIGHT_50,viewAttributes);
        nativeBannerAdContainer = (LinearLayout) findViewById(R.id.native_banner_ad_container);
        nativeBannerAdContainer.addView(adView);
    }
}

