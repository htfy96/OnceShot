package com.intmainreturn0.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import java.util.ArrayList;
import android.media.tv.TvInputService;
import android.os.Looper;
import android.preference.DialogPreference;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.gc.materialdesign.views.ProgressBarIndeterminateDeterminate;
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
import android.support.v7.widget.ShareActionProvider;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.ocr.HciCloudOcr;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.ocr.OcrInitParam;
import com.sinovoice.hcicloudsdk.common.ocr.OcrRecogRegion;
import com.sinovoice.hcicloudsdk.common.ocr.OcrRecogResult;

public class LargeImageTileViewActivity extends ActionBarActivity  {
    public ProgressBarDeterminate progressBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_large_image_tile_view, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        provider.setShareIntent(getDefaultIntent());

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_ocr:
            {
                ocrFile();
                return true;
            }
            case R.id.action_rotate:
            {
                mImage = (ImageViewTouch)findViewById(R.id.image);
                bm = rotateBitmap(90,bm);
                mImage.setImageBitmap(bm);
                mImage.resetDisplay();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final String TESSBASE_PATH = "/storage/extSdCard/";
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String CHINESE_LANGUAGE = "chi_sim";

    public void ocr()
    {
        final Intent i = getIntent();

        Toast.makeText(this, "识别中", Toast.LENGTH_SHORT).show();

/*
        new Thread(){
            public void run(){

                baseAPI.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
                baseAPI.setImage(new File(i.getStringExtra("fn")));
                final String outputText;

                outputText = baseAPI.getUTF8Text();
                        Log.d("ifttt", outputText);


            }
        }.start();*/

        InitParam initparam = new InitParam();
// 授权文件所在路径，此项必填
        String authDirPath = this.getFilesDir().getAbsolutePath();
        initparam.addParam(InitParam.PARAM_KEY_AUTH_PATH, authDirPath);
// 是否自动访问云授权
        initparam.addParam(InitParam.PARAM_KEY_AUTO_CLOUD_AUTH, "yes");
// 灵云云服务的接口地址，此项必填
        initparam.addParam(InitParam.PARAM_KEY_CLOUD_URL, " \ttest.api.hcicloud.com:8888");
// 开发者密钥，此项必填，由捷通华声提供
        initparam.addParam(InitParam.PARAM_KEY_DEVELOPER_KEY, "1eebf7c66b21e6a4cdd9b713668e5ab6");
// 应用程序序号，此项必填，由捷通华声提供
        initparam.addParam(InitParam.PARAM_KEY_APP_KEY, "d85d541f");

        int errCode = HciCloudSys.hciInit(initparam.getStringConfig(), this);
        if(errCode != HciErrorCode.HCI_ERR_NONE) {
            // "系统初始化失败"
            return;
        }
        errCode = HciCloudSys.hciCheckAuth();

        Log.d("ifttt", String.valueOf(errCode));
        OcrInitParam ocrInitParam = new OcrInitParam();
        String initParam = ocrInitParam.getStringConfig();
        errCode = HciCloudOcr.hciOcrInit(initParam);

        Log.d("ifttt", String.valueOf(errCode));

        Session mSessionId = new Session();
        String sSessionConfig="capKey=ocr.cloud.chinese";
        errCode = HciCloudOcr.hciOcrSessionStart(sSessionConfig, mSessionId);
        Log.d("ifttt", String.valueOf(errCode));
        errCode = HciCloudOcr.hciOcrSetImageByAndroidBitmap(mSessionId, bm);
        Log.d("ifttt", String.valueOf(errCode));
        String sRecogCfg = "autoDeskew=yes";
        ArrayList<OcrRecogRegion> recogRegion = null;
// 识别
        OcrRecogResult recogResult = new OcrRecogResult();
        int nRet = HciCloudOcr.hciOcrRecog(mSessionId, sRecogCfg, recogRegion, recogResult);

        Log.d("ifttt", String.valueOf(nRet));
        errCode = HciCloudOcr.hciOcrSessionStop(mSessionId);
        Log.d("ifttt", String.valueOf(errCode));
        errCode = HciCloudOcr.hciOcrRelease();
        Log.d("ifttt", String.valueOf(errCode));
        errCode = HciCloudSys.hciRelease();
        dialog(recogResult.getResultText());



    }

    public void ocrFile()
    {
        android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(this);
        b.setTitle("OCR");
        final TextView tv = new TextView(this);
        tv.setText("真的要开始识别图像中文字吗？\n请保持联网，大约需要半分钟左右的时间");
        b.setView(tv);
        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ocr();

            }
        });

        b.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        b.show();
    }

    protected void dialog(String s) {

       android.support.v7.app.AlertDialog.Builder b= new android.support.v7.app.AlertDialog.Builder(this);
        b.setTitle("结果");
        final EditText input = new EditText(this);
        input.setText(s);
        input.setSelectAllOnFocus(true);
        b.setView(input);
        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        b.show();
    }


    private Intent getDefaultIntent() {
        Intent i = getIntent();
        String s = i.getStringExtra("fn");
        Intent intent = new Intent(Intent.ACTION_SEND);
        File f=new File(s);

        intent.setType("image/jpeg");
        Log.d("ifttt", s);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        intent.putExtra(Intent.EXTRA_TITLE, i.getStringExtra("description"));
        intent.putExtra(Intent.EXTRA_SUBJECT, i.getStringExtra("description"));
        intent.putExtra(Intent.EXTRA_TEXT, i.getStringExtra("description"));
        return intent;
}

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

        setContentView(R.layout.lflayout);



    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static Bitmap createImageThumbnail(String filePath){
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, 1680*1024);
        opts.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        }catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);


        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return bitmap;
    }
    private Bitmap bm;
    @Override
    public void onStart()
    {
        super.onStart();
        mImage = (ImageViewTouch)findViewById(R.id.image);

        Intent i = getIntent();
        String s = i.getStringExtra("fn");
        if (i == null) return;

        if (bm != null && !bm.isRecycled()) bm.recycle();
        System.gc();
        bm = createImageThumbnail(s);

        Log.d("ifttt", s);
        mImage.setDisplayType(DisplayType.FIT_TO_SCREEN);
        mImage.setScrollEnabled(true);
        mImage.setQuickScaleEnabled(true);
        if (bm != null)
        mImage.setImageBitmap(rotateBitmap( readPicDegree(s), bm));
        mImage.setScaleEnabled(true);
        mImage.setDoubleTapEnabled(true);
        System.gc();

    }
}