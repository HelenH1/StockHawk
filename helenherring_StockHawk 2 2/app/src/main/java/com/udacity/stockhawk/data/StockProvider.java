package com.udacity.stockhawk.data;

import android.net.Uri;
import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by helenherring on 5/26/17.
 */

@ContentProvider(authority = StockProvider.AUTHORITY, database = QuoteDbHelper.class)

public class StockProvider {
    public static final String AUTHORITY = "com.udacity.stockhawk.data.StockProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String QUOTES = "quotes";
        String HIST_QUOTE = "historical_quotation";
    }

    private static Uri buildUri(String... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path:paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = QuoteDbHelper.QUOTES)
    public static class Quotes{
        @ContentUri(
                path = Path.QUOTES,
                type = "vnd.android.cursor.dir/quote"
        )
        public static final Uri CONTENT_URI = buildUri(Path.QUOTES);

        @InexactContentUri(
                name = "QUOTE_ID",
                path = Path.QUOTES + "/*",
                type = "vnd.android.cursor.item/quote",
                whereColumn = QuoteCols.SYMBOL,
                pathSegment = 1
        )
        public static Uri withSymbol(String symbol){
            return buildUri(Path.QUOTES, symbol);
        }
    }

    @TableEndpoint(table = QuoteDbHelper.HIST_QUOTE)
    public static class HistoricalQuotation{
        @ContentUri(
                path = Path.HIST_QUOTE,
                type = "vnd.android.cursor.dir/historical_quotation"
        )
        public static final Uri CONTENT_URI = buildUri(Path.HIST_QUOTE);
    }
}