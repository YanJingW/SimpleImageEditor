package com.yjing.imageeditlibrary.editimage;


/**
 * Created by wangyanjing on 2017/4/13.
 */

public class SaveMode {

    private static SaveMode saveMode = new SaveMode();
    private EditMode currentMode = EditMode.NONE;


    public static SaveMode getInstant() {
        return saveMode;
    }

    public void setMode(EditMode mode) {
        this.currentMode = mode;

    }

    public EditMode getMode() {
        return this.currentMode;
    }

    /**
     * Created by wangyanjing on 2017/4/13.
     */

    public enum EditMode {
        NONE, //未设置模式
        STICKERS,// 贴图模式
        FILTER,// 滤镜模式
        CROP,// 剪裁模式
        ROTATE,// 旋转模式
        TEXT,// 文字模式
        PAINT,//绘制模式
        MOSAIC//马赛克模式
    }

}
