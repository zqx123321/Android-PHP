package ouc.oucjw;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqx on 2017/2/20.
 */

public  class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<String> nameDatas;
    public List<String> scoreDatas;
    public List<String> imgDatas;
    public List<String> idDatas;

    IpConfig ip = new IpConfig();
    JSONParser jParser = new JSONParser();
    private  String url = ip.ip+"android/zqx/article.php";
    JSONArray products = null;

    private static final int HEAD_VIEW = 0;//头布局
    private static final int BODY_VIEW = 2;//内容布局
    private static final int TAG_VIEW = 1;//内容布局

    public MyAdapter(Context context){
        this.context = context;
        //初始化recycleview的数据
        nameDatas = new ArrayList<String>();
        scoreDatas = new ArrayList<String>();
        imgDatas = new ArrayList<String>();
        idDatas = new ArrayList<String>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("", ""));
        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url, "GET", params);

        // Check your log cat for JSON reponse
        Log.d("All Products: ", json.toString());

        try {
            // products found
            // Getting Array of Products
            products = json.getJSONArray("article");

            // looping through All Products
            for (int i = 0; i < products.length(); i++) {
                JSONObject c = products.getJSONObject(i);

                // Storing each json item in variable
                nameDatas.add(c.getString("passage_title"));
                scoreDatas.add(c.getString("passage_content"));
                imgDatas.add("http://123.206.61.96:8088/android/zqx/"+c.getString("passage_picture"));
                idDatas.add(c.getString("id"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //创建ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //加载布局文件
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycle, parent, false);
        MyBodyViewHolder viewHolder = new MyBodyViewHolder(view);
        return viewHolder;
    }

    //绑定ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ((MyBodyViewHolder) holder).tvname.setText(nameDatas.get(position));
        ((MyBodyViewHolder) holder).tvscore.setText(scoreDatas.get(position));
        ((MyBodyViewHolder) holder).tvid.setText(idDatas.get(position));
        ((MyBodyViewHolder) holder).img.setImageBitmap(returnBitMap(imgDatas.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String pid = idDatas.get(position);
                Intent in = new Intent(((Activity) context), ArticleDetail.class);
                in.putExtra("id", pid);
                context.startActivity(in);
                ((Activity) context).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    @Override
    public int getItemCount(){
        return nameDatas.size();
    }
    //如果是第一项，则加载头布局
    @Override
    public int getItemViewType(int position) {
        return BODY_VIEW;
    }

    class MyBodyViewHolder extends RecyclerView.ViewHolder {
        TextView tvname;
        TextView tvscore;
        ImageView img;

        TextView tvid;

        public MyBodyViewHolder(View itemView) {
            super(itemView);
            tvname = (TextView) itemView.findViewById(R.id.recycle_class);
            tvscore = (TextView) itemView.findViewById(R.id.recycle_score);
            img= (ImageView) itemView.findViewById(R.id.recycle_img);
            tvid= (TextView) itemView.findViewById(R.id.recycle_id);
        }
    }

    public Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}