package com.androidexample.lazyimagedownload;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//Adapter class extends with BaseAdapter and implements with OnClickListener 
public class LazyImageLoadAdapter extends BaseAdapter implements OnClickListener{
    
    private Activity activity;
    private ArrayList<CommitEntry> ar = new ArrayList<CommitEntry>();
    private ArrayList<CommitEntry> artemp = new ArrayList<CommitEntry>();

    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    private LazyImageLoadAdapter me;

    public LazyImageLoadAdapter(Activity a) {
        activity = a;
        me = this;

        Thread t = new Thread(myRunnable);
        t.start();

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Create ImageLoader object to download and show image in list
        // Call ImageLoader constructor to initialize FileCache
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    private Runnable myRunnable = new Runnable(){
        public void run(){
            try{
                artemp.clear();
                String GHUrl = "https://api.github.com/repos/rails/rails/commits";
                String response = "";
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(GHUrl);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                    // At this point we should have the raw JSON string in response
                    JSONArray jArray = null;
                    try {
                        jArray = new JSONArray(response);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    int numCommits = jArray.length();
                    String author = "";
                    String sha = "";
                    String commitmsg = "";
                    String avatar_url = "";
                    Log.d("", Integer.toString(numCommits));
                    for (int cn=0; cn < numCommits; cn++) {
                        JSONObject jObj = jArray.getJSONObject(cn);
                        JSONObject commit = jObj.getJSONObject("commit");
                        author = commit.getJSONObject("author").getString("name");
                        sha = jObj.getString("sha");
                        sha = sha.substring(0,6);
                        commitmsg = commit.getString("message");
                        String parts[] = commitmsg.split("\\r?\\n");
                        commitmsg = parts[0];
                        JSONObject authorobj = jObj.getJSONObject("author");
                        avatar_url = authorobj.getString("avatar_url");
                        CommitEntry cm = new CommitEntry();
                        cm.id = Integer.toString(cn+1);
                        cm.name = author;
                        cm.sha = sha;
                        cm.comments = commitmsg;
                        cm.imageUrl = avatar_url;
                        artemp.add(cm);
                        Log.d("",author);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ar = artemp;
                me.notifyDataSetChanged();
                Log.d("","Done");
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    };

    public int getCount() {
        return ar.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
         
        public TextView text;
        public TextView text1;
        public TextView textWide;
        public ImageView image;
 
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        View vi=convertView;
        ViewHolder holder;
         
        if(convertView==null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.listview_row, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.text1=(TextView)vi.findViewById(R.id.text1);
            holder.image=(ImageView)vi.findViewById(R.id.image);
             
           /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else 
            holder=(ViewHolder)vi.getTag();
        
        
        holder.text.setText(ar.get(position).id + ": "+ar.get(position).name);
        holder.text1.setText(ar.get(position).sha + ": " + ar.get(position).comments);
        ImageView image = holder.image;
        
        //DisplayImage function from ImageLoader Class
        //imageLoader.DisplayImage(data[position], image);
        imageLoader.DisplayImage(ar.get(position).imageUrl, image);

        /******** Set Item Click Listner for LayoutInflater for each row ***********/
        vi.setOnClickListener(new OnItemClickListener(position));
        return vi;
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
    
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
        
       OnItemClickListener(int position){
        	 mPosition = position;
        }
        
        @Override
        public void onClick(View arg0) {
        	MainActivity sct = (MainActivity)activity;
        	sct.onItemClick(mPosition);
        }               
    }

}