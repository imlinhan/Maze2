package com.linhan.maze;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.linhan.maze.utils.MazeWalker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView textView;

    Button btnStart;
    Button btnStep;
    Button btnCheck;
    MazeWalker walker;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.text);
        textView.setText("");

        btnStart = (Button)findViewById(R.id.btn_start);
        btnStep = (Button)findViewById(R.id.btn_step);
        btnCheck = (Button)findViewById(R.id.btn_check);

        btnStart.setOnClickListener(this);
        btnStep.setOnClickListener(this);
        btnStep.setVisibility(View.GONE);
        btnCheck.setOnClickListener(this);

        scrollView = (ScrollView)findViewById(R.id.sv);

        walker = new MazeWalker();
        walker.setLogCallback(new MazeWalker.LogTextCallback() {
            @Override
            public void onLogText(String text) {
                textView.append(text + "\n");
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_start:
                walker.start();
                break;
            case R.id.btn_step:
                //walker.step();
                break;
            case R.id.btn_check:
                walker.check();
                break;
        }
    }
}
