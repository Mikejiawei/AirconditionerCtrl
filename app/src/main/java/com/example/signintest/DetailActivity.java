package com.example.signintest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private LinearLayout mCurrentTempLayout,mOnlineLayout,mUpperLimitLayout,mLowerLimitlayout;
    private TextView mCurrentTempText,mCurrentTempTextTitle,mOnlineText,mUpperLimitTemp,mLowerLimitTemp,mUpperLimitTempTitle,mLowerLimitTempTitle;
    private NetWorkBusiness netWorkBusiness;
    private SPHelper spHelper;
    private String deviceID;
    private ImageView mAirStateImageView;
    private boolean isDeviceExist = false;
    private  boolean isDeviceOnline = false;
    private static final int GET_REMOTE_INFO = 101;
    private static final int GET_REMOTE_INFO_DELAY = 1000;
    private Context mContext;

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
        mContext = this;
        deviceID = (String) getIntent().getSerializableExtra("device");
        netWorkBusiness = new NetWorkBusiness(DataCache.getAccessToken(getApplicationContext()),DataCache.getBaseUrl(getApplicationContext()));
        initView();
        getDeviceInfo(deviceID);
        initEvent();
    }

    private void initEvent() {
        mAirStateImageView.setOnClickListener(new ControlPowerListener());
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
//  检查设备是否存在
    private void displayExistState(int status) {
        if (status == 0){
            Log.d(TAG, "displayExistState: >>It exists!");
            isDeviceExist = true;
            mOnlineLayout.setVisibility(View.GONE);
            queryRemoteInfo();
        }else{
            isDeviceExist = false;
            mOnlineLayout.setVisibility(View.VISIBLE);
            mOnlineText.setText("设备不存在");
            Log.d(TAG, "displayExistState: >>设备-");
        }

    }
//  数据查询函数
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
                        JSONArray result = (JSONArray) jsonObject.get("ResultObj");
                        Log.d(TAG, ">>>Online: "+result);
                        value = result.getJSONObject(0).getBoolean("IsOnline");
                        displayOnlineState(value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Log.d(TAG, "onResponse: get Online status fail");
                }

            }
        });
//       查询单个传感器,实时的温度
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
        //    查询温度上限值
        netWorkBusiness.getSensor(deviceID, Constants.apiTagUpperLimit, new NCallBack<BaseResponseEntity<SensorInfo>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> response) {
                if (response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(gson.toJson(response));
                        JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                        String value = (String) resultObj.get("Value");
//                        Log.d(TAG, "onResponse: Upper>>>" + value);
                        displayUpperTemp(Integer.parseInt(value));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "onResponse: Get UpperLimit failed!");
                }
            }
        });
//        查询温度下限值
        netWorkBusiness.getSensor(deviceID, Constants.apiTagLowerLimit, new NCallBack<BaseResponseEntity<SensorInfo>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> response) {
                if (response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(gson.toJson(response));
                        JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                        String value = (String) resultObj.get("Value");
//                        Log.d(TAG, "onResponse: Upper>>>" + value);
                        displayLowerTemp(Integer.parseInt(value));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "onResponse: Get LowerLimit failed!");
                }
            }
        });
        mHandler.sendEmptyMessageDelayed(GET_REMOTE_INFO, GET_REMOTE_INFO_DELAY);
    }

    private void displayUpperTemp(int upperTemp) {
        mUpperLimitTemp.setText(upperTemp+"°C");
    }

    private void displayLowerTemp(int lowerTemp){
        mLowerLimitTemp.setText(lowerTemp+"°C");
    }

    private void displayOnlineState(boolean status) {
        isDeviceOnline = status;
        Log.d(TAG, "displayOnlineState: >>" + status );
        if(!status){
            mOnlineLayout.setVisibility(View.VISIBLE);
            mOnlineText.setText("设备已离线");
        }else{
            mOnlineLayout.setVisibility(View.GONE);
        }
    }
//  温度展示函数
    private void displayCurrentTemp(int value) {
        Log.d(TAG, "displayCurrentTemp: " + value);
        mCurrentTempText.setText(value + "°C");
    }
//  初始化组件
    private void initView() {
        mAirStateImageView = (ImageView) findViewById(R.id.switch_imageview);
        mAirStateImageView.setTag(false);
        mUpperLimitTemp = findViewById(R.id.upperLimitTemp_text);
        mUpperLimitTempTitle = findViewById(R.id.upperTemp_Title);
        mLowerLimitTemp = findViewById(R.id.lowerLimitTemp_text);
        mLowerLimitTempTitle = findViewById(R.id.lowerTemp_Title);
        mCurrentTempLayout = findViewById(R.id.currentTemp_layout);
        mOnlineLayout = findViewById(R.id.online_layout);
        mOnlineText = findViewById(R.id.online_text);
        mCurrentTempText = findViewById(R.id.currentTemp_text);
        mCurrentTempTextTitle = findViewById(R.id.currentTemp_title);

    }
//  控制器函数--监听器
    private class ControlPowerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isDeviceExist){
                Log.d(TAG, "onClick: Device is not exist");
                Toast.makeText(mContext,"设备不存在，请确认",Toast.LENGTH_LONG).show();
                return;
            }
            if (!isDeviceOnline){
                Log.d(TAG, "onClick: >>>"+isDeviceOnline);
                Toast.makeText(mContext,"设备已离线，请确认",Toast.LENGTH_LONG).show();
                return;
            }
            final int controlValue = (boolean) mAirStateImageView.getTag() == false ? 1:0;
            final Gson gson = new Gson();
//            调用命令控制接口
            netWorkBusiness.control(deviceID, Constants.apiTagPowerCtrl, controlValue, new NCallBack<BaseResponseEntity>(getApplicationContext()) {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                protected void onResponse(BaseResponseEntity response) {
                    if(response!=null){
                        try {
                            JSONObject jsonObject = new JSONObject(gson.toJson(response));
                            int status = (int) jsonObject.get("Status");
                            Log.d(TAG, "control PowerCtrl Status: "+status);
                            if(0==status){
                                displayPowerStatus(controlValue);
                            }else{
                                Log.d(TAG, "return status value is error, open boc fail!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void displayPowerStatus(int control) {
        if (control==Constants.closePowerValue){
            displayPowerStatusClose();
        }else if(control == Constants.openPowerValue){
            displayPowerStatusOpen();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void displayPowerStatusClose() {
        mAirStateImageView.setBackground(getResources().getDrawable(R.mipmap.off));
        mAirStateImageView.setTag(false);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void displayPowerStatusOpen() {
        mAirStateImageView.setBackground(getResources().getDrawable(R.mipmap.on));
        mAirStateImageView.setTag(true);
    }
}
