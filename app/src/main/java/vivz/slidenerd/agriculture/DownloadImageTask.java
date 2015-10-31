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
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import vivz.slidenerd.agriculture.list.ListDetailActivity;

/**
 * Created by makejin on 2015-10-30.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    public static int width = 0;
    public static int height = 0;

    public DownloadImageTask(ImageView bmImage) {
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
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            //mIcon11 = BitmapFactory.decodeStream(in);
            Log.i("asd", "W : " + width + " H : " + height);
            return getRoundedCornerBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), width , height, false), 347);
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
    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}