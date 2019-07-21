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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        try {
            initViewData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    protected void initViewData() throws JSONException {
        TextView LoginMsg = findViewById(R.id.loginMsg);
        projectInfo = findViewById(R.id.projectInfo);
        if (userBaseResponseEntity!=null&&userBaseResponseEntity.getStatus()==0){
            LoginMsg.setTextColor(Color.parseColor("#FF39C42F"));
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(gson.toJson(userBaseResponseEntity));
            int resultObj = (int) jsonObject.get("Status");
            if(resultObj==0){
                LoginMsg.setText("Signed in successfully!");
            }
//            JSONArray values = (JSONArray) resultObj.get("nameValuePairs");
//            String name = values.getJSONObject(3).getString("UserName");
//            LoginMsg.setText(name);

//            Tools.printJson(LoginMsg,gson.toJson(userBaseResponseEntity),false);
        }
    }
}
