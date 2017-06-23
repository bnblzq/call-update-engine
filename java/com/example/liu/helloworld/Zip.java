package com.example.liu.helloworld;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.content.Context;
/**
 * Created by liu on 2017/6/20.
 */
public class Zip {

    private static final String TAG="Zip";
    private Context mContext;
    public Zip(Context con){this.mContext = con;}

    //decompress the file to /data/data/com.example.liu.helloworld/files
    public  void unZipToFolder( String zipFileString) throws Exception{
        ZipInputStream inZip = null;
        ZipEntry zipEntry    ;
        String szName        ;
        byte[] buffer = new byte[1024];
        File zipFile = new File(zipFileString);

     //   Log.d(TAG,"" + Environment.getDataDirectory());
     //   Log.d(TAG, "" + mContext.getFilesDir());
        /*if(  zipFile.isFile() && zipFile.delete() ){
            Log.d(TAG, zipFile +" exists before and be deleted");
        }*/


        inZip = new ZipInputStream(new FileInputStream(zipFile));


        while( ( zipEntry=inZip.getNextEntry()) != null ){

            //files we need reside on the toppest level
            if( zipEntry.getName().contains("/") ){
                Log.d(TAG,"found directory:" + zipEntry.getName() + "but ignore");

            }else{
                szName = zipEntry.getName();
                Log.d(TAG,szName);

                //it will overwrite old files
                FileOutputStream out = mContext.openFileOutput(szName, mContext.MODE_PRIVATE);
                int len;
                while( (len = inZip.read(buffer)) != -1){
                        out.write(buffer,0,len);
                        out.flush();
                    }
                out.close();
            }
        }
        buffer=null;

    }
   /* public static void main(String [] args){

        try {
            Zip.unZipToFolder("0614ota.zip",System.getProperty("user.dir"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
