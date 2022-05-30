package com.summon.finder.component.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;
import com.summon.finder.R;

public class ContinuousSlider extends LinearLayout {
    private int maxField;
    private int minField;
    private int valueField;
    private String prefixField;
    private String titleField;
    private Slider slideView;
    private TextView titleView;
    private TextView valueView;

    public ContinuousSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);

        handleAttr(attrs);
        handleSetEvent();
    }

    private void handleSetEvent() {
        slideView.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                valueView.setText(String.format("%d %s", Math.round(value), prefixField));
            }
        });
    }

    private void handleAttr(@Nullable AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ContinuousSlide);

        prefixField = ta.getString(R.styleable.ContinuousSlide_prefix);
        prefixField = prefixField == null || prefixField.isEmpty() ? "" : prefixField;

        titleField = ta.getString(R.styleable.ContinuousSlide_titleContinuousSlide);
        titleField = titleField == null || titleField.isEmpty() ? "Title" : titleField;

        minField = ta.getInteger(R.styleable.ContinuousSlide_minContinuousSlide, 0);
        maxField = ta.getInteger(R.styleable.ContinuousSlide_maxContinuousSlide, 100);

        valueField = ta.getInteger(R.styleable.ContinuousSlide_value, 0);

        titleView.setText(titleField);
        handleSetValueDefault();
    }

    private void handleSetValueDefault() {
        if (minField > valueField) {
            valueField = minField;
        }

        if (maxField < valueField) {
            valueField = maxField;
        }


        valueView.setText(String.format("%d %s", valueField, prefixField));
        slideView.setValueTo(maxField);
        slideView.setValueFrom(minField);
        slideView.setValue((float) valueField);
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.continuous_slider, this);

        // layout is inflated, assign local variables to components
        slideView = (Slider) findViewById(R.id.continuousSlider);
        titleView = (TextView) findViewById(R.id.title);
        valueView = (TextView) findViewById(R.id.value);
    }
}
