package com.yjing.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.task.StickerTask;
import com.yjing.imageeditlibrary.editimage.utils.FileUtils;
import com.yjing.imageeditlibrary.editimage.view.mosaic.MosaicUtil;
import com.yjing.imageeditlibrary.editimage.view.mosaic.MosaicView;


public class MosaicFragment extends BaseFragment implements View.OnClickListener, ImageEditInte {
    public static final int INDEX = 4;
    private MosaicView mMosaicView;
    private SaveMosaicPaintTask mSaveMosaicPaintImageTask;
    private View action_base;
    private View action_ground_glass;
    private View action_flower;
    private View mRevokeView;
    private View preMosaicButton;

    public MosaicFragment() {
        // Required empty public constructor
    }

    public static MosaicFragment newInstance(EditImageActivity activity) {
        MosaicFragment fragment = new MosaicFragment();
        fragment.activity = activity;
        fragment.mMosaicView = activity.mMosaicView;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_mosaic, container, false);
        action_base = mainView.findViewById(R.id.action_base);
        action_ground_glass = mainView.findViewById(R.id.action_ground_glass);
        action_flower = mainView.findViewById(R.id.action_flower);
        mRevokeView = mainView.findViewById(R.id.paint_revoke);
        return mainView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        action_base.setOnClickListener(this);
        action_ground_glass.setOnClickListener(this);
        action_flower.setOnClickListener(this);
        mRevokeView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Bitmap mainBitmap = activity.mainBitmap;
        int i = v.getId();

        if (i == R.id.action_base) {
            mMosaicView.setMosaicBackgroundResource(mainBitmap);
            Bitmap bitmapMosaic = MosaicUtil.getMosaic(mainBitmap);
            mMosaicView.setMosaicResource(bitmapMosaic);

            changeToolsSelector(v);
        } else if (i == R.id.action_ground_glass) {
            mMosaicView.setMosaicBackgroundResource(mainBitmap);
            Bitmap bitmapBlur = MosaicUtil.getBlur(mainBitmap);
            mMosaicView.setMosaicResource(bitmapBlur);

            changeToolsSelector(v);
        } else if (i == R.id.action_flower) {
            mMosaicView.setMosaicBackgroundResource(mainBitmap);
            Bitmap bit = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.hi4);
            bit = FileUtils.ResizeBitmap(bit, mainBitmap.getWidth(), mainBitmap.getHeight());
            mMosaicView.setMosaicResource(bit);
        } else if (i == R.id.paint_revoke) {
//            mMosaicView.setMosaicType(MosaicUtil.MosaicType.ERASER);
            mMosaicView.undo();
        } else {
        }
    }

    private void changeToolsSelector(View v) {
        View toolsButton = ((ViewGroup) v).getChildAt(0);

        if (preMosaicButton != null) {
            preMosaicButton.setSelected(false);
        }
        //更新按钮状态
        toolsButton.setSelected(true);
        preMosaicButton = toolsButton;

    }

    /**
     * 设置马赛克的样式和粗细
     */
    private void initSetting() {

// *         1. 布局中引用view mosaic = (DrawMosaicView) findViewById(R.id.mosaic);
// *
// *         2. 设置所要打马赛克的图片
//                *
// *         mosaic.setMosaicBackgroundResource(mBitmap);
// *
// *         3.设置马赛克资源样式
//                *
// *         mosaic.setMosaicResource(bit);
// *
// *         4.设置绘画粗细
//                *
// *         mosaic.setMosaicBrushWidth(10);
// *
// *         5.设置马赛克类型(马赛克，橡皮擦)
//                *
// *         mosaic.setMosaicType(MosaicType.ERASER);
        mMosaicView.setMosaicBackgroundResource(activity.mainBitmap);
        Bitmap bit = MosaicUtil.getMosaic(activity.mainBitmap);

        mMosaicView.setMosaicResource(bit);
        mMosaicView.setMosaicBrushWidth(10);

        //默认选中基础模式
        changeToolsSelector(action_base);

    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
//        appleEdit(null);
        activity.mainImage.setVisibility(View.VISIBLE);
//        this.mMosaicView.setVisibility(View.GONE);
        mMosaicView.setIsOperation(false);
    }


    public void onShow() {
//        this.mMosaicView.setVisibility(View.VISIBLE);
        mMosaicView.setIsOperation(true);
        initSetting();
    }

    @Override
    public void appleEdit(SaveCompletedInte inte) {
        if (mSaveMosaicPaintImageTask != null && !mSaveMosaicPaintImageTask.isCancelled()) {
            mSaveMosaicPaintImageTask.cancel(true);
        }

        mSaveMosaicPaintImageTask = new SaveMosaicPaintTask(activity, inte);
        mSaveMosaicPaintImageTask.execute(activity.mainBitmap);
    }

    @Override
    public void method2() {

    }

    @Override
    public void method3() {

    }


    /**
     * 保存马赛克图片
     */
    private final class SaveMosaicPaintTask extends StickerTask {

        public SaveMosaicPaintTask(EditImageActivity activity, SaveCompletedInte inte) {
            super(activity, inte);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {

//            float[] f = new float[9];
//            m.getValues(f);
//            int dx = (int) f[Matrix.MTRANS_X];
//            int dy = (int) f[Matrix.MTRANS_Y];
//            float scale_x = f[Matrix.MSCALE_X];
//            float scale_y = f[Matrix.MSCALE_Y];
            canvas.save();
//            canvas.translate(dx, dy);
//            canvas.scale(scale_x, scale_y);

            if (mMosaicView.getMosaicBit() != null) {
                canvas.drawBitmap(mMosaicView.getMosaicBit(), 0, 0, null);
            }
            canvas.restore();
        }

        @Override
        public void onPostResult(Bitmap result) {
            mMosaicView.reset();
            activity.changeMainBitmap(result);
        }
    }//end inner class
}
