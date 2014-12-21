package com.androidexample.lazyimagedownload;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
    
    ListView list;
    LazyImageLoadAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        list=(ListView)findViewById(R.id.list);
        
        // Create custom adapter for listview
        adapter=new LazyImageLoadAdapter(this);

        //Set adapter to listview
        list.setAdapter(adapter);
        
        Button b=(Button)findViewById(R.id.button1);
        b.setOnClickListener(listener);
    }

    @Override
    public void onDestroy()
    {
    	// Remove adapter refference from list
        list.setAdapter(null);
        super.onDestroy();
    }
    
    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
        	
        	//Refresh cache directory downloaded images
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };
    
    
    public void onItemClick(int mPosition)
    {
    }
}