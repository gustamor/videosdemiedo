package net.laenredadera.full.peliculas.gratis.de.terror.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;

import com.google.android.gms.cast.framework.CastButtonFactory;

import net.laenredadera.full.peliculas.gratis.de.terror.R;

public class ExpandedControlsActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.expanded_controller, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }
}