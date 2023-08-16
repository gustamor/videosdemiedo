package net.laenredadera.full.peliculas.gratis.de.terror.Utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.R;

import java.net.URLEncoder;
import java.util.List;

public class AdvertisingActivities {

    //market://details?id=PACKAGE_NAME
    //url: LA URL DE LA TIENDA DE GOOGLE PLAY
    public static void openGooglePlayMarketActiviyURL(Context context, String marketUrl, String webUrl) {
        MyApp app = new MyApp();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)));
            app.setGoToOtherApp(true);

        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
            app.setGoToOtherApp(true);
        }
    }

    public static void openGooglePlayMarketActiviyURL(Context context, String webUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(webUrl));
            intent.setPackage("com.android.vending");
            context.startActivity(intent);

            // context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
        } catch (android.content.ActivityNotFoundException e) {
        }
    }

    public static void openDynamicLink(Context context, String dynamicLink)
    {
        try
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dynamicLink)));
            MyApp app = new MyApp();
            app.setGoToOtherApp(true);

        } catch (ActivityNotFoundException ignored) {

        }
    }

    public static void sendPromotonialEmailIntent(final Context context, String email, String subject, String title, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(emailIntent, title));
        MyApp app = new MyApp();
        app.setGoToOtherApp(true);

    }
    public static void sendSuggestionEmailIntent(final Context context, String email, String subject, String title, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(emailIntent, title));
        MyApp app = new MyApp();
        app.setGoToOtherApp(true);
    }

    public static void sendPromotionalTweetIntent(final Context context, String text) {
        MyApp app = new MyApp();
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                URLEncoder.encode(text),
                URLEncoder.encode(app.getAppContext().getString(R.string.installDeeplink)));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

        try {
            List<ResolveInfo> matches = app.getAppContext().getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    intent.setPackage(info.activityInfo.packageName);

                }
            }
        } catch (ActivityNotFoundException e) {
            tweetUrl = "https://twitter.com/intent/tweet?text=" + text + "&url="
                    + app.getAppContext().getString(R.string.installDeeplink);
            Uri uri = Uri.parse(tweetUrl);
            intent = new Intent(Intent.ACTION_VIEW, uri);

        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        app.setGoToOtherApp(true);

    }

    public static void sendPromotonialFacebookIntent(final Context context, String urlToShare) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        MyApp app = new MyApp();

        boolean facebookAppFound = false;
        List<ResolveInfo> matches = app.getAppContext().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        app.setGoToOtherApp(true);


    }

}


