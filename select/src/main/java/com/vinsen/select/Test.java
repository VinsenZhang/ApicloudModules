package com.vinsen.select;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Test extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mo_select_layout);
        init();
    }

    private void init(){

        Spinner spinner = findViewById(R.id.spinner);
        String[] arr={"孙悟空","猪八戒","唐僧"};
        //创建ArrayAdapter对象
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arr);

        spinner.setAdapter(adapter);

    }


}
