package com.example.liu.helloworld;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by liu on 2017/6/20.
 */
public class Zip {

    private static final String TAG="Zip";

    public Zip(){}
    public static void unZipToFolder( String zipFileString, String outPathString) throws Exception{
        ZipInputStream inZip = null;
        ZipEntry zipEntry    ;
        String szName        ;
        byte[] buffer = new byte[1024];

        try {
            inZip = new ZipInputStream(new FileInputStream(zipFileString));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while( ( zipEntry=inZip.getNextEntry()) != null ){

            //files we need rely on the toppest level
            if( zipEntry.getName().contains("/") ){
                Log.d(TAG,"found directory:" + zipEntry.getName() + "but ignore");
                //System.out.println("occur directory :"+ zipEntry.getName());

            }else{
                szName = zipEntry.getName();
                System.out.println(szName);

                File file = new File(outPathString + File.separator + szName);

                if( file.exists() && file.delete()){
                    Log.d(TAG,"file " + file.getName() + " exists and be deleted");
                }

                if( !file.createNewFile()){
                    Log.d(TAG,"create" + file.getName() + "fail");
                    throw  new IOException();
                }

                FileOutputStream out = new FileOutputStream(file);
                int len;
                try {
                    while( (len = inZip.read(buffer)) != -1){
                        out.write(buffer,0,len);
                        out.flush();
                    }
                } catch (IOException e) {
                    out.close();
                    buffer = null;
                    e.printStackTrace();
                }
                out.close();
            }
        }
        buffer = null;
    }
   /* public static void main(String [] args){

        try {
            Zip.unZipToFolder("0614ota.zip",System.getProperty("user.dir"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
