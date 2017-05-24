package com.yjing.imageeditlibrary.editimage.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjing.imageeditlibrary.BaseActivity;
import com.yjing.imageeditlibrary.R;
import com.yjing.imageeditlibrary.editimage.EditImageActivity;
import com.yjing.imageeditlibrary.editimage.SaveMode;
import com.yjing.imageeditlibrary.editimage.model.RatioItem;
import com.yjing.imageeditlibrary.editimage.utils.Matrix3;
import com.yjing.imageeditlibrary.editimage.view.CropImageView;
import com.yjing.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片剪裁Fragment
 */
public class CropFragment extends BaseFragment implements ImageEditInte{
    public static final int INDEX = 5;
	public static final String TAG = CropFragment.class.getName();
	private View mainView;
	private View backToMenu;// 返回主菜单
	public CropImageView mCropPanel;// 剪裁操作面板
	private LinearLayout ratioList;
	private static List<RatioItem> dataList = new ArrayList<RatioItem>();
	static {
		// init data
		dataList.add(new RatioItem("任意", -1f));
		dataList.add(new RatioItem("1:1", 1f));
		dataList.add(new RatioItem("1:2", 1 / 2f));
		dataList.add(new RatioItem("1:3", 1 / 3f));
		dataList.add(new RatioItem("2:3", 2 / 3f));
		dataList.add(new RatioItem("3:4", 3 / 4f));
		dataList.add(new RatioItem("2:1", 2f));
		dataList.add(new RatioItem("3:1", 3f));
		dataList.add(new RatioItem("3:2", 3 / 2f));
		dataList.add(new RatioItem("4:3", 4 / 3f));
	}
	private List<TextView> textViewList = new ArrayList<TextView>();

	public static int SELECTED_COLOR = Color.YELLOW;
	public static int UNSELECTED_COLOR = Color.WHITE;
	private CropRationClick mCropRationClick = new CropRationClick();
	public TextView selctedTextView;
	private View back_btn;
	private View save_btn;

	public static CropFragment newInstance(EditImageActivity activity) {
		CropFragment fragment = new CropFragment();
		fragment.activity = activity;
		fragment.mCropPanel = activity.mCropPanel;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
		backToMenu = mainView.findViewById(R.id.back_to_main);
		back_btn = mainView.findViewById(R.id.back_btn);
		save_btn = mainView.findViewById(R.id.save_btn);
		ratioList = (LinearLayout) mainView.findViewById(R.id.ratio_list_group);
		setUpRatioList();
		return mainView;
	}

	private void setUpRatioList() {
		// init UI
		ratioList.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.leftMargin = 20;
		params.rightMargin = 20;
		for (int i = 0, len = dataList.size(); i < len; i++) {
			TextView text = new TextView(activity);
			text.setTextColor(UNSELECTED_COLOR);
			text.setTextSize(20);
			text.setText(dataList.get(i).getText());
			textViewList.add(text);
			ratioList.addView(text, params);
			text.setTag(i);
			if (i == 0) {
				selctedTextView = text;
			}
			dataList.get(i).setIndex(i);
			text.setTag(dataList.get(i));
			text.setOnClickListener(mCropRationClick);
		}// end for i
		selctedTextView.setTextColor(SELECTED_COLOR);
	}

	/**
	 * 保存剪切图片
	 */
	@Override
	public void appleEdit(SaveCompletedInte inte) {
		// System.out.println("保存剪切图片");
		CropImageTask task = new CropImageTask();
		task.execute(activity.mainBitmap);
	}

	@Override
	public void onShow() {
		//在剪切之前必须将之前的操作进行保存
		EditImageActivity.SaveBtnClick saveBtnClick = activity.new SaveBtnClick(false, new SaveCompletedInte() {
			@Override
			public void completed() {
				activity.fl_main_menu.setVisibility(View.GONE);
				activity.banner.setVisibility(View.GONE);

				mCropPanel.setVisibility(View.VISIBLE);
				mCropPanel.setIsOperation(true);
				activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
				activity.mainImage.setScaleEnabled(false);// 禁用缩放
				//
				RectF r = activity.mainImage.getBitmapRect();
				activity.mCropPanel.setCropRect(r);
			}
		});
		saveBtnClick.onClick(null);
	}

