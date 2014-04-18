package com.erlantz.eightpuzzle.app.provider;

import android.net.Uri;

/**
 * Created by erlantz on 4/04/14.
 */
public final class PuzzleContract {
    public static final String AUTHORITY = "com.erlantz.eightpuzzle.app.provider.PuzzleContentProvider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/");

    // Table name
    public static final String PUZZLES_TABLE_NAME = "puzzles";

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PUZZLES_TABLE_NAME);

    // Columns
    public static final String _ID = "_id";
    public static final String PUZZLE_NAME = "name";
    public static final String PUZZLE_BITMAP_PATH = "bitmapPath";
    public static final String PUZZLE_THUMB_PATH = "thumbPath";
}
