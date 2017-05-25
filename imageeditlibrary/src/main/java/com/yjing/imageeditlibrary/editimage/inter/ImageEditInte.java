package com.yjing.imageeditlibrary.editimage.inter;

/**
 * Created by wangyanjing on 2017/4/13.
 */

public interface ImageEditInte {
    void appleEdit(SaveCompletedInte inte);

    /**
     * 退出当前模式要保存的一些状态，或者改变
     */
    void backToMain();

    void onShow();

    void method2();

    void method3();
}
