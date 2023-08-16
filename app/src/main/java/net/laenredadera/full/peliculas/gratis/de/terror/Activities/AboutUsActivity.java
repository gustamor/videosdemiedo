package net.laenredadera.full.peliculas.gratis.de.terror.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import net.laenredadera.full.peliculas.gratis.de.terror.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.image_backarrow_tab)
    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        setoolbar();
    }

    private void backToMainActivity() {
        Intent mainIntent = new Intent(AboutUsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                |Intent.FLAG_ACTIVITY_NO_HISTORY
                |Intent.FLAG_ACTIVITY_NEW_TASK);
        AboutUsActivity.this.startActivity(mainIntent);
        AboutUsActivity.this.finish();
    }
    @SuppressLint("NonConstantResourceId")
    @OnClick (R.id.image_backarrow_tab)
    public void back(){
        backToMainActivity();
    }

    public void setoolbar() {

        mToolbar.setTitle("");
        backArrow.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    @Override
    public void onBackPressed() {
        backToMainActivity();
    }
    @Override
    public void onClick(View v) {
        backToMainActivity();
    }

}
