package za.co.kierendavies.imageviewer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageResource(imageIds[imageId]);
        banner = (TextView) findViewById(R.id.banner);
        banner.setText(titles[imageId]);
    }

    public void navPrev(View view) {
        imageId--;
        if (imageId < 0) imageId += imageIds.length;  // java does modular arithmetic stupidly
        imageView.setImageResource(imageIds[imageId]);
        banner.setText(titles[imageId]);
    }

    public void navPlayPause(View view) {
        Button button = (Button) view;
        if (playing) {
            //pause
            playing = false;
            handler.removeCallbacks(runnableNext);
            button.setText(R.string.button_play);
        } else {
            //play
            playing = true;
            System.out.println("scheduling");
            handler.postDelayed(runnableNext, 0);  // let's not delay the first time
            System.out.println("modifying button");
            button.setText(R.string.button_pause);
        }
    }

    public void navNext(View view) {
        imageId = (imageId + 1) % imageIds.length;
        imageView.setImageResource(imageIds[imageId]);
        banner.setText(titles[imageId]);
    }
}
