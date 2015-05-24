package com.intmainreturn0.myapplication.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.intmainreturn0.myapplication.R;
import com.qozix.tileview.TileView;

import android.os.Bundle;
import android.view.Window;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchDoubleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.OnDrawableChangeListener;


import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.IOException;


public class LargeImageTileViewActivity extends Activity {

    ImageViewTouch mImage;
    static int displayTypeCount = 0;

    public static int readPicDegree(String path) {
        int degree = 0;

        // 读取图片文件信息的类ExifInterface
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }

        return degree;
    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lflayout);





    }

    public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() *5/6,
                bitmap.getHeight()*5/6, matrix, true);
        return bm;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mImage = (ImageViewTouch)findViewById(R.id.image);

        Intent i = getIntent();
        String s = i.getStringExtra("fn");
        System.gc();
        Bitmap bm = BitmapFactory.decodeFile(s);
        Log.d("ifttt", s);
        mImage.setDisplayType(DisplayType.FIT_TO_SCREEN);
        mImage.setScrollEnabled(true);
        mImage.setQuickScaleEnabled(true);
        if (bm != null)
        mImage.setImageBitmap(rotateBitmap( readPicDegree(s), bm));

    }
}