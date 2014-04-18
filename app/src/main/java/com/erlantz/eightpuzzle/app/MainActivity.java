package com.erlantz.eightpuzzle.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.ListFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.erlantz.eightpuzzle.app.provider.PuzzleContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_delete_all) {
            ((PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.container)).cursorAdapter.removeAll();
        }
        return super.onOptionsItemSelected(item);
    }

    /*private boolean isInTwoPaneMode() {
        return findViewById(R.id.fragment_game) == null;
    }*/


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements
            android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

        private static final int GET_IMAGE_CODE = 1;

        private ListView imagesList;
        ImageArrayAdapter cursorAdapter;
        LayoutInflater inflater;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            this.inflater = inflater;
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            imagesList = (ListView)rootView.findViewById(R.id.imageList);
            cursorAdapter = new ImageArrayAdapter(getActivity(), null, 0);
            imagesList.setAdapter(cursorAdapter);
            registerForContextMenu(imagesList);

            getLoaderManager().initLoader(0, null, this);

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            CursorLoader cursorLoader = new CursorLoader(getActivity(), PuzzleContract.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> newLoader, Cursor newCursor) {
            cursorAdapter.swapCursor(newCursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> newLoader) {
            cursorAdapter.swapCursor(null);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            getActivity().getMenuInflater().inflate(R.menu.puzzle, menu);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_rename) {
                return true;
            }
            if (id == R.id.action_delete) {
                return true;
            }
            return super.onContextItemSelected(item);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == GET_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
                try {
                    InputStream stream = getActivity().getContentResolver().openInputStream(data.getData());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] imageData = new byte[16384];

                    while ((nRead = stream.read(imageData, 0, imageData.length)) != -1) {
                        buffer.write(imageData, 0, nRead);
                    }

                    buffer.flush();

                    //TODO: Check if the default name is used
                    String name = "Puzzle_" + (imagesList.getCount() + 1);
                    Puzzle puzzle = new Puzzle(name, buffer.toByteArray());
                    stream.close();
                    cursorAdapter.add(puzzle);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.selector, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_load) {
                loadImage();
                return true;
            }
            if (id == R.id.action_delete_all) {
                cursorAdapter.removeAll();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void loadImage() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, GET_IMAGE_CODE);
        }
    }
}
