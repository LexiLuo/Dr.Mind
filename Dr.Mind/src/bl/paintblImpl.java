package bl;

import android.R.id;
import service.paintService;
import vo.Node;
import vo.paintInfoVo;

public class paintblImpl implements paintService{

	//��������
	public paintInfoVo createPaint(){
		System.out.println("create");
		return new paintInfoVo();
	}
	
	//�½����
	public Node InsertNode(Node node){
		Node inNode=new Node();
		if(node.getLeftChild()==null){
			node.setLeftChild(inNode);
		}//�������ŮΪ�գ�������ڽ�������Ů
		else{
			node=node.getLeftChild();
			for(;;){
              
               if(node.getRightChild()==null){
            	   node.setRightChild(inNode);
            	   break;
               }
               node=node.getRightChild();
			}//�������Ů���ڣ��������������Ů�����ֵܵ����һ��
			
		}
		
		return inNode;
	}
	
	//����������
	public Node InputText(Node node,String text){
		node.setTextValue(text);
		node.setTextLength(text.length());
		return node;
	}
	
	//���㵱ǰ�����ӽ�����
	public int countNode(Node node){
		int num=0;
		System.out.println("count");
		if(node.getLeftChild()==null){
			System.out.println(num+"mmm");
			return 0;
		}
		else{
			node=node.getLeftChild();
			for(;;){
				num++;
				System.out.println(num);
				if(node.getRightChild()==null)
					break;
				node=node.getRightChild();
			}
			System.out.println(num);
			return num;
		}
		
	}
	
}

