package service;

import android.graphics.Bitmap;
import vo.Node;
import vo.TextType;
import vo.paintInfoVo;

public interface paintService {
    
	 //paintInfoVo getPaintInfo(Node root);
	 
	 paintInfoVo createPaint();//新建画板
	 
	 Node InsertNode(Node node);//插入结点
	 
	 Node InputText(Node node,String text,TextType font);//输入结点文字
	 
	 int countNode(Node node);//计算当前结点下子结点个数，多叉树，不要调用该方法，调用新的
	 
	 int numNode(Node node);//计算用于界面排版的子结点个数，多叉树，调用此方法
	 
	 Node InputImage(Node node,Bitmap bmp);//输入结点图片
	 
	 Boolean DeleteAllChild(Node node);//删除所有子结点
	 
	 Boolean DeleteAndMerge(Node node);//删除归并
	 
	 Boolean SavePaint(String paintName,paintInfoVo paintvo);//存储画图
	 
	paintInfoVo OpenPaint(String  paintName);//打开画图 
	 	 	 
}
