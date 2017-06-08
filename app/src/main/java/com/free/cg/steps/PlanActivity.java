package com.free.cg.steps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class PlanActivity extends AppCompatActivity {
    public static final String TAG = "PlanActivity";

    //
    private int mPlanID = -1;
    private EditText mEditContent = null;
    private EditText mEditMemo = null;
    private Switch mSwitchStatus = null;
    private Button mBtnDel = null;
    private Button mBtnSave = null;
    private Button mBtnAdd = null;

    private DbOperator mOP = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        mEditContent = (EditText)findViewById(R.id.edittext_content);
        mEditMemo = (EditText)findViewById(R.id.edittext_memo);
        mSwitchStatus = (Switch)findViewById(R.id.switch_active);
        mBtnAdd = (Button)findViewById(R.id.button_add);
        mBtnDel = (Button)findViewById(R.id.button_delete);
        mBtnSave = (Button)findViewById(R.id.button_save);

        mOP = new DbOperator(this);

        Intent intent = getIntent();
        mPlanID = intent.getIntExtra(DbMap.Plans._ID, -1);
        updatePlanAct(mPlanID);
    }

    public void onDeletePlan(View view){
        if(mPlanID != -1){
            mOP.deletePlan(mPlanID, true);
            mPlanID = -1;
            updatePlanAct(mPlanID);
            Toast.makeText(this, "Delete OK", Toast.LENGTH_SHORT).show();
            finish();
        }else
            Toast.makeText(this, "Wrong PlanID", Toast.LENGTH_SHORT).show();
    }

    public void onSavePlan(View view){
        if(mPlanID != -1){
            try {
                String strContent = mEditContent.getText().toString();
                String strMemo = mEditMemo.getText().toString();
                int nStatus = mSwitchStatus.isChecked() ? DbMap.Plans.STATUS.YES : DbMap.Plans.STATUS.NO;
                mOP.updatePlanDetail(mPlanID, strContent, strMemo, nStatus, 0);
                Toast.makeText(this, "Update OK", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else
            Toast.makeText(this, "Wrong PlanID", Toast.LENGTH_SHORT).show();
    }

    public void onAddPlan(View view){
        if(mPlanID == -1){
            String strContent = mEditContent.getText().toString();
            String strMemo = mEditMemo.getText().toString();
            int nStatus = mSwitchStatus.isChecked()? DbMap.Plans.STATUS.YES: DbMap.Plans.STATUS.NO;
            mPlanID = (int)mOP.addPlan(strContent, strMemo, nStatus, 0);

            if(mPlanID != -1){
                mOP.addStep(mPlanID, mOP.getCurDateStr());
                Toast.makeText(this, "Add OK", Toast.LENGTH_SHORT).show();
                updatePlanAct(mPlanID);
                finish();
            }else
                Toast.makeText(this, "Add Failed", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(this, "Wrong PlanID", Toast.LENGTH_SHORT).show();
    }

    public void onSwitchStatus(View view){
        if(mPlanID != -1){
            int nStatus = mSwitchStatus.isChecked()? DbMap.Plans.STATUS.YES: DbMap.Plans.STATUS.NO;
            String strContent = mEditContent.getText().toString();
            String strMemo = mEditMemo.getText().toString();
            mOP.updatePlanDetail(mPlanID, strContent, strMemo, nStatus, 0);
        }
    }

    private void updatePlanAct(int nPlanID){

        updateBtnStatus(nPlanID);
        if(nPlanID==-1)
            return;

        try{
            Map<String,String> oVals = mOP.getPlanDetail(nPlanID);
            mPlanID = Integer.valueOf(oVals.get(DbMap.Plans._ID));
            mEditContent.setText(oVals.get(DbMap.Plans.COL_CONTENT));
            mEditMemo.setText(oVals.get(DbMap.Plans.COL_MEMO));
            int nStatus = Integer.valueOf(oVals.get(DbMap.Plans.COL_STATUS));
            mSwitchStatus.setChecked(nStatus== DbMap.Plans.STATUS.YES);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateBtnStatus(int nPlanID){
        if(nPlanID==-1) {
            mBtnAdd.setEnabled(true);
            mBtnSave.setEnabled(false);
            mBtnDel.setEnabled(false);
        }else{
            mBtnAdd.setEnabled(false);
            mBtnDel.setEnabled(true);
            mBtnSave.setEnabled(true);
        }

    }
}
