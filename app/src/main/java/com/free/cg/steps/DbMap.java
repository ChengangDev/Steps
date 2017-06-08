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

        public static class STATUS{
            public static final int YES = 1;
            public static final int NO = 0;
        }
    }

    public static abstract class Steps implements BaseColumns{
        public static final String TABLE_NAME = "Steps";

        public static final String COL_PLANID = "plan_id";
        public static final String COL_STARTTIME = "start_time";
        public static final String COL_STATUS = "status";
        public static final String COL_SUCCESS = "success";

        public static class STATUS{
            public static final int READY = 0;
            public static final int FAIL = -1;
            public static final int OK = 1;
        }
    }

}
