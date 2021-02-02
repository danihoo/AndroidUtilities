package de.danihoo94.www.androidutilities.view;

import android.util.Log;
import android.view.MenuItem;

import java.util.Calendar;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class DelayedMenuClickListener implements MenuItem.OnMenuItemClickListener {

    public static final int DELAY_SHORT = 600;
    public static final int DELAY_MEDIUM = 1200;
    public static final int DELAY_LONG = 1800;

    private final long delayMillis;
    private long lastClick;

    public DelayedMenuClickListener() {
        this.delayMillis = DELAY_MEDIUM;
    }

    public DelayedMenuClickListener(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public final boolean onMenuItemClick(MenuItem item) {
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - delayMillis > lastClick) {
            lastClick = now;
            return performClick(item);
        } else {
            Log.i("info", "double click was catched");
            return false;
        }
    }

    public abstract boolean performClick(MenuItem item);
}