	@Override
	public void method2() {

	}

	@Override
	public void method3() {

	}

	/**
	 * 选择剪裁比率
	 * 
	 * @author
	 * 
	 */
	private final class CropRationClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			TextView curTextView = (TextView) v;
			selctedTextView.setTextColor(UNSELECTED_COLOR);
			RatioItem dataItem = (RatioItem) v.getTag();
			selctedTextView = curTextView;
			selctedTextView.setTextColor(SELECTED_COLOR);

			mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(),
					dataItem.getRatio());
			// System.out.println("dataItem   " + dataItem.getText());
		}
	}// end inner class

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
		back_btn.setOnClickListener(new BackToMenuClick());// 返回主菜单
		save_btn.setOnClickListener(new SaveToMenuClick());// 返回主菜单
	}

	/**
	 * 返回按钮逻辑
	 * 
	 * @author panyi
	 * 
	 */
	private final class BackToMenuClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			activity.backToMain();
		}
	}// end class
	/**
	 * 保存按钮逻辑
	 *
	 * @author panyi
	 *
	 */
	private final class SaveToMenuClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			appleEdit(null);
			activity.backToMain();
		}
	}// end class

	/**
	 * 返回主菜单
	 */
	public void backToMain() {
//		appleEdit(null);

		activity.fl_main_menu.setVisibility(View.VISIBLE);
		activity.banner.setVisibility(View.VISIBLE);

		mCropPanel.setVisibility(View.GONE);
		mCropPanel.setIsOperation(false);
		activity.mainImage.setScaleEnabled(true);// 恢复缩放功能
		if (selctedTextView != null) {
			selctedTextView.setTextColor(UNSELECTED_COLOR);
		}
		mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(), -1);
	}


	/**
	 * 图片剪裁生成 异步任务
	 * 
	 * @author panyi
	 * 
	 */
	private final class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private Dialog dialog;

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dialog.dismiss();
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void onCancelled(Bitmap result) {
			super.onCancelled(result);
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
					false);
			dialog.show();
		}

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			RectF cropRect = mCropPanel.getCropRect();// 剪切区域矩形
			Matrix touchMatrix = activity.mainImage.getImageViewMatrix();
			// Canvas canvas = new Canvas(resultBit);
			float[] data = new float[9];
			touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
			Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
			Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
			Matrix m = new Matrix();
			m.setValues(inverseMatrix.getValues());
			m.mapRect(cropRect);// 变化剪切矩形

			// Paint paint = new Paint();
			// paint.setColor(Color.RED);
			// paint.setStrokeWidth(10);
			// canvas.drawRect(cropRect, paint);
			// Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
			// Bitmap.Config.ARGB_8888, true);
			Bitmap resultBit = Bitmap.createBitmap(params[0],
					(int) cropRect.left, (int) cropRect.top,
					(int) cropRect.width(), (int) cropRect.height());

			//saveBitmap(resultBit, activity.saveFilePath);
			return resultBit;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result == null)
				return;

//			if (activity.mainBitmap != null
//					&& !activity.mainBitmap.isRecycled()) {
//				activity.mainBitmap.recycle();
//			}
//			activity.mainBitmap = result;
//			activity.mainImage.setImageBitmap(activity.mainBitmap);
//			activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            activity.changeMainBitmap(result);
			activity.mCropPanel.setCropRect(activity.mainImage.getBitmapRect());
			activity.backToMain();
		}
	}// end inner class

	/**
	 * 保存Bitmap图片到指定文件
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("保存文件--->" + f.getAbsolutePath());
	}
}// end class
