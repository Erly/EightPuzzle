package com.erlantz.eightpuzzle.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

/**
 * Created by erlantz on 13/03/14.
 */
public class SelectorFragment extends ListFragment {

    public interface  SelectionListener {
        public void onItemSelected(int position);
    }

    private SelectionListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(android.R.layout.activity_list_item);
    }
}
