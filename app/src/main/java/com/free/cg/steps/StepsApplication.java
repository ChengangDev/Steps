package com.free.cg.steps;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2017/3/2.
 */

public class StepsApplication extends Application {
    public static final String TAG = "StepAppliction";

    private static StepsApplication singleApp;

    private String mHomePath;

    public static StepsApplication getStepAppliction(){
        return singleApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleApp = this;
        mHomePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/FreeSteps";

        Log.d(TAG, String.format("HomePath: %s", mHomePath));
    }

    public String getHomePath(){
        File dir = new File(mHomePath);
        if(!dir.exists())
            dir.mkdirs();
        return mHomePath;
    }

    public String getDbPath(){
        String path = getHomePath() + "/"
                + DbHelper.DB_NAME;
        return path;
    }
}
