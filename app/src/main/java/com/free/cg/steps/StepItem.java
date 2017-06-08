package com.free.cg.steps;

/**
 * Created by ee on 17-6-6.
 */

public class StepItem {
    public int mStepID = -1;
    public int mPlanID = -1;
    public String mDate = "";
    public String mPlanContent = "";
    public String mPlanMemo = "";
    public int mStepStatus = DbMap.Steps.STATUS.READY;
    public int mCompleted = 1;
    public int mTotal = 1;
    public int mSuccess = 1;

}
