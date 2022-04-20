package com.example.csprecorder;

import android.content.Context;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.chaquo.python.PyObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.lang.*;


public class Writer{


    Context c;
    Object ene_20_all, ene_20_all_db, ene_50_all, ene_50_all_db, ene_20_aud, ene_20_aud_db, ene_50_aud, ene_50_aud_db, ene_all, ene_all_db, ene_aud_all, ene_aud_all_db;
    double latitude,longitude;
    String ts;
    Writer(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i, Object j, Object k, Object l, double lat, double log, String Timestamp, Context context)
    {
        ene_20_all=a;
        ene_20_all_db=b;
        ene_50_all=c;
        ene_50_all_db=d;
        ene_20_aud=e;
        ene_20_aud_db=f;
        ene_50_aud=g;
        ene_50_aud_db=h;
        ene_all=i;
        ene_all_db=j;
        ene_aud_all=k;
        ene_aud_all_db=l;

        //System.out.println("top 20 "+ ene_20_all);
        latitude=lat;
        longitude=log;
        ts=Timestamp;
        c=context;
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(new Runnable() {
            @Override
            public void run() {
                int flg=0;
                File exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CSVFile");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                File file = new File(exportDir,"file1" + ".csv");
                try {
                    if(!file.exists()) {
                        flg=1;
                        file.createNewFile();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                CSVwriter csvWrite=null;
                try {

                    csvWrite = new CSVwriter(new FileWriter(file, true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(flg==1)
                {
                    String arrStr[] = new String[15];
                    arrStr[0] ="Energy_20_all";
                    arrStr[1]="Energy_20_all_db";
                    arrStr[2] ="Energy_50_all";
                    arrStr[3]="Energy_50_all_db";
                    arrStr[4] ="Energy_20_aud";
                    arrStr[5]="Energy_20_aud_db";
                    arrStr[6] ="Energy_50_aud";
                    arrStr[7]="Energy_50_aud_db";
                    arrStr[8] ="Energy_all";
                    arrStr[9]="Energy_all_db";
                    arrStr[10]="Energy_aud_all";
                    arrStr[11]="Energy_aud_all_db";
                    arrStr[12]="Latitude";
                    arrStr[13]="Longitude";
                    arrStr[14]="TimeStamp";
                    csvWrite.writeNext(arrStr);
                }
                String arrStr[] = new String[15];
                arrStr[0] =""+ene_20_all;
                arrStr[1]=""+ene_20_all_db;
                arrStr[2]=""+ene_50_all;
                arrStr[3]=""+ene_50_all_db;
                arrStr[4]=""+ene_20_aud;
                arrStr[5]=""+ene_20_aud_db;
                arrStr[6]=""+ene_50_aud;
                arrStr[7]=""+ene_50_aud_db;
                arrStr[8]=""+ene_all;
                arrStr[9]=""+ene_all_db;
                arrStr[10]=""+ene_aud_all;
                arrStr[11]=""+ene_aud_all_db;
                arrStr[12]=""+latitude;
                arrStr[13]=""+longitude;
                arrStr[14]=""+ts;
                csvWrite.writeNext(arrStr);

                try {
                    csvWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*@Override
    protected Void doInBackground(Void... voids) {
        File exportDir = new File(c.getExternalFilesDir(""), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir,"file1" +location+ ".csv");
        try {
            if(!file.exists())
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CSVwriter csvWrite=null;
        try {

            csvWrite = new CSVwriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

            String arrStr[] = new String[5];
            arrStr[0] =""+l1.id;
            arrStr[1]=""+l1.logt;
            arrStr[2]=""+l1.latt;
            arrStr[3]=""+l1.time;
            arrStr[4]=""+l1.audio_file_name;
            csvWrite.writeNext(arrStr);

        try {
            csvWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}