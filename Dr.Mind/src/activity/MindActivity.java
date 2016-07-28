package activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import swipemenulistview.SimpleActivity;
import ui.ViewToPicture;
import util.Constant;
import view.DEditTextView;
import view.DViewGroup;
import voice.VoiceToWord;
import FAB.FloatingActionButton;
import FAB.FloatingActionMenu;
import FAB.SubActionButton;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import cn.edu.cn.R;

public class MindActivity extends Activity {
	public static MindActivity a;
	private AlarmManager alarmManager;
	private PendingIntent pi;
	private MenuDrawer mDrawer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT);
		mDrawer.setContentView(R.layout.main);

		// ①获取AlarmManager对象:
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// 指定要启动的是Activity组件,通过PendingIntent调用getActivity来设置
		Intent intent = new Intent(MindActivity.this, ClockActivity.class);
		pi = PendingIntent.getActivity(MindActivity.this, 0, intent, 0);

		// 全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// setContentView(R.layout.main);
		init();
		initButton();

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			String state = bundle.getString("state");
			if (state != null && state.equals("open")) {
				String name = bundle.getString("name");
				DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
				group.load(name);
			}
		}

	}

	@SuppressWarnings("deprecation")
	private void initButton() {
		// 中心图标
		ImageView icon = new ImageView(this); // Create an icon
		icon.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_add));
		FloatingActionButton actionButton = new FloatingActionButton.Builder(this).setContentView(icon).build();

		// 分散式图标
		SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
		ImageView itemIcon1 = new ImageView(this);
		itemIcon1.setImageDrawable(this.getResources().getDrawable(R.drawable.voice));
		SubActionButton button1 = itemBuilder.setContentView(itemIcon1).build();
		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 别人的讯飞账户，我的待审核
				VoiceToWord voice = new VoiceToWord(MindActivity.this, "534e3fe2",
						(DViewGroup) findViewById(R.id.viewgroup));
				voice.GetWordFromVoice();
			}
		});

		ImageView itemIcon2 = new ImageView(this);
		itemIcon2.setImageDrawable(this.getResources().getDrawable(R.drawable.delete));
		SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();
		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				new AlertDialog.Builder(MindActivity.this).setTitle("您选择删除：").setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("当前结点", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
								group.deleteNode();
							}
						}).setNeutralButton("当前及后代", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
								group.deleteNode();
							}
						}).setNegativeButton("取消", null).show();
			}
		});

		ImageView itemIcon3 = new ImageView(this);
		itemIcon3.setImageDrawable(this.getResources().getDrawable(R.drawable.plus));
		SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();
		button3.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				DViewGroup group = (DViewGroup) findViewById(R.id.viewgroup);
				group.insertNode();
			}
		});

		ImageView itemIcon4 = new ImageView(this);
		itemIcon4.setImageDrawable(this.getResources().getDrawable(R.drawable.list));
		SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();
		button4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Calendar currentTime = Calendar.getInstance();
				// // 弹出一个时间设置的对话框,供用户选择时间
				// new TimePickerDialog(MindActivity.this, 0,
				// new OnTimeSetListener() {
				// public void onTimeSet(TimePicker view,
				// int hourOfDay, int minute) {
				// // 设置当前时间
				// Calendar c = Calendar.getInstance();
				// c.setTimeInMillis(System.currentTimeMillis());
				// // 根据用户选择的时间来设置Calendar对象
				// c.set(Calendar.HOUR, hourOfDay);
				// c.set(Calendar.MINUTE, minute);
				// // ②设置AlarmManager在Calendar对应的时间启动Activity
				// alarmManager.set(AlarmManager.RTC_WAKEUP,
				// c.getTimeInMillis(), pi);
				// // 提示闹钟设置完毕:
				// Toast.makeText(MindActivity.this, "闹钟设置完毕~",
				// Toast.LENGTH_SHORT).show();
				// }
				// }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime
				// .get(Calendar.MINUTE), false).show();

				startActivity(new Intent(MindActivity.this, SimpleActivity.class));
			}
		});

		ImageView itemIcon5 = new ImageView(this);
		itemIcon5.setImageDrawable(this.getResources().getDrawable(R.drawable.success2));
		SubActionButton button5 = itemBuilder.setContentView(itemIcon5).build();
		button5.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
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
								}
							}
						}).setNegativeButton("取消", null).show();

			}
		});

		ImageView itemIcon6 = new ImageView(this);
		itemIcon6.setImageDrawable(this.getResources().getDrawable(R.drawable.picture));
		SubActionButton button6 = itemBuilder.setContentView(itemIcon6).build();
		button6.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				View view = findViewById(R.id.viewgroup);
				if (view != null) {
					ViewToPicture viewToPic = new ViewToPicture();
					viewToPic.save(view, "Liu");
				} else {
					System.out.println("view null");
				}
			}
		});

		// 整合在一起
		FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this).addSubActionView(button1)
				.addSubActionView(button2).addSubActionView(button3).addSubActionView(button4).addSubActionView(button5)
				.addSubActionView(button6).attachTo(actionButton).build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		a = this;
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
		LinearLayout.LayoutParams lay = (LayoutParams) findViewById(R.id.viewgroup).getLayoutParams();

		lay.height = 3 * height;
		lay.width = 3 * width;

		dView.setScreenWidth(width);
		dView.setScreenHeight(height);
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
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
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
			manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}