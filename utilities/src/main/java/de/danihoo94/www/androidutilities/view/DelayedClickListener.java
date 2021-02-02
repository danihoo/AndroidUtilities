package de.danihoo94.www.androidutilities.view;

import android.util.Log;
import android.view.View;

import java.util.Calendar;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class DelayedClickListener implements View.OnClickListener {

    public static final int DELAY_SHORT = 600;
    public static final int DELAY_MEDIUM = 1200;
    public static final int DELAY_LONG = 1800;

    private final long delayMillis;
    private long lastClick;

    public DelayedClickListener() {
        this.delayMillis = DELAY_MEDIUM;
    }

    public DelayedClickListener(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public final void onClick(View v) {
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - delayMillis > lastClick) {
            lastClick = now;
            performClick(v);
        } else {
            Log.i("info", "double click was catched");
        }
    }

    protected abstract void performClick(View v);
}
