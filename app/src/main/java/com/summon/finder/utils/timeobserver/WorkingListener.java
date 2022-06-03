package com.summon.finder.utils.timeobserver;

import android.widget.TextView;

import com.summon.finder.helper.time.TimeHelper;

public class WorkingListener extends TimeListener {
    TextView editText;
    String time;

    public WorkingListener(String id, TextView editText, String time) {
        super(id);
        this.editText = editText;
        this.time = time;
    }

    @Override
    public void update(String newTime) {
        handleSetStatus(newTime);
    }

    public void handleSetStatus(String newTime) {
        String now = TimeHelper.findDifference(time, newTime);
        if (now.contains("0")) {
            editText.setText("Bây giờ");
            return;
        }

        editText.setText("Hoạt động " + now + " trước");
    }
}
