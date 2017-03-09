package com.wanjian.sak;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import com.wanjian.sak.config.Config;
import com.wanjian.sak.converter.SizeConverter;
import com.wanjian.sak.layer.AbsLayer;
import com.wanjian.sak.layerview.DragLayerView;
import com.wanjian.sak.mapper.ItemLayerLayout;
import com.wanjian.sak.mapper.ItemLayerViewLayout;
import com.wanjian.sak.mapper.UnitLayout;
import com.wanjian.sak.view.DrawingBoardView;
import com.wanjian.sak.view.SAKCoverView;
import com.wanjian.sak.view.WheelView;

import java.util.List;

/**
 * Created by wanjian on 2017/3/7.
 */

class Manager {

    private Config mConfig;

    private SAKCoverView mCoverView;

    private int mStartLayer = 5;
    private int mEndLayer = 30;

    public Manager(Context context, Config config) {
        mConfig = config;
        mCoverView = new SAKCoverView(context.getApplicationContext());
        final List<AbsLayer> layers = config.getLayers();

        for (AbsLayer layer : layers) {
            mCoverView.addItem(new ItemLayerLayout(layer));
        }

        List<DragLayerView> layerViews = config.getLayerViews();
        for (DragLayerView layerView : layerViews) {
            mCoverView.addItem(new ItemLayerViewLayout(layerView));
        }
        for (SizeConverter converter : config.getSizeConverters()) {
            mCoverView.addItem(new UnitLayout(converter));
        }
        initCoverView();
    }

    private void initCoverView() {
        mCoverView.setOnDrawListener(new DrawingBoardView.OnDrawListener() {
            @Override
            public void onDraw(Canvas canvas) {
                View root = mCoverView.getRootView();
                List<AbsLayer> layers = mConfig.getLayers();
                for (AbsLayer layer : layers) {
                    layer.draw(canvas, root, mStartLayer, mEndLayer);
                }
            }
        });
        mCoverView.setStartLayerChangeListener(new WheelView.OnChangeListener() {
            @Override
            public void onChange(int num) {
                mStartLayer = num;
            }
        });

        mCoverView.setEndLayerChangeListener(new WheelView.OnChangeListener() {
            @Override
            public void onChange(int num) {
                mEndLayer = num;
            }
        });
        mCoverView.setOnCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (AbsLayer layer : mConfig.getLayers()) {
                    layer.enable(false);
                }
            }
        });

    }


    public void detach(Activity activity) {
        mCoverView.detach(activity);
        ((ViewGroup) activity.getWindow().getDecorView()).removeView(mCoverView);
    }

    public void attach(Activity activity) {
        if (mCoverView.getParent() != null) {
            ((ViewGroup) mCoverView.getParent()).removeView(mCoverView);
        }

        ViewGroup dectorView = ((ViewGroup) activity.getWindow().getDecorView());
        dectorView.addView(mCoverView);
        mCoverView.attach(activity);
    }
}