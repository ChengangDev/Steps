package com.free.cg.steps;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2017/3/2.
 */

public class DbMap {

    private static final String TAG = "DbMap";

    public DbMap(){}

    public static abstract class Plans implements BaseColumns{
        public static final String TABLE_NAME = "Plans";

        public static final String COL_CONTENT = "content";
        public static final String COL_TYPE = "type";
        public static final String COL_STATUS = "status";
        public static final String COL_CREATETIME = "createtime";
        public static final String COL_MEMO = "memo";
    }

    public static abstract class Steps implements BaseColumns{
        public static final String TABLE_NAME = "Steps";

        public static final String COL_PLANID = "plan_id";
        public static final String COL_STARTTIME = "start_time";
        public static final String COL_DUETIME = "due_time";
        public static final String COL_STATUS = "status";
    }
}
