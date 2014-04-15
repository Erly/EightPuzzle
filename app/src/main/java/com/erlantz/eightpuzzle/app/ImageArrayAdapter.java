package com.erlantz.eightpuzzle.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
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

    private ArrayList<Puzzle> list = new ArrayList<Puzzle>();
    private static LayoutInflater inflater = null;
    private String mImageStoragePath;

    public ImageArrayAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        mContext = context;
        inflater = LayoutInflater.from(mContext);

        try {
            File imageStorageDir = new File(Environment.getDataDirectory().getCanonicalPath(), "Images");
            imageStorageDir.mkdirs();
            mImageStoragePath = imageStorageDir.getCanonicalPath();
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
                    list.add(new Puzzle(newCursor.getString(newCursor.getColumnIndex(PuzzleContract.PUZZLE_NAME)),
                            newCursor.getString(newCursor.getColumnIndex(PuzzleContract.PUZZLE_BITMAP_PATH))));
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
        if (storeBitmapToFile(listItem.image, filePath)) {
            listItem.path = filePath;
            list.add(listItem);

            ContentValues values = new ContentValues();
            values.put(PuzzleContract.PUZZLE_NAME, listItem.name);
            values.put(PuzzleContract.PUZZLE_BITMAP_PATH, listItem.path);
            mContext.getContentResolver().insert(PuzzleContract.CONTENT_URI, values);
        }
    }

    public void removeAll() {
        list.clear();

        mContext.getContentResolver().delete(PuzzleContract.CONTENT_URI, null, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.image.setImageBitmap(getBitmapFromFile(cursor.getString(cursor.getColumnIndex(PuzzleContract.PUZZLE_BITMAP_PATH))));
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

    private boolean storeBitmapToFile(Bitmap bitmap, String filePath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
    }
}