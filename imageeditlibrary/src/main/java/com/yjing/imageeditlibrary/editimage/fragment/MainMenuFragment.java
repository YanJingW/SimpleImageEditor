package com.yjing.imageeditlibrary.editimage.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.SaveMode;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * 工具栏主菜单
 */
public class MainMenuFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = MainMenuFragment.class.getName();
    private View mainView;
    private EditImageActivity activity;

    private View stickerBtn;// 贴图按钮
    //    private View fliterBtn;// 滤镜按钮
    private View cropBtn;// 剪裁按钮
    //    private View rotateBtn;// 旋转按钮
    private View mTextBtn;//文字型贴图添加
    private View mPaintBtn;//编辑按钮
    private View mosaicBtn;//马赛克按钮
    private View preToolButton;

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
        SaveMode.EditMode clickMode = getEditMode(v);//当前点击后将要处于的模式，默认为NONE

        if (clickMode == SaveMode.EditMode.STICKERS || clickMode == SaveMode.EditMode.CROP) {
            Toast.makeText(getActivity(), "未开通", Toast.LENGTH_SHORT).show();
            return;
        }

        //更改buttom状态
        changeToolsSelector(v);

        //如果点击前和点击后处于一种模式，则隐藏当前编辑模式（设置当前模式为NONE）
        if (preMode == clickMode) {
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

    private SaveMode.EditMode getEditMode(View v) {
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

    private void changeToolsSelector(View v) {

        if (preToolButton != null) {
            preToolButton.setSelected(false);
        }
        //更新按钮状态
        View toolsButton = ((ViewGroup) v).getChildAt(0);
        //如果点击前和点击后处于一种模式，则隐藏当前编辑模式（设置当前模式为NONE）
        if (preToolButton == toolsButton) {
            toolsButton.setSelected(false);
            preToolButton = null;
            activity.backToMain();
        } else {
            toolsButton.setSelected(true);
            preToolButton = toolsButton;
        }
    }
}// end class
