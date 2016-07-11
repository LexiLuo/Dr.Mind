package vo;

import android.graphics.Bitmap;

public class Node {

	Node leftChild=null;//����Ů��������
	Node rightChild=null;//���ֵܣ�������
	Node parent=null;//�����,������ṹ�еĸ����
	String textValue=null;//�������
	int textLength=0;//������ֳ���
	TextType font;//�����������
	Bitmap bmp;//���ͼƬ
	
	public Node(){
		leftChild=null;
		rightChild=null;
		textValue=null;
		parent=null;
		textLength=0;
	    font=null;
	    bmp=null;
	}

	
public Node getParent() {
		return parent;
	}


	public void setParent(Node parent) {
		this.parent = parent;
	}


public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

//���ý���ı�
public void setTextValue(String text){
	this.textValue=text;
}

public void setLeftChild(Node lchild){
	this.leftChild=lchild;
}

public TextType getFont() {
	return font;
}

public void setFont(TextType font) {
	this.font = font;
}

public void setRightChild(Node rchild){
	this.rightChild=rchild;
}

public void setTextLength(int len){
	this.textLength=len;
}

public String getTextValue(){
    return textValue;
}

public Node getLeftChild(){
	return leftChild;
}

public Node getRightChild(){
	return rightChild;
}

public int getTextLength(){
	return textLength;
}
}