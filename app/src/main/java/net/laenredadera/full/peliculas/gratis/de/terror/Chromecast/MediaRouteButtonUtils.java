package net.laenredadera.full.peliculas.gratis.de.terror.Chromecast;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.mediarouter.app.MediaRouteButton;

import com.google.android.gms.cast.framework.CastButtonFactory;

import net.laenredadera.full.peliculas.gratis.de.terror.Activities.YoutubeVideoActivity;
import net.laenredadera.full.peliculas.gratis.de.terror.R;

public class MediaRouteButtonUtils {
    static public MediaRouteButton initMediaRouteButton(Context context) {
        MediaRouteButton mediaRouteButton = new MediaRouteButton(context);
        CastButtonFactory.setUpMediaRouteButton(context, mediaRouteButton);
        mediaRouteButton.setClickable(true);


        return mediaRouteButton;
    }

    static public void addMediaRouteButtonToPlayerUi(
            MediaRouteButton mediaRouteButton, int tintColor,
            @Nullable YoutubeVideoActivity.MediaRouteButtonContainer disabledContainer, YoutubeVideoActivity.MediaRouteButtonContainer activatedContainer) {

        setMediaRouterButtonTint(mediaRouteButton, tintColor);

        if(disabledContainer != null)
            disabledContainer.removeMediaRouteButton(mediaRouteButton);

        if(mediaRouteButton.getParent() != null)
            return;

        try {
            activatedContainer.addMediaRouteButton(mediaRouteButton);

        } catch (Exception ignored) {

        }
    }

    static private void setMediaRouterButtonTint(MediaRouteButton mediaRouterButton, int color) {
        ContextThemeWrapper castContext = new ContextThemeWrapper(mediaRouterButton.getContext(), R.style.Theme_MediaRouter);
        TypedArray styledAttributes = castContext.obtainStyledAttributes(null, androidx.mediarouter.R.styleable.MediaRouteButton, androidx.mediarouter.R.attr.mediaRouteButtonStyle, 0);
        Drawable drawable = styledAttributes.getDrawable(androidx.mediarouter.R.styleable.MediaRouteButton_externalRouteEnabledDrawable);

        if(drawable == null) {
            Log.e("MediaRouteButtonUtils", "can't apply tint to MediaRouteButton");
            return;
        }

        styledAttributes.recycle();
        DrawableCompat.setTint(drawable, ContextCompat.getColor(mediaRouterButton.getContext(), color));

        mediaRouterButton.setRemoteIndicatorDrawable(drawable);
    }
}
