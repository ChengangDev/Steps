package com.free.cg.steps;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by ee on 17-6-7.
 */

public class StepsChecker {
    private static final String TAG = "StepChecker";
    private Context mContext;
    private DbOperator mDbOp;

    public StepsChecker(Context ctx){
        mContext = ctx;
        mDbOp = new DbOperator(mContext);
    }

    public int addSteps(String strDate){
        Log.d(TAG, String.format("addSteps of %s", strDate));
        String strWhere = DbMap.Plans.COL_STATUS + " = ?";
        String[] args = {
                String.valueOf(DbMap.Plans.STATUS.YES)
        };
        List<Map<String, String>> plans = mDbOp.getPlansList(strWhere, args);
        for(int i = 0; i < plans.size(); ++i){
            int nPlanID = Integer.valueOf(plans.get(i).get(DbMap.Plans._ID));
            mDbOp.addStep(nPlanID, strDate);
        }

        return plans.size();
    }

    public int clearSteps(String strDate){
        Log.d(TAG, String.format("clearSteps of %s", strDate));
        List<Map<String, String>> steps = mDbOp.getStepsList(strDate);
        for(int i = 0; i < steps.size(); ++i){
            int nStatus = Integer.valueOf(steps.get(i).get(DbMap.Steps.COL_STATUS));
            int nStepID = Integer.valueOf(steps.get(i).get(DbMap.Steps._ID));
            if(nStatus != DbMap.Steps.STATUS.OK)
            {
                mDbOp.finishStep(nStepID, DbMap.Steps.STATUS.FAIL);
            }
        }
        return steps.size();
    }

    public void refresh(){
        String strToday = mDbOp.getCurDateStr();
        String strLatest = mDbOp.getLatestDate();

        if(strLatest.isEmpty()) {
            Log.d(TAG, "Latest is Empty, add Today " + strToday);
            addSteps(strToday);
            return;
        }

        if(strLatest.equalsIgnoreCase(strToday) || strLatest.compareTo(strToday) > 0) {
            Log.d(TAG, "Latest is not earlier than today, finish.");
            return;
        }

        clearSteps(strLatest);
        addSteps(mDbOp.getOffsetDateStr(strLatest, 1));

        Log.d(TAG, "Start another refresh...");
        refresh();
    }
}
