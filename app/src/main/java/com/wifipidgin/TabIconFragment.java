package com.wifipidgin;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;



public final class TabIconFragment extends ListFragment {

    private static final String TAG = "TabIconFragment";

    private static final String KEY_CONTENT_String = "TabIconFragment:Content_String";

    private static final String KEY_CONTENT_Int = "TabIconFragment:Content_Int";

    String[] values = new String[] { "Sue", "FiFi", "DQ",
            "Xiao Pang", "Lao8"};

    String[] values2 = new String[] { "Sue", "FiFi" };


    String[] values3 = new String[] {  "DQ", "Xiao Pang", "Lao8" };

    private View.OnClickListener mTitleOnClickListener;
    private int mTabIdx = -1;
    private String mFunctionTabName = "";

    public static TabIconFragment newInstance(String functionTabName,
                                              int tab_idx,
                                              View.OnClickListener onClick) {
        TabIconFragment fragment = new TabIconFragment();

        /*
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(content).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        */

        fragment.mFunctionTabName = functionTabName;
        Log.d(TAG, "mFunctionTabName: " + fragment.mFunctionTabName);

        fragment.mTabIdx = tab_idx;
        fragment.mTitleOnClickListener = onClick;

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null)){

            if(savedInstanceState.containsKey(KEY_CONTENT_String)) {
                mTabIdx = savedInstanceState.getInt(KEY_CONTENT_Int);
                mTitleOnClickListener = FunctionSelectResources.getOnClickTab(mTabIdx);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.contact_list, container, false);
        switch (mTabIdx) {
            case 0:
                setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values));
                break;
            case 1:
                setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values2));
                break;
            case 2:
                setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values3));
                break;
            default:
                setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values));

        }

        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT_String, mFunctionTabName);
        outState.putInt(KEY_CONTENT_Int, mTabIdx);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "Clicked User: " + getListAdapter().getItem(position).toString());

    }
}
