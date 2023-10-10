package in.xiandan.countdowntimer.example;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import in.xiandan.countdowntimer.CountDownTimerSupport;
import in.xiandan.countdowntimer.OnCountDownTimerListener;
import in.xiandan.countdowntimer.TimerState;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    TextView tv_state;
    EditText minute,secend;
    private Vibrator vibrator;
    private boolean pause=false;
    Button pause_resume;
    Switch voice;
    Switch vibra;


    int voisenum=0;

    RingtoneManager manager = new RingtoneManager(this) ;
    Ringtone mRingtone;


    private CountDownTimerSupport mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        tv_state = (TextView) findViewById(R.id.tv_state);
        minute = (EditText) findViewById(R.id.minute);
        secend = (EditText) findViewById(R.id.secend);
        pause_resume=findViewById(R.id.pause_resume);
        vibra=findViewById(R.id.vibra);
        voice=findViewById(R.id.voice);
        //manager.setType(RingtoneManager.TYPE_ALARM);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();
        mRingtone= manager.getRingtone(2);



    }

    public void clickStart(View v) {
        if (mTimer != null) {
            mTimer.stop();
            mTimer = null;
        }
        long millisInFuture = Long.parseLong(minute.getText().toString())*60
                + Long.parseLong(secend.getText().toString());
        long countDownInterval = Long.parseLong("10");
        mTimer = new CountDownTimerSupport(millisInFuture*1000, countDownInterval);
        mTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                tv.setText(millisUntilFinished / 60000+"分\n"+
                        millisUntilFinished % 60000/1000+"秒");
                Log.d("CountDownTimerSupport", "onTick : " + millisUntilFinished + "ms");
            }

            @Override
            public void onFinish() {
                tv.setText("倒计时已结束");
                Log.d("CountDownTimerSupport", "onFinish");
                String vibratorService = Context.VIBRATOR_SERVICE;
                tv_state.setText(getStateText());
                //从系统服务中获取振动管理器
                if(vibra.isChecked()){
                    vibrator = (Vibrator) getSystemService(vibratorService);
                    if (vibrator.hasVibrator()) {
                        //振动的秒数
                        vibrator.vibrate(500);
                    }
                }
                if(voice.isChecked()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        mRingtone.setLooping(false);
                    }
                    mRingtone.play();

                }


            }

            @Override
            public void onCancel() {
                tv.setText("倒计时已手动停止");
                Log.d("CountDownTimerSupport", "onCancel");
                tv_state.setText(getStateText());
            }
        });
        mTimer.start();
        tv_state.setText(getStateText());
    }

    public void clickPause(View v) {
        if(!pause){
            onResume();
            pause_resume.setText("继续");
        }else{
            onPause();
            pause_resume.setText("暂停");
        }
        pause=!pause;
    }


    public void clickCancel(View v) {
        if(mRingtone.isPlaying())  mRingtone.stop();
        if (mTimer != null) {
            mTimer.reset();
            tv_state.setText(getStateText());
        }
    }

    public void clickResetStart(View v) {
        if (mTimer != null) {
            mTimer.reset();
            mTimer.start();

            tv_state.setText(getStateText());
        }
    }

    public void clickList(View v) {
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTimer != null) {
            mTimer.resume();
            tv_state.setText(getStateText());
            pause_resume.setBackgroundColor(Color.rgb(161,251,142));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.pause();
            tv_state.setText(getStateText());
            pause_resume.setBackgroundColor(Color.rgb(240,135,132));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.stop();
            tv_state.setText(getStateText());
        }
    }

    private String getStateText() {
        TimerState state = mTimer.getTimerState();
        if (TimerState.START == state) {
            return "正在倒计时";
        } else if (TimerState.PAUSE == state) {
            return "倒计时暂停";
        } else if (TimerState.FINISH == state) {
            return "倒计时闲置";
        }
        return "";
    }

}
