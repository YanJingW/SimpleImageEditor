package com.yjing.imageeditlibrary.editimage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.fragment.StirckerFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyanjing on 2018/2/27.
 */

public class StrickerAdapter extends BaseAdapter {

    public DisplayImageOptions imageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).showImageOnLoading(R.drawable.yd_image_tx)
            .build();// 下载图片显示

    private final StirckerFragment mStirckerFragment;
    private List<String> pathList = new ArrayList<String>();// 图片路径列表

    public static final String[] stickerPath = {"stickers/type1", "stickers/type2", "stickers/type3", "stickers/type4", "stickers/type5", "stickers/type6"};

    public StrickerAdapter(StirckerFragment fragment) {
        super();
        this.mStirckerFragment = fragment;
        for (String path : stickerPath) {
            addStickerImages(path);
        }
    }

    @Override
    public int getCount() {
        return pathList.size();
    }

    @Override
    public Object getItem(int i) {
        return pathList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = null;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.view_sticker_item, viewGroup, false);
        ImageHolder imageHoler = new ImageHolder(v);

        String path = pathList.get(i);
        ImageLoader.getInstance().displayImage("assets://" + path,
                imageHoler.image, imageOption);
        v.setTag(path);

        imageHoler.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStirckerFragment.selectedStickerItem((String) view.getTag());
            }
        });
        return v;
    }


    public class ImageHolder {
        public ImageView image;

        public ImageHolder(View itemView) {
            this.image = (ImageView) itemView.findViewById(R.id.img);
        }
    }// end inner class

    public void addStickerImages(String folderPath) {
        pathList.clear();
        try {
            String[] files = mStirckerFragment.getActivity().getAssets()
                    .list(folderPath);
            for (String name : files) {
                pathList.add(folderPath + File.separator + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.notifyDataSetChanged();
    }
}
