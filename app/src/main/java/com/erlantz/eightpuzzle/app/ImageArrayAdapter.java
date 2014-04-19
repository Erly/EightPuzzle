package com.erlantz.eightpuzzle.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erlantz.eightpuzzle.app.provider.PuzzleContract;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by erlantz on 13/03/14.
 */
public class ImageArrayAdapter extends CursorAdapter {

    private static final String APP_DIR = "EightPuzzle/Puzzles";
    private ArrayList<Puzzle> list = new ArrayList<Puzzle>();
    private static LayoutInflater inflater = null;
    private String mImageStoragePath;

    public ImageArrayAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        mContext = context;
        inflater = LayoutInflater.from(mContext);

        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String root = mContext.getExternalFilesDir(null).getCanonicalPath();
                if (null != root) {
                    File imageStorageDir = new File(root, APP_DIR);
                    imageStorageDir.mkdirs();
                    mImageStoragePath = imageStorageDir.getCanonicalPath();
                }
            }
            /*File imageStorageDir = new File(Environment.getDataDirectory().getCanonicalPath(), );
            imageStorageDir.mkdirs();
            mImageStoragePath = imageStorageDir.getCanonicalPath();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);

        if (null != newCursor) {
            list.clear();
            if (newCursor.moveToFirst()) {
                do {
                    list.add(new Puzzle(newCursor.getInt(newCursor.getColumnIndex(PuzzleContract._ID)),
                            newCursor.getString(newCursor.getColumnIndex(PuzzleContract.PUZZLE_NAME)),
                            newCursor.getString(newCursor.getColumnIndex(PuzzleContract.PUZZLE_BITMAP_PATH)),
                            newCursor.getString(newCursor.getColumnIndex(PuzzleContract.PUZZLE_THUMB_PATH))));
                } while (newCursor.moveToNext());
            }
        }
        return newCursor;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Puzzle listItem) {
        String filePath = mImageStoragePath + "/" + listItem.name;
        Log.i("Adapter", filePath);

        try {
            while (!(new File(filePath).createNewFile())) filePath += "_2";
            if (storeBitmapToFile(listItem.image, filePath)) {
                listItem.path = filePath;
                listItem.thumbPath = filePath + "_thumb";
                list.add(listItem);

                insert(listItem);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rename(int position, String newName) {
        Puzzle p = list.get(position);
        File path = new File(p.path);
        File thumbPath = new File(p.path);
        String filePath = mImageStoragePath + "/" + newName;
        File newPath = new File(filePath);
        while(newPath.exists()) {
            filePath += "_2";
            newPath = new File(filePath);
        }
        File newThumbPath = new File(filePath + "_thumb");
        path.renameTo(newPath);
        p.name = newName;
        p.path = filePath;
        p.thumbPath = filePath + "_thumb";

        update(p);
    }

    public void remove(int position) {
        Puzzle p = list.get(position);
        new File(p.path).delete();
        new File(p.thumbPath).delete();

        String selectionArgs[] = { String.valueOf(p.getId()) };
        mContext.getContentResolver().delete(PuzzleContract.CONTENT_URI, PuzzleContract._ID + "=?", selectionArgs);
    }

    public void removeAll() {
        for(Puzzle p : list) {
            new File(p.path).delete();
            new File(p.thumbPath).delete();
        }
        list.clear();

        mContext.getContentResolver().delete(PuzzleContract.CONTENT_URI, null, null);
    }

    /**
     * Inserts a puzzle in the DB.
     * @param puzzle The new puzzle to be inserted.
     */
    private void insert(Puzzle puzzle) {
        ContentValues values = new ContentValues();
        values.put(PuzzleContract.PUZZLE_NAME, puzzle.name);
        values.put(PuzzleContract.PUZZLE_BITMAP_PATH, puzzle.path);
        values.put(PuzzleContract.PUZZLE_THUMB_PATH, puzzle.thumbPath);
        mContext.getContentResolver().insert(PuzzleContract.CONTENT_URI, values);
    }

    /**
     * Updates a puzzle data in the DB.
     * @param updatedPuzzle The puzzle with updated attributes.
     */
    private void update(Puzzle updatedPuzzle) {
        String selectionArgs[] = { String.valueOf(updatedPuzzle.getId()) };

        ContentValues values = new ContentValues();
        values.put(PuzzleContract.PUZZLE_NAME, updatedPuzzle.name);
        values.put(PuzzleContract.PUZZLE_BITMAP_PATH, updatedPuzzle.path);
        values.put(PuzzleContract.PUZZLE_THUMB_PATH, updatedPuzzle.thumbPath);
        mContext.getContentResolver().update(PuzzleContract.CONTENT_URI, values,  PuzzleContract._ID + "=?", selectionArgs);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(PuzzleContract.PUZZLE_THUMB_PATH))));
        //holder.image.setImageBitmap(getBitmapFromFile(cursor.getString(cursor.getColumnIndex(PuzzleContract.PUZZLE_BITMAP_PATH))));
        holder.name.setText(cursor.getString(cursor.getColumnIndex(PuzzleContract.PUZZLE_NAME)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView;
        ViewHolder holder = new ViewHolder();

        newView = inflater.inflate(R.layout.image_row, null);
        holder.image = (ImageView) newView.findViewById(R.id.imagen);
        holder.name = (TextView) newView.findViewById(R.id.tituloImagen);

        newView.setTag(holder);
        return newView;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = inflater.inflate(R.layout.image_row, null);

        ImageView imagen = (ImageView) convertView.findViewById(R.id.imagen);
        TextView name = (TextView) convertView.findViewById(R.id.tituloImagen);

        imagen.setImageBitmap(images[position].);
        return null;
    }*/

    private Bitmap getBitmapFromFile(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    private boolean storeBitmapToFile(byte[] image, String filePath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            out.write(image);
            Bitmap thumbnail = null;
            Bitmap bitmap = getBitmapFromFile(filePath);
            float ratio = (float)bitmap.getWidth() / (float)bitmap.getHeight();
            Log.i("Adapter", "Width/Height ratio: " + ratio);
            if (ratio > 1)
                thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 400, (int)(400/ratio));
            else
                thumbnail = ThumbnailUtils.extractThumbnail(bitmap, (int)(400*ratio), 400);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath + "_thumb"));
            thumbnail.compress(Bitmap.CompressFormat.PNG, 70, bos);
            bos.flush();
            bos.close();
            Log.i("Adapter", "File succesfully saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        return true;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
    }
}