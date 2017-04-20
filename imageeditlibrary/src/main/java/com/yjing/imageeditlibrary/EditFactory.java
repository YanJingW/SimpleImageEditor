package com.yjing.imageeditlibrary;

import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.SaveMode;
import com.yjing.imageeditlibrary.editimage.fragment.AddTextFragment;
import com.yjing.imageeditlibrary.editimage.fragment.CropFragment;
import com.yjing.imageeditlibrary.editimage.fragment.FliterListFragment;
import com.yjing.imageeditlibrary.editimage.fragment.ImageEditInte;
import com.yjing.imageeditlibrary.editimage.fragment.MainMenuFragment;
import com.yjing.imageeditlibrary.editimage.fragment.MosaicFragment;
import com.yjing.imageeditlibrary.editimage.fragment.PaintFragment;
import com.yjing.imageeditlibrary.editimage.fragment.RotateFragment;
import com.yjing.imageeditlibrary.editimage.fragment.StirckerFragment;

import java.util.List;

/**
 * Created by wangyanjing on 2017/4/13.
 */

public class EditFactory {

    private final View bottomView;
    private final View aboveView;
    public StirckerFragment mStirckerFragment;// 贴图Fragment
    public FliterListFragment mFliterListFragment;// 滤镜FliterListFragment
    public CropFragment mCropFragment;// 图片剪裁Fragment
    public RotateFragment mRotateFragment;// 图片旋转Fragment
    public AddTextFragment mAddTextFragment;//图片添加文字
    public PaintFragment mPaintFragment;//绘制模式Fragment
    public MosaicFragment mMosaicFragment;//马赛克模式Fragment

    private final FragmentManager supportFragmentManager;
    private SaveMode.EditMode currentMode = SaveMode.EditMode.NONE;

    public EditFactory(EditImageActivity activity, View bottomView, View aboveView) {
        supportFragmentManager = activity.getSupportFragmentManager();
        this.bottomView = bottomView;
        this.aboveView = aboveView;

        mStirckerFragment = StirckerFragment.newInstance(activity);
        mFliterListFragment = FliterListFragment.newInstance(activity);
        mCropFragment = CropFragment.newInstance(activity);
        mRotateFragment = RotateFragment.newInstance(activity);
        mAddTextFragment = AddTextFragment.newInstance(activity);
        mPaintFragment = PaintFragment.newInstance(activity);
        mMosaicFragment = MosaicFragment.newInstance(activity);

        supportFragmentManager.beginTransaction()
                .add(bottomView.getId(), mAddTextFragment).hide(mAddTextFragment)
                .add(bottomView.getId(), mStirckerFragment).hide(mStirckerFragment)
                .add(aboveView.getId(), mFliterListFragment).hide(mFliterListFragment)
                .add(aboveView.getId(), mCropFragment).hide(mCropFragment)
                .add(aboveView.getId(), mRotateFragment).hide(mRotateFragment)
                .add(aboveView.getId(), mPaintFragment).hide(mPaintFragment)
                .add(aboveView.getId(), mMosaicFragment).hide(mMosaicFragment).commit();

    }

    public void setCurrentEditMode(SaveMode.EditMode mode) {
        currentMode = mode;
        Fragment x = getFragment(currentMode);
        if (x == null) {
            aboveView.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
            return;
        }
        hideFragment(x);
        if (x instanceof AddTextFragment || x instanceof StirckerFragment) {
            bottomView.setVisibility(View.VISIBLE);
            aboveView.setVisibility(View.GONE);
            supportFragmentManager.beginTransaction().show(x).commit();
        } else {
            aboveView.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.GONE);
            supportFragmentManager.beginTransaction().show(x).commit();
        }
    }

    //隐藏所有fragment
    private void hideFragment(Fragment x) {
        List<Fragment> fragments = supportFragmentManager.getFragments();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        for (Fragment fragment : fragments) {
            //条件解释：：：
            if ((x == null || fragment != x) && !fragment.isHidden() && !(fragment instanceof MainMenuFragment)) {
                fragmentTransaction.hide(fragment);
            }
        }
        fragmentTransaction.commit();
    }


    public ImageEditInte getCurrentMode() {
        return (ImageEditInte) getFragment(currentMode);
    }

    @Nullable
    private Fragment getFragment(SaveMode.EditMode mode) {
        switch (mode) {
            case STICKERS:// 贴图
                return mStirckerFragment;
            case FILTER:// 滤镜
                return mFliterListFragment;
            case CROP://剪裁
                return mCropFragment;
            case ROTATE://旋转
                return mRotateFragment;
            case TEXT://添加文字
                return mAddTextFragment;
            case PAINT:
                return mPaintFragment;//绘制
            case MOSAIC:
                return mMosaicFragment;//马赛克
            case NONE:
                break;
        }//end switch
        return null;
    }

    public void setContainerVisiable(Fragment fragment, int visiable) {
        if (fragment instanceof AddTextFragment || fragment instanceof StirckerFragment) {
            bottomView.setVisibility(visiable);
        } else {
            aboveView.setVisibility(visiable);
        }
    }
}
