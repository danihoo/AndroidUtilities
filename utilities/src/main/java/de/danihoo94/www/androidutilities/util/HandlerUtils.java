package de.danihoo94.www.androidutilities.util;

import android.os.Handler;
import android.os.HandlerThread;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class HandlerUtils {
    private static Handler getHandler() {
        HandlerThread thread = new HandlerThread("handler");
        thread.start();
        return new Handler(thread.getLooper());
    }

    public static void runOnDataThread(Runnable r) {
        final Handler handler = HandlerUtils.getHandler();
        handler.post(() -> {
            r.run();
            ((HandlerThread) handler.getLooper().getThread()).quitSafely();
        });
    }
}
