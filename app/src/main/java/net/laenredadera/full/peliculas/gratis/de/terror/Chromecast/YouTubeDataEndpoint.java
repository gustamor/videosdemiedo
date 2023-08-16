package net.laenredadera.full.peliculas.gratis.de.terror.Chromecast;


import android.graphics.Bitmap;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.VideoInfo;
import net.laenredadera.full.peliculas.gratis.de.terror.R;
import net.laenredadera.full.peliculas.gratis.de.terror.Utils.NetworkUtils;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

public class YouTubeDataEndpoint {
    MyApp app = new MyApp();
    private static final String APP_NAME = "TerrorAPP";
    //final static String YOUTUBE_DATA_API_KEY = app.getAppContext().getString(R.string.APIendpoint);
    //  private static final String YOUTUBE_DATA_API_KEY = MyApp.getEndpoint();
    public static Single<VideoInfo> getVideoInfoFromYouTubeDataAPIs(String videoId) {
        SingleOnSubscribe<VideoInfo> onSubscribe = emitter -> {
            try {

                YouTube youTubeDataAPIEndpoint = buildYouTubeEndpoint();

                YouTube.Videos.List query = buildVideosListQuery(youTubeDataAPIEndpoint, videoId);
                VideoListResponse videoListResponse = query.execute();

                if(videoListResponse.getItems().size() != 1)
                    throw new RuntimeException("There should be exactly one video with the specified id");

                Video video = videoListResponse.getItems().get(0);

                String videoTitle = video.getSnippet().getTitle();

                Bitmap bitmap = NetworkUtils.getBitmapFromURL(video.getSnippet().getThumbnails().getMedium().getUrl());

                ChannelListResponse channel = buildChannelsListQuery(youTubeDataAPIEndpoint, video.getSnippet().getChannelId()).execute();
                String channelTitle = channel.getItems().get(0).getSnippet().getTitle();

                if (bitmap != null) {
                    emitter.onSuccess(new VideoInfo(videoTitle, channelTitle, bitmap));
                }

            } catch (IOException e) {
                emitter.onError(e);
            }
        };

        return Single.create(onSubscribe);
    }

    private static YouTube.Videos.List buildVideosListQuery(YouTube youTubeDataAPIEndpoint, String videoId) throws IOException {

        return youTubeDataAPIEndpoint
                .videos()
                .list("snippet")
                .setFields("items(snippet(title,channelId,thumbnails(medium(url))))")
                .setId(videoId)
                .setKey("YOUTUBE_DATA_API_KEY");
    }

    private static YouTube.Channels.List buildChannelsListQuery(YouTube youTubeDataAPIEndpoint, String channelId) throws IOException {
        return youTubeDataAPIEndpoint
                .channels()
                .list("snippet")
                .setFields("items(snippet(title))")
                .setId(channelId)
                .setKey("YOUTUBE_DATA_API_KEY");
    }

    private static YouTube buildYouTubeEndpoint() {
        return new YouTube
                .Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setApplicationName(APP_NAME)
                .build();
    }
}


