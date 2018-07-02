package com.zjut.jesse.gotogoal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BoardActivity extends AppCompatActivity {

    private ListView list_msg;
    private Button submit;
    private EditText message;
    private TextView header;
    private static final String URL = "http://192.168.2.131/test";
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        initView();
        initListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            new AlertDialog.Builder(BoardActivity.this)
                    .setTitle("提示")
                    .setMessage("是否要注销？")
                    .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("username");
                            editor.commit();
                            Toast.makeText(getApplicationContext(), "注销成功！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BoardActivity.this, MainActivity.class);
                            startActivity(intent);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
        else if (id == R.id.refresh) {
            //更新ListView
            try {
                Request request = new Request.Builder()
                        .url(URL+"/getmsg.php")
                        .build();
                Response response = client.newCall(request).execute();
                String responseJSONData = response.body().string();
                JSONArray jsonArray = new JSONArray(responseJSONData);
                LinkedList<Message> msgs = new LinkedList<Message>();
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    String author = jsonObject.optString("author");
                    String content = jsonObject.optString("content");
                    String time = jsonObject.optString("time");
                    int ID = jsonObject.optInt("id");
                    int heart_num = jsonObject.optInt("heart_num");
                    msgs.add(new Message(ID, author, time, content, heart_num));
                }
                refreshList(msgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        list_msg = (ListView) findViewById(R.id.message_list);
        submit = (Button) findViewById(R.id.submit);
        message = (EditText) findViewById(R.id.message);
        header = (TextView) findViewById(R.id.textView);
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        header.setText("世界杯畅聊吧~~~ 当前登录用户：" + username);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(URL+"/getmsg.php")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseJSONData = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseJSONData);
                    final LinkedList<Message> msgs = new LinkedList<Message>();
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        String author = jsonObject.optString("author");
                        String content = jsonObject.optString("content");
                        String time = jsonObject.optString("time");
                        int id = jsonObject.optInt("id");
                        int heart_num = jsonObject.optInt("heart_num");
                        msgs.add(new Message(id, author, time, content, heart_num));
                    }
                    final MessageAdapter messageAdapter = new MessageAdapter(msgs, BoardActivity.this);
                    list_msg.setAdapter(messageAdapter);
                    setListViewHeightBasedOnChildren(list_msg);

                    //实现listview内部item监听
                    messageAdapter.setOnItemAddHeartListener(new MessageAdapter.onItemAddHeartListener() {
                        @Override
                        public void onAddHeartClick(int i) {
                            msgs.get(i).setHeartNum(msgs.get(i).getHeartNum()+1);
                            msgs.get(i).setHeart();
                            int id = msgs.get(i).getId();
                            try {
                                Request request = new Request.Builder()
                                        .url(URL+"/addheart.php?id="+String.valueOf(id))
                                        .build();
                                Response response = client.newCall(request).execute();
                                Log.d("点赞是否成功", response.body().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            messageAdapter.refresh(msgs);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();

    }

    private void initListener() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                final String insert_author = sharedPreferences.getString("username", null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                        // 获取当前时间
                        Date date = new Date(System.currentTimeMillis());
                        String insert_time = simpleDateFormat.format(date);
                        String insert_content = message.getText().toString();
                        try {
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("author", insert_author)
                                    .add("content", insert_content)
                                    .add("time", insert_time)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(URL+"/addmsg.php")
                                    .post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            showResponse(responseData);
                            //更新ListView
                            Request request2 = new Request.Builder()
                                    .url(URL+"/getmsg.php")
                                    .build();
                            Response response2 = client.newCall(request2).execute();
                            String responseJSONData = response2.body().string();
                            JSONArray jsonArray = new JSONArray(responseJSONData);
                            LinkedList<Message> msgs = new LinkedList<Message>();
                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                String author = jsonObject.optString("author");
                                String content = jsonObject.optString("content");
                                String time = jsonObject.optString("time");
                                int id = jsonObject.optInt("id");
                                int heart_num = jsonObject.optInt("heart_num");
                                msgs.add(new Message(id, author, time, content, heart_num));
                            }
                            refreshList(msgs);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


    }

    public void setListViewHeightBasedOnChildren(final ListView listView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 获取ListView对应的Adapter
                ListAdapter listAdapter = listView.getAdapter();
                if (listAdapter == null) {
                    return;
                }
                int totalHeight = 0;
                for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
                    // listAdapter.getCount()返回数据项的数目
                    View listItem = listAdapter.getView(i, null, listView);
                    // 计算子项View 的宽高
                    listItem.measure(0, 0);
                    // 统计所有子项的总高度
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
                // listView.getDividerHeight()获取子项间分隔符占用的高度
                // params.height最后得到整个ListView完整显示需要的高度
                listView.setLayoutParams(params);
            }
        });

    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.equals("发表成功")) {
                    Toast.makeText(getApplicationContext(), "发表成功！", Toast.LENGTH_SHORT).show();
                    message.setText("");
                }
                else if (response.equals("发表失败")) {
                    Toast.makeText(getApplicationContext(), "发表失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refreshList(final LinkedList<Message> messages) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final MessageAdapter messageAdapter = new MessageAdapter(messages, BoardActivity.this);
                list_msg.setAdapter(messageAdapter);
                setListViewHeightBasedOnChildren(list_msg);
                //实现listview内部item监听
                messageAdapter.setOnItemAddHeartListener(new MessageAdapter.onItemAddHeartListener() {
                    @Override
                    public void onAddHeartClick(int i) {
                        messages.get(i).setHeartNum(messages.get(i).getHeartNum()+1);
                        messages.get(i).setHeart();
                        int id = messages.get(i).getId();
                        try {
                            Request request = new Request.Builder()
                                    .url(URL+"/addheart.php?id="+String.valueOf(id))
                                    .build();
                            Response response = client.newCall(request).execute();
                            System.out.println(response.body().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        messageAdapter.refresh(messages);
                    }
                });
            }
        });
    }

}
