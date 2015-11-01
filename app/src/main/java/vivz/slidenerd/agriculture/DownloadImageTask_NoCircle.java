package vivz.slidenerd.agriculture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import vivz.slidenerd.agriculture.list.ListDetailActivity;

/**
 * Created by makejin on 2015-10-30.
 */
public class DownloadImageTask_NoCircle extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask_NoCircle(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), 960 , 460, false);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}