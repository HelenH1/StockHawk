package com.udacity.stockhawk.sync;

/**
 * Created by helenherring on 5/26/17.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.udacity.stockhawk.R;


public class InvalidStockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, R.string.invalid_stock_toast, Toast.LENGTH_LONG).show();
    }
}
