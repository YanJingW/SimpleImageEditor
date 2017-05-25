package com.yjing.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.inter.ImageEditInte;
import com.yjing.imageeditlibrary.editimage.inter.SaveCompletedInte;
import com.yjing.imageeditlibrary.editimage.view.RotateImageView;
import com.yjing.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 图片旋转Fragment
 */
public class RotateFragment extends BaseFragment implements ImageEditInte {
    private View mainView;
    private View backToMenu;// 返回主菜单
    public SeekBar mSeekBar;// 角度设定
    private RotateImageView mRotatePanel;// 旋转效果展示控件

    public static RotateFragment newInstance(EditImageActivity activity) {
        RotateFragment fragment = new RotateFragment();
        fragment.activity = activity;
        fragment.mRotatePanel = activity.mRotatePanel;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_rotate, null);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        mSeekBar = (SeekBar) mainView.findViewById(R.id.rotate_bar);
        mSeekBar.setProgress(0);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
        mSeekBar.setOnSeekBarChangeListener(new RotateAngleChange());
    }

    /**
     * 保存旋转图片
     */
    @Override
    public void appleEdit(SaveCompletedInte inte) {
        if (mSeekBar.getProgress() == 0 || mSeekBar.getProgress() == 360) {// 没有做旋转
            activity.backToMain();
            if (inte != null) {
                inte.completed();
            }
            return;
        } else {// 保存图片
            SaveRotateImageTask task = new SaveRotateImageTask();
            task.execute(activity.mainBitmap);
        }
    }

    @Override
    public void onShow() {
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        mRotatePanel.addBit(activity.mainBitmap, activity.mainImage.getBitmapRect());
        if (mSeekBar != null) {
            mSeekBar.setProgress(0);
        }
        mRotatePanel.reset();
//        mRotatePanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void method2() {

    }

    @Override
    public void method3() {

    }

    /**
     * 角度改变监听
     */
    private final class RotateAngleChange implements OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int angle,
                                      boolean fromUser) {
            mRotatePanel.rotateImage(angle);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 返回按钮逻辑
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            activity.backToMain();
        }
    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
//        appleEdit(null);
        activity.mainImage.setVisibility(View.VISIBLE);
//        this.mRotatePanel.setVisibility(View.GONE);
    }


    /**
     * 保存图片线程
     */
    private final class SaveRotateImageTask extends
            AsyncTask<Bitmap, Void, Bitmap> {
        //private Dialog dialog;

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //dialog.dismiss();
        }

        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            //dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
            //        false);
            //dialog.show();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            RectF imageRect = mRotatePanel.getImageNewRect();
            Bitmap originBit = params[0];
            Bitmap result = Bitmap.createBitmap((int) imageRect.width(),
                    (int) imageRect.height(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(result);
            int w = originBit.getWidth() >> 1;
            int h = originBit.getHeight() >> 1;
            float centerX = imageRect.width() / 2;
            float centerY = imageRect.height() / 2;

            float left = centerX - w;
            float top = centerY - h;

            RectF dst = new RectF(left, top, left + originBit.getWidth(), top
                    + originBit.getHeight());
            canvas.save();
            canvas.scale(mRotatePanel.getScale(), mRotatePanel.getScale(),
                    imageRect.width() / 2, imageRect.height() / 2);
            canvas.rotate(mRotatePanel.getRotateAngle(), imageRect.width() / 2,
                    imageRect.height() / 2);

            canvas.drawBitmap(originBit, new Rect(0, 0, originBit.getWidth(),
                    originBit.getHeight()), dst, null);
            canvas.restore();

            //saveBitmap(result, activity.saveFilePath);// 保存图片
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //dialog.dismiss();
            if (result == null)
                return;

            // 切换新底图
            activity.changeMainBitmap(result);
            activity.backToMain();
        }
    }

    /**
     * 保存Bitmap图片到指定文件
     *
     * @param bm
     */
    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
