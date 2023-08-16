package net.laenredadera.full.peliculas.gratis.de.terror.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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

import net.laenredadera.full.peliculas.gratis.de.terror.App.MyApp;
import net.laenredadera.full.peliculas.gratis.de.terror.Models.RealmMovieData;
import net.laenredadera.full.peliculas.gratis.de.terror.R;

import io.realm.RealmList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class YoutubeActivityVideoCardAdapter extends RecyclerView.Adapter<YoutubeActivityVideoCardAdapter.ViewHolder> {

    public String TAG = "YoutubeActivityVideoCardAdapter";

    private Context context;
    private final RealmList<RealmMovieData> videos;
    private final int layout;
    private int expanded;
    private String videoID;
    private final YoutubeActivityVideoCardAdapter.OnItemClickListener itemClickListener;
    private final YoutubeActivityVideoCardAdapter.OnPlayClickListener playClickListener;
    private final YoutubeActivityVideoCardAdapter.OnFavoriteClickListener favoriteClickListener;
    private final YoutubeActivityVideoCardAdapter.Player activePlayer;
    private int mExpandedPosition = -1;
    private int mPlayingPosition = -1;
    private int previousExpandedPosition = -1;
    private int previousPlayingPosition = -1;
    PopupWindow popupWindow;
    View popupView;
    MyApp app = new MyApp();
    public YoutubeActivityVideoCardAdapter(int expanded,
                                           RealmList<RealmMovieData> videos,
                                           int layout,
                                           OnItemClickListener listener,
                                           OnPlayClickListener playClickListener,
                                           Player player ,
                                           YoutubeActivityVideoCardAdapter.OnFavoriteClickListener favorite) {
        this.expanded = expanded;
        this.videos = videos;
        this.layout = layout;
        this.itemClickListener = listener;
        this.playClickListener = playClickListener;
        this.activePlayer = player;
        this.favoriteClickListener = favorite;
    }

    @NonNull
    @Override
    public YoutubeActivityVideoCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View card;
        context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_loading,null);
        card = LayoutInflater.from(context).inflate(layout,parent,false);

        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubeActivityVideoCardAdapter.ViewHolder holder, final int position) {

        if ( position == expanded) {
            mPlayingPosition = expanded;
            expanded = -1;
        }
        holder.bind(videos.get(position), itemClickListener, playClickListener, activePlayer, position);
    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final ImageView poster;
        public final ImageView icon;
        public final TextView title;
        public final TextView year;
        public final TextView overview;
        public final LinearLayout details;
        public String videoUrl;
        public final ImageView expandLess;
        public final ImageView expandMore;
        public final RelativeLayout cardHeader;
        public final ImageView share;
        public final ImageView ytbShare;

        public final ImageView favorite;
        public final TextView runtime;

        public final String screenSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.imageViewPoster);
            icon = itemView.findViewById(R.id.imageViewIcon);
            title = itemView.findViewById(R.id.textViewTitle);
            year = itemView.findViewById(R.id.text_view_year);
            overview = itemView.findViewById(R.id.textViewOverview);
            details = itemView.findViewById(R.id.cardExpandedLayout);
            expandLess = itemView.findViewById(R.id.imageViewExpandLessIcon);
            expandMore = itemView.findViewById(R.id.imageViewExpandMoreIcon);
            cardHeader = itemView.findViewById(R.id.cardCollapsedLayout);
            share = itemView.findViewById(R.id.imageViewShareIcon);
            ytbShare = itemView.findViewById(R.id.imageViewYtbSend);

            favorite = itemView.findViewById(R.id.imageViewFavoriteEmptyIcon);
            runtime = itemView.findViewById(R.id.textViewRuntime);
            screenSize = getScreeenSize();
            //   adsCardView = (ViewGroup) itemView;
            ytbShare.setVisibility(app.auxUno() ? View.VISIBLE: View.GONE);
            share.setVisibility(app.auxUno() ? View.GONE: View.VISIBLE);

        }

        @SuppressLint("RestrictedApi")
        public void bind(final RealmMovieData video,
                         final OnItemClickListener listener,
                         final OnPlayClickListener playClickListener,
                         Player activePlayer,
                         final int position){

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

            favorite.setBackgroundResource(video.isFavorite()?R.drawable.ic_favorite_red_24dp:R.drawable.ic_favorite_border_almost_white_24dp);

            final boolean isExpanded = position==mExpandedPosition;
            final boolean isPlaying = position==mPlayingPosition;

            final int colorActive =  ContextCompat.getColor(context, R.color.grey_darker);
            final int colorNoActive = ContextCompat.getColor(context,R.color.black_almost_pure);

            if (isExpanded)
                previousExpandedPosition = position;
            if (isPlaying)
                previousPlayingPosition = position;

            //   details.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            cardHeader.setBackgroundColor(isPlaying?colorActive:colorNoActive);
            runtime.setVisibility(isPlaying?View.GONE:View.VISIBLE);
            // fab.setVisibility(mPlayingPosition==position?View.GONE:View.VISIBLE);
            poster.setClickable(mPlayingPosition != position);
            poster.setEnabled(mPlayingPosition != position);

            switch (screenSize){
                case "large":
                case "xlarge":
                    expandLess.setVisibility(View.GONE);
                    expandMore.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                    cardHeader.setBackgroundColor(isPlaying?colorActive:colorNoActive);

                    break;
                case "normal":
                    details.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                    expandLess.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                    expandMore.setVisibility(isExpanded?View.GONE:View.VISIBLE);
                    cardHeader.setActivated(isExpanded);
                    if (isExpanded)
                        previousExpandedPosition = position;

                    cardHeader.setOnClickListener(view -> {
                        mExpandedPosition = isExpanded ? -1:position;
                        notifyItemChanged(previousExpandedPosition);
                        notifyItemChanged(position);
                        if (!isExpanded) {
                            listener.onItemClick(getAdapterPosition(), 0);
                        }
                    });
                    break;
            }
            if (app.auxUno()) {
                ytbShare.setOnClickListener(view -> {
                    String text = videoUrl;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(text));
                    context.startActivity(intent);
                });
            } else {
                share.setOnClickListener(view -> {
                    String text = video.getTitle() + " - " + video.getSubtitle() +": " + videoUrl;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    sendIntent.setType("text/plain");
                    context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.send_to)));
                });
            }


            poster.setOnClickListener(view -> {
                if (isExpanded) {
                    mPlayingPosition = isPlaying ? -1 : position;
                }
                if (screenSize.equals("large") || screenSize.equals("xlarge")) {
                    mPlayingPosition = isPlaying ? -1 : position;
                }
                notifyItemChanged(previousPlayingPosition);
                notifyItemChanged(position);
                playClickListener.onPlayItemClick(video);
            });

            favorite.setOnClickListener(view -> {
                favoriteClickListener.onFavoriteClick(video);
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            });
        }

        public void openNullPointerDialog(){
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(context)
                    .setMessage(app.getAppContext().getResources().getString(R.string.noconnection))
                    .setNeutralButton(app.getAppContext().getResources().getString(R.string.ok), (dialog, which) -> System.exit(0));
            dialog1.show();
        }
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

    public interface Player {
        void adIsDisplaying(boolean state);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int YPos);
    }


    public interface OnFavoriteClickListener {
        void onFavoriteClick(RealmMovieData v);
    }


    public interface OnPlayClickListener {
        void onPlayItemClick(RealmMovieData video);

    }
}