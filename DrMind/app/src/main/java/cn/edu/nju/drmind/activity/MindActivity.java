package cn.edu.nju.drmind.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import cn.edu.cn.R;
import cn.edu.nju.drmind.FAB.FloatingActionButton;
import cn.edu.nju.drmind.FAB.FloatingActionMenu;
import cn.edu.nju.drmind.FAB.SubActionButton;
import cn.edu.nju.drmind.drawer.ContentAdapter;
import cn.edu.nju.drmind.drawer.ContentModel;
import cn.edu.nju.drmind.swipemenulistview.SimpleActivity;
import cn.edu.nju.drmind.util.Constant;
import cn.edu.nju.drmind.view.DEditTextView;
import cn.edu.nju.drmind.view.DViewGroup;
import cn.edu.nju.drmind.voice.VoiceToWord;


public class MindActivity extends Activity {
	public static MindActivity a;
	private AlarmManager alarmManager;
	private PendingIntent pi;

	// new drawerlayout
	private DrawerLayout drawerLayout;
	private RelativeLayout leftLayout;
	private RelativeLayout rightLayout;
	private List<ContentModel> list;
	private ContentAdapter adapter;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// ①获取AlarmManager对象:
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// 指定要启动的是Activity组件,通过PendingIntent调用getActivity来设置
		Intent intent = new Intent(MindActivity.this, ClockActivity.class);
		pi = PendingIntent.getActivity(MindActivity.this, 0, intent, 0);

		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 设置总布局
		setContentView(R.layout.main);

		// 大小的初始化
		init();

		// 右侧FAB的初始化
		initRightButton();

		// 左侧DrawerLayout的初始化
		initMyDrawer();

