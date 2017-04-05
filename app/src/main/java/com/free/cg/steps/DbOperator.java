package com.free.cg.steps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.free.cg.steps.DbMap.Plans;
import com.free.cg.steps.DbMap.Steps;

/**
 * Created by Administrator on 2017/3/29.
 */

public class DbOperator {
    private static final String TAG = "DbOperator";

    private DbHelper mDbHelper;

    public DbOperator(Context context){
        mDbHelper = new DbHelper(context, StepsApplication.getStepAppliction().getDbPath());
    }

    public static String getCurDateStr(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public List<Map<String, String>> getStepsList(String strDate){
        List<Map<String,String>> oList = new ArrayList<>();

        String strSql = String.format("select %s, %s, %s, %s from %s, %s where %s = %s and %s = '%s'",
                DbMap.Steps.COL_STATUS, Steps.COL_STARTTIME, DbMap.Plans.COL_CONTENT, DbMap.Plans.COL_MEMO,
                DbMap.Steps.COL_PLANID, DbMap.Plans._ID,
                DbMap.Steps.COL_STARTTIME, strDate);
        SQLiteDatabase oDb = mDbHelper.getReadableDatabase();
        Cursor c = oDb.rawQuery(strSql, null);
        if(c.getCount()==0)
            return oList;

        do{
            Map<String,String> m = new HashMap<String,String>();
            m.put(Steps.COL_STATUS,
                    c.getString(c.getColumnIndexOrThrow(Steps.COL_STATUS)));
            m.put(Steps.COL_STARTTIME,
                    c.getString(c.getColumnIndexOrThrow(Steps.COL_STARTTIME)));
            m.put(Plans.COL_CONTENT,
                    c.getString(c.getColumnIndexOrThrow(Plans.COL_CONTENT)));
            m.put(Plans.COL_MEMO,
                    c.getString(c.getColumnIndexOrThrow(Plans.COL_MEMO)));
            oList.add(m);
        }while(c.moveToNext());

        return oList;
    }
}