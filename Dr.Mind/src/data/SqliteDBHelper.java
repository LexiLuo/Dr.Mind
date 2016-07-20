package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDBHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME ="paint_db";
	private static final int VERSION = 1;
	private static final String TABLE_NAME ="Paint";
		
	//重载构造方法
	public SqliteDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context,DATABASE_NAME,null,VERSION);
	}
	
	public static final String CREATE_PAINT="create table Paint ("+
	"id integer primary key autoincrement,paintName text,root integer,parent integer,leftChild integer,rightChild integer,textValue text,level integer)";
	
	/*
	      * 参数介绍：context 程序上下文环境 即：XXXActivity.this 
	      * name 数据库名字 
	      * factory 接收数据，一般情况为null
	      * version 数据库版本号
	      */
	
	  public SqliteDBHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	  }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%");
	/*	String strSQL = "create table "+TABLE_NAME+
				"(id integer primary key autoincrement,paintName text,root integer,parent integer,leftChild integer,rightChild integer,textValue text,level integer)"*/;
		db.execSQL(CREATE_PAINT);	
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists Paint");
		onCreate(db);
		
	}  
	
	
	

}
