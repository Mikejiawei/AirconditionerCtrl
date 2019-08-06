package com.example.signintest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    private SeekBar high;
    private SeekBar low;
    private Context mContext;
    private TextView hTmp;
    private int Progress;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        initView();
        initEvent();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initEvent() {
        int curHigh = curHigh();
        high.setProgress(50,true);
        high.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hTmp.setText(i+"/"+seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(mContext,"start",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Progress = seekBar.getProgress();

            }
        });
    }

    private int curHigh() {
//        功能： 获取现在的最高温度
        return 50;
    }

    private void initView() {
        high = findViewById(R.id.high);
        low = findViewById(R.id.low);
        hTmp = findViewById(R.id.hTmp);

    }
}
