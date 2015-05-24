package com.intmainreturn0.myapplication;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.doomonafireball.betterpickers.timepicker.TimePickerBuilder;
import com.gc.materialdesign.widgets.Dialog;
import com.intmainreturn0.myapplication.util.LargeImageTileViewActivity;
import com.intmainreturn0.mylibrary.SlideCutListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements DatePickerDialogFragment.DatePickerDialogHandler {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public TimePickerBuilder tpb;
    public DatePickerBuilder dpb;
    public SlideCutListView lv;
    public ArrayList<Info> infos;
    public MyListAdapter ma;
    public AdapterView.OnItemClickListener icl;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type)
    {

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Ifttt");
        // Log.d("ifttt",mediaStorageDir.getAbsolutePath());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Ifttt", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "ifttt_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static Bitmap createImageThumbnail(String filePath){
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, 160*160);
        opts.inJustDecodeBounds = false;

        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        }catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
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

    public  void addToSql(String dat, String filenam)
    {
        Log.d("ifttt", dat + "   " + filenam);


        SQLiteDatabase db=openOrCreateDatabase("ifttt.db",MainActivity.MODE_MULTI_PROCESS,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS t (date text, filename text, msg text)");
        db.execSQL("INSERT INTO t (date,filename) VALUES (?,?)",new Object[]{dat,filenam});
        Cursor c=db.rawQuery("SELECT * FROM t", null);
        while (c.moveToNext())
        {
            Log.d("ifttt", c.getString(c.getColumnIndex("date")) + "   " + c.getString(c.getColumnIndex("filename")));
        }
        db.close();


    }

    @Override
    protected  void onStart()
    {
        super.onStart();
        Intent intentx=new Intent(this, MyIntentService.class);
        this.startService(intentx);
        alarmMgr = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(this, MyIntentService.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
        refreshInfos();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Calendar c= Calendar.getInstance();
        dpb= new DatePickerBuilder().setFragmentManager(getSupportFragmentManager()).setStyleResId(R.style.BetterPickersDialogFragment)
                .setYear(c.get(Calendar.YEAR))
                .setMonthOfYear(c.get(Calendar.MONTH));

        icl = new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view,int arg2, long arg3)
            {
                Intent i = new Intent(MainActivity.this, LargeImageTileViewActivity.class);
                i.putExtra("fn",( (Info)(adapterView.getItemAtPosition(arg2))).pat);
                startActivity(i);
            }
        };


        lv = (SlideCutListView)findViewById(R.id.listView) ;
        lv.setRemoveListener(new SlideCutListView.RemoveListener() {
            @Override
            public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
                Info i = (Info)lv.getItemAtPosition(position);
                SQLiteDatabase db=openOrCreateDatabase("ifttt.db",MainActivity.MODE_MULTI_PROCESS,null);
                db.execSQL("CREATE TABLE IF NOT EXISTS t (date text, filename text, msg text)");
                File f= new File(i.pat);
                if (f.exists())
                    f.delete();

                db.execSQL("DELETE from t where filename=?",new Object[]{i.pat});
                db.close();
                refreshInfos();

            }
        });
        infos = new ArrayList<Info>();
        ma = new MyListAdapter(this, infos);
        lv.setAdapter(ma);
        lv.setOnItemClickListener(icl);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder b= new AlertDialog.Builder(MainActivity.this);
                b.setTitle("更改简记");
                final EditText input = new EditText(MainActivity.this);
                input.setText(((Info) lv.getItemAtPosition(position)).msg);
                b.setView(input);
                b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = input.getText().toString();
                        SQLiteDatabase db = initSQL();
                        db.execSQL("UPDATE t SET msg=? WHERE filename=?", new Object[]{s, ((Info) lv.getItemAtPosition(position)).pat});
                        db.close();
                        refreshInfos();
                    }
                });
                b.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }

                });
                SQLiteDatabase db = initSQL();
                if( db.rawQuery("Select * from t where filename=?",new String[]{ ((Info) lv.getItemAtPosition(position)).pat }  ).getCount()>0)
                    b.show();

                db.close();

                return true;
            }
        });
                Toast.makeText(getApplicationContext(), "读取图片中...",
                        Toast.LENGTH_SHORT).show();
        refreshInfos();
        Toast.makeText(getApplicationContext(), "读取完成",
                Toast.LENGTH_SHORT).show();

    }

    public SQLiteDatabase initSQL()
    {
        SQLiteDatabase db=openOrCreateDatabase("ifttt.db", MainActivity.MODE_MULTI_PROCESS, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS t (date text, filename text, msg text)");
        return db;
    }

    public void refreshInfos()
    {
        Intent intentx=new Intent(this, MyIntentService.class);
        this.startService(intentx);
        alarmMgr = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(this, MyIntentService.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

        SQLiteDatabase db= initSQL();
        Cursor c=db.rawQuery("SELECT * FROM t ORDER BY date ", null);
        infos.clear();
        System.gc();
        System.runFinalization();
        BitmapFactory.Options opt = new BitmapFactory.Options();

        while (c.moveToNext())
        {
            File f=new File(c.getString(c.getColumnIndex("filename")));
            if (f.exists() && f.length()>50000)
            infos.add(
                    new Info(
                    createImageThumbnail(c.getString(c.getColumnIndex("filename"))),
                    f.getName()+"\n待删除时间："+c.getString(c.getColumnIndex("date")),
                            f.getPath(),c.getString(c.getColumnIndex("msg"))
            )
            );
        }
        db.close();
        ma.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void bClick(View view)
    {
        dpb.show();
    }

    @Override
    public  void onDialogDateSet(int ref, int year, int month, int day)
    {
        Dialog dialog = new Dialog(this, "date", String.valueOf(year * 10000 + month * 100 + day) );
        Date d= new Date(year-1900,month,day);
        btnClick(d);


    }


    public void btnClick(Date pendingd)
    {

        Intent intentx=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        if(intentx.resolveActivity(getPackageManager())!=null)
        {
            File f = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (f == null) return;
            intentx.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(intentx, 1);

            Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            addToSql(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(pendingd), f.getAbsolutePath());
            mediaScan.setData(Uri.fromFile(f));
            this.sendBroadcast(mediaScan);
            refreshInfos();
            Intent intentxx=new Intent(this, MyIntentService.class);
            this.startService(intentxx);
            alarmMgr = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            Intent intent=new Intent(this, MyIntentService.class);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);


        }
    }

}
