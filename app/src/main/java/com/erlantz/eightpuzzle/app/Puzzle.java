package com.erlantz.eightpuzzle.app;

import android.graphics.Bitmap;

/**
 * Created by erlantz on 4/04/14.
 */
public class Puzzle {
    String name;
    String path;
    Bitmap image;

    public Puzzle(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
