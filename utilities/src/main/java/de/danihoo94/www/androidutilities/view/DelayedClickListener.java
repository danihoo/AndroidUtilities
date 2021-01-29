package de.danihoo94.www.androidutilities.view;

import android.view.View;

import java.util.Calendar;

@SuppressWarnings({"unused", "RedundantSuppression"})
public abstract class DelayedClickListener implements View.OnClickListener {

    private static final int DELAY_MILLIS = 1000;

    private final long delayMillis;
    private long lastClick;

    public DelayedClickListener() {
        this.delayMillis = DELAY_MILLIS;
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
        }
    }

    protected abstract void performClick(View v);
}