		// 跳转时的过滤，需修改
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			String state = bundle.getString("state");
			if (state != null && (state.equals("open") || state.equals("save"))) {
				String name = bundle.getString("name");
				DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
				group.load(name);
			}

		}

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	// 左侧drawerlayout的初始化
	private void initMyDrawer() {

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		leftLayout = (RelativeLayout) findViewById(R.id.left);
		rightLayout = (RelativeLayout) findViewById(R.id.right);
		ListView listView = (ListView) leftLayout
				.findViewById(R.id.left_listview);
		initData();
		adapter = new ContentAdapter(this, list);
		listView.setAdapter(adapter);
		initDrawerListener();

	}

	// 左侧listview里的数据
	private void initData() {
		list = new ArrayList<ContentModel>();
		list.add(new ContentModel(R.drawable.mulu, "目录"));
		list.add(new ContentModel(R.drawable.xinjian, "新建"));
		list.add(new ContentModel(R.drawable.save, "保存"));
		list.add(new ContentModel(R.drawable.daochu, "导出"));
		list.add(new ContentModel(R.drawable.clock, "提醒"));
	}

	// 左侧ListView里每一个item的监听
	private void initDrawerListener() {
		ListView listView = (ListView) leftLayout
				.findViewById(R.id.left_listview);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String choose = list.get(position).getText();

				if (choose.equals("目录")) {
					startActivity(new Intent(MindActivity.this,
							SimpleActivity.class));
				} else if (choose.equals("新建")) {
					startActivity(new Intent(MindActivity.this,
							MindActivity.class));
					Toast.makeText(getApplicationContext(), "新建图表成功",
							Toast.LENGTH_LONG).show();
				} else if (choose.equals("保存")) {
					final EditText editText = new EditText(MindActivity.this);
					DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
					if (group.isOpenSaved()) {
						editText.setText(group.getCurretFileName());
					}

					new AlertDialog.Builder(MindActivity.this).setTitle("请输入保存的图表名")
							.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									String name = editText.getText().toString();
									DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
									if (name.equals("")) {
										Toast.makeText(getApplicationContext(), "图表名不能为空哟！" + name, Toast.LENGTH_LONG)
												.show();
										return;
									}
									if (group.existPaint(name)) {
										Toast.makeText(getApplicationContext(), "图表 " + name + "已存在！", Toast.LENGTH_LONG)
												.show();
										return;
									} else {
										System.out.println("保存的图名： " + name);

										group.save(name);

										Intent intent = new Intent(MindActivity.this, MindActivity.class);
										Bundle bundle = new Bundle();
										bundle.putString("state", "save");
										bundle.putString("name", name);
										intent.putExtras(bundle);
										startActivity(intent);

										Toast.makeText(getApplicationContext(), "图表" + name + "保存成功~", Toast.LENGTH_LONG)
												.show();
									}
								}
							}).setNegativeButton("取消", null).show();

				} else if (choose.equals("导出")) {
//					View myview = findViewById(R.id.viewgroup);
//					if (myview != null) {
//						ViewToPicture viewToPic = new ViewToPicture();
//						viewToPic.save(myview, "Liu");
//					} else {
//						System.out.println("myview null");
//					}
					final EditText editText = new EditText(MindActivity.this);
					DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);

					new AlertDialog.Builder(MindActivity.this).setTitle("请输入导出的图片名")
							.setIcon(android.R.drawable.ic_dialog_info).setView(editText)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									String name = editText.getText().toString();
									DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
									if (name.equals("")) {
										Toast.makeText(getApplicationContext(), "图表名不能为空哟！" + name, Toast.LENGTH_LONG)
												.show();
										return;
									} else {
										System.out.println("导出的图片名： " + name);


										//viewGroup.setDrawingCacheEnabled(false);

										try {
											if (group.exportPicture(name)) {
												System.out.println("daochu success !");
											} else {
												Toast.makeText(getApplicationContext(), "图片 " + name + "已存在！", Toast.LENGTH_LONG)
														.show();
											}
										} catch (Exception e1) {
											e1.printStackTrace();
										}

										Toast.makeText(getApplicationContext(), "图片" + name + "导出成功~", Toast.LENGTH_LONG)
												.show();
									}
								}
							}).setNegativeButton("取消", null).show();


				} else if (choose.equals("提醒")) {
					Calendar currentTime = Calendar.getInstance();
					// 弹出一个时间设置的对话框,供用户选择时间
					new TimePickerDialog(MindActivity.this, 0,
							new OnTimeSetListener() {
								public void onTimeSet(TimePicker view,
													  int hourOfDay, int minute) {
									// 设置当前时间
									Calendar c = Calendar.getInstance();
									c.setTimeInMillis(System
											.currentTimeMillis());
									// 根据用户选择的时间来设置Calendar对象
									c.set(Calendar.HOUR, hourOfDay);
									c.set(Calendar.MINUTE, minute);
									// ②设置AlarmManager在Calendar对应的时间启动Activity
									alarmManager.set(AlarmManager.RTC_WAKEUP,
											c.getTimeInMillis(), pi);
									// 提示闹钟设置完毕:
									Toast.makeText(MindActivity.this,
											"闹钟设置完毕~", Toast.LENGTH_SHORT)
											.show();
								}
							}, currentTime.get(Calendar.HOUR_OF_DAY),
							currentTime.get(Calendar.MINUTE), false).show();
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	// 右侧的FAB按钮
	private void initRightButton() {
		// 中心图标
		ImageView icon = new ImageView(this); // Create an icon
		icon.setImageDrawable(this.getResources().getDrawable(R.drawable.fab));
		FloatingActionButton actionButton = new FloatingActionButton.Builder(
				this).setContentView(icon).build();

		// 分散式图标
		// 语音功能
		SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
		ImageView itemIcon1 = new ImageView(this);
		itemIcon1.setImageDrawable(this.getResources().getDrawable(
				R.drawable.voice));
		SubActionButton button1 = itemBuilder.setContentView(itemIcon1).build();
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 别人的讯飞账户，我的待审核
				VoiceToWord voice = new VoiceToWord(MindActivity.this,
						"534e3fe2", (DViewGroup) findViewById(R.id.viewgroup));
				voice.GetWordFromVoice();
			}
		});

		// 删除节点
		ImageView itemIcon2 = new ImageView(this);
		itemIcon2.setImageDrawable(this.getResources().getDrawable(
				R.drawable.delete));
		SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();
		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				new AlertDialog.Builder(MindActivity.this)
						.setTitle("您选择删除：")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("全部删除",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {
										DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
										group.deleteNode();
									}
								})
						.setNeutralButton("仅删除此节点",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {
										DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
										group.deleteMerge();
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		// 增加节点
		ImageView itemIcon3 = new ImageView(this);
		itemIcon3.setImageDrawable(this.getResources().getDrawable(
				R.drawable.plus));
		SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();
		button3.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
				group.insertNode();
			}
		});

		// 收缩节点
		ImageView itemIcon4 = new ImageView(this);
		itemIcon4.setImageDrawable(this.getResources().getDrawable(
				R.drawable.minus));
		SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();
		button4.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
				group.hideOrShow();
			}
		});


		// 整合在一起
		 FloatingActionMenu actionMenu =
		new FloatingActionMenu.Builder(this).addSubActionView(button1)
				.addSubActionView(button2)
				.addSubActionView(button3)
				.addSubActionView(button4)
				.attachTo(actionButton).build();

	}

	@Override
	protected void onStart() {
		super.onStart();
//		// ATTENTION: This was auto-generated to implement the App Indexing API.
//		// See https://g.co/AppIndexing/AndroidStudio for more information.
//		client.connect();
//		a = this;
//		// ATTENTION: This was auto-generated to implement the App Indexing API.
//		// See https://g.co/AppIndexing/AndroidStudio for more information.
//		Action viewAction = Action.newAction(
//				Action.TYPE_VIEW, // TODO: choose an action type.
//				"Mind Page", // TODO: Define a title for the content shown.
//				// TODO: If you have web page content that matches this app activity's content,
//				// make sure this auto-generated web page URL is correct.
//				// Otherwise, set the URL to null.
//				Uri.parse("http://host/path"),
//				// TODO: Make sure this auto-generated app URL is correct.
//				Uri.parse("android-app://cn.edu.nju.drmind.activity/http/host/path")
//		);
//		AppIndex.AppIndexApi.start(client, viewAction);
	}

	/**
	 * 初始化：设定viewGroup大小为3*3倍屏幕大小
	 */
	@SuppressWarnings("deprecation")
	private void init() {
		WindowManager wm = this.getWindowManager();

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		// 设定常量
		Constant.setScreenHeight(height);
		Constant.setScreenWidth(width);
		DViewGroup dView = (DViewGroup) findViewById(R.id.viewgroup);
		LayoutParams lay = (LayoutParams) findViewById(
				R.id.viewgroup).getLayoutParams();

		lay.height = 3 * height;
		lay.width = 3 * width;

		dView.setScreenWidth(width);
		dView.setScreenHeight(height);
		dView.bringToFront();
		findViewById(R.id.viewgroup).setX(0);
		findViewById(R.id.viewgroup).setY(-height);
		findViewById(R.id.viewgroup).setLayoutParams(lay);

	}

	@Override
	protected void onResume() {
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

	// 获取点击事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isHideInput(view, ev)) {
				HideSoftInput(view.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	// 判定是否需要隐藏
	private boolean isHideInput(View v, MotionEvent ev) {
		if (v != null && (v instanceof DEditTextView)) {
			int[] l = {0, 0};
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (ev.getX() > left && ev.getX() < right && ev.getY() > top
					&& ev.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	// 隐藏软键盘
	private void HideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Mind Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://cn.edu.nju.drmind.activity/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}