package activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.edu.cn.R;
import ui.ColorPickerDialog;

public class ColorPickerActivity extends Activity{
	
	 Context context;  
	    private Button btnColorPicker;  
	    private TextView tvText;  
	      
	    private ColorPickerDialog dialog;  
	      
	    @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	        context = this;  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.colorpicker);  
	        initViews();  
	    }  
	    /** 
	     * 初始化UI 
	     */  
	    private void initViews() {  
	        btnColorPicker = (Button) findViewById(R.id.btn_color_picker);  
	        btnColorPicker.setOnClickListener(new View.OnClickListener() {  
	              
	            public void onClick(View v) {  
	                dialog = new ColorPickerDialog(context, tvText.getTextColors().getDefaultColor(),   
	                        getResources().getString(R.string.btn_color_picker),   
	                        new ColorPickerDialog.OnColorChangedListener() {  
	                      
	                    public void colorChanged(int color) {  
	                        tvText.setTextColor(color);  
	                    }  
	                });  
	                dialog.show();  
	            }  
	        });  
	        tvText = (TextView) findViewById(R.id.tv_text);  
	    }  
	}  

