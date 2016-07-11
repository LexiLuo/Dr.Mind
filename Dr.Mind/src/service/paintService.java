package service;

import android.graphics.Bitmap;
import vo.Node;
import vo.TextType;
import vo.paintInfoVo;

public interface paintService {
    
	 //paintInfoVo getPaintInfo(Node root);
	 
	 paintInfoVo createPaint();//�½�����
	 
	 Node InsertNode(Node node);//����ӽ��
	 
	 Node InputText(Node node,String text,TextType font);//����������
	 
	 int countNode(Node node);//ͳ�Ƶ�ǰ������ӽ�����
	 
	 Node InputImage(Node node,Bitmap bmp);//������ͼƬ
	 
	 Boolean DeleteAllChild(Node node);//ɾ�����֮ɾ�������������ӽ��
	 
	 Boolean DeleteAndMerge(Node node);//ɾ������Һϲ��ӽ��
	 	 	 
}
