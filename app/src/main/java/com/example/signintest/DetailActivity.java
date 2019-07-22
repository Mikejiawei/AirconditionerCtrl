package com.example.signintest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.signintest.util.Constants;
import com.example.signintest.util.DataCache;
import com.example.signintest.util.SPHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.com.newland.nle_sdk.responseEntity.DeviceInfo;
import cn.com.newland.nle_sdk.responseEntity.DeviceState;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private LinearLayout mCurrentTempLayout,mOnlineLayout;
    private TextView mCurrentTempText,mCurrentTempTextTitle,mOnlineText;
    private NetWorkBusiness netWorkBusiness;
    private SPHelper spHelper;
    private String deviceID;
    private static final int GET_REMOTE_INFO = 101;
    private static final int GET_REMOTE_INFO_DELAY = 1000;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_REMOTE_INFO:
                    queryRemoteInfo();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        deviceID = (String) getIntent().getSerializableExtra("device");
        netWorkBusiness = new NetWorkBusiness(DataCache.getAccessToken(getApplicationContext()),DataCache.getBaseUrl(getApplicationContext()));
        initView();
//        initEvent();
        getDeviceInfo(deviceID);
    }

    private void getDeviceInfo(String deviceID) {
        final Gson gson = new Gson();
//        查询设备是否存在
        netWorkBusiness.getDeviceInfo(deviceID, new NCallBack<BaseResponseEntity<DeviceInfo>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<DeviceInfo> response) {
                int status = 1;
                if(response!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(gson.toJson(response));
                        status = (int) jsonObject.get("Status");
                        displayExistState(status);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Log.d(TAG, "getDeviceInfo Status: " + status);
                }
            }

            @Override
            public void onFailure(Call<BaseResponseEntity<DeviceInfo>> call, Throwable t) {
                Log.d(TAG, "onFailure: getDeviceInfo error\n"+t.getMessage());
            }
        });

    }

    private void displayExistState(int status) {
        if (status == 0){
            Log.d(TAG, "displayExistState: >>It exists!");
            mOnlineLayout.setVisibility(View.GONE);
            queryRemoteInfo();
        }else{
            mOnlineLayout.setVisibility(View.VISIBLE);
            mOnlineText.setText("设备不存在");
            Log.d(TAG, "displayExistState: >>设备-");
        }

    }

    private void queryRemoteInfo() {
        final Gson gson = new Gson();
//        查询设备在线情况
        netWorkBusiness.getBatchOnLine(deviceID, new NCallBack<BaseResponseEntity<List<DeviceState>>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<List<DeviceState>> response) {
                if (response!=null){
                    boolean value = false;
                    try {
                        JSONObject jsonObject = new JSONObject(gson.toJson(response));
                        JSONArray resultObj = (JSONArray) jsonObject.get("ResultObj");
//                        value = resultObj.getJSONObject(7).getBoolean("IsOnline");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "onResponse: get Online status fail");
                }

            }
        });
//       查询单个传感器
          netWorkBusiness.getSensor(deviceID, Constants.apiTagCurrentTemp, new NCallBack<BaseResponseEntity<SensorInfo>>(getApplicationContext()) {
              @Override
              protected void onResponse(BaseResponseEntity<SensorInfo> response) {
                  if(response!=null){
                      try {
                          JSONObject jsonObject = new JSONObject(gson.toJson(response));
                          JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                          String value = (String) resultObj.get("Value");
                          String res = gson.toJson(resultObj);
                          Log.d(TAG, "onSensor: "+ res);
                          displayCurrentTemp(Integer.parseInt(value));
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                  }else{
                      Log.d(TAG, "onResponse: Get current temp failed!");
                  }
              }
          });
        mHandler.sendEmptyMessageDelayed(GET_REMOTE_INFO, GET_REMOTE_INFO_DELAY);
    }

    private void displayCurrentTemp(int value) {
        Log.d(TAG, "displayCurrentTemp: " + value);
        mCurrentTempText.setText(value + "°C");
    }

    private void initView() {
        mCurrentTempLayout = findViewById(R.id.currentTemp_layout);
        mOnlineLayout = findViewById(R.id.online_layout);
        mOnlineText = findViewById(R.id.online_text);
        mCurrentTempText = findViewById(R.id.currentTemp_text);
        mCurrentTempTextTitle = findViewById(R.id.currentTemp_title);
    }
}
