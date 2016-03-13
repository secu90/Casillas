package com.android.casillas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MiPreferences extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Controles
    TextView cprefe_tv_axisX, cprefe_tv_axisY, cprefe_tv_value;
    SeekBar cprefe_sb_axisX, cprefe_sb_axisY, cprefe_sb_value;
    RadioButton cprefe_rb_numbers, cprefe_rb_colours, cprefe_rb_song1,cprefe_rb_song2,cprefe_rb_song3;
    CheckBox cprefe_cb_vibration, cprefe_cb_sound;
    ArrayList arrayCheckBox = new ArrayList<CheckBox>();
    Button cprefe_b_play;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Escuchadores
        //En lugar de crear un escuchador para cada seekbar, se crea un único escuchador y se comprueba la id del seekbar
        SeekBar.OnSeekBarChangeListener listenerSeekBar=new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
                    case R.id.cprefe_sb_axisX:
                        //cprefe_sb_axisX.getProgress()+3 porque un seekBar no deja cambiar el valor mínimo
                        cprefe_tv_axisX.setText(getResources().getString(R.string.cprefe_tv_axisX_text)+" "+ (cprefe_sb_axisX.getProgress()+3));
                        break;
                    case R.id.cprefe_sb_axisY:
                        //cprefe_sb_axisY.getProgress()+3 porque un seekBar no deja cambiar el valor mínimo
                        cprefe_tv_axisY.setText(getResources().getString(R.string.cprefe_tv_axisY_text) +" "+ (cprefe_sb_axisY.getProgress()+3));
                        break;
                    case R.id.cprefe_sb_value:
                        //cprefe_sb_axisX.getProgress()+2 porque un seekBar no deja cambiar el valor mínimo
                        cprefe_tv_value.setText(getResources().getString(R.string.cprefe_tv_value_text) +" "+ (cprefe_sb_value.getProgress()+2));
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };


        CheckBox.OnCheckedChangeListener listenerCheckBox = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                switch (buttonView.getId()){

                    case R.id.cprefe_cb_sound:
                        if (isChecked){
                            Toast.makeText(getApplicationContext(), R.string.cprefe_tv_sound_on_text, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),R.string.cprefe_tv_sound_off_text,Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.cprefe_cb_vibration:
                        if (isChecked){
                            Toast.makeText(getApplicationContext(),R.string.cprefe_tv_vibration_on_text,Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),R.string.cprefe_tv_vibration_off_text,Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

        View.OnClickListener listenerRadioButtonType = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.cprefe_rb_colours:
                        Toast.makeText(getApplicationContext(),R.string.cprefe_tv_radiogroup_colours_text, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.cprefe_rb_numbers:
                        Toast.makeText(getApplicationContext(),R.string.cprefe_tv_radiogroup_numbers_text, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        View.OnClickListener listenerRadioButtonSongs = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cadena = getResources().getString(R.string.toast_chose_song);
                switch (v.getId()){
                    case R.id.cprefe_rb_song1:
                        Toast.makeText(getApplicationContext(),cadena+(getResources().getString(R.string.cprefe_tv_song1)+"\""), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.cprefe_rb_song2:
                        Toast.makeText(getApplicationContext(), cadena+(getResources().getString(R.string.cprefe_tv_song2)+"\""), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.cprefe_rb_song3:
                        Toast.makeText(getApplicationContext(),cadena+(getResources().getString(R.string.cprefe_tv_song3)+"\""), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        preferences =getSharedPreferences("Preferences_Play", Context.MODE_PRIVATE);

        cprefe_sb_axisX = (SeekBar) findViewById(R.id.cprefe_sb_axisX);
        cprefe_sb_axisY = (SeekBar) findViewById(R.id.cprefe_sb_axisY);
        cprefe_sb_value = (SeekBar) findViewById(R.id.cprefe_sb_value);

        //Se añade el escuchador a los seekbar
        cprefe_sb_axisX.setOnSeekBarChangeListener(listenerSeekBar);
        cprefe_sb_axisY.setOnSeekBarChangeListener(listenerSeekBar);
        cprefe_sb_value.setOnSeekBarChangeListener(listenerSeekBar);

        cprefe_tv_axisX = (TextView) findViewById(R.id.cprefe_tv_axisX);
        cprefe_tv_axisY = (TextView) findViewById(R.id.cprefe_tv_axisY);
        cprefe_tv_value = (TextView) findViewById(R.id.cprefe_tv_value);


        cprefe_rb_numbers = (RadioButton) findViewById(R.id.cprefe_rb_numbers);
        cprefe_rb_colours = (RadioButton) findViewById(R.id.cprefe_rb_colours);

        //Se añade el escuchador a los radiobuttons
        cprefe_rb_colours.setOnClickListener(listenerRadioButtonType);
        cprefe_rb_numbers.setOnClickListener(listenerRadioButtonType);

        cprefe_rb_song1 = (RadioButton) findViewById(R.id.cprefe_rb_song1);
        cprefe_rb_song1.setText(getResources().getString(R.string.title_song1));
        cprefe_rb_song2 = (RadioButton) findViewById(R.id.cprefe_rb_song2);
        cprefe_rb_song2.setText(getResources().getString(R.string.title_song2));
        cprefe_rb_song3 = (RadioButton) findViewById(R.id.cprefe_rb_song3);
        cprefe_rb_song3.setText(getResources().getString(R.string.title_song3));

        //Se añade el escuchador a los radiobuttons
        cprefe_rb_song1.setOnClickListener(listenerRadioButtonSongs);
        cprefe_rb_song2.setOnClickListener(listenerRadioButtonSongs);
        cprefe_rb_song3.setOnClickListener(listenerRadioButtonSongs);

        cprefe_cb_vibration = (CheckBox) findViewById(R.id.cprefe_cb_vibration);
        cprefe_cb_sound = (CheckBox) findViewById(R.id.cprefe_cb_sound);

        //Se añade el escuchador a los checkbox
        cprefe_cb_vibration.setOnCheckedChangeListener(listenerCheckBox);
        cprefe_cb_sound.setOnCheckedChangeListener(listenerCheckBox);

        cprefe_b_play = (Button) findViewById(R.id.cprefe_b_play);

        cprefe_b_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlay();
            }
        });

        LoadPreferences();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
            openPlay();
        } else if (id == R.id.nav_preferences) {

        }
        else if (id == R.id.nav_help) {
            Intent i = new Intent(MiPreferences.this, Help.class);
            startActivity(i);
        }
        else if (id == R.id.nav_about) {
            Intent i = new Intent(MiPreferences.this, AboutMe.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void openPlay(){
        Intent i = new Intent(MiPreferences.this, Play.class);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("preference_axisX", cprefe_sb_axisX.getProgress());
        editor.putInt("preference_axisY", cprefe_sb_axisY.getProgress());
        editor.putInt("preference_value", cprefe_sb_value.getProgress());
        editor.putBoolean("preference_sound", cprefe_cb_sound.isChecked());
        editor.putBoolean("preference_vibration", cprefe_cb_vibration.isChecked());
        if (cprefe_rb_numbers.isChecked()) {
            editor.putString("preference_radiogroupType", "" + cprefe_rb_numbers.getText());
        }
        else if (cprefe_rb_colours.isChecked()) {
            editor.putString("preference_radiogroupType", "" + cprefe_rb_colours.getText());
        }
        if (cprefe_rb_song1.isChecked()) {
            editor.putString("preference_radiogroupSong", "" + cprefe_rb_song1.getText());
        }
        else if (cprefe_rb_song2.isChecked()) {
            editor.putString("preference_radiogroupSong", "" + cprefe_rb_song2.getText());
        }
        else if (cprefe_rb_song3.isChecked()) {
            editor.putString("preference_radiogroupSong", "" + cprefe_rb_song3.getText());
        }

        editor.commit();
        startActivity(i);
    }
    private void LoadPreferences() {
        cprefe_sb_axisX.setProgress(preferences.getInt("preference_axisX",3));
        cprefe_sb_axisY.setProgress(preferences.getInt("preference_axisY", 3));
        cprefe_sb_value.setProgress(preferences.getInt("preference_value", 2));
        cprefe_cb_vibration.setChecked(preferences.getBoolean("preference_vibration", false));
        cprefe_cb_sound.setChecked(preferences.getBoolean("preference_sound", false));

        String preference_radiobutton = preferences.getString("preference_radiogroupType",getResources().getString(R.string.cprefe_rb_numbers));
        if (preference_radiobutton.equals(getResources().getString(R.string.cprefe_rb_numbers))){
            cprefe_rb_numbers.setChecked(true);
        }
        if (preference_radiobutton.equals(getResources().getString(R.string.cprefe_rb_colours))){
            cprefe_rb_colours.setChecked(true);
        }

        cprefe_tv_axisX.setText(getResources().getString(R.string.cprefe_tv_axisX_text) + (cprefe_sb_axisX.getProgress()+3));

        cprefe_tv_axisY.setText(getResources().getString(R.string.cprefe_tv_axisY_text) + (cprefe_sb_axisY.getProgress()+3));

        cprefe_tv_value.setText(getResources().getString(R.string.cprefe_tv_value_text)+ (cprefe_sb_value.getProgress()+2));
    }
}

