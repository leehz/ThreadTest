package top.itmp.threadtest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity {

    private Button button = null;
    private TextView textView0 = null;
    private TextView textView1 = null;
    private TextView textView = null;
    private String reads = null;
    private int length = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.run);
        textView0 = (TextView)findViewById(R.id.text);
        textView1 = (TextView)findViewById(R.id.text2);
        textView = (TextView)findViewById(R.id.out);
        textView.setTextColor(getResources().getColor(R.color.text));
        textView.setMovementMethod(new ScrollingMovementMethod());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        //super.run();
                        try {
                            URL url = new URL("https://raw.githubusercontent.com/racaljk/hosts/master/hosts");
                            URLConnection urlConnection = url.openConnection();
                            urlConnection.setRequestProperty("Accept-Encoding", "identity");
                            // textView.setText(urlConnection.getContent().toString());
                            //urlConnection.setDoInput(true);
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            length = urlConnection.getContentLength();
                            //textView1.setText(urlConnection.getContentLength());

                            byte[] buffer = new byte[4096];
                            int readLength = 0;

                            // begins download
                            Message message = new Message();
                            message.what = 0;
                            message.arg1 = urlConnection.getContentLength();
                            handler.sendMessage(message);

                            while (readLength < urlConnection.getContentLength()) {
                                readLength += in.read(buffer);
                                reads = new String(buffer, 0, 4096, "UTF-8");
                                //textView0.setText(readLength);
                                Message message1 = new Message();
                                message1.arg1 = readLength;
                                message1.what = 1;
                                handler.sendMessage(message1);
                            }

                            Message message2 = new Message();
                            message2.what = 2;
                            handler.sendMessage(message2);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        /*Message message = new Message();

                        if(Math.round(Math.random()) == 0){
                        message.what = 0;
                        }else{
                            message.what = 1;
                        }

                        handler.sendMessage(message);
                        */
                    }
                };
                thread.start();
            }
        });



    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            if(!Thread.currentThread().isInterrupted()){
                switch (msg.what){
                    case 0:
                        textView1.setText(msg.arg1 + "");
                        Toast.makeText(getApplicationContext(),"下载开始",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        textView0.setText(msg.arg1 + "");
                        textView.append(reads);
                        Toast.makeText(getApplicationContext(),"下载进行中，下载了"+ msg.arg1, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        }
    };
}
