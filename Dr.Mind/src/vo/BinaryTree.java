package vo;

import android.sax.RootElement;

public class BinaryTree {

	Node root;//���ڵ�
	
	public BinaryTree(){
		root=new Node();
	}
	
/*	//�����ӽ��
	private void insert(Node node){
			root.leftChild=node;
	}*/
	
	public Node getRoot(){
		return root;
	}
	
	public void setRoot(){
		this.root=new Node();
	}
	
}
