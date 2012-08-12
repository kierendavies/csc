package za.co.kierendavies.imageviewer;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ImageViewer extends Activity
{
    public static final String logtag = "ImageViewer";
    Drawable[] images;
    ImageView imageView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.v(logtag, "blarg!");
        this.imageView = (ImageView) findViewById(R.id.image);
        this.images = new Drawable[]{};
    }

    public void navPrev(View view) {
        imageView.setImageResource(R.color.red);
    }
    public void navPlay(View view) {
        imageView.setImageResource(R.color.green);
    }
    public void navPause(View view) {
        imageView.setImageResource(R.color.blue);
    }
    public void navNext(View view) {
        imageView.setImageResource(R.color.white);
    }
}
