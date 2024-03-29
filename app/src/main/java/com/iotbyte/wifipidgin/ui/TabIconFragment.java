package com.iotbyte.wifipidgin.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.iotbyte.wifipidgin.R;
import com.iotbyte.wifipidgin.ui.discoverlist.DiscoverListManager;
import com.iotbyte.wifipidgin.ui.friendlist.FriendListManager;

import java.util.ArrayList;


public final class TabIconFragment extends ListFragment {

    private static final String TAG = "TabIconFragment";

    private static final String KEY_CONTENT_String = "TabIconFragment:Content_String";

    private static final String KEY_CONTENT_Int = "TabIconFragment:Content_Int";

    private FriendListManager fm;

    private DiscoverListManager dm;


    ArrayList<String> values = new ArrayList<String>() {{
        add("Sue");
        add("FiFi");
        add("DQ");
    }};
    ArrayList<String> values2 = new ArrayList<String>() {{
        add("Sue");
        add("FiFi");
        add("DQ");
        add("PangPang");
    }};
    ArrayList<String> values3 = new ArrayList<String>() {{
        add("Sue");
        add("FiFi");
        add("DQ");
        add("Hao");
    }};


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

        //View rootView = inflater.inflate(R.layout.list_friends, container, false);

        switch (mTabIdx) {
            //Channel Tab
            case 0:
                setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_friends, R.id.friendName, values));
                break;
            //Contact Tab
            case 1:
                fm = new FriendListManager(getActivity(), this);
                fm.InflateSettingView();
                break;
            //Discover Tab
            case 2:
                dm = new DiscoverListManager(getActivity(), this);
                dm.InflateSettingView();
                break;
            //Setting Tab
            //case 3:
            //    sm = new SettingsManager(getActivity(), this);
            //    sm.InflateSettingView();
            //    break;
            default:
                setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_friends, R.id.friendName, values));

        }

        return super.onCreateView(inflater, container, savedInstanceState);
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

        switch (mTabIdx) {
            //Channel Tab
            case 0:

                break;
            //Contact Tab
            case 1:

                break;
            //Discover Tab
            case 2:

                break;
            //Setting Tab
            case 3:

                break;
            default:

        }



        //ArrayAdapter<String> adapter=(ArrayAdapter<String>)getListAdapter();
        //adapter.insert("NEW", adapter.getCount());

        //adapter.notifyDataSetChanged();
    }
}
