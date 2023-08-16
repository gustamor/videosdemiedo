package net.laenredadera.full.peliculas.gratis.de.terror.UI;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import net.laenredadera.full.peliculas.gratis.de.terror.R;

public class DrawableShapes {

    private final RecyclerView mRecyclerView;
    private final Context context;

    public DrawableShapes(Context context, RecyclerView rv) {
        this.context = context;
        this.mRecyclerView = rv;
        addHorizontalDecorationToRecyclerView();
    }

    public void addHorizontalDecorationToRecyclerView(){

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.separator_line);
        assert horizontalDivider != null;
        dividerItemDecoration.setDrawable(horizontalDivider);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

    }

}
