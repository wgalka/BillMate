package com.example.billmate.itemsBean;

import java.util.concurrent.TimeUnit;

public class CurrentTime {

    private static final String TAG = CurrentTime.class.getSimpleName();
    private long date;

    public CurrentTime() {
    }

    public Long milliseconds() {
        date = System.currentTimeMillis();
        String milliseconds = String.valueOf(date);
        return date;
    }

    public Long convertMillisecondsToMinutes(Long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        return minutes;
    }
}
