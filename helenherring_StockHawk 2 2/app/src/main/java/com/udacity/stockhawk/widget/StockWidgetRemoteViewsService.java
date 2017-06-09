package com.udacity.stockhawk.widget;

/**
 * Created by helenherring on 5/26/17.
 */

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.QuoteCols;
import com.udacity.stockhawk.data.StockProvider;
import com.udacity.stockhawk.ui.DetailActivity;


public class StockWidgetRemoteViewsService implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context mContext;
    int mWidgetId;

    public StockWidgetRemoteViewsService(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(
                StockProvider.Quotes.CONTENT_URI,
                new String[]{
                        QuoteCols._ID,
                        QuoteCols.SYMBOL,
                        QuoteCols.BIDPRICE,
                        QuoteCols.PERCENT_CHANGE,
                        QuoteCols.CHANGE,
                        QuoteCols.ISUP},
                QuoteCols.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        if (mCursor.moveToPosition(position)) {
            String symbol = mCursor.getString(mCursor.getColumnIndex(QuoteCols.SYMBOL));
            remoteViews.setTextViewText(R.id.symbol, symbol);
            remoteViews.setTextViewText(R.id.price, mCursor.getString(mCursor.getColumnIndex(QuoteCols.BIDPRICE)));
            remoteViews.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteCols.CHANGE)));

            if (mCursor.getInt(mCursor.getColumnIndex("is_up")) == 1) {
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }

            Intent intent = new Intent();
            intent.putExtra(DetailActivity.ARG_STOCK_SYMBOL, symbol);
            intent.putExtra(DetailActivity.ARG_PARENT, DetailActivity.ARG_PARENT_WIDGET_VALUE);
            remoteViews.setOnClickFillInIntent(R.id.indiv_stock, intent);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
