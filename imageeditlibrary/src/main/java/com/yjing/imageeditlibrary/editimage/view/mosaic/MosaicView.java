package com.yjing.imageeditlibrary.editimage.view.mosaic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.yjing.imageeditlibrary.editimage.inter.EditFunctionOperationInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 马赛克
 * 使用说明：
 * 1. 布局中引用
 * view mosaic = (MosaicView) findViewById(R.id.mosaic);
 * 2. 设置所要打马赛克的图片
 * mosaic.setMosaicBackgroundResource(mBitmap);
 * 3.设置马赛克资源样式
 * mosaic.setMosaicResource(bit);
 * 4.设置绘画粗细
 * mosaic.setMosaicBrushWidth(10);
 * 5.设置马赛克类型(马赛克，橡皮擦)
 * mosaic.setMosaicType(MosaicType.ERASER);
 */
public class MosaicView extends View implements EditFunctionOperationInterface {

    public static final String TAG = "MosaicView";

    // default image inner padding, in dip pixels
    private static final int INNER_PADDING = 0;

    /**
     * 马赛克粗细默认值
     **/
    private static final int PATH_WIDTH = 30;

    /**
     * 绘画板宽度和高度，数据来自要处理的图片
     */
    private int mImageWidth, mImageHeight;

    /**
     * 橡皮擦图层
     */
    private Bitmap bmCoverLayer;

    private Bitmap bmMosaicLayer;

    /**
     * 画笔
     */
    private int mBrushWidth;

    private Rect mImageRect;

    private Paint mPaint;

    private int mPadding;

    /**
     * 触摸路径数据
     */

    private List<MosaicPath> touchPaths;
    private List<MosaicPath> erasePaths;

    private MosaicPath touchPath;

    /**
     * 马赛克类型 Mosaic: 打码 erase: 橡皮擦
     */

    private MosaicUtil.MosaicType mMosaicType = MosaicUtil.MosaicType.MOSAIC;

    private Context mContext;
    private boolean isOperation = false;

    public MosaicView(Context context) {
        super(context);
        this.mContext = context;
        initDrawView();
    }

