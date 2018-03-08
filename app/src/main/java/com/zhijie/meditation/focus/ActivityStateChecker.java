package com.zhijie.meditation.focus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseManagerAndroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

import MuseConnection.MuseConnectionHelper;

import static constants.Constants.SVM_MODEL;
import static constants.Constants.USE_MUSE;
import libsvm.*;

public class ActivityStateChecker extends Activity implements View.OnClickListener {

    private final String TAG = "ActivityStateChecker";
    private final Handler handler = new Handler();

    private MuseConnectionHelper museConnectionHelper;
    private MuseManagerAndroid manager;

    private String muse_status;

    private Context context;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = this;
        // Load the Muse Library
        manager = MuseManagerAndroid.getInstance();
        manager.setContext(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        museConnectionHelper = new MuseConnectionHelper();


        // Connect Muse Activity
        if (USE_MUSE) {
            Intent i = new Intent(this, ActivityConnectMuse.class);
            startActivityForResult(i, R.integer.SELECT_MUSE_REQUEST);
        } else {

        }

        try{
            AssetManager am = context.getApplicationContext().getAssets();
            InputStreamReader ims = new InputStreamReader(am.open(SVM_MODEL));
            BufferedReader reader = new BufferedReader(ims);

            svm.svm_load_model(reader);

        }catch (IOException e){
            Log.d(TAG,"Exception: " + e);
//            e.printStackTrace();
        }



    }

    private void initUI() {
        setContentView(R.layout.eeg_state_checker);

        TextView eeg1 = findViewById(R.id.eeg1);
        TextView eeg2 = findViewById(R.id.eeg2);
        TextView eeg3 = findViewById(R.id.eeg3);
        TextView eeg4 = findViewById(R.id.eeg4);

        TextView tv_muse_status = findViewById(R.id.tv_muse_status);

        museConnectionHelper.setEEGTextView(eeg1, eeg2, eeg3, eeg4);
        museConnectionHelper.setTv_muse_status(tv_muse_status);
        museConnectionHelper.updateGUI.run();


    }

    @Override
    public void onClick(View v) {

    }





    /*
     *  -------------- Return from startActivityForResult ------------------
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == R.integer.SELECT_MUSE_REQUEST) {
            if (resultCode == RESULT_OK) {

                int position = data.getIntExtra("pos", 0);
                List<Muse> availableMuse = manager.getMuses();
                connect_to_muse(availableMuse.get(position));
                initUI();
            } else {
                finish();
            }
        }
    }

    private void connect_to_muse(Muse muse) {
        museConnectionHelper.setMuse(muse);
        museConnectionHelper.connect_to_muse();
    }

//    @Override
//    public void onBackPressed() {
//
//        // TODO: Prompt dialog to ask if really want to exit
////        }
//
//    }

}
