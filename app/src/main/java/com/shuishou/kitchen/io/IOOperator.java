package com.shuishou.kitchen.io;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.shuishou.kitchen.InstantValue;
import com.shuishou.kitchen.ui.MainActivity;
import com.shuishou.kitchen.utils.CommonTool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 2017/6/8.
 */

public class IOOperator {

    public static void saveServerURL(String fileName, String url){
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName);
            writer.write(url);
            writer.close();
        } catch (IOException e) {
            Log.e("IOException", "error to save ServerURL +\n"+e.getStackTrace());
        } finally {
            try {
                if (writer != null)
                    writer.close();
            }catch (IOException e) {}
        }
    }
    public static String loadServerURL(String fileName){
        File file = new File(fileName);
        if (!file.exists())
            return InstantValue.NULLSTRING;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            return line;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException", "error to load ServerURL +\n"+e.getStackTrace());
        } finally {
            try {
                if (in != null)
                    in.close();
            }catch (IOException e) {}
        }
        return null;
    }

    @Nullable
    public static BitmapDrawable getDishImageDrawable(Resources res, String filename){
        File file = new File(filename);
        if (!file.exists())
            return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        try {
            InputStream in = new FileInputStream(file);
            int readLength = in.read(bytes);
            while(readLength != -1){
                outputStream.write(bytes, 0, readLength);
                readLength = in.read(bytes);
            }
            byte[] data = outputStream.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            BitmapDrawable d = new BitmapDrawable(res, bitmap);
            return d;
        } catch (IOException e) {
            Log.e("DishTabBuilder", "Failed to load dish image");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * delete all files under the catalog
     * @param directoryName
     */
    public static void deleteLocalFiles(String directoryName){
        File dir = new File(directoryName);
        if (dir.exists() && dir.isDirectory()){
            File[] files = dir.listFiles();
            if (files != null){
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 1. compare logfile's date, if too old delete it, then load all left log files and zip them
     * 2. use http protocol to upload
     * 3. load finish successfully, then delete this file.
     */
    public static void onUploadErrorLog(MainActivity mainActivity){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        File logdir = new File(InstantValue.ERRORLOGPATH);
        if (logdir.exists() && logdir.isDirectory()) {
            File[] files = logdir.listFiles();
            if (files != null && files.length > 0) {
                //delete the old log files, we just upload them in 30 days, the logfile's name has the format crash-2017-10-05-17-53-25-1507226005123
                Calendar c = Calendar.getInstance();
                for (File file : files) {
                    String filename = file.getName();
                    String[] times = filename.split("-");
                    if (times.length<3)
                        continue;//wrong log file, jump up to avoid exception here
                    c.set(Calendar.YEAR, Integer.parseInt(times[1]));
                    c.set(Calendar.MONTH, Integer.parseInt(times[2]));
                    c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(times[3]));
                    if ((new Date().getTime() - c.getTime().getTime()) / (24 * 60 * 60 * 1000) > 30) {
                        file.delete();
                    }
                }
            }
            files = logdir.listFiles();
            if (files != null && files.length > 0) {
                String zipfilename = InstantValue.ERRORLOGPATH + "/logs-" + format.format(new Date()) +"-"+System.currentTimeMillis()+ ".zip";
                //zip log files
                try {
                    BufferedInputStream origin = null;
                    FileOutputStream dest = new FileOutputStream(zipfilename);
                    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
                    byte data[] = new byte[2048];
                    // get a list of files from current directory
                    for (File file : files) {
                        FileInputStream fi = new FileInputStream(file);
                        origin = new BufferedInputStream(fi, 2048);
                        ZipEntry entry = new ZipEntry(file.getName());
                        out.putNextEntry(entry);
                        int count;
                        while((count = origin.read(data, 0, 2048)) != -1) {
                            out.write(data, 0, count);
                        }
                        origin.close();
                    }
                    out.close();
                    //delete log files
                    for(File file : files){
                        file.delete();
                    }

                } catch(Exception e) {
                    CrashHandler.getInstance().handleException(e, false);
                    CommonTool.popupWarnDialog(mainActivity, -1,"Error", "error while zip log files");
                    return;
                }
                File logzip = new File(zipfilename);
                //get MAC address as the unique id
                WifiManager wifiManager = (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();

                mainActivity.startProgressDialog("upload", "prepare to upload error log files");
                mainActivity.getHttpOperator().uploadErrorLog(logzip, macAddress);
            } else {
                CommonTool.popupToast(mainActivity,"There is no error log now.", Toast.LENGTH_LONG);
            }
        }
    }
}
