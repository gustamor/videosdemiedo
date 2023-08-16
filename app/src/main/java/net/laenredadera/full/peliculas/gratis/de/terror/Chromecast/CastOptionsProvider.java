package net.laenredadera.full.peliculas.gratis.de.terror.Chromecast;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.NotificationOptions;

import net.laenredadera.full.peliculas.gratis.de.terror.Activities.ExpandedControlsActivity;

import java.util.List;

public final class CastOptionsProvider implements OptionsProvider {

    public CastOptions getCastOptions(Context appContext) {


        final String receiverId = "XXXXXXXX";

        NotificationOptions notificationOptions = new NotificationOptions.Builder()
                .setTargetActivityClassName(ExpandedControlsActivity.class.getName())
                .build();
        CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
                .setNotificationOptions(notificationOptions)
                .setExpandedControllerActivityClassName(ExpandedControlsActivity.class.getName())
                .build();
        return new CastOptions.Builder()
                .setReceiverApplicationId(receiverId)
                .setCastMediaOptions(mediaOptions)
                .build();
    }

    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}