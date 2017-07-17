package com.example.younghyeon.silverwatch;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends Activity {
    private Button startButton,stopButton, webviewButton;

    public byte[] buffer;
    public static DatagramSocket socket;
    private int port=50005;

    AudioRecord recorder;

    private int sampleRate = 16000 ; // 44100 for music
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;
    WebView webView;

    Boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById (R.id.start_button);
      //  stopButton = (Button) findViewById (R.id.stop_button);
     //   webviewButton = (Button) findViewById (R.id.web_view_button);

        startButton.setOnClickListener (startListener);
      //  stopButton.setOnClickListener (stopListener);
     //   webviewButton.setOnClickListener(webviewListener);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("token");

        String str = FirebaseInstanceId.getInstance().getToken();
        myRef.setValue(str);

        // webView setting
        webView=(WebView)findViewById(R.id.webView);

    }

    private final OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (flag == false) {
                flag = true;
                startButton.setBackgroundResource(R.drawable.callcancel);

                status = true;
                startStreaming();

                webView.setWebViewClient(new WebViewClient());
                WebSettings set = webView.getSettings();
                set.setJavaScriptEnabled(true);
                set.setBuiltInZoomControls(true);
                webView.loadUrl("http://19.168.22.29:8080/javascript_simple.html");
            }
            else {
                status = false;
                recorder.release();
                Log.d("VS","Recorder released");
                finish();
            }
        }
    };

    private final OnClickListener webviewListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            //Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            //startActivity(intent);
        }
    };

    public void startStreaming() {
        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    minBufSize = 4096;
                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");
                    byte[] buffer = new byte[minBufSize];
                    Log.d("VS","Buffer created of size " + minBufSize);
                    DatagramPacket packet;
                    final InetAddress destination = InetAddress.getByName("19.168.22.29");
                    Log.d("VS", "Address retrieved");

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();

                    while(status == true) {
                        //reading data from MIC into buffer
                        recorder.read(buffer, 0, buffer.length);

                        //putting buffer in the packet
                        packet = new DatagramPacket(buffer, buffer.length, destination, port);
                        socket.send(packet);
                        System.out.println("MinBufferSize: " + minBufSize);
                    }
                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }
        });
        streamThread.start();
    }
}