package za.co.kierendavies.imageviewer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.*;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

public class ImageViewer extends Activity {
    public static final int delay = 2000; // slideshow delay, milliseconds
    ImageView imageView;
    TextView titleView;
    ImageView buttonPlayPause;
    Cursor imageCursor;
    boolean isPlaying;

    // handler for automatic slideshow
    Handler handler = new Handler();
    Runnable runnableNext = new Runnable() {
        @Override
        public void run() {
            // move to next image, or first if it fails
            if (!imageCursor.moveToNext()) {
                imageCursor.moveToFirst();
            }
            updateImage();
            handler.postDelayed(runnableNext, delay);
        }
    };

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // get orientation and set correct view
        int orientation = getWindowManager().getDefaultDisplay().getRotation();
        if (orientation == Surface.ROTATION_90) {
            setContentView(R.layout.landscape_left);
        } else if (orientation == Surface.ROTATION_270) {
            setContentView(R.layout.landscape_right);
        } else {
            setContentView(R.layout.portrait);
        }

        imageView = (ImageView) findViewById(R.id.image);
        titleView = (TextView) findViewById(R.id.title);
        buttonPlayPause = (ImageView) findViewById(R.id.button_play_pause);

        isPlaying = false;

        // query for image files on external media
        imageCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // URI
                new String[] {                                 // these fields
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME},
                null,                                          // all entries
                null,                                          // no filter
                MediaStore.Images.Media._ID                    // sort by ID
        );

        imageCursor.moveToFirst();
        onRestoreInstanceState(state);
        // TODO handle no images on device
        updateImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        // save current image
        int colId = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
        int id = imageCursor.getInt(colId);
        state.putInt("currentImageId", id);
        // save isPlaying
        state.putBoolean("isPlaying", isPlaying);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        if (state != null) {
            // search for last viewed image
            int colId = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
            int targetId = state.getInt("currentImageId");
            while (imageCursor.getInt(colId) != targetId) {
                // increment, or reset and break if it fails
                if (!imageCursor.moveToNext()) {
                    imageCursor.moveToFirst();
                    break;
                }
            }
            // resume playing if necessary
            if (state.getBoolean("isPlaying")) {
                play();
            } else {
                pause();
            }
        }
    }

    protected void updateImage() {
        // find and set title
        int colTitle = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
        getActionBar().setTitle(imageCursor.getString(colTitle));

        // find and set image
        int colPath = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        String path = imageCursor.getString(colPath);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
    }

    protected void play() {
        if (isPlaying) return;  // break if already playing
        isPlaying = true;
        handler.postDelayed(runnableNext, delay);
        buttonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
    }

    protected void pause() {
        if (!isPlaying) return;  // break if already paused
        isPlaying = false;
        handler.removeCallbacks(runnableNext);
        buttonPlayPause.setImageResource(android.R.drawable.ic_media_play);
    }

    // button handler
    public void navPrev(View view) {
        // move to previous image, or last if it fails
        if (!imageCursor.moveToPrevious()) {
            imageCursor.moveToLast();
        }
        updateImage();
        pause();
    }

    // button handler
    public void navPlayPause(View view) {
        if (isPlaying) {
            pause();
        } else {
            play();
        }
    }

    // button handler
    public void navNext(View view) {
        // move to next image, or first if it fails
        if (!imageCursor.moveToNext()) {
            imageCursor.moveToFirst();
        }
        updateImage();
        pause();
    }
}
