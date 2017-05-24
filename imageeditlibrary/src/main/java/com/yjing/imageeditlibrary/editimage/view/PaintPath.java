package com.yjing.imageeditlibrary.editimage.view;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * 装饰设计模式
 * 一段路径 包含path对象和paint对象的一些信息
 * Created by wangyanjing on 2017/5/17.
 */

class PaintPath {


    private final Path path;
    private final Paint paint;

    public PaintPath(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public Paint getPaint() {
        return paint;
    }
}
