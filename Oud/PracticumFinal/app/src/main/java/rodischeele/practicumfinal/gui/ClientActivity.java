package rodischeele.practicumfinal.gui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gj_webdev.communicatie.R;
import com.gj_webdev.communicatie.practicum_final.enities.Globals;
import com.gj_webdev.communicatie.practicum_final.logic.ClientManager;
import com.gj_webdev.communicatie.practicum_final.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ClientActivity extends Activity {

    private String ip;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("description"));
        ip = getIntent().getStringExtra("ip");

        setContentView(R.layout.activity_client);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.canvas_container);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.back_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientActivity.this.finish();
            }
        });

        //Connect and request setup
        new RTSPTask(){
            @Override
            protected void onCancelled(){
                if(this.error != null) {
                    if(this.error instanceof IllegalStateException) {
                        Toast.makeText(ClientActivity.this, this.error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ClientActivity.this, this.error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                ClientActivity.this.finish();
            }
        }.execute(Globals.REQUEST_CONNECT, Globals.REQUEST_SETUP);
        Globals.CLIENT_MANAGER.setFrameReceiveListener(new ClientManager.FrameReceiveListener() {
            @Override
            public void onReceive(final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap rotatedBitmap = rotateBitmap(bitmap, 90);
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageBitmap(rotatedBitmap);
                    }
                });
            }
        });
//        Globals.CLIENT_MANAGER.setSocketCloseListener(new ClientManager.SocketCloseListener() {
//            @Override
//            public void onClose() {
//                ClientActivity.this.finish();
//            }
//        });
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onResume(){
        super.onResume();
        //Request play
        new RTSPTask(){
            @Override
            protected void onCancelled(){
                if(this.error != null) {
                    if(this.error instanceof IllegalStateException) {
                        Toast.makeText(ClientActivity.this, this.error.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ClientActivity.this, this.error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }.execute(Globals.REQUEST_PLAY);
    }

    @Override
    public void onPause(){
        super.onPause();
        //Request pause
        new RTSPTask().execute(Globals.REQUEST_PAUSE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //Request teardown
        new RTSPTask().execute(Globals.REQUEST_TEARDOWN);
        Globals.CLIENT_MANAGER.removeFrameReceiveListener();
//        Globals.CLIENT_MANAGER.removeSocketCloseListener();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class RTSPTask extends AsyncTask<String, Integer, String> {

        public Throwable error;

        @Override
        protected String doInBackground(String... params) {
            try {
                for (String param : params) {
                    switch(param){
                        case Globals.REQUEST_CONNECT:
                            Globals.CLIENT_MANAGER.connect(ip);
                            break;
                        case Globals.REQUEST_SETUP:
                            Globals.CLIENT_MANAGER.setup();
                            break;
                        case Globals.REQUEST_TEARDOWN:
                            Globals.CLIENT_MANAGER.tearDown();
                            break;
                        case Globals.REQUEST_PAUSE:
                            Globals.CLIENT_MANAGER.pause();
                            break;
                        case Globals.REQUEST_PLAY:
                            Globals.CLIENT_MANAGER.play();
                            break;
                    }
                }
            }catch(Throwable e){
                e.printStackTrace();
                error = e;
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String e){

        }
    }
}
