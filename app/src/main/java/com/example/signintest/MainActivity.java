package com.example.signintest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.signintest.util.Constants;
import com.example.signintest.util.DataCache;
import com.google.gson.Gson;

import java.util.List;

import cn.com.newland.nle_sdk.responseEntity.ProjectInfo;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import cn.com.newland.nle_sdk.util.Tools;

public class MainActivity extends AppCompatActivity {
    TextView projectInfo;
    Gson gson = new Gson();
    private BaseResponseEntity userBaseResponseEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userBaseResponseEntity = (BaseResponseEntity) getIntent().getSerializableExtra("userBaseResponseEntity");
        initViewData();
        Button button = findViewById(R.id.launch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                projectInfo.setTextColor(Color.parseColor("#9900CC"));
                final NetWorkBusiness netWorkBusiness = new NetWorkBusiness(DataCache.getAccessToken(getApplicationContext()), DataCache.getBaseUrl(getApplicationContext()));
                netWorkBusiness.getProject(Constants.projectId, new NCallBack<BaseResponseEntity<ProjectInfo>>(getApplicationContext()) {
                    @Override
                    protected void onResponse(BaseResponseEntity<ProjectInfo> response) {
                        Tools.printJson(projectInfo, gson.toJson(response));
                    }
                });
            }
        });
    }
    protected void initViewData(){
        TextView LoginMsg = findViewById(R.id.loginMsg);
        projectInfo = findViewById(R.id.projectInfo);
        if (userBaseResponseEntity!=null&&userBaseResponseEntity.getStatus()==0){
            LoginMsg.setTextColor(Color.parseColor("#FF39C42F"));
            Gson gson = new Gson();
            Tools.printJson(LoginMsg,gson.toJson(userBaseResponseEntity),false);
        }
    }
}
