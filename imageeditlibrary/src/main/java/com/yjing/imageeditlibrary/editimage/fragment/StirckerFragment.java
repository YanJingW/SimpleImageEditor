package com.yjing.imageeditlibrary.editimage.fragment;

import android.app.Dialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;

import com.yjing.imageeditlibrary.BaseActivity;
import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.StrickerAdapter;
import com.yjing.imageeditlibrary.editimage.inter.ImageEditInte;
import com.yjing.imageeditlibrary.editimage.inter.SaveCompletedInte;
import com.yjing.imageeditlibrary.editimage.model.StickerBean;
import com.yjing.imageeditlibrary.editimage.task.StickerTask;
import com.yjing.imageeditlibrary.editimage.view.StickerItem;
import com.yjing.imageeditlibrary.editimage.view.StickerView;
import com.yjing.imageeditlibrary.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 贴图分类fragment
 */
public class StirckerFragment extends BaseFragment implements ImageEditInte {

    private static final String SELECT_IMAGE_COMPLETED_RECEIVER_ACTION = "SELECT_IMAGE_COMPLETED_RECEIVER_ACTION";

    public static final String TAG = StirckerFragment.class.getName();
    public static final String STICKER_FOLDER = "stickers";
    private static final String SELECT_IMAGE_RECEIVER_ACTION = "SELECT_IMAGE_RECEIVER_ACTION";

    private View mainView;
    private StickerView mStickerView;// 贴图显示控件

    private LoadStickersTask mLoadStickersTask;
    private List<StickerBean> stickerBeanList = new ArrayList<StickerBean>();

    private SaveStickersTask mSaveTask;

    public static StirckerFragment newInstance(EditImageActivity activity) {
        StirckerFragment fragment = new StirckerFragment();
        fragment.activity = activity;
        fragment.mStickerView = activity.mStickerView;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(SELECT_IMAGE_COMPLETED_RECEIVER_ACTION);
//        activity.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String path = intent.getStringExtra("PATH");
//                Log.i("wangyanjing", "收到广播啦。SELECT_IMAGE_COMPLETED_RECEIVER_ACTION" + path);
//                if (path == null || path.trim().isEmpty()) {
//                    activity.backToMain();
//                    return;
//                }
//                //添加图片到
//                selectedStickerItem(path);
//            }
//        }, filter);
//        receiverMap.put(receiver, Constant.YES_INT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_sticker_type, null);
        GridView gv_stirck = (GridView) mainView.findViewById(R.id.gv_stirck);

        gv_stirck.setNumColumns(4);
        StrickerAdapter strickerAdapter = new StrickerAdapter(this);
        gv_stirck.setAdapter(strickerAdapter);

        View back = mainView.findViewById(R.id.back_btn);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mainView.setVisibility(View.GONE);
            }
        });
        return mainView;
    }

    //导入贴图数据
    private void loadStickersData() {
        if (mLoadStickersTask != null) {
            mLoadStickersTask.cancel(true);
        }
        mLoadStickersTask = new LoadStickersTask();
        mLoadStickersTask.execute(1);
    }

    /**
     * 保存贴图层 合成一张图片
     */
    @Override
    public void appleEdit(SaveCompletedInte inte) {
        // System.out.println("保存 合成图片");
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }
        mSaveTask = new SaveStickersTask((EditImageActivity) getActivity(), inte);
        mSaveTask.execute(activity.mainBitmap);
    }

    @Override
    public void onShow() {
//        mStickerView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.VISIBLE);
        mStickerView.setIsOperation(true);
        //发送广播启动选图acticity

//        Intent intent = new Intent();
//        intent.setAction(StirckerFragment.SELECT_IMAGE_RECEIVER_ACTION);
//        activity.sendBroadcast(intent);
    }

    @Override
    public void method2() {

    }

    @Override
    public void method3() {

    }


    /**
     * 导入贴图数据
     */
    private final class LoadStickersTask extends AsyncTask<Integer, Void, Void> {
        private Dialog loadDialog;

        public LoadStickersTask() {
            super();
            loadDialog = BaseActivity.getLoadingDialog(getActivity(), R.string.saving_image, false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            stickerBeanList.clear();
            AssetManager assetManager = getActivity().getAssets();
            try {
                String[] lists = assetManager.list(STICKER_FOLDER);
                for (String parentPath : lists) {

                }//end for each
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadDialog.dismiss();

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loadDialog.dismiss();
        }
    }//end inner class

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadStickersTask != null) {
            mLoadStickersTask.cancel(true);
        }
    }

    /**
     * 从Assert文件夹中读取位图数据
     *
     * @param fileName
     * @return
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 选择贴图加入到页面中
     *
     * @param path
     */
    public void selectedStickerItem(String path) {
        Bitmap image = FileUtils.getImageFromAssetsFile(this.getContext(), path);
//        Bitmap image = FileUtils.getBitmapForPath(path);

        mStickerView.addBitImage(image);
    }

    public StickerView getmStickerView() {
        return mStickerView;
    }

    public void setmStickerView(StickerView mStickerView) {
        this.mStickerView = mStickerView;
    }

    /**
     * 返回主菜单页面
     *
     * @author panyi
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            activity.backToMain();
        }
    }// end inner class

    public void backToMain() {
//        appleEdit(null);
//        mStickerView.setVisibility(View.GONE);
        activity.mainImage.setVisibility(View.VISIBLE);
        mStickerView.setIsOperation(false);

    }

    /**
     * 保存贴图任务
     *
     * @author panyi
     */
    private final class SaveStickersTask extends StickerTask {
        public SaveStickersTask(EditImageActivity activity, SaveCompletedInte inte) {
            super(activity, inte);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            LinkedHashMap<Integer, StickerItem> addItems = mStickerView.getBank();
            for (Integer id : addItems.keySet()) {
                StickerItem item = addItems.get(id);
                item.matrix.postConcat(m);// 乘以底部图片变化矩阵
                canvas.drawBitmap(item.bitmap, item.matrix, null);
            }// end for
        }

        @Override
        public void onPostResult(Bitmap result) {
            mStickerView.clear();
            activity.changeMainBitmap(result);
        }
    }// end inner class

}// end class
