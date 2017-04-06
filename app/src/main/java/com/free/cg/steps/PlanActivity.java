package com.free.cg.steps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

import java.util.List;
import java.util.Map;

public class PlanActivity extends AppCompatActivity {
    public static final String TAG = "PlanActivity";

    //
    private int mPlanID = -1;
    private EditText mEditContent = null;
    private EditText mEditMemo = null;
    private Switch mSwitchStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        mEditContent = (EditText)findViewById(R.id.editText_content);
        mEditMemo = (EditText)findViewById(R.id.editText_memo);
        mSwitchStatus = (Switch)findViewById(R.id.switch_active);
    }

    public void updatePlanAct(int nPlanID, String strContent, String strMemo, int nStatus){
        mPlanID = nPlanID;
        mEditContent.setText(strContent);
        mEditMemo.setText(strMemo);
        if(nStatus==1)
            mSwitchStatus.setChecked(true);
        else
            mSwitchStatus.setChecked(false);
    }

    public void updatePlanAct(int nPlanID){
        try{
            List<Map<String,String>> oList;
        }catch (Exception e){

        }
    }
}
