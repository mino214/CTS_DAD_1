package com.cts_dad_1;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;

public class MainActivity extends AppCompatActivity
{
   public static String url= "";
    MediaPlayer player;
    int position=0; // 현재 재생한 위치

    MediaRecorder recorder;
    String filename;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSelfPermission();

        File sdcard = Environment.getExternalStorageDirectory();
        File file =new File(sdcard,"recorded.mp4");
        filename = file.getAbsolutePath();
        Log.d("MainActivity","저장할 파일명 : "+ filename);

        Button button_1 =(Button) findViewById(R.id.Button_1); //녹음재생
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                playAudio();
            }
        });

        Button button_2 =(Button) findViewById(R.id.Button_2); //일시정지
        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pauseAudio();
            }
        });

        Button button_3 =(Button) findViewById(R.id.Button_3); // 재시작
        button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                resumeAudio();
            }
        });

        Button button_4 =(Button) findViewById(R.id.Button_4); // 정지
        button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                stopAudio();
            }
        });

        Button button_5 =(Button) findViewById(R.id.Button_5); //녹음 시작
        button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                recordAudio();
            }
        });

        Button button_6 =(Button) findViewById(R.id.Button_6); //녹음 중지
        button_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                stopRecording();
            }
        });

    }

    //권한에 대한 응답이 있을때 작동하는 함수
     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults)
     {
         //권한을 허용 했을 경우
         if(requestCode == 1)
         {
             int length = permissions.length;
             for (int i = 0; i < length; i++)
             {
                 if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                 {
                     // 동의
                     Log.d("MainActivity","권한 허용 : "+ permissions[i]);
                 }

             }
         }
     }

     public void checkSelfPermission()
     {
         String temp ="";
         // 파일 읽기 권한 확인
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                 != PackageManager.PERMISSION_GRANTED)
         {
             temp+=Manifest.permission.READ_EXTERNAL_STORAGE+" ";
         }

         // 파일 쓰기 권한 확인
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 != PackageManager.PERMISSION_GRANTED)
         {
             temp+=Manifest.permission.WRITE_EXTERNAL_STORAGE+" ";
         }

         // 마이크 권한 확인
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                 != PackageManager.PERMISSION_GRANTED)
         {
             temp+=Manifest.permission.RECORD_AUDIO+" ";
         }



         if(TextUtils.isEmpty(temp)==false)
         {
             // 권한요청
             ActivityCompat.requestPermissions(this,temp.trim().split(" "),1);
         }
         else
         {
             // 모두 허용 상태
             Toast.makeText(this,"권한을 모두 허용",Toast.LENGTH_SHORT).show();
         }
     }

    public void recordAudio() // 녹음
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 마이크에서 음성을 받아오겠다
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 파일 확장자
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); // 인코더
        recorder.setOutputFile(filename);

        try
        {
            recorder.prepare();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        recorder.start();
        Toast.makeText(this,"녹음시작됨.", Toast.LENGTH_LONG).show();
    }

    public void stopRecording() //녹음중지
    {

        if(recorder!=null)
        {
            recorder.stop();
            recorder.release();
            recorder=null;

            Toast.makeText(this,"녹음중지됨.", Toast.LENGTH_LONG).show();


        }

    }

    public void pauseAudio() // 일시정지
    {
        if(player != null)
        {
            position = player.getCurrentPosition();//어디까지 재생이 되었나
            player.pause();// 일시정지

            Toast.makeText(this,"일시정지됨.", Toast.LENGTH_LONG).show();

        }

    }

    public void stopAudio() // 끝
    {
        if(player!=null && player.isPlaying())
        {
            player.stop();
            closePlayer();
            Toast.makeText(this,"끝.", Toast.LENGTH_LONG).show();
        }

    }

    public void resumeAudio() // 재시작
    {
        if(player!=null && !player.isPlaying())
        {
            player.seekTo(position);
            player.start();
            Toast.makeText(this,"재시작됨.", Toast.LENGTH_LONG).show();
        }

    }

    public void playAudio() //녹음 파일 플레이
    {
        try
        {
            closePlayer();

            player = new MediaPlayer(); // 객체 생성
            //player.setDataSource(url);
            player.setDataSource(filename);
            player.prepare();
            player.start();
            Toast.makeText(this,"재생 시작됨.", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            e.printStackTrace(); // 에러 발생시 로그로 에러정보 출력
        }
    }



    public void closePlayer()
    {
        if(player != null)
        {
            player.release();
            player=null;
        }
    }
}

