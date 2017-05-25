package com.yjing.imageeditlibrary.editimage.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.contorl.SaveMode;
import com.yjing.imageeditlibrary.editimage.inter.ImageEditInte;

/**
 * 工具栏主菜单
 */
public class MainMenuFragment extends Fragment implements View.OnClickListener {
    private View mainView;
    private EditImageActivity activity;
    private View stickerBtn;// 贴图按钮
    //    private View fliterBtn;// 滤镜按钮
    private View cropBtn;// 剪裁按钮
    //    private View rotateBtn;// 旋转按钮
    private View mTextBtn;//文字型贴图添加
    private View mPaintBtn;//编辑按钮
    private View mosaicBtn;//马赛克按钮

    public static MainMenuFragment newInstance(EditImageActivity activity) {
        MainMenuFragment fragment = new MainMenuFragment();
        fragment.activity = activity;
        SaveMode.getInstant().setMode(SaveMode.EditMode.NONE);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu,
                null);
        stickerBtn = mainView.findViewById(R.id.btn_stickers);
//        fliterBtn = mainView.findViewById(R.id.btn_fliter);
        cropBtn = mainView.findViewById(R.id.btn_crop);
//        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        mTextBtn = mainView.findViewById(R.id.btn_text);
        mPaintBtn = mainView.findViewById(R.id.btn_paint);
        mosaicBtn = mainView.findViewById(R.id.btn_mosaic);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stickerBtn.setOnClickListener(this);
//        fliterBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
//        rotateBtn.setOnClickListener(this);
        mTextBtn.setOnClickListener(this);
        mPaintBtn.setOnClickListener(this);
        mosaicBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SaveMode.EditMode preMode = SaveMode.getInstant().getMode();//点击之前处于的编辑模式
        SaveMode.EditMode clickMode = getEditModeForView(v);
        //1.确定当前要处于模式
        if (preMode == clickMode) {//如果点击前和点击后处于一种模式，则隐藏当前编辑模式（设置当前模式为NONE）
            clickMode = SaveMode.EditMode.NONE;
        }
        //2.更改按钮状态
        if (preMode == SaveMode.EditMode.PAINT || preMode == SaveMode.EditMode.MOSAIC) {
            //设置上次选中的模式按钮状态为false
            getButtonForMode(preMode).setSelected(false);
        }

        if (clickMode == SaveMode.EditMode.PAINT || clickMode == SaveMode.EditMode.MOSAIC) {
            getButtonForMode(clickMode).setSelected(true);
        }

        //3.设置当前所处于的模式
        if (clickMode == SaveMode.EditMode.NONE) {
            activity.backToMain();
        } else {
            //0.退出之前模式
            ImageEditInte preEditMode = activity.editFactory.getCurrentMode();
            if (preEditMode != null) {
                preEditMode.backToMain();
            }
            //1.将当前模式保存到SaveMode
            SaveMode.getInstant().setMode(clickMode);
            //2.重新设置mainImage。（因为在模式发生变化时，mainBitmap可能会被更新）
            activity.mainImage.setImageBitmap(activity.mainBitmap);
            //3.设置当前模式改变之后要显示的fragment
            activity.editFactory.setCurrentEditMode(clickMode);
            //4.显示fragment时，需要进行初始化
            activity.editFactory.getCurrentMode().onShow();
            //5.保存应用图标的更改
//            activity.bannerFlipper.showNext();
        }
    }

    private View getButtonForMode(SaveMode.EditMode mode) {
        View v = null;
        switch (mode) {
            case PAINT:
                v = mPaintBtn;
                break;
            case MOSAIC:
                v = mosaicBtn;
                break;
            case STICKERS:
                v = stickerBtn;
                break;
            case CROP:
                v = cropBtn;
                break;
            case TEXT:
                v = mTextBtn;
                break;
        }
        return v;
    }

    private SaveMode.EditMode getEditModeForView(View v) {
        SaveMode.EditMode clickMode = SaveMode.EditMode.NONE;
        if (v == stickerBtn) {
            clickMode = SaveMode.EditMode.STICKERS;
//        } else if (v == fliterBtn) {
//            clickMode = SaveMode.EditMode.FILTER;
        } else if (v == cropBtn) {
            clickMode = SaveMode.EditMode.CROP;
//        } else if (v == rotateBtn) {
//            clickMode = SaveMode.EditMode.ROTATE;
        } else if (v == mTextBtn) {
            clickMode = SaveMode.EditMode.TEXT;
        } else if (v == mPaintBtn) {
            clickMode = SaveMode.EditMode.PAINT;
        } else if (v == mosaicBtn) {
            clickMode = SaveMode.EditMode.MOSAIC;
        }
        return clickMode;
    }
}
