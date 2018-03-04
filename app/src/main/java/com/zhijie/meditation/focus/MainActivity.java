package com.zhijie.meditation.focus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.graphics.Color.RED;


public class MainActivity extends Activity implements OnClickListener {

    private final String TAG = "Main_Activity!";
    private EditText et_name;
    private TextView tv_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordData = findViewById(R.id.refresh);
        recordData.setOnClickListener(this);

        et_name = findViewById(R.id.nameField);
        tv_feedback = findViewById(R.id.tv_name_feedback);


    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.refresh) {
            String name = et_name.getText().toString();

            if ( !(name == null) && !name.equals("")){
                Log.d(TAG, "Name:" + et_name.getText());

                Intent myIntent = new Intent(MainActivity.this,
                        ActivityStateChecker.class);
                myIntent.putExtra("name", name);
                startActivity(myIntent);

            }else {
                tv_feedback.setText(R.string.enter_name);
                tv_feedback.setTextColor(RED);
            }

        }

    }
}
