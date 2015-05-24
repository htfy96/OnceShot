package com.intmainreturn0.myapplication;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    public void scanAndDelete()
    {
        SQLiteDatabase db=openOrCreateDatabase("ifttt.db", MODE_MULTI_PROCESS, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS t (date text, filename text, msg text)");
        Cursor c=db.rawQuery("SELECT * FROM t where date<?", new String[]{
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())} );
        Log.d("ifttt", "{Started!}");
        Log.d("ifttt", c.getCount()+"" );
        while (c.moveToNext())
        {
            String fn=c.getString(  c.getColumnIndex("filename"));
            File f=new File(fn);
            if (f.exists())
                f.delete();


            Log.d("ifttt", fn);
        }

        db.execSQL("delete from t where date<?", new String[]{
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())});
        c.close();
        db.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            scanAndDelete();

        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        Log.i("ifttt",this+" is destroy");
    }


}


