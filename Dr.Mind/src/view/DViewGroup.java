package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import service.paintService;
import ui.ViewToPicture;
import util.Constant;
import vo.Node;
import vo.paintInfoVo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import bl.paintblImpl;
import cn.edu.cn.R;
import data.paintDao;

public class DViewGroup extends ViewGroup {
	
	private Canvas	cacheCanvas;
	public Bitmap	cachebBitmap;
	

	private paintService paintService;
	private paintInfoVo paintInfo;
	private paintDao dao;
	// 存放所有子节点
	private ArrayList<DEditTextView> editTexts;
	// 存放所有节点与node对应的hash表
	private HashMap<Node, DEditTextView> maps;

	private float posX = this.getX();
	private float posY = this.getY();
	private float startX;
	private float startY;
	private int screenWidth;
	private int screenHeight;
	private int singleRec;

	private boolean openSaved;
	private String curretFileName;

	private Paint paint;
	private int level;

	/**
	 * 构造函数，继承父类
	 * 
	 * @param context
	 */
	public DViewGroup(Context context) {
		super(context);
//		
//		cachebBitmap = Bitmap.createBitmap(1000, 1600, Config.ARGB_8888);
//		cacheCanvas = new Canvas(cachebBitmap);
		//cacheCanvas.drawColor(Color.WHITE);
		
		paintService = new paintblImpl();
		paintInfo = paintService.createPaint();
		// dao = new paintDao(getContext());
		dao = paintDao.getDao(getContext());
		init();
	}

	public DViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
//		cachebBitmap = Bitmap.createBitmap(1000, 1600, Config.ARGB_8888);
//		cacheCanvas = new Canvas(cachebBitmap);
		paintService = new paintblImpl();
		paintInfo = paintService.createPaint();
		// dao = new paintDao(getContext());
		dao = paintDao.getDao(getContext());
		init();
	}

	public DViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		cachebBitmap = Bitmap.createBitmap(1000, 1600, Config.ARGB_8888);
