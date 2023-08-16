package net.laenredadera.full.peliculas.gratis.de.terror.Chromecast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;

import java.util.Objects;

public class PlaybackControllerBroadcastReceiver extends BroadcastReceiver implements ChromecastConnectionListener {

    public static final String TOGGLE_PLAYBACK = "net.laenredadera.full.peliculas.gratis.de.terror.TOGGLE_PLAYBACK";
    public static final String STOP_CAST_SESSION = "net.laenredadera.full.peliculas.gratis.de.terror.STOP_CAST_SESSION";
    public static final String DISCONNECT_CAST_SESSION = "net.laenredadera.full.peliculas.gratis.de.terror.DISCONNECT_CAST_SESSION";
    private ChromecastYouTubePlayerContext chromecastYouTubePlayerContext;
    private final Runnable togglePlayback;

    public PlaybackControllerBroadcastReceiver() {
        this.togglePlayback = null;
    }

    public PlaybackControllerBroadcastReceiver(Runnable togglePlayback) {
        this.togglePlayback = togglePlayback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "intent received: " +intent.getAction());

        switch (Objects.requireNonNull(intent.getAction())) {
            case TOGGLE_PLAYBACK:
                assert togglePlayback != null;
                togglePlayback.run();
                break;

            case STOP_CAST_SESSION:
                if (chromecastYouTubePlayerContext != null) {
                    chromecastYouTubePlayerContext.endCurrentSession();
                }

                break;
            case DISCONNECT_CAST_SESSION:
                if (chromecastYouTubePlayerContext != null) {
                    chromecastYouTubePlayerContext.release();
                    chromecastYouTubePlayerContext.onChromecastDisconnected();
                }

        }
    }

    @Override
    public void onChromecastConnected(@NonNull ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        this.chromecastYouTubePlayerContext = chromecastYouTubePlayerContext;
    }

    @Override
    public void onChromecastConnecting() {

    }

    @Override
    public void onChromecastDisconnected() {

    }

}
