package com.yjing.imageeditlibrary.editimage.fragment;


import android.support.v4.app.Fragment;

import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.SaveMode;

public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
        // Required empty public constructor
    }

    protected EditImageActivity activity;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        switch (SaveMode.getInstant().getMode()) {
            case PAINT:

        }
        if (hidden) {

        }
    }
}
