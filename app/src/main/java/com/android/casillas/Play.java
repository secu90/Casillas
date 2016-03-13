package com.android.casillas;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Play extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    Boolean mSoundEnable;

    SharedPreferences mPreferences;
    private Thread THREADSONG;//Hilo para la canción
    private MediaPlayer SONG;
    private SoundPool TOUCHSOUND;
    private int LOADTOUCHSOUND;//indicardor para cargar la canción
    private Menu mMENU;// Con esto se podrá ocultar el item del overflow (star/pausa) par la canción

    private SensorManager mSENSORMANAGER;
    private Sensor mACELEROMETRO;
    private ShakeListener mShaker;

    private TableLayout mTable;

    private Chronometer mTime;

    private int mPulsation_count;
    private TextView mPulsation_text;

    private int axixX, axixY, valueGame;

    private Button CHEAT;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mPreferences = getSharedPreferences("Preferences_Play", Context.MODE_PRIVATE);

        if (mPreferences.getAll().isEmpty()) {//Por si no hubiera archivo de preferencias
            DefaultPreferences();
        }

        DeclarationControllers();//Se declaran todos los controles

        LoadPreferences();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        CreatePanel();
        mTime.start();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void DeclarationControllers() {

        mSoundEnable = mPreferences.getBoolean("preference_sound", false);
        axixX = mPreferences.getInt("preference_axisX", 3) + 3;
        axixY = mPreferences.getInt("preference_axisY", 3) + 3;
        valueGame = mPreferences.getInt("preference_value", 2) + 2;
        mTable = (TableLayout) findViewById(R.id.cp_tl_container_table);

        mPulsation_count = 0;
        mPulsation_text = (TextView) findViewById(R.id.cp_tv_pulsation_count);
        mTime = (Chronometer) findViewById(R.id.cp_c_chronometer);

        if (mSoundEnable) {

            TOUCHSOUND = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
            LOADTOUCHSOUND = TOUCHSOUND.load(this, R.raw.touch, 1);
            SONG = new MediaPlayer();
            SONG.setLooping(true);

            mSENSORMANAGER = (SensorManager) getSystemService(SENSOR_SERVICE);
            mACELEROMETRO = mSENSORMANAGER.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShaker = new ShakeListener(this);
            mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
                public void onShake() {
                    if (SONG.isPlaying()) {
                        PauseSong();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.p_shaked_pause), Toast.LENGTH_SHORT).show();
                    } else {
                        StartSong();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.p_shaked_play), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        CHEAT = (Button) findViewById(R.id.cp_cheat);
        CHEAT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndGame();
            }
        });
    }

    private void CreatePanel() {
        DisplayMetrics dm = new DisplayMetrics();
        //Se saca el ancho de la pantalla
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //Se saca los margenes del contenedor de la tabla y se pasan a pixel (por defecto vienen en dp)
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());

        //Se resta el ancho por el margen*2 (margen de la derecha y de la izquierda) y se divide por el numero de filas
        int width = (int) ((dm.widthPixels - (px * 2)) / axixX);
        // Se vuelve a usar el ancho y no el alto porque no se tiene la altura.
        int height = (int) ((dm.widthPixels - (px * 2)) / axixY);
        for (int y = 0; y < axixY; y++) {
            //Se construye la filas
            // - |__|
            TableRow fila = new TableRow(this);
            fila.setId(100 * (y + 1));
            for (int x = 0; x < axixX; x++) {
                // |__| |__| |__| |__| |__|
                //Se construyen las columnas y se añaden a las filas
                final ButtonGame item = new ButtonGame(this);
                item.mConstructor(y, x);
                item.setId((fila.getId()) + (x + 1));
                item.setMinimumHeight(height);
                item.setMinimumWidth(width);
                item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                item.getId();
                                                if (mSoundEnable) {
                                                    TOUCHSOUND.play(LOADTOUCHSOUND, 1, 1, 0, 0, 0);
                                                }
                                                mPulsation_count++;
                                                mPulsation_text.setText(getResources().getString(R.string.mPulsation_text) + " " + mPulsation_count);
                                                ChangeValue(item.valueY, item.valueX);
                                                if (isCompleteGame()) {
                                                    EndGame();
                                                }
                                            }
                                        }
                );
                fila.addView(item);
                fila.setGravity(Gravity.CENTER_HORIZONTAL);

            }
            mTable.addView(fila);
            mTable.setGravity(Gravity.CENTER_VERTICAL);
        }
    }

    private void ChangeValue(int y, int x) {
        boolean check = true;
        onChangeValueItem(y, x);//item pulsado
        if (y == 0) {
            //Top
            if (x == 0 || (x + 1) == axixX) {
                //Top && Corner
                TopCornerRow(y, x);
                check = false;
            } else {
                TopRow(y, x);
                check = false;
            }
        }
        if ((y + 1) == axixY) {
            //Bottom
            if (x == 0 || (x + 1) == axixX) {
                //Bottom && Corner
                ButtomCornerRow(y, x);
                check = false;
            } else {
                BottomRow(y, x);
                check = false;
            }
        }

        if (check) {
            if (x == 0 || (x + 1) == axixX) {
                LateralRow(y, x);
            } else {
                onChangeValueItem(y, (x + 1));//item de la derecha
                onChangeValueItem(y, (x - 1));//item de debajo
                onChangeValueItem((y + 1), x);//item diagonal inferior derecha
                onChangeValueItem((y - 1), x);//item diagonal inferior derecha
            }
        }

    }

    private void LateralRow(int y, int x) {
        if (x == 0) {
            onChangeValueItem(y, (x + 1));//item de la derecha
            onChangeValueItem((y - 1), x);//item de arriba
            onChangeValueItem((y + 1), x);//item dabajo
        }
        if ((x + 1) == axixX) {
            onChangeValueItem(y, (x - 1));//item de la derecha
            onChangeValueItem((y - 1), x);//item de arriba
            onChangeValueItem((y + 1), x);//item dabajo
        }
    }

    private void TopCornerRow(int y, int x) {
        if (x == 0) {
            onChangeValueItem(y, (x + 1));//item de la derecha
            onChangeValueItem((y + 1), x);//item de debajo
            onChangeValueItem((y + 1), (x + 1));//item diagonal inferior derecha
        }
        if ((x + 1) == axixX) {
            onChangeValueItem(y, (x - 1));//item de la izquierda
            onChangeValueItem((y + 1), x);//item abajo
            onChangeValueItem((y + 1), (x - 1));//item diagonal abajo izquierda
        }
    }

    private void TopRow(int y, int x) {
        onChangeValueItem(y, (x + 1));//item de la derecha
        onChangeValueItem(y, (x - 1));//item de izquierda
        onChangeValueItem((y + 1), (x));//item de abajo
    }

    private void ButtomCornerRow(int y, int x) {
        if (x == 0) {
            onChangeValueItem(y, (x + 1));//item de la derecha
            onChangeValueItem((y - 1), x);//item de arriba
            onChangeValueItem((y - 1), (x + 1));//item diagonal derecha arriba
        }
        if ((x + 1) == axixX) {
            onChangeValueItem(y, (x - 1));//item de la izquierda
            onChangeValueItem((y - 1), x);//item de arriba
            onChangeValueItem((y - 1), (x - 1));//item diagonal izquierda arriba
        }
    }

    private void BottomRow(int y, int x) {
        onChangeValueItem(y, (x + 1));//item de la derecha
        onChangeValueItem(y, (x - 1));//item de izquierda
        onChangeValueItem((y - 1), (x));//item de arriba
    }

    private void onChangeValueItem(int y, int x) {
        if ((((ButtonGame) ((TableRow) mTable.getChildAt(y)).getChildAt(x)).mValue + 1) > valueGame) {
            ((ButtonGame) ((TableRow) mTable.getChildAt(y)).getChildAt(x)).mValue = 1;
        } else {
            ((ButtonGame) ((TableRow) mTable.getChildAt(y)).getChildAt(x)).mValue++;
        }
        ((ButtonGame) ((TableRow) mTable.getChildAt(y)).getChildAt(x)).onValueChange();
    }

    private void LoadPreferences() {

        String preference_radiobutton = mPreferences.getString("preference_radiogroupType", getResources().getString(R.string.cprefe_rb_numbers));
        /*
            Se compruba que radiobutton de la canción está activo
            Song 1: after_mi_god_prot_8827_hifi=After mi god prot
            Song 2: bawa_sus_bawa_sus_10236_hifi=Bawa sus
            Song3: jewelbea_jewelbea_10592_hifi=Jewelbea
        */

        preference_radiobutton = mPreferences.getString("preference_radiogroupSong", getResources().getString(R.string.cprefe_rb_numbers));
        if (mSoundEnable) {
            LOADTOUCHSOUND = TOUCHSOUND.load(this, R.raw.touch, 1);
            if (preference_radiobutton.equals(getResources().getString(R.string.title_song1))) {
                SONG = MediaPlayer.create(this, R.raw.after_mi_god_prot_8827_hifi);
            } else if (preference_radiobutton.equals(getResources().getString(R.string.title_song2))) {
                SONG = MediaPlayer.create(this, R.raw.bawa_sus_bawa_sus_10236_hifi);
            } else if (preference_radiobutton.equals(getResources().getString(R.string.title_song3))) {
                SONG = MediaPlayer.create(this, R.raw.jewelbea_jewelbea_10592_hifi);
            }
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mSoundEnable) {
            mMENU = menu;
            getMenuInflater().inflate(R.menu.menu_player, mMENU);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.mp_action_play) {
            StartSong();
            return true;
        }
        if (id == R.id.mp_action_pause) {
            PauseSong();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_play) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(), R.string.toast_id_nav_play_text, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_preferences) {
            Intent i = new Intent(Play.this, MiPreferences.class);
            startActivity(i);
        } else if (id == R.id.nav_help) {
            Intent i = new Intent(Play.this, Help.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(Play.this, AboutMe.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        if (mSoundEnable) {
            StopSong();
            mSENSORMANAGER.unregisterListener(this);
            mShaker.pause();
        }
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Play Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.android.casillas/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onPause() {
        if (mSoundEnable) {
            PauseSong();
            mSENSORMANAGER.unregisterListener(this);
            mShaker.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mSoundEnable) {
            mSENSORMANAGER.registerListener(this, mACELEROMETRO, SensorManager.SENSOR_DELAY_NORMAL);
            mShaker.resume();
        }
        super.onResume();
    }

    private void PauseSong() {
        if (mSoundEnable) {
            THREADSONG = new Thread() {
                @Override
                public void run() {
                    SONG.pause();
                }
            };
            MenuItem i = mMENU.findItem(R.id.mp_action_play);
            i.setEnabled(true);
            i.setVisible(true);
            i = mMENU.findItem(R.id.mp_action_pause);
            i.setEnabled(false);
            i.setVisible(false);
            THREADSONG.start();
        }
    }

    private void StartSong() {
        if (mSoundEnable) {
            THREADSONG = new Thread() {
                @Override
                public void run() {
                    SONG.setLooping(true);
                    SONG.start();

                }
            };

            MenuItem i = mMENU.findItem(R.id.mp_action_pause);
            i.setEnabled(true);
            i.setVisible(true);
            i = mMENU.findItem(R.id.mp_action_play);
            i.setEnabled(false);
            i.setVisible(false);
            THREADSONG.start();
        }
    }

    private void StopSong() {
        if (mSoundEnable) {
            MenuItem i = mMENU.findItem(R.id.mp_action_play);
            i.setEnabled(true);
            i.setVisible(true);
            i = mMENU.findItem(R.id.mp_action_pause);
            i.setEnabled(false);
            i.setVisible(false);
            THREADSONG = new Thread() {
                @Override
                public void run() {
                    SONG.stop();
                }
            };
            THREADSONG.start();
        }
    }

    private void DefaultPreferences() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("preference_axisX", 0);
        editor.putInt("preference_axisY", 0);
        editor.putInt("preference_value", 0);
        editor.putBoolean("preference_sound", false);
        editor.putBoolean("preference_vibration", false);
        editor.putString("preference_radiogroupType", getResources().getString(R.string.cprefe_rb_numbers));
        editor.putString("preference_radiogroupSong", getResources().getString(R.string.title_song1));
        editor.commit();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //cplay_tv_acelerometer.setText(event.values[0]+"     "+event.values[1]+"     "+event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressLint("LongLogTag")
    private boolean isCompleteGame() {
        TableRow tr = (TableRow) mTable.getChildAt(0);
        ButtonGame btn = (ButtonGame) tr.getChildAt(0);
        int valueBefore = btn.mValue;
        for (int y = 0; y < mTable.getChildCount(); y++) {
            tr = (TableRow) mTable.getChildAt(y);
            for (int x = 0; x < tr.getChildCount(); x++) {
                btn = (ButtonGame) tr.getChildAt(x);
                if (valueBefore != btn.mValue) {
                    return false;
                }
                valueBefore = btn.mValue;
            }
        }
        return true;
    }

    private void EndGame() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(getResources().getString(R.string.p_EndGame_ad_tittle));
        ad.setMessage(getResources().
                getString(R.string.p_EndGame_ad_msg1) + " " + mTime.getText() + " " + getResources().getString(R.string.p_EndGame_ad_msg2) + "\r\n"
                + getResources().getString(R.string.p_EndGame_ad_msg3) + mPulsation_count + "\r\n"
                + getResources().getString(R.string.p_EndGame_ad_msg4));
        ad.setCancelable(false);
        ad.setPositiveButton(getResources().getString(R.string.p_EndGame_ad_button_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface ad, int id) {
                Intent i = new Intent(Play.this, MainActivity.class);
                startActivity(i);
            }
        });
        ad.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Play Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.android.casillas/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }


    public class ButtonGame extends ImageView implements Parcelable{

        private int mValue, valueX, valueY;

        public ButtonGame(Context context) {
            super(context);
        }

        public ButtonGame(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ButtonGame(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public ButtonGame(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }


        protected ButtonGame(Parcel in) {
            super(null,null,0,0);
            mValue = in.readInt();
            valueX = in.readInt();
            valueY = in.readInt();
        }

        public final Creator<ButtonGame> CREATOR = new Creator<ButtonGame>() {
            @Override
            public ButtonGame createFromParcel(Parcel in) {
                return new ButtonGame(in);
            }

            @Override
            public ButtonGame[] newArray(int size) {
                return new ButtonGame[size];
            }
        };

        public void mConstructor(int valueY, int valueX){
            this.valueY=valueY;
            this.valueX=valueX;
            mValue = (int) (Math.random() * (valueGame) + 1);
            onValueChange();
        }
        private void onValueChange() {
            if (mPreferences.getString("preference_radiogroupType", getResources().getString(R.string.cprefe_rb_numbers)).equals(getResources().getString(R.string.cprefe_rb_numbers))) {
                setBackground(getNumberIcon(mValue));
            }
            if (mPreferences.getString("preference_radiogroupType", getResources().getString(R.string.cprefe_rb_colours)).equals(getResources().getString(R.string.cprefe_rb_colours))) {
                setBackground(getColourIcon(mValue));
            }
        }

        private Drawable getNumberIcon(int number) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getNumberIconLollipop(number);
            }
            return getNumberIconOlder(number);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private Drawable getNumberIconLollipop(int number) {
            Drawable icon = null;
            switch (number) {
                case 1:
                    icon = getResources().getDrawable(R.drawable.ic_1n, getTheme());
                    break;
                case 2:
                    icon = getResources().getDrawable(R.drawable.ic_2n, getTheme());
                    break;
                case 3:
                    icon = getResources().getDrawable(R.drawable.ic_3n, getTheme());
                    break;
                case 4:
                    icon = getResources().getDrawable(R.drawable.ic_4n, getTheme());
                    break;
                case 5:
                    icon = getResources().getDrawable(R.drawable.ic_5n, getTheme());
                    break;
                case 6:
                    icon = getResources().getDrawable(R.drawable.ic_6n, getTheme());
                    break;
            }
            return icon;
        }

        private Drawable getNumberIconOlder(int number) {
            Drawable icon = null;
            switch (number) {
                case 1:
                    icon = getResources().getDrawable(R.drawable.ic_1n);
                    break;
                case 2:
                    icon = getResources().getDrawable(R.drawable.ic_2n);
                    break;
                case 3:
                    icon = getResources().getDrawable(R.drawable.ic_3n);
                    break;
                case 4:
                    icon = getResources().getDrawable(R.drawable.ic_4n);
                    break;
                case 5:
                    icon = getResources().getDrawable(R.drawable.ic_5n);
                    break;
                case 6:
                    icon = getResources().getDrawable(R.drawable.ic_6n);
                    break;
            }
            return icon;
        }

        private Drawable getColourIcon(int number) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getColourIconLollipop(number);
            }
            return getColourIconOlder(number);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private Drawable getColourIconLollipop(int number) {
            Drawable icon = null;
            switch (number) {
                case 1:
                    icon = getResources().getDrawable(R.drawable.ic_1c, getTheme());
                    break;
                case 2:
                    icon = getResources().getDrawable(R.drawable.ic_2c, getTheme());
                    break;
                case 3:
                    icon = getResources().getDrawable(R.drawable.ic_3c, getTheme());
                    break;
                case 4:
                    icon = getResources().getDrawable(R.drawable.ic_4c, getTheme());
                    break;
                case 5:
                    icon = getResources().getDrawable(R.drawable.ic_5c, getTheme());
                    break;
                case 6:
                    icon = getResources().getDrawable(R.drawable.ic_6c, getTheme());
                    break;
            }
            return icon;
        }

        private Drawable getColourIconOlder(int number) {
            Drawable icon = null;
            switch (number) {
                case 1:
                    icon = getResources().getDrawable(R.drawable.ic_1c);
                    break;
                case 2:
                    icon = getResources().getDrawable(R.drawable.ic_2c);
                    break;
                case 3:
                    icon = getResources().getDrawable(R.drawable.ic_3c);
                    break;
                case 4:
                    icon = getResources().getDrawable(R.drawable.ic_4c);
                    break;
                case 5:
                    icon = getResources().getDrawable(R.drawable.ic_5c);
                    break;
                case 6:
                    icon = getResources().getDrawable(R.drawable.ic_6c);
                    break;
            }
            return icon;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mValue);
            dest.writeInt(valueX);
            dest.writeInt(valueY);
        }
        private void readFromParcel(Parcel in) {
            mValue = in.readInt();
            valueX = in.readInt();
            valueY = in.readInt();
        }
    }
}


