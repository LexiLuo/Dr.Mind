package service;

import vo.Node;
import vo.paintInfoVo;

public interface paintService {
    
	 //paintInfoVo getPaintInfo(Node root);
	 
	 paintInfoVo createPaint();//�½�����
	 
	 Node InsertNode(Node node);//����ӽ��
	 
	 Node InputText(Node node,String text);//����������
	 
	 int countNode(Node node);//ͳ�Ƶ�ǰ������ӽ�����
	 	 	 
}
