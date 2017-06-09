package com.udacity.stockhawk.widget;

/**
 * Created by helenherring on 5/26/17.
 */


import android.content.Intent;
import android.widget.RemoteViewsService;

public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetRemoteViewsService(getApplicationContext(), intent);
    }
}