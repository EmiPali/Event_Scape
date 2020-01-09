package com.project.emi.eventscape.util;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {
    //storage/emulated/0
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";


    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray= new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();

        for (int i = 0 ; i < listfiles.length ; i++){
            if (listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }return pathArray;

    }

    //search a directory and return a list of all files contained inside

    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray= new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();

        for (int i = 0 ; i < listfiles.length ; i++) {
            if (listfiles[i].isFile()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }return pathArray;
    }
}
