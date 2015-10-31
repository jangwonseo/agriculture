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
    public static int width = 0;
    public static int height = 0;


    public DownloadImageTask_NoCircle(ImageView bmImage) {
        this.bmImage = bmImage;

        final ImageView tempThumb = bmImage;

        if(width==0 && height==0) {
            tempThumb.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("asd", "width : " + width + " height : " + height);
                    width = tempThumb.getWidth();
                    height = tempThumb.getHeight();
                }
            });
        }
       // Log.i("asd3201", "W : " + tempThumb.getWidth() + " H : " + tempThumb.getHeight());
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            Log.i("asd", "W : " + width + " H : " + height);
            return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), width , height, false);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return null;
        //return Bitmap.createScaledBitmap(mIcon11, 100, 100, false);
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}