    public MosaicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initDrawView();
    }

    public MosaicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }

    /**
     * 初始化绘画板 默认的情况下是马赛克模式
     */
    private void initDrawView() {
        touchPaths = new ArrayList<>();
        erasePaths = new ArrayList<>();

        mPadding = dp2px(INNER_PADDING);
        mBrushWidth = dp2px(PATH_WIDTH);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(0xff2a5caa);
        mImageRect = new Rect();
        setWillNotDraw(false);
        setMosaicType(MosaicUtil.MosaicType.MOSAIC);
    }

    /**
     * 设置画刷的宽度
     *
     * @param brushWidth 画刷宽度大小
     */
    public void setMosaicBrushWidth(int brushWidth) {
        this.mBrushWidth = dp2px(brushWidth);
    }

    /**
     * 设置马赛克类型
     *
     * @param type 类型
     */

    public void setMosaicType(MosaicUtil.MosaicType type) {
        this.mMosaicType = type;
    }

    /**
     * 设置所要打码的图片资源
     *
     * @param imgPath 图片路径
     */

    public void setMosaicBackgroundResource(String imgPath) {
        File file = new File(imgPath);
        if (file == null || !file.exists()) {
            Log.w(TAG, "setSrcPath invalid file path " + imgPath);
            return;
        }

        reset();

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
//        bmBaseLayer = bitmap;
        requestLayout();
        invalidate();
    }

    /**
     * 设置马赛克样式资源
     *
     * @param imgPath 样式图片路径
     */
    public void setMosaicResource(String imgPath) {

        File file = new File(imgPath);
        if (file == null || !file.exists()) {
            Log.w(TAG, "setSrcPath invalid file path " + imgPath);
            setMosaicType(MosaicUtil.MosaicType.ERASER);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        if (bitmap != null) {
            setMosaicType(MosaicUtil.MosaicType.MOSAIC);

            if (bmCoverLayer != null) {
                bmCoverLayer.recycle();
            }
            bmCoverLayer = bitmap;
        } else {
            Log.i("jarlen", " setMosaicResource bitmap = null ");
            return;
        }

        updatePathMosaic();
        invalidate();
    }

    /**
     * 设置所要打码的资源图片
     *
     * @param bitmap 资源图片路径
     */
    public void setMosaicBackgroundResource(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("jarlen", "setMosaicBackgroundResource : bitmap == null");
            return;
        }

        reset();
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();

//        bmBaseLayer = bitmap;

        requestLayout();
        invalidate();
    }

    /**
     * 设置马赛克样式资源
     *
     * @param bitmap 样式图片资源
     */
    public void setMosaicResource(Bitmap bitmap) {
        setMosaicType(MosaicUtil.MosaicType.MOSAIC);

        if (bmCoverLayer != null) {
            bmCoverLayer.recycle();
        }
        erasePaths.clear();
        touchPaths.clear();

        //复制一张和马赛克图片一样的bitmap
        bmCoverLayer = copyBitmap(bitmap);
        updatePathMosaic();

        invalidate();
    }

    private Bitmap copyBitmap(Bitmap bit) {
        Bitmap bitmap = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bit, 0, 0, null);
        canvas.save();
        return bitmap;
    }

    /**
     * 清除绘画数据
     */
    public void clear() {
        touchPaths.clear();
        erasePaths.clear();

        if (bmMosaicLayer != null) {
            bmMosaicLayer.recycle();
            bmMosaicLayer = null;
        }

        invalidate();
    }

    /**
     * 重置绘画版
     *
     * @return
     */
    public boolean reset() {
        this.mImageWidth = 0;
        this.mImageHeight = 0;
        if (bmCoverLayer != null) {
            bmCoverLayer.recycle();
            bmCoverLayer = null;
        }
        if (bmMosaicLayer != null) {
            bmMosaicLayer.recycle();
            bmMosaicLayer = null;
        }

        touchPaths.clear();
        erasePaths.clear();

        invalidate();

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        //外部通过isOperation变量可以控制是否操作本图层
        if (!isOperation) {
            return isOperation;
        }
        super.dispatchTouchEvent(event);

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return true;
        }

        if (x < mImageRect.left || x > mImageRect.right || y < mImageRect.top || y > mImageRect.bottom) {
            return true;
        }

        float ratio = (mImageRect.right - mImageRect.left) / (float) mImageWidth;
        x = (int) ((x - mImageRect.left) / ratio);
        y = (int) ((y - mImageRect.top) / ratio);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchPath = new MosaicPath();
                touchPath.drawPath = new Path();
                touchPath.drawPath.moveTo(x, y);
                touchPath.paintWidth = mBrushWidth;

                if (this.mMosaicType == MosaicUtil.MosaicType.MOSAIC) {
                    touchPaths.add(touchPath);
                } else {
                    erasePaths.add(touchPath);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                touchPath.drawPath.lineTo(x, y);
                updatePathMosaic();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                break;

        }
        return true;
    }

    /**
     * 刷新绘画板
     */
    private void updatePathMosaic() {
        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return;
        }

        if (bmMosaicLayer != null) {
            bmMosaicLayer.recycle();
        }
        //创建马赛克图层
        bmMosaicLayer = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);

        //临时的bitmap
        Bitmap bmTouchLayer = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);

        //创建paint并设置
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));//将路径的转角变得圆滑
        paint.setStrokeWidth(mBrushWidth);
        paint.setColor(Color.BLUE);

        //临时的canvas
        Canvas canvas = new Canvas(bmTouchLayer);

        //向画布上画马赛克，正常画路径
        for (MosaicPath path : touchPaths) {
            Path pathTemp = path.drawPath;
            int drawWidth = path.paintWidth;
            paint.setStrokeWidth(drawWidth);
            canvas.drawPath(pathTemp, paint);
        }

        //向画布画橡皮擦路径
        //
        paint.setColor(Color.TRANSPARENT);//
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//没有懂  尝试过clear、srcin都可以使用 如果不设置该属性，橡皮擦功能不能使用

        for (MosaicPath path : erasePaths) {
            Path pathTemp = path.drawPath;
            int drawWidth = path.paintWidth;
            paint.setStrokeWidth(drawWidth);
            canvas.drawPath(pathTemp, paint);
        }

        canvas.setBitmap(bmMosaicLayer);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawBitmap(bmCoverLayer, 0, 0, null);//马赛克样式的图层

        paint.reset();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//显示手指触摸的地方
        canvas.drawBitmap(bmTouchLayer, 0, 0, paint);//手指操作的图层
        paint.setXfermode(null);
        canvas.save();

        bmTouchLayer.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bmMosaicLayer != null) {
            canvas.drawBitmap(bmMosaicLayer, null, mImageRect, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {

        if (mImageWidth <= 0 || mImageHeight <= 0) {
            return;
        }

        int contentWidth = right - left;
        int contentHeight = bottom - top;
        int viewWidth = contentWidth - mPadding * 2;
        int viewHeight = contentHeight - mPadding * 2;
        float widthRatio = viewWidth / ((float) mImageWidth);
        float heightRatio = viewHeight / ((float) mImageHeight);
        float ratio = widthRatio < heightRatio ? widthRatio : heightRatio;
        int realWidth = (int) (mImageWidth * ratio);
        int realHeight = (int) (mImageHeight * ratio);

        int imageLeft = (contentWidth - realWidth) / 2;
        int imageTop = (contentHeight - realHeight) / 2;
        int imageRight = imageLeft + realWidth;
        int imageBottom = imageTop + realHeight;
        mImageRect.set(imageLeft, imageTop, imageRight, imageBottom);
    }

    /**
     * 返回马赛克最终结果
     *
     * @return 马赛克最终结果
     */
    public Bitmap getMosaicBitmap() {
        if (bmMosaicLayer == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//        canvas.drawBitmap(bmBaseLayer, 0, 0, null);
        canvas.drawBitmap(bmMosaicLayer, 0, 0, null);
        canvas.save();
        return bitmap;
    }


    public Bitmap getMosaicBit() {
        return bmMosaicLayer;
    }

    private int dp2px(int dip) {
        Context context = this.getContext();
        Resources resources = context.getResources();
        int px = Math
                .round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        dip, resources.getDisplayMetrics()));
        return px;
    }

    @Override
    public void setIsOperation(boolean isOperation) {
        this.isOperation = isOperation;
    }

    @Override
    public Boolean getIsOperation() {
        return null;
    }

    public void undo() {
        if (touchPaths.size() > 0) {
            MosaicPath undoable = touchPaths.remove(touchPaths.size() - 1);
            Log.i("wangyanjing", "撤销了一个mPath"+undoable.hashCode()+"===="+touchPaths.size());
            updatePathMosaic();
            invalidate();
        }
    }
}//end class
