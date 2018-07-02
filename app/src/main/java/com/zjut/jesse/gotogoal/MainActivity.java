package com.zjut.jesse.gotogoal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText userName, passWord;
    private Button Login, Register, bt_username_clear, bt_pwd_eye, bt_pwd_clear;
    private static final String URL = "http://192.168.2.131/test/login.php";
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    private void initView() {
        userName = (EditText) findViewById(R.id.username);
        passWord = (EditText) findViewById(R.id.password);
        Login = (Button) findViewById(R.id.login);
        Register = (Button) findViewById(R.id.register);
        bt_username_clear = (Button) findViewById(R.id.bt_username_clear);
        bt_pwd_eye = (Button) findViewById(R.id.bt_pwd_eye);
        bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
    }

    private void initListener() {
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLoginRequest();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 获得文本框中的用户
                String user = userName.getText().toString().trim();
                if ("".equals(user)) {
                    // 用户名为空,设置按钮不可见
                    bt_username_clear.setVisibility(View.INVISIBLE);
                } else {
                    // 用户名不为空，设置按钮可见
                    bt_username_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // 监听文本框内容变化
        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 获得文本框中的用户
                String pwd = passWord.getText().toString().trim();
                if ("".equals(pwd)) {
                    // 用户名为空,设置按钮不可见
                    bt_pwd_clear.setVisibility(View.INVISIBLE);
                } else {
                    // 用户名不为空，设置按钮可见
                    bt_pwd_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        bt_username_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName.setText("");
            }
        });

        bt_pwd_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passWord.setText("");
            }
        });

        bt_pwd_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 密码可见与不可见的切换
                if (isOpen) {
                    isOpen = false;
                } else {
                    isOpen = true;
                }

                // 默认isOpen是false,密码不可见
                changePwdOpenOrClose(isOpen);

            }
        });

    }

    private void sendLoginRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username", userName.getText().toString())
                            .add("password", passWord.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url(URL)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    showResponse(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.equals("SUCCESS")) {
                    Toast.makeText(getApplicationContext(), "登录成功！" + userName.getText().toString(), Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", userName.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, BoardActivity.class);
                    startActivity(intent);
                }
                else if (response.equals("FAIL")) {
                    Toast.makeText(getApplicationContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePwdOpenOrClose(boolean flag) {
        // 第一次过来是false，密码不可见
        if (flag) {
            // 设置EditText的密码可见
            passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            // 设置EditText的密码隐藏
            passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


}
