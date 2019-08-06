package com.example.signintest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.signintest.util.Temp;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DataAcitivity extends AppCompatActivity {
    private LineChart chart;
    private static final String TAG = "DataAcitivity";
    private SQLiteDatabase db;
    private Button get;
    private Button next;
    private Button prev;
    private Timer timer;
    private int valueT;
    private String Time;
    private ArrayList<Entry> values = new ArrayList<>();
    private LineDataSet set1;
    private int p =0;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_acitivity);
//        SQLiteDatabase db = LitePal.getDatabase();
        dbInit();
        screenInit();
        initView();
        clickInit();
//        showData(0);
//        showData(values);
//        timerInit();

    }

    private void timerInit() {

    }

    private void showData(int page) {
        values = getInfo(page);
        setData(values);
        chart.setScaleEnabled(true);
        chart.animateX(1000);
        chart.invalidate();
    }

    private void setData(ArrayList<Entry> values) {

        if (values!=null){
            if(chart.getData()!=null){
                Log.d(TAG, "setData: >>> change");
                set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                set1.notifyDataSetChanged();
                chart.notifyDataSetChanged();

            }else{
                Log.d(TAG, "setData: >>> Not change");
                set1 = new LineDataSet(values,"温度");
                set1.enableDashedLine(10f, 5f, 0f);
                set1.enableDashedHighlightLine(10f, 5f, 0f);

                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(true);
                set1.setValueTextSize(9f);
                set1.setDrawFilled(true);
                set1.setFormLineWidth(1f);
                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                set1.setFormSize(15.f);
                if (Utils.getSDKInt() >= 18) {
                    // 填充背景只支持18以上
                    //Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
                    //set1.setFillDrawable(drawable);
                    set1.setFillColor(Color.BLUE);
                } else {
                    set1.setFillColor(Color.BLACK);
                }
                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                //添加数据集
                dataSets.add(set1);

                //创建一个数据集的数据对象
                LineData data = new LineData(dataSets);
                chart.setData(data);

            }
        }

    }

    private ArrayList<Entry> getInfo(int page) {
        ArrayList<Entry> v = new ArrayList<>();
        int end;
        int begin;
        int beginx;
        List<Temp> Temps = LitePal.where("tempture>?","0").limit(5).offset(page*5).find(Temp.class);
        if(!Temps.isEmpty()){
//            begin = page*5;
//            end = page*5+5;
//            beginx = begin*2;
//            for (int i=begin,j=beginx;i<end;i++,j+=2){
//                v.add(new Entry(j,Temps.get(i).getTempture()));
//            }
            int j=0;
            for (Temp tmp:Temps){
                v.add(new Entry(j,tmp.getTempture()));
                j+=2;
            }

//            for (int i=0,j=5;i<5;i++,j+=5){
//                v.add(new Entry(j,Temps.get(i).getTempture()));
//            }
        }

        return v;
    }
    private void getDebug(){
        List<Temp> Temps = LitePal.where("tempture>?","0").find(Temp.class);
        Log.d(TAG, "getDebug: >>>"+Temps.get(4).getTempture());
    }

    private void clickInit() {
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
//                showData(0);
//                getDebug();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){
                    showData(0);
                    flag=false;
                }else if (p<5){
                    showData(++p);
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (p>=1){
                    showData(--p);
                }
            }
        });
    }

    private void startTimer() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                这里应该获取传感器数据
                valueT = (int) (10+Math.random()*(60-10+1));
                Date dt = new Date( );
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                Time = ft.format(dt);
                addData(valueT,Time);
            }
        },0,800 );

    }

    private void addData(float value1,String value2) {
        Temp tmp = new Temp();
        tmp.setTempture(value1);
        tmp.setGettime(value2);
        tmp.save();

    }

    private void screenInit() {
        Window window = getWindow();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag,flag);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
    }

    private void dbInit() {
        db = LitePal.getDatabase();
    }


    private void initView() {
        chart = (LineChart) findViewById(R.id.mLineChar);
        get = findViewById(R.id.get);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
    }
}
