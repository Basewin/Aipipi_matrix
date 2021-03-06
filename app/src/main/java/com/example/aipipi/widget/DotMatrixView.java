package com.example.aipipi.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;


public class DotMatrixView extends View {

    private int dotColorEmpty = 0xFFCCCCCC;
    private int dotColorFull = 0xFF000000;
    private int bgColor = Color.WHITE;
    private Paint dotPaint;
    private boolean[][] matrix = new boolean[24][24];

    private float dotSpace;
    private float dotSize;
    private int totalRow = 16;
    private int totalColumn = 0;
    private int startColumn = 0;
    private int scrollTime = 50;
    private boolean isScroll = false;
    private boolean isAddFont = false;
    private int width, height;

    public DotMatrixView(Context context) {
        this(context, null);
    }

    public DotMatrixView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotMatrixView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dotPaint = new Paint();
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setAntiAlias(true);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                width = getWidth();
                height = getHeight();
                initParams();
                return true;
            }
        });
    }

    private void initParams() {
        int scale = 9;
        if(matrix != null && matrix.length != 0) {
            totalRow = matrix[0].length;
        }
        dotSpace = height / (float)(scale * totalRow + totalRow + 1);
        dotSize = scale * dotSpace;
        dotPaint.setStrokeWidth(dotSize);

        totalColumn = 0;
        while(getXPosition(totalColumn) < width) {
            totalColumn++;
        }
        startColumn = -totalColumn;
    }

    public void setMatrix(boolean[][] _matrix) {
        this.matrix = _matrix;
        isAddFont = true;
        initParams();
        invalidate();
    }

    public void startScroll(int scrollTime) {
        this.scrollTime = scrollTime;
        isScroll = true;
        startColumn = -totalColumn;
        handler.removeCallbacks(scrollTask);
        handler.postDelayed(scrollTask, scrollTime);
    }

    public void stopScroll() {
        isScroll = false;
    }

    public void resumeScroll() {
        if(isAddFont) {
            isScroll = true;
            handler.removeCallbacks(scrollTask);
            handler.postDelayed(scrollTask, scrollTime);
        }
    }

    Handler handler = new Handler();
    Runnable scrollTask = new Runnable() {
        @Override
        public void run() {
            startColumn++;
            if(startColumn >= matrix.length) {
                startColumn = -totalColumn;
            }
            invalidate();
            if(isScroll) {
                handler.postDelayed(this, scrollTime);
            }
        }
    };

    public void setDotColor(@ColorInt int color) {
        this.dotColorFull = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(bgColor);

        for(int i = 0; i < totalColumn + 1; i++) {
            for (int j = 0; j < totalRow; j++) {
                float x = getXPosition(i);
                float y = (j + 1) * dotSpace + j * dotSize + dotSize / 2;
                if(matrix != null &&  i + startColumn > 0 && i + startColumn < matrix.length && matrix[i + startColumn][j]) {
                    dotPaint.setColor(dotColorFull);
                } else {
                    dotPaint.setColor(dotColorEmpty);
                }
                canvas.drawPoint(x, y, dotPaint);
            }

        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(scrollTask);
    }

    private float getXPosition(int column) {
        return (column + 1) * dotSpace + column * dotSize + dotSize / 2;
    }

}
