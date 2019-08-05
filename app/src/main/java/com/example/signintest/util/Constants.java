package com.example.signintest.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by marco on 2017/8/21.
 * 常量文件
 */

public class Constants {

    /*云平台属性SP配置*/
    public final static String SETTING_GATEWAY_TAG = "SETTING_GATEWAY_TAG";
    public final static String SETTING_PLATFORM_ADDRESS = "api.nlecloud.com";
    public final static String SETTING_PORT = "80";
    public final static String LOGIN_USER_NAME = "LOGIN_USER_NAME";
    public final static String LOGIN_PWD = "LOGIN_PWD";
    public final static String BASE_URL = "BASE_URL";
    public final static String ACCESS_TOKEN = "ACCESS_TOKEN";
//  帐号
    public static String PLATFORMADDRESS = "api.nlecloud.com";
    public static  String PORT = "80";
    public static String userName = "18745910366";
    public static String pwd = "liuhuijun990423";
    public static String projectId = "34148";
// 传感器

    public static String deviceId = "";
    public static String apiTagCurrentTemp = "currentTemp";
    public static String apiTagPowerCtrl = "control";
    public static int openPowerValue = 1;
    public static int closePowerValue = 0;
    public static String apiTagUpperLimit = "upperLimit";
    public static String apiTagLowerLimit = "lowerLimit";
    public static String apiTagUpperLimitCtrl = "line";
    public static String apiTagLowerLimitCtrl = "lowerLimitCtrl";


    public static String getStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 30);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = df.format(calendar.getTime());

        return startTime;
    }

    //获取当前时间
    public static String getEndTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTime = df.format(new Date());
        return endTime;
    }

    public static void handlerSensorData(String mJsonData)
    {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(mJsonData);
            JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
            JSONArray dataPoints = (JSONArray) resultObj.getJSONArray("DataPoints");


            JSONArray pointDTO = (JSONArray) dataPoints.getJSONObject(0).getJSONArray("PointDTO");
            //tv.setText(" ");
            for (int i = 0; i < pointDTO.length(); i++) {
                JSONObject subObject = pointDTO.getJSONObject(i);
                String value = (String) subObject.get("Value");
                String recordTime = (String) subObject.get("RecordTime");



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void handlerDeviceData(String mJsonData)
    {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(mJsonData);

            JSONArray resultObj = (JSONArray) jsonObject.get("ResultObj");
            JSONArray datas =  (JSONArray) resultObj.getJSONObject(0).getJSONArray("Datas");

            for (int i = 0; i < datas.length(); i++) {
                JSONObject subObject = null;

                subObject = datas.getJSONObject(i);
                String value = (String) subObject.get("Value");

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


//    void getSensorInfo(String deviceId,String apiTag){
//        netWorkBusiness.getSensor(deviceId, apiTag, new Callback<BaseResponseEntity>() {
//            @Override
//            public void onResponse(@NonNull Call<BaseResponseEntity> call, @NonNull Response<BaseResponseEntity> response) {
//                BaseResponseEntity baseResponseEntity = response.body();
//
//                if (baseResponseEntity != null) {
//                    //Tools.printJson(tv, gson.toJson(baseResponseEntity));
//                    handlerSensorInfo(gson.toJson(baseResponseEntity));
//                } else {
//                    //tv.setText("请求出错 : 请求参数不合法或者服务出错");
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<BaseResponseEntity> call, @NonNull Throwable t) {
//                //tv.setText("请求出错 : \n" + t.getMessage());
//            }
//        });
//    }
//
//    void handlerSensorInfo(String mJsonData)
//    {
//        JSONObject jsonObject = null;
//
//        try {
//            jsonObject = new JSONObject(mJsonData);
//            JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
//            final Double value = resultObj.getDouble("Value");
//            final String recordTime = (String) resultObj.get("RecordTime");
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }







}
