package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.stockhawk.data.Contract.Quote;


import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = QuoteDbHelper.VERSION)
public class QuoteDbHelper {
    private QuoteDbHelper(){


    }

    public static final int VERSION = 3;

    @Table(QuoteCols.class) public static final String QUOTES = "quotes";
    @Table(HistQuoteCols.class) public static final String HIST_QUOTE = "historical_quotation";
}