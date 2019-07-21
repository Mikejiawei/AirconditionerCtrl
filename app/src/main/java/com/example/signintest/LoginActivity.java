package com.example.signintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.signintest.util.Constants;
import com.example.signintest.util.DataCache;
import com.example.signintest.util.SPHelper;

import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;


public class LoginActivity extends AppCompatActivity {
    private SPHelper spHelper;
    //private NetWorkBusiness netWorkBusiness;
    public String name;
    public String pwd;

    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        EditText edit1 = (EditText) findViewById(R.id.name);
//        name = edit1.getText().toString();
        name = Constants.userName;

//        EditText edit2 = (EditText) findViewById(R.id.pwd);
//        pwd = edit2.getText().toString();
        pwd = Constants.pwd;
        Button sign = (Button) findViewById(R.id.sign);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cloudInit();
                initSign(name,pwd);
            }
        });
    }

    void cloudInit(){
        DataCache.updateBaseUrl(getApplicationContext(),"http://" + Constants.PLATFORMADDRESS+ ":" + "80" + "/");
         String url =  DataCache.getBaseUrl(getApplicationContext());
//        Toast.makeText(LoginActivity.this,">>"+url,Toast.LENGTH_SHORT).show();
        spHelper = SPHelper.getInstant(getApplicationContext());
        spHelper.putData2SP(getApplicationContext(), Constants.SETTING_PLATFORM_ADDRESS, Constants.PLATFORMADDRESS);
        spHelper.putData2SP(getApplicationContext(), Constants.SETTING_PORT, Constants.PORT);
    }

    private void initSign(final String name, final String pwd){
        final NetWorkBusiness netWorkBusiness = new NetWorkBusiness("", DataCache.getBaseUrl(getApplicationContext()));
        netWorkBusiness.signIn(new SignIn(name, pwd), new NCallBack<BaseResponseEntity<User>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<User> response) {
//                int stat = response.getStatus();
//                int code = response.getStatusCode();
                DataCache.updateUserName(getApplicationContext(), name);
                DataCache.updatePwd(getApplicationContext(), pwd);

                String accessToken = response.getResultObj().getAccessToken();
                DataCache.updateAccessToken(getApplicationContext(), accessToken);
//                Toast.makeText(LoginActivity.this,">>"+accessToken,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userBaseResponseEntity", response);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }
}
