package com.summon.finder.component.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.RangeSlider;
import com.summon.finder.R;

import java.util.List;

public class RangeSlide extends LinearLayout {
    private int maxField;
    private int minField;
    private String prefixField;
    private String titleField;
    private RangeSlider slideView;
    private TextView titleView;
    private TextView valueView;
    private int valueMinField;
    private int valueMaxField;

    public RangeSlide(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);

        handleAttr(attrs);
        handleSetEvent();
    }

    private void handleSetEvent() {
        slideView.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider rangeSlider, float value, boolean fromUser) {
                List<Float> values = slideView.getValues();

                valueView.setText(String.format("%d %d", Math.round(values.get(0)), Math.round(values.get(1))));
            }
        });
    }

    private void handleAttr(@Nullable AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RangeSlide);

        prefixField = ta.getString(R.styleable.RangeSlide_prefixRangeSlide);
        prefixField = prefixField == null || prefixField.isEmpty() ? "" : prefixField;

        titleField = ta.getString(R.styleable.RangeSlide_titleRangeSlide);
        titleField = titleField == null || titleField.isEmpty() ? "Title" : titleField;

        minField = ta.getInteger(R.styleable.RangeSlide_minRangeSlide, 0);
        maxField = ta.getInteger(R.styleable.RangeSlide_maxRangeSlide, 100);

        valueMinField = ta.getInteger(R.styleable.RangeSlide_minValue, 0);
        valueMaxField = ta.getInteger(R.styleable.RangeSlide_maxValue, 0);

        titleView.setText(titleField);
        handleSetValueDefault();
    }

    private void handleSetValueDefault() {
        if (minField > valueMinField) {
            valueMinField = minField;
        }

        if (maxField < valueMaxField) {
            valueMaxField = maxField;
        }

        valueView.setText(String.format("%d - %d", valueMinField, valueMaxField));
        slideView.setValueTo(maxField);
        slideView.setValueFrom(minField);

        slideView.setValues(new Float[]{Float.valueOf(valueMinField), Float.valueOf(valueMaxField)});
    }

    private void initControl(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.range_slider, this);

        // layout is inflated, assign local variables to components
        slideView = (RangeSlider) findViewById(R.id.continuousSlider);
        titleView = (TextView) findViewById(R.id.title);
        valueView = (TextView) findViewById(R.id.value);
    }
}
