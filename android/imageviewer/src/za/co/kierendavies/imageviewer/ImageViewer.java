package za.co.kierendavies.imageviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class ImageViewer extends Activity {
    public static final int delay = 5000; // milliseconds
    int[] imageIds = {
            R.drawable.ts,
            R.drawable.aj,
            R.drawable.fs,
            R.drawable.rd,
            R.drawable.pp,
            R.drawable.r,
    };  // TODO maybe search for image files automatically
    String[] titles = {
            "Twilight Sparkle",
            "Applejack",
            "Fluttershy",
            "Rainbow Dash",
            "Pinkie Pie",
            "Rarity"
    };  // TODO generate titles from file names
    int imageId = 0;
    ImageView imageView;
    TextView banner;
    boolean playing = false;

    Handler handler = new Handler();
    Runnable runnableNext = new Runnable() {
        @Override
        public void run() {
            navNext(null);
            handler.postDelayed(runnableNext, delay);
        }
    };

    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageResource(imageIds[imageId]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share).getActionProvider();
        mShareActionProvider.setShareIntent(getDefaultShareIntent());

        getActionBar().setTitle(titles[imageId]);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle fullscreen, rotate, etc.
        return false;
    }

    private Intent getDefaultShareIntent() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void navPrev(View view) {
        imageId--;
        if (imageId < 0) imageId += imageIds.length;  // java does modular arithmetic stupidly
        imageView.setImageResource(imageIds[imageId]);
        getActionBar().setTitle(titles[imageId]);
        if (playing) {
            // reset the timing
            handler.removeCallbacks(runnableNext);
            handler.postDelayed(runnableNext, delay);
        }
    }

    public void navPlayPause(View view) {
        ImageButton button = (ImageButton) view;
        if (playing) {
            //pause
            playing = false;
            handler.removeCallbacks(runnableNext);
            button.setImageResource(R.drawable.av_pause);
        } else {
            //play
            playing = true;
            System.out.println("scheduling");
            handler.postDelayed(runnableNext, delay);
            button.setImageResource(R.drawable.av_play);
        }
    }

    public void navNext(View view) {
        imageId = (imageId + 1) % imageIds.length;
        imageView.setImageResource(imageIds[imageId]);
        getActionBar().setTitle(titles[imageId]);
        if (playing) {
            // reset the timing
            handler.removeCallbacks(runnableNext);
            handler.postDelayed(runnableNext, delay);
        }
    }
}
