package net.laenredadera.full.peliculas.gratis.de.terror.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.laenredadera.full.peliculas.gratis.de.terror.Activities.YoutubeVideoActivity;
import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;
import net.laenredadera.full.peliculas.gratis.de.terror.R;

import io.realm.RealmList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import java.util.Objects;

public class FavoriteCardAdapter extends RecyclerView.Adapter<FavoriteCardAdapter.ViewHolder> {

    public String TAG = "FavoriteCardAdapter";
    private Context context;
    private final RealmList<RealmMovieData> videos;
    private final int layout;
    private final OnItemClickListener itemClickListener;
    private final OnFavoriteClickListener favoriteClickListener;
    private final OnPlayClickListener playClickListener;
    private int mExpandedPosition = -1;
    private int previousExpandedPosition = -1;
    PopupWindow popupWindow;
    View popupView;

    public FavoriteCardAdapter(RealmList<RealmMovieData> videos,
                               int layout,
                               FavoriteCardAdapter.OnItemClickListener listener,
                               FavoriteCardAdapter.OnFavoriteClickListener favoritelistener,
                               FavoriteCardAdapter.OnPlayClickListener playListener){
        this.videos = videos;
        this.layout = layout;
        this.itemClickListener = listener;
        this.favoriteClickListener = favoritelistener;
        this.playClickListener = playListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card;
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_loading, null);
        card = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (videos != null && Objects.requireNonNull(videos.get(position)).getEnabled() == 1) {
                holder.bind(videos.get(position), itemClickListener, position);
            }
        } catch (IllegalStateException ignored) {

        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView poster;
        public final ImageView icon;
        public final TextView title;
        public final TextView year;
        public final TextView overview;
        public final LinearLayout details;
        public String videoUrl;
        public ImageView expandLess;
        public ImageView expandMore;
        public final RelativeLayout cardHeader;
        public final ImageView share;
        public final ImageView favorite;
        public final TextView runtime;
        MyApp app = new MyApp();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.cardExpandedLayout);
            expandLess = itemView.findViewById(R.id.imageViewExpandLessIcon);
            expandMore = itemView.findViewById(R.id.imageViewExpandMoreIcon);
            cardHeader = itemView.findViewById(R.id.cardCollapsedLayout);
            poster = itemView.findViewById(R.id.imageViewPoster);
            icon = itemView.findViewById(R.id.imageViewIcon);
            title = itemView.findViewById(R.id.textViewTitle);
            year = itemView.findViewById(R.id.text_view_year);
            overview = itemView.findViewById(R.id.textViewOverview);
            expandLess = itemView.findViewById(R.id.imageViewExpandLessIcon);
            expandMore = itemView.findViewById(R.id.imageViewExpandMoreIcon);
            share = itemView.findViewById(R.id.imageViewShareIcon);
            favorite = itemView.findViewById(R.id.imageViewFavoriteEmptyIcon);
            runtime = itemView.findViewById(R.id.textViewRuntime);
        }

        public void bind(final RealmMovieData video, final OnItemClickListener listener, final int position) {
            String size = getScreeenSize();
            int tries = 3;

            for (int t = 0; t < tries; t++) {
                try {
                    title.setText(video.getTitle());
                    year.setText(video.getYear());
                    videoUrl = video.getLink();
                    overview.setText(video.getPlot());
                    runtime.setText(video.getRuntime());

                    TranslateAnimation animate = new TranslateAnimation(0,
                            0,
                            0,
                            0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    favorite.startAnimation(animate);
                } catch (NullPointerException npe) {
                    if (t == tries - 1) {
                        openNullPointerDialog();
                    }
                }
            }

            if (!video.getIcon().isEmpty()) {
                String iconUrl = app.getAppContext().getString(R.string.strapiendpoint) + video.getIcon();
                Glide.with(context).load(iconUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(icon);
            }
            if (!video.getPoster().isEmpty()) {
                String posterUrl = app.getAppContext().getString(R.string.strapiendpoint) + video.getPoster();
                Glide.with(context).load(posterUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(poster);
            }

            favorite.setBackgroundResource(video.isFavorite() ? R.drawable.ic_favorite_red_24dp : R.drawable.ic_favorite_border_almost_white_24dp);
            final boolean isExpanded = position == mExpandedPosition;
            final int colorActive =  ContextCompat.getColor(context, R.color.grey_darker);
            final int colorNoActive = ContextCompat.getColor(context,R.color.black_almost_pure);
            cardHeader.setBackgroundColor(isExpanded?colorActive:colorNoActive);

            switch (size) {
                case "large":
                case "xlarge":
                    expandLess.setVisibility(View.GONE);
                    expandMore.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                    cardHeader.setBackgroundColor(isExpanded?colorActive:colorNoActive);
                    break;
                case "normal":
                    details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    expandLess.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    expandMore.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                    cardHeader.setActivated(isExpanded);
                    if (isExpanded)
                        previousExpandedPosition = position;
                    cardHeader.setOnClickListener(view -> {

                        mExpandedPosition = isExpanded ? -1 : position;
                        notifyItemChanged(previousExpandedPosition);
                        notifyItemChanged(position);
                        if (!isExpanded) {
                            listener.onItemClick(getAdapterPosition(), 0);
                        }
                    });

                    break;
            }

            share.setOnClickListener(view -> {
                StringBuilder text = new StringBuilder(video.getTitle() + " - " + video.getSubtitle() + ": " + videoUrl);
                String title = context.getResources().getText(R.string.send_to).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, title));
            });
            poster.setOnClickListener(view -> {

                Intent youtubePlayer = new Intent(context.getApplicationContext(), YoutubeVideoActivity.class);
                youtubePlayer.putExtra("YoutubeVideoUrl", videoUrl);
                youtubePlayer.putExtra("ID", video.getId());
                youtubePlayer.putExtra( "Title",video.getTitle());
                youtubePlayer.putExtra("Year",video.getYear());
                youtubePlayer.putExtra("Icon",video.getIcon());
                youtubePlayer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                popupWindow = new PopupWindow(context);

                if (app.isInterstitialAdEnabled()) {
                    try {
                        if(popupView.getParent() != null) {
                            ((ViewGroup)popupView.getParent()).removeView(popupView);
                        }
                        popupWindow.setContentView(popupView);
                        popupWindow.showAtLocation(poster, Gravity.CENTER,0,0);
                    }catch (IllegalStateException ise){
                        ise.printStackTrace();
                    }

                } else {
                    nextActivity(youtubePlayer);
                }
                playClickListener.onPlayClick(youtubePlayer,popupWindow);

            });


            favorite.setOnClickListener(view -> {

                favoriteClickListener.onFavoriteClick(video, position);
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            });

        }

        private void nextActivity(Intent intent) {
            context.startActivity(intent);
            if (popupWindow != null) popupWindow.dismiss();
        }
        public void openNullPointerDialog(){

            AlertDialog.Builder dialog1 = new AlertDialog.Builder(context)

                    .setMessage(app.getAppContext().getResources().getString(R.string.noconnection))
                    .setNeutralButton(app.getAppContext().getResources().getString(R.string.ok), (dialog, which) -> System.exit(0));
            dialog1.show();
        }

        private String getScreeenSize() {
            int screenSize = context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;
            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    return "xlarge";
                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                    return "undefined";
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    return "large";
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    return "normal";
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    return "small";
                default:
                    return "";
            }

        }
    }


    public interface OnItemClickListener {
        void onItemClick(int position, int YPos);
    }


    public interface OnFavoriteClickListener {
        void onFavoriteClick(RealmMovieData v, int position);

    }
    public interface OnPlayClickListener {
        void onPlayClick(Intent intent, PopupWindow popupwindow);

    }
    public interface mAdapaterFinishResponse {
        void processFinish();
    }
}