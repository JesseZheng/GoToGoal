package com.zjut.jesse.gotogoal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName, passWord, passWord2;
    private Button Register, Login;
    private static final String URL = "http://192.168.2.131/test/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initListener();
    }

    private void initView() {
        userName = (EditText) findViewById(R.id.username);
        passWord = (EditText) findViewById(R.id.password);
        passWord2 = (EditText) findViewById(R.id.password2);
        Register = (Button) findViewById(R.id.register);
        Login = (Button) findViewById(R.id.login);

    }

    private void initListener() {
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd1 = passWord.getText().toString();
                String pwd2 = passWord2.getText().toString();
                if (pwd1.equals(pwd2)) {
                    sendRegisterRequest();
                }
                else {
                    Toast.makeText(getApplicationContext(), "前后密码不统一", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendRegisterRequest() {
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
                if (response.equals("注册成功")) {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
