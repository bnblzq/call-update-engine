package com.example.liu.helloworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by liu on 2017/6/20.
 */
public class FileRead {
    public FileRead(){ }

    //we need payload_properties.txt from zip
    public static void readProperty(String filePath, String [] output) throws Exception {
        File file = new File(filePath);
        if( !file.exists()){
            throw new FileNotFoundException(" not found"+ filePath);
        }

        FileReader fr = new FileReader(filePath);
        BufferedReader bf = new BufferedReader(fr);
        String line = null;
        int index =0;

        try {
            while( (line=bf.readLine()) !=null ){
                output[index] = line;
                if( (index++) >4 )
                    break;
            }
        } catch (IOException e) {
            bf.close();
            e.printStackTrace();
        }
        bf.close();
    }
}