//		cacheCanvas = new Canvas(cachebBitmap);
		paintService = new paintblImpl();
		paintInfo = paintService.createPaint();
		// dao = new paintDao(getContext());
		dao = paintDao.getDao(getContext());
		init();
	}

	public void save(String name) {
		Iterator<Map.Entry<Node, DEditTextView>> itr = maps.entrySet().iterator();
		while (itr.hasNext()) {

			Map.Entry<Node, DEditTextView> entry = (Entry<Node, DEditTextView>) itr.next();

			DEditTextView textView = entry.getValue();
			Node node = entry.getKey();
			node.setX(textView.getxPos());
			node.setY(textView.getyPos());
			node.setTextValue(textView.getText().toString());
		}

		// dao.deleteDatabase();
		paintService.SavePaint(name, paintInfo, dao);
		load(name);
	}

	// 是否存在相同名字的图表
	public boolean existPaint(String name) {
		if (isOpenSaved())
			return false;
		else
			return dao.isExistPaint(name);
	}

	public void load(String name) {
		// 先清空
		this.removeAllViews();
		this.removeAllViewsInLayout();
		editTexts = new ArrayList<DEditTextView>();
		maps = new HashMap<Node, DEditTextView>();
		// 读取
		int maxid = dao.maxID(name);
		System.out.println("max" + maxid);
		paintInfo = paintService.OpenPaint(name, dao, maxid);
		ArrayList<Node> roots = paintInfo.getbTreeRoot().getRoot();
		for (Node node : roots) {
			DEditTextView view = new DEditTextView(getContext());
			view.setNode(node);
			view.setText(node.getTextValue());
			view.setxPos(node.getX());
			view.setyPos(node.getY());
			addView(view);
			view.measure(0, 0);
			view.setRaw_width(view.getMeasuredWidth());
			maps.put(node, view);
			addSons(view);
		}
		requestLayout();
		System.out.println("读取成功");
		openSaved = true;
		curretFileName = name;

	}

	private void addSons(DEditTextView view) {
		Node node = view.getNode();
		ArrayList<Node> sons = paintService.getAllSon(node);
		if (sons.size() == 0) {
			view.setLittleSon(view);
			return;
		}
		for (Node son : sons) {
			DEditTextView text = new DEditTextView(getContext());
			text.setNode(son);
			text.setText(son.getTextValue());
			text.setxPos(son.getX());
			text.setyPos(son.getY());
			text.setDad(view);
			addView(text);
			text.measure(0, 0);
			text.setRaw_width(text.getMeasuredWidth());
			addSons(text);
			editTexts.add(text);
			maps.put(son, text);
		}
		if (sons.size() != 0) {
			DEditTextView little = maps.get(sons.get(sons.size() - 1));
			view.setLittleSon(little);
		}
	}

	/**
	 * 初始化建立一个跟节点
	 */
	@SuppressWarnings("deprecation")
	public void init() {
		WindowManager wm = ((Activity) this.getContext()).getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		screenWidth = width;
		screenHeight = height;
		singleRec = width / 8 + 10;
		editTexts = new ArrayList<DEditTextView>();
		maps = new HashMap<Node, DEditTextView>();
		openSaved = false;
		curretFileName = "";

		DEditTextView root = new DEditTextView(getContext());
		root.setNode(paintInfo.getbTreeRoot().getRoot().get(0));
		root.setText("思维导图");
		root.setLittleSon(root);
		root.measure(0, 0);
		root.setRaw_width(root.getMeasuredWidth());
		int s_x = 3 * width / 2 - root.getMeasuredWidth() / 2;
		int s_y = 3 * height / 2 - root.getMeasuredHeight() / 2;
		root.setxPos(s_x);
		root.setyPos(s_y);
		addView(root);
		maps.put(root.getNode(), root);

		paint = new Paint();
	}

	public void textMove(DEditTextView view) {
		view.measure(0, 0);
		int dis = view.getMeasuredWidth() - view.getRaw_width();
		// DO
		ArrayList<Node> childs = paintService.getAllChild(view.getNode());
		System.out.println(childs.size());
		for (Node node : childs) {
			DEditTextView v = maps.get(node);
			v.setxPos(v.getxPos() + dis);
		}
		requestLayout();
		view.setRaw_width(view.getMeasuredWidth());
	}

	private boolean isInsideAnyText(DEditTextView text, float x, float y) {
		float left = text.getxPos();
		float right = left + text.getMeasuredWidth();
		float top = text.getyPos();
		float bottom = top + text.getMeasuredHeight();
		if (x < right && x > left && y > top && y < bottom)
			return true;
		else
			return false;
	}

	private boolean checkInside(DEditTextView view, float x, float y) {
		DEditTextView ancestorBefore = view;
		while (ancestorBefore.getDad() != null) {
			ancestorBefore = ancestorBefore.getDad();
		}

		Iterator<Map.Entry<Node, DEditTextView>> itr = maps.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<Node, DEditTextView> entry = (Entry<Node, DEditTextView>) itr.next();
			DEditTextView textView = entry.getValue();
			if (isInsideAnyText(textView, x, y)) {
				if (textView == view||textView==view.getDad()) {
					continue;
				}
				System.out.println("换换换");
				DEditTextView ancestorNow = textView;
				while (ancestorNow.getDad() != null) {
					ancestorNow = ancestorNow.getDad();
				}
				boolean hasLastBro = !(textView.getLittleSon() == textView);
				ArrayList<Node> before = paintService.getAllChild(ancestorBefore.getNode());
				ArrayList<Node> now = paintService.getAllChild(ancestorNow.getNode());
				ArrayList<Node> childs = paintService.getAllChild(view.getNode());
				before.removeAll(childs);
				now.removeAll(childs);
				now.remove(view.getNode());
				// 一脉剔除
				Node p = view.getNode();
				while (p != null) {
					before.remove(p);
					p = p.getParent();
				}
				p = textView.getNode();
				while (p != null) {
					now.remove(p);
					p = p.getParent();
				}
				int level_dis = textView.getLevel() + 1 - view.getLevel();
				view.setLevel(view.getLevel()+level_dis);
				for (Node node : childs) {
					DEditTextView child = maps.get(node);
					child.setLevel(child.getLevel() + level_dis);
					child.invalidate();
				}
				
				// if ancestorBefore==ancestorNow
				// else

				// 原树的后续
				boolean selfSon = false;
				if (view.getDad() == null) {
					editTexts.add(view);
					for (Node node : childs) {
						DEditTextView temp = maps.get(node);
						temp.setyPos(temp.getyPos() + singleRec / 2);
					}
				} else {
					DEditTextView pa = view.getDad();
					ArrayList<Node> cousins = paintService.getAllSon(pa.getNode());
					if (cousins.size() == 1) {
						pa.setLittleSon(pa);
						selfSon = true;
					} else {
						if (pa.getLittleSon() == view) {
							pa.setLittleSon(maps.get(cousins.get(cousins.size() - 2)));
						}
					}
				}
				int weight = paintService.numNode(view.getNode());
				if (selfSon) {
					for (int i = 0; i < before.size(); i++) {
						DEditTextView temp = maps.get(before.get(i));
						if (temp.getyPos() > view.getRaw_y()) {
							int tempY = temp.getyPos() - (weight - 1) * singleRec / 2;
							temp.setyPos(tempY);
						} else {
							int tempY = temp.getyPos() + (weight - 1) * singleRec / 2;
							temp.setyPos(tempY);
						}
					}
				} else {
					for (int i = 0; i < before.size(); i++) {
						DEditTextView temp = maps.get(before.get(i));
						System.out.println(temp.getText().toString());
						if (temp.getyPos() > view.getRaw_y()) {
							int tempY = temp.getyPos() - (weight) * singleRec / 2;
							temp.setyPos(tempY);
						} else {
							int tempY = temp.getyPos() + (weight) * singleRec / 2;
							temp.setyPos(tempY);
						}
					}

				}

				view.setDad(textView);

				// 移动到新树
				if (!hasLastBro) {
					// TODO 插入到跟节点时
					int x_dis = textView.getxPos() + textView.getMeasuredWidth() + Constant.SIN_WIDTH - view.getxPos();
					int y_dis = textView.getyPos() - view.getyPos();
					if(textView.getNode().getLevel()==0)
						y_dis -= textView.getMeasuredHeight()/2;
					view.setxPos(textView.getxPos() + textView.getMeasuredWidth() + Constant.SIN_WIDTH);
					view.setyPos(view.getyPos()+y_dis);
					move(view, x_dis, y_dis);
					for (int i = 0; i < now.size(); i++) {
						DEditTextView text = maps.get(now.get(i));
						System.out.println(text.getText().toString());
						int y_pos = view.getyPos();
						int y_temp = text.getyPos();
						if (y_pos > y_temp) {
							y_temp -= (weight - 1) * singleRec / 2;
							text.setyPos(y_temp);
						} else {
							y_temp += (weight - 1) * singleRec / 2;
							text.setyPos(y_temp);
						}
					}
					requestLayout();
					paintService.MoveNode(view.getNode(), textView.getNode(), null);
				} else {
					DEditTextView bro = textView.getLittleSon();
					int x_dis = bro.getxPos() - view.getxPos();
					int y_dis = bro.getyPos() + singleRec / 2 - view.getyPos();
					view.setxPos(bro.getxPos());
					view.setyPos(bro.getyPos() + singleRec / 2);
					move(view, x_dis, y_dis);
					for (int i = 0; i < now.size(); i++) {
						DEditTextView text = maps.get(now.get(i));
						int y_pos = view.getyPos();
						int y_temp = text.getyPos();
						if (y_pos > y_temp) {
							y_temp -= weight * singleRec / 2;
							text.setyPos(y_temp);
						} else {
							y_temp += weight * singleRec / 2;
							text.setyPos(y_temp);
						}
					}
					requestLayout();
					paintService.MoveNode(view.getNode(), textView.getNode(), bro.getNode());
				}
				textView.setLittleSon(view);

				return true;
			}
		}
		return false;
	}

	public void checkMove(DEditTextView view, float y_pos, float x_pos) {
		// TODO 动画移动效果
		if (!checkInside(view, x_pos, y_pos)) {
			if (view.getNode().getLevel() == 0) {
				// 跟节点不调整
				return;
			} else {
				int weight = paintService.numNode(view.getNode());
				ArrayList<Node> nodes = paintService.getAllSon(view.getNode().getParent());
				int new_pos = 0;
				for (; new_pos < nodes.size(); new_pos++) {
					DEditTextView v = maps.get(nodes.get(new_pos));
					if (v.getyPos() > view.getyPos()) {
						break;
					}
				}
				int position = nodes.indexOf(view.getNode());
				// 没有移动超过临界
				if (new_pos - position == 1) {
					float y_dis = view.getyPos();
					float x_dis = view.getxPos();
					view.setxPos(view.getRaw_x());
					view.setyPos(view.getRaw_y());
					y_dis = view.getyPos() - y_dis;
					x_dis = view.getxPos() - x_dis;

					// ValueAnimator va = new
					// ValueAnimator().ofInt(view.getyPos() -
					// (int) y_dis, view.getyPos());
					// va.setTarget(view);
					// va.setDuration(2000);
					// va.addUpdateListener(new animatorListener(view));
					// va.start();

					move(view, x_dis, y_dis);
					requestLayout();
					return;
				}
				boolean up = new_pos < position;
				if (up) {
					// 上行
					for (int i = new_pos; i < position; i++) {
						DEditTextView v = maps.get(nodes.get(i));
						v.setyPos(v.getyPos() + weight * singleRec);
						move(v, 0, weight * singleRec);
					}
					Node next = nodes.get(new_pos);
					DEditTextView next_text = maps.get(next);
					float y_dis = view.getyPos();
					float x_dis = view.getxPos();
					view.setyPos(next_text.getLittleSon().getyPos() - singleRec * paintService.numNode(next)
							- (weight - 1) * singleRec / 2);
					view.setxPos(next_text.getxPos());
					y_dis = view.getyPos() - y_dis;
					x_dis = view.getxPos() - x_dis;
					move(view, x_dis, y_dis);
					if (position == nodes.size() - 1) {
						DEditTextView new_little = maps.get(nodes.get(position - 1));
						view.getDad().setLittleSon(new_little);
					}
					requestLayout();
					// 树形结构更新
					if (new_pos == 0) {
						paintService.MoveNode(view.getNode(), view.getNode().getParent(), null);
					} else {
						paintService.MoveNode(view.getNode(), view.getNode().getParent(), nodes.get(new_pos - 1));
					}
				} else {
					// 下行
					new_pos = nodes.size() - 1;
					for (; new_pos >= 0; new_pos--) {
						DEditTextView v = maps.get(nodes.get(new_pos));
						if (v.getyPos() < view.getyPos()) {
							break;
						}
					}
					for (int i = new_pos; i > position; i--) {
						DEditTextView v = maps.get(nodes.get(i));
						v.setyPos(v.getyPos() - weight * singleRec);
						move(v, 0, -weight * singleRec);
					}
					Node last = nodes.get(new_pos);
					DEditTextView last_text = maps.get(last);
					float y_dis = view.getyPos();
					float x_dis = view.getxPos();
					view.setyPos(last_text.getLittleSon().getyPos() + singleRec + (weight - 1) * singleRec / 2);
					view.setxPos(last_text.getxPos());
					y_dis = view.getyPos() - y_dis;
					x_dis = view.getxPos() - x_dis;
					move(view, x_dis, y_dis);
					if (new_pos == nodes.size() - 1) {
						view.getDad().setLittleSon(view);
					}
					requestLayout();
					paintService.MoveNode(view.getNode(), view.getNode().getParent(), last);
				}
			}
		}
	}

	/**
	 * 移动节点及其所有后代
	 * 
	 * @param text
	 *            节点Text
	 * @param x_dis
	 *            x方向位移
	 * @param y_dis
	 *            y方向位移
	 */
	public void move(DEditTextView text, float x_dis, float y_dis) {
		ArrayList<Node> sons = paintService.getAllChild(text.getNode());
		for (Node son : sons) {
			DEditTextView v = maps.get(son);
			int x = (int) (v.getxPos() + x_dis);
			int y = (int) (v.getyPos() + y_dis);
			v.setxPos(x);
			v.setyPos(y);
			v.layout(x, y, x + v.getMeasuredWidth(), y + v.getMeasuredHeight());
		}
		invalidate();
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

	public void voice(String text) {
		View v = getFocusedChild();
		if (v != null)
			((DEditTextView) v).setText(text);
		else
			Toast.makeText(getContext(), "请选择节点", Toast.LENGTH_LONG).show();
	}

	/**
	 * 归并删除
	 */
	public void deleteMerge() {
		View v = getFocusedChild();
		if (v instanceof DEditTextView) {
			DEditTextView text = (DEditTextView) v;
			Node node = text.getNode();
			if (node.getLevel() == 0) {
				deleteNode();
			} else {
				int x_dis = text.getLittleSon().getxPos() - text.getxPos();
				ArrayList<Node> childs = paintService.getAllChild(node);
				DEditTextView childText;
				// 子孙左移
				for (Node child : childs) {
					childText = maps.get(child);
					childText.setxPos(childText.getxPos() - x_dis);
					childText.setLevel(childText.getLevel() - 1);
					childText.levelChanged();
				}
				ArrayList<Node> sons = paintService.getAllSon(node);
				// 孙子辈分上移
				for (Node son : sons) {
					childText = maps.get(son);
					childText.setDad(text.getDad());
				}

				// 没有儿子，等同于删除
				if (sons.size() == 0) {
					deleteNode();
				} else {
					// 有儿子
					childText = maps.get(sons.get(sons.size() - 1));
					text.getDad().setLittleSon(childText);

					removeView(text);
					editTexts.remove(text);
					maps.remove(node);
					paintService.DeleteAndMerge(node);
					requestLayout();
				}

			}
		}
	}

	/**
	 * 删除节点，其他的相应上下浮动
	 */
	public void deleteNode() {
		View v = getFocusedChild();
		int scaleWeight = 0;
		boolean selfSon = false;
		if (v instanceof DEditTextView) {
			// 移除View
			DEditTextView text = (DEditTextView) v;
			DEditTextView ancestor = text;
			boolean root = false;
			while (ancestor.getDad() != null) {
				ancestor = ancestor.getDad();
			}
			int weight = paintService.numNode(text.getNode());
			ArrayList<Node> sons = paintService.getAllChild(text.getNode());
			for (Node node : sons) {
				DEditTextView son = maps.get(node);
				removeView(son);
				editTexts.remove(son);
				maps.remove(node);
			}
			if (text.getDad() == null) {
				root = true;
				paintService.DeleteRoot(text.getNode(), paintInfo);
				paintService.DeleteAllChild(text.getNode());
			} else if (text.getDad().getLittleSon() == text) {
				ArrayList<Node> cousins = paintService.getAllSon(text.getNode().getParent());
				if (cousins.size() == 1) {
					selfSon = true;
					text.getDad().setLittleSon(text.getDad());
				} else {
					text.getDad().setLittleSon(maps.get(cousins.get(cousins.size() - 2)));
				}
				scaleWeight = paintService.numNode(text.getNode().getParent());
				paintService.DeleteAllChild(text.getNode());
			} else {
				scaleWeight = paintService.numNode(text.getNode().getParent());
				paintService.DeleteAllChild(text.getNode());
			}
			maps.remove(text.getNode());
			removeView(text);
			editTexts.remove(text);

			ArrayList<Node> roots = new ArrayList<Node>();
			roots.addAll(paintInfo.getbTreeRoot().getRoot());
			roots.remove(ancestor.getNode());

			ArrayList<Node> relatives = paintService.getAllChild(ancestor.getNode());
			Node p = text.getNode();
			while (p != ancestor.getNode()) {
				p = p.getParent();
				relatives.remove(p);
			}
			relatives.remove(p);
			// 其他View移动

			if ((!root) && scaleWeight > 1) {
				if (!selfSon) {
					for (int i = 0; i < relatives.size(); i++) {
						DEditTextView view = maps.get(relatives.get(i));
						int y = view.getyPos();
						int lowest = text.getLittleSon() == null ? text.getyPos() : text.getLittleSon().getyPos();
						if (y > lowest) {
							y -= weight * singleRec / 2;
							view.setyPos(y);
						} else {
							y += weight * singleRec / 2;
							view.setyPos(y);
						}
					}
				} else {
					for (int i = 0; i < relatives.size(); i++) {
						DEditTextView view = maps.get(relatives.get(i));
						int y = view.getyPos();
						int lowest = text.getLittleSon() == null ? text.getyPos() : text.getLittleSon().getyPos();
						if (y > lowest) {
							y -= (weight - 1) * singleRec / 2;
							view.setyPos(y);
						} else {
							y += (weight - 1) * singleRec / 2;
							view.setyPos(y);
						}
					}

				}
			}
			text = null;
			requestLayout();
		}
	}

	/**
	 * 插入节点，其他的相应上下浮动
	 */
	public void insertNode() {
		View v = getFocusedChild();
		if (v instanceof DEditTextView) {
			// 创建，初始化
			DEditTextView text = (DEditTextView) v;
			Node node = paintService.InsertNode(text.getNode());
			DEditTextView son = new DEditTextView(getContext());
			son.setDad(text);
			son.setNode(node);
			son.setText("新建节点");
			son.setLittleSon(son);
			// 定位
			DEditTextView little = text.getLittleSon();
			if (little == text) {
				// 第一个子节点
				son.setxPos(text.getxPos() + text.getMeasuredWidth() + Constant.SIN_WIDTH);
				son.setyPos(text.getyPos());
				if (node.getLevel() == 1) {
					son.setyPos(text.getyPos() - text.getMeasuredHeight() / 2);
				}
				addView(son);
				editTexts.add(son);
				maps.put(node, son);
				son.measure(0, 0);
				son.setRaw_width(son.getMeasuredWidth());
				text.setLittleSon(son);
				requestLayout();
			} else {
				// 加到父节点的最后一个
				son.setxPos(little.getxPos());
				son.setyPos(little.getyPos() + (paintService.numNode(little.getNode())) * singleRec / 2);
				addView(son);
				editTexts.add(son);
				maps.put(node, son);
				son.measure(0, 0);
				son.setRaw_width(son.getMeasuredWidth());
				System.out.println("添加大成功");
				// TODO 移动的动画
				ArrayList<DEditTextView> moveList = new ArrayList<DEditTextView>();
				for (int i = 0; i < getChildCount(); i++) {
					if (getChildAt(i) instanceof DEditTextView) {
						moveList.add((DEditTextView) getChildAt(i));
					}
				}
				DEditTextView p = son;
				while (p.getNode().getLevel() != 0) {
					moveList.remove(p);
					p = p.getDad();
				}
				moveList.remove(p);
				for (DEditTextView dEditTextView : moveList) {
					int pos = dEditTextView.getyPos() - son.getyPos() > 0 ? singleRec / 2 : -singleRec / 2;
					int y = dEditTextView.getyPos() + pos;
					dEditTextView.setyPos(y);
				}
				text.setLittleSon(son);
				requestLayout();
			}

		} else {
			// 新建跟节点
			Node node = paintService.NewRoot(paintInfo);
			DEditTextView root = new DEditTextView(getContext());
			root.setNode(node);
			root.setText("思维导图");
			root.setLittleSon(root);
			root.measure(0, 0);
			root.setRaw_width(root.getMeasuredWidth());
			int s_x = 3 * screenWidth / 2 - root.getMeasuredWidth() / 2;
			int s_y = 3 * screenHeight / 2 - root.getMeasuredHeight() / 2;
			s_x += singleRec * (paintInfo.getbTreeRoot().getRoot().size() - 1);
			s_y += singleRec * (paintInfo.getbTreeRoot().getRoot().size() - 1);
			root.setxPos(s_x);
			root.setyPos(s_y);
			addView(root);
			maps.put(root.getNode(), root);
			System.out.println("MDZZ");
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	/*
	 * 可划动，设定边界
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		View view = getFocusedChild();
		if (isHideInput(view, event)) {
			view.clearFocus();
			if (view instanceof DEditTextView) {
				DEditTextView v = (DEditTextView) view;
				v.clearFocusing();
			}
		}
		if (event.getPointerCount() == 1) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float stopX = event.getX();
				float stopY = event.getY();
				Log.e("pos", startX + "," + startY);
				posX += stopX - startX;
				posY += stopY - startY;
				posX = posX > 0 ? 0 : posX;
				posY = posY > 0 ? 0 : posY;
				posX = posX < -2 * screenWidth ? -2 * screenWidth : posX;
				posY = posY < -2 * screenHeight ? -2 * screenHeight : posY;
				this.setX(posX);
				this.setY(posY);
				requestLayout();
				invalidate();// call onDraw()
				break;
			}
		}
		return true;
	}

	/**
	 * 绘图
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//     //
//		canvas.drawBitmap(cachebBitmap, 0,0,null);
		
		for (int i = 0; i < editTexts.size(); i++) {
			DEditTextView view = editTexts.get(i);
			DEditTextView pa = view.getDad();
			level = view.getNode().getLevel();
			int x_start = pa.getRight() - 5;
			int x_end = view.getLeft();
			if (x_end < x_start && x_start - x_end > singleRec) {
				if (view.getRight() > x_start - singleRec)
					x_end = x_start - singleRec;
				else
					x_end = view.getRight();
			}
			System.out.println(view.getBottom() - 5+"ahhhhhhhhhhh"+paint.getStrokeWidth());
			if (level == 1) {
				myDraw(pa.getRight() - 3, (pa.getBottom() + pa.getTop()) / 2 - 5, x_end, view.getBottom() - 5, canvas);
			} else {

				myDraw(pa.getRight() - 3, pa.getBottom() - 5, x_end, view.getBottom() - 5, canvas);
			}
		}
	}

	private void myDraw(int x_start, int y_start, int x_end, int y_end, Canvas canvas) {
		paint_color();
		paint_width();
		int A = (y_end - y_start) / 2;
		int T = Math.abs(x_start - x_end) * 2;
		float w = (float) (Math.PI * 2 / T);
		float x_value;
		float y_value;
		float x_newvalue;
		float y_newvalue;
		boolean left = x_start >= x_end;
		if (!left) {
			for (int i = 0; i < T / 2; i++) {
				x_value = i + x_start;
				y_value = (float) (-A * Math.sin(w * i + Math.PI / 2) + A + y_start);
				x_newvalue = i + x_start + 1;
				int j = i + 1;
				y_newvalue = (float) (-A * Math.sin(w * j + Math.PI / 2) + A + y_start);
				canvas.drawLine(x_value, y_value, x_newvalue, y_newvalue, paint);
			}
		} else {
			for (int i = 0; i < T / 2; i++) {
				x_value = -i + x_start;
				y_value = (float) (-A * Math.sin(w * i + Math.PI / 2) + A + y_start);
				x_newvalue = -i + x_start - 1;
				int j = i + 1;
				y_newvalue = (float) (-A * Math.sin(w * j + Math.PI / 2) + A + y_start);
				canvas.drawLine(x_value, y_value, x_newvalue, y_newvalue, paint);
			}
		}
	}

	private void paint_color() {
		int index = level % 7;
		switch (index) {
		case 0:
			paint.setColor(Color.rgb(3, 22, 52));
			break;
		case 1:
			paint.setColor(Color.rgb(131, 175, 155));
			break;
		case 2:
			paint.setColor(Color.rgb(118, 77, 57));
			break;
		case 3:
			paint.setColor(Color.rgb(248, 147, 29));
			break;
		case 4:
			paint.setColor(Color.rgb(56, 13, 49));
			break;
		case 5:
			paint.setColor(Color.rgb(107, 194, 53));
			break;
		case 6:
			paint.setColor(Color.rgb(137, 157, 192));
			break;
		}
	}

	private void paint_width() {
		int width = 8 - level;
		if (width <= 0) {
			width = 1;
		}
		paint.setStrokeWidth(width);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof DEditTextView) {
				DEditTextView view = (DEditTextView) v;
				view.measure(0, 0);
				view.layout(view.getxPos(), view.getyPos(), view.getxPos() + view.getMeasuredWidth(),
						view.getyPos() + view.getMeasuredHeight());
			}
		}
		invalidate();
	}

	public void newP() {
		this.removeAllViews();
		this.removeAllViewsInLayout();
		paintInfo = paintService.createPaint();
		init();
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public boolean isOpenSaved() {
		return openSaved;
	}

	public String getCurretFileName() {
		return curretFileName;
	}
	
	public boolean exportPicture(String name){
		DViewGroup viewGroup=(DViewGroup)findViewById(R.id.viewgroup);
		//Bitmap bm=viewGroup.cachebBitmap;
		ViewToPicture viewToPicture=new ViewToPicture();
		if(viewToPicture.save(viewGroup, name,getContext()))return true;
		return false;
	}

}