package com.udacity.stockhawk.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.udacity.stockhawk.R;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.net.URL;

import com.udacity.stockhawk.data.HistQuoteCols;
import com.udacity.stockhawk.data.StockProvider;
import com.udacity.stockhawk.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * Created by helenherring on 2/17/17.
 */

public class DetailActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private Context mContext;
    private String stockSymbol;
    private LineChartView lineChartView;

    private LineChartView mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private TextView tv_graph_begin, tv_graph_end, tv_graph_evolution, tv_graph_max, tv_graph_min;

    private static final int CURSOR_LOADER_ID = 0;

    public static final String ARG_STOCK_SYMBOL = "stock_symbol";
    public static final String ARG_PARENT = "parent";
    public static final String ARG_PARENT_ACTIVITY_VALUE = "activity";
    public static final String ARG_PARENT_WIDGET_VALUE = "widget";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity !=null)

        {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                stockSymbol = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        lineChartView = (LineChartView) findViewById(R.id.chart);

        tv_graph_begin = (TextView) findViewById(R.id.tv_graph_begin);
        tv_graph_end = (TextView) findViewById(R.id.tv_graph_end);
        tv_graph_evolution = (TextView) findViewById(R.id.tv_graph_evolution);
        tv_graph_max = (TextView) findViewById(R.id.tv_graph_max);
        tv_graph_min = (TextView) findViewById(R.id.tv_graph_min);


        this.setTitle(stockSymbol);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);


        Timber.d("zzz Detail activity created ",stockSymbol);
        System.out.println("zzz Stock is "+stockSymbol);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                HistQuoteCols._ID,
                HistQuoteCols.SYMBOL,
                HistQuoteCols.DATE,
                HistQuoteCols.OPENPRICE};

        String selection = HistQuoteCols.SYMBOL + " = ?";

        String[] selectionArgs = new String[]{stockSymbol};

        String sortOrder = HistQuoteCols.DATE + " ASC";

        return new CursorLoader(this,
                StockProvider.HistoricalQuotation.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int x = 0;

        if (data.moveToFirst()){

            Float maxValue = 0f;
            Float minValue = 0f;

            List<AxisValue> axisValuesX = new ArrayList<>();
            List<PointValue> pointValues = new ArrayList<>();

            // Add point and define min and max value
            do {
                String date = data.getString(data.getColumnIndex(HistQuoteCols.DATE));
                String sPrice = data.getString(data.getColumnIndex(HistQuoteCols.OPENPRICE));
                Float fPrice = Float.valueOf(sPrice);

                if (maxValue == 0f || fPrice > maxValue){
                    maxValue = fPrice;
                }
                if (minValue == 0f || fPrice < minValue){
                    minValue = fPrice;
                }

                PointValue pointValue = new PointValue(x, fPrice);
                pointValues.add(pointValue);

                if (x == (data.getCount() / 3) || x == (data.getCount() / 3 * 2)) {
                    AxisValue axisValueX = new AxisValue(x);
                    axisValueX.setLabel(date);
                    axisValuesX.add(axisValueX);
                }

                x++;
            } while (data.moveToNext());

            // Draw Line
            Line line = new Line(pointValues).setColor(Color.WHITE).setCubic(false);
            List<Line> lines = new ArrayList<>();
            lines.add(line);

            LineChartData lineChartData = new LineChartData();
            lineChartData.setLines(lines);

            // Define x-axis
            Axis axisX = new Axis(axisValuesX);
            axisX.setHasLines(true);
            axisX.setMaxLabelChars(4);
            lineChartData.setAxisXBottom(axisX);

            // Define y-axis
            Axis axisY = new Axis();
            axisY.setAutoGenerated(true);
            axisY.setHasLines(true);
            axisY.setMaxLabelChars(4);
            lineChartData.setAxisYLeft(axisY);

            // Update chart with data
            lineChartView.setInteractive(false);
            lineChartView.setLineChartData(lineChartData);

            // Define start date and end date, and evolution of price on this period
            data.moveToFirst();
            String startDate = data.getString(data.getColumnIndex(HistQuoteCols.DATE));
            String sStartPrice = data.getString(data.getColumnIndex(HistQuoteCols.OPENPRICE));
            Float fStartPrice = Float.valueOf(sStartPrice);
            data.moveToLast();
            String endDate = data.getString(data.getColumnIndex(HistQuoteCols.DATE));
            String sEndPrice = data.getString(data.getColumnIndex(HistQuoteCols.OPENPRICE));
            Float fEndPrice = Float.valueOf(sEndPrice);
            String evolution = String.format("%.4f",(fEndPrice-fStartPrice)*100/fStartPrice) + " %";

            // Update details information
            tv_graph_begin.setText(startDate);
            tv_graph_end.setText(endDate);
            tv_graph_evolution.setText(evolution);
            tv_graph_max.setText(String.valueOf(maxValue));
            tv_graph_min.setText(String.valueOf(minValue));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getIntent().getExtras().getString(ARG_PARENT) == ARG_PARENT_WIDGET_VALUE) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            finish();
        }
    }
}