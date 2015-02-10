package com.wifipidgin.ui;

import android.util.Log;
import android.view.View;

/**
 * Created by fire on 2/2/15.
 */
public class FunctionSelectResources {

    private static final String TAG = "FunctionSelectResources";

    public static View.OnClickListener getOnClickTab(int tabidx) {
        switch (tabidx) {
            case 0:
                Log.d(TAG, "Selected Tab: " + 0);
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Selected Tab: " + 0);
                    }
                };
            case 1:
                Log.d(TAG, "Selected Tab: " + 1);
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Selected Tab: " + 1);
                    }
                };
            case 2:
                Log.d(TAG, "Selected Tab: " + 2);
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Selected Tab: " + 2);
                    }
                };
            default:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Selected Unsupported Tab: ");
                    }
                };
        }
    }
}
