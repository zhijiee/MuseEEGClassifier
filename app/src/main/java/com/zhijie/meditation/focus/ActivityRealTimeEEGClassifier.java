package com.zhijie.meditation.focus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseManagerAndroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import controllers.MuseControllers.MuseConnectionHelper;
import libsvm.svm;
import libsvm.svm_model;

import static constants.AppConstants.SVM_MODEL;
import static constants.AppConstants.USE_MUSE;


public class ActivityRealTimeEEGClassifier extends Activity implements View.OnClickListener {

    private final String TAG = "RealTimeEEGClassifier";
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

        svm_model svmModel = null;
//        svm.svm_train()
        try{
            AssetManager am = getAssets();
            BufferedReader br = new BufferedReader(new InputStreamReader(am.open(SVM_MODEL)));
            svmModel = svm.svm_load_model(br);

        }catch (IOException e){
            e.printStackTrace();
        }

//        Log.d(TAG, gson.toJson(svmModel));
//        svm.svm_predict(svmModel, ,'b -1' );

    }

    private void initUI() {
        setContentView(R.layout.activity_eeg_realtime_classifier);

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
