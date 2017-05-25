package com.yjing.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yjing.imageeditlibrary.editimage.inter.EditFunctionOperationInterface;

import static android.R.attr.x;

public class PaintModeView extends View implements EditFunctionOperationInterface {
    private Paint mPaint;

    private int mStokeColor;
    private float mStokeWidth =x -1;

    private float mRadius;
    private boolean isOperation = false;

    public PaintModeView(Context context) {
        super(context);
        initView(context);
    }

    public PaintModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PaintModeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PaintModeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    protected void initView(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);

        //mStokeWidth = 10;
        //mStokeColor = Color.RED;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mStokeColor);
        mRadius = mStokeWidth;

        canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, mRadius, mPaint);
    }

    public void setPaintStrokeColor(final int newColor) {
        this.mStokeColor = newColor;
        this.invalidate();
    }

    public void setPaintStrokeWidth(final float width) {
        this.mStokeWidth = width;
        this.invalidate();
    }

    public float getStokenWidth() {
        if (mStokeWidth < 0) {
            mStokeWidth = getMeasuredHeight();
        }
        return mStokeWidth;
    }

    public int getStokenColor() {
        return mStokeColor;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isOperation ? super.onTouchEvent(event) : isOperation;
    }

    @Override
    public void setIsOperation(boolean isOperation) {
        this.isOperation = isOperation;
    }

    @Override
    public Boolean getIsOperation() {
        return null;
    }
}//end class
