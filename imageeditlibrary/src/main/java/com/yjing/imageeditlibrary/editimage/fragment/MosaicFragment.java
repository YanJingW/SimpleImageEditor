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
import com.yjing.imageeditlibrary.editimage.inter.ImageEditInte;
import com.yjing.imageeditlibrary.editimage.inter.SaveCompletedInte;
import com.yjing.imageeditlibrary.editimage.task.StickerTask;
import com.yjing.imageeditlibrary.editimage.view.mosaic.MosaicUtil;
import com.yjing.imageeditlibrary.editimage.view.mosaic.MosaicView;
import com.yjing.imageeditlibrary.utils.FileUtils;

import java.util.HashMap;


public class MosaicFragment extends BaseFragment implements View.OnClickListener, ImageEditInte {
    private MosaicView mMosaicView;
    private SaveMosaicPaintTask mSaveMosaicPaintImageTask;
    private View action_base;
    private View action_ground_glass;
    private View action_flower;
    private View mRevokeView;
    private View preMosaicButton;
    private HashMap<MosaicUtil.Effect, Bitmap> mosaicResMap;

    public MosaicFragment() {
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

            if (hasMosaicRes(MosaicUtil.Effect.MOSAIC)) {
                Bitmap bit = MosaicUtil.getMosaic(activity.mainBitmap);
                mosaicResMap.put(MosaicUtil.Effect.MOSAIC, bit);
                mMosaicView.setMosaicResource(mosaicResMap);
            }
            mMosaicView.setMosaicEffect(MosaicUtil.Effect.MOSAIC);
            changeToolsSelector(MosaicUtil.Effect.MOSAIC);
        } else if (i == R.id.action_ground_glass) {

            if (hasMosaicRes(MosaicUtil.Effect.BLUR)) {
                Bitmap blur = MosaicUtil.getBlur(activity.mainBitmap);
                mosaicResMap.put(MosaicUtil.Effect.BLUR, blur);
                mMosaicView.setMosaicResource(mosaicResMap);
            }
            mMosaicView.setMosaicEffect(MosaicUtil.Effect.BLUR);
            changeToolsSelector(MosaicUtil.Effect.BLUR);
        } else if (i == R.id.action_flower) {

            if (hasMosaicRes(MosaicUtil.Effect.FLOWER)) {
                Bitmap bit = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.hi4);
                bit = FileUtils.ResizeBitmap(bit, mainBitmap.getWidth(), mainBitmap.getHeight());

                mosaicResMap.put(MosaicUtil.Effect.FLOWER, bit);
                mMosaicView.setMosaicResource(mosaicResMap);
            }
            mMosaicView.setMosaicEffect(MosaicUtil.Effect.FLOWER);
            changeToolsSelector(MosaicUtil.Effect.FLOWER);
        } else if (i == R.id.paint_revoke) {
            mMosaicView.undo();
        } else {
        }
    }

    private boolean hasMosaicRes(MosaicUtil.Effect effect) {
        if (mosaicResMap.containsKey(effect)) {
            Bitmap bitmap = mosaicResMap.get(effect);
            if (bitmap != null) {
                return true;
            }
        }
        return false;
    }

    private void changeToolsSelector(MosaicUtil.Effect effect) {
        View v = null;
        switch (effect) {
            case MOSAIC:
                v = action_base;
                break;
            case BLUR:
                v = action_ground_glass;
                break;
            case FLOWER:
                v = action_flower;

        }
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

        mMosaicView.setMosaicBackgroundResource(activity.mainBitmap);
        Bitmap bit = MosaicUtil.getMosaic(activity.mainBitmap);
        Bitmap blur = MosaicUtil.getBlur(activity.mainBitmap);

        mosaicResMap = new HashMap<>();
        mosaicResMap.put(MosaicUtil.Effect.MOSAIC, bit);
        mosaicResMap.put(MosaicUtil.Effect.BLUR, blur);
        mMosaicView.setMosaicResource(mosaicResMap);

        mMosaicView.setMosaicBrushWidth(30);

        MosaicUtil.Effect mosaicEffect = mMosaicView.getMosaicEffect();
        //默认选中基础模式
        changeToolsSelector(mosaicEffect);

    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
        activity.mainImage.setVisibility(View.VISIBLE);
        mMosaicView.setIsOperation(false);
    }


    public void onShow() {
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
    }
}
