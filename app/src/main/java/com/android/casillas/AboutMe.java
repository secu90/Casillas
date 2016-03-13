package com.android.casillas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutMe extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
TextView mMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mMail=(TextView)findViewById(R.id.c_about_tv_text4);
        mMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String[] to = direccionesEmail;
                //String[] cc = copias;
                String[] to = { "sergio.cuenca@iesdoctorbalmis.com"};

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));

                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
              //  emailIntent.putExtra(Intent.EXTRA_CC, cc);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.activity_aboutme_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.activity_aboutme_messaje));
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Email "));
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_play) {
            Intent i = new Intent(AboutMe.this, Play.class);
            startActivity(i);
        } else if (id == R.id.nav_preferences) {
            Intent i = new Intent(AboutMe.this, MiPreferences.class);
            startActivity(i);
        }
        else if (id == R.id.nav_help) {
            Intent i = new Intent(AboutMe.this, Help.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
