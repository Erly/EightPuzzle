package com.erlantz.eightpuzzle.app;

import android.graphics.Bitmap;

/**
 * Created by erlantz on 4/04/14.
 */
public class Puzzle {
    String name;
    String path;
    String thumbPath;
    byte[] image;

    public Puzzle(String name, String path, String thumbPath) {
        this.name = name;
        this.path = path;
        this.thumbPath = thumbPath;
    }

    /*public Puzzle(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }*/

    public Puzzle(String name, byte[] image) {
        this.name = name;
        this.image = image;
    }
}
