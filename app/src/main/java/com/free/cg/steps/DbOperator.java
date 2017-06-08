package com.free.cg.steps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static final String KEY_PLAN_INDEX = "plan_index";
    public static final String KEY_PALN_STATUS = "plan_status";

    private static final String TAG = "DbOperator";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private DbHelper mDbHelper;

    public DbOperator(Context context){
        mDbHelper = new DbHelper(context, StepsApplication.getStepAppliction().getDbPath());
    }

    public static String getCurDateStr(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getOffsetDateStr(String strDate, int nOffset){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = dateFormat.parse(strDate);

            Calendar oCal = Calendar.getInstance();
            oCal.setTime(date);
            oCal.add(Calendar.DATE, nOffset);
            Date offsetDate = oCal.getTime();

            return dateFormat.format(offsetDate);
        }catch(ParseException e){

        }

        return strDate;
    }

    public String getLatestDate(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = String.format("select %s from %s order by %s desc limit 1",
                Steps.COL_STARTTIME, Steps.TABLE_NAME, Steps.COL_STARTTIME);
        Cursor c = db.rawQuery(sql, null);
        if(c.getCount()>0){
            c.moveToFirst();
            String str = c.getString(c.getColumnIndexOrThrow(Steps.COL_STARTTIME));
            Log.d(TAG, String.format("getLatestDate %s", str));
            c.close();
            db.close();
            return str;
        }

        return "";
    }

    public List<Map<String, String>> getStepsList(String strDate){
        List<Map<String,String>> oList = new ArrayList<>();

        String strSql = String.format("select %s.%s, %s.%s, %s, %s, %s, %s, %s from %s, %s where %s.%s = %s.%s and %s = '%s'",
                Steps.TABLE_NAME, Steps._ID,
                Steps.TABLE_NAME, Steps.COL_STATUS,
                Steps.COL_PLANID, Steps.COL_STARTTIME, Steps.COL_SUCCESS,
                Plans.COL_CONTENT, Plans.COL_MEMO,
                Steps.TABLE_NAME, Plans.TABLE_NAME,
                Steps.TABLE_NAME, Steps.COL_PLANID,
                Plans.TABLE_NAME, Plans._ID,
                Steps.COL_STARTTIME, strDate);
        SQLiteDatabase oDb = mDbHelper.getReadableDatabase();
        Cursor c = oDb.rawQuery(strSql, null);
        if(c.getCount()>0) {
            c.moveToFirst();
            do {
                Map<String, String> m = new HashMap<String, String>();
                m.put(Steps._ID,
                        c.getString(c.getColumnIndexOrThrow(Steps._ID)));
                m.put(Steps.COL_PLANID,
                        c.getString(c.getColumnIndexOrThrow(Steps.COL_PLANID)));
                m.put(Steps.COL_STATUS,
                        c.getString(c.getColumnIndexOrThrow(Steps.COL_STATUS)));
                m.put(Steps.COL_STARTTIME,
                        c.getString(c.getColumnIndexOrThrow(Steps.COL_STARTTIME)));
                m.put(Steps.COL_SUCCESS,
                        c.getString(c.getColumnIndexOrThrow(Steps.COL_SUCCESS)));
                m.put(Plans.COL_CONTENT,
                        c.getString(c.getColumnIndexOrThrow(Plans.COL_CONTENT)));
                m.put(Plans.COL_MEMO,
                        c.getString(c.getColumnIndexOrThrow(Plans.COL_MEMO)));
                oList.add(m);
            } while (c.moveToNext());
        }
        c.close();
        oDb.close();
        return oList;
    }

    /**
     *
     * @param strDate
     * @param nPlanID
     * @return 不存在则返回0
     */
    public int getSuccessiveSteps(String strDate, int nPlanID){
        String[] cols = {
                Steps._ID, Steps.COL_SUCCESS, Steps.COL_STARTTIME, Steps.COL_STATUS
        };
        String selection = Steps.COL_STARTTIME + " = ? and " + Steps.COL_PLANID + " = ?";
        String[] selectionArgs = {
                strDate,
                String.format("%d", nPlanID)
        };
        SQLiteDatabase oDb = mDbHelper.getReadableDatabase();
        Cursor c = oDb.query(Steps.TABLE_NAME, cols, selection, selectionArgs, null, null, null);

        int nSucc = 1;
        if(c.getCount()>0) {
            c.moveToFirst();
            int nStatus = c.getInt(c.getColumnIndexOrThrow(Steps.COL_STATUS));
            nSucc = c.getInt(c.getColumnIndexOrThrow(Steps.COL_SUCCESS));

        }else
            nSucc = 0;
        c.close();
        oDb.close();
        return nSucc;
    }

    /**
     *
     * @param nPlanID
     * @param strDate
     * @return
     */
    public long addStep(int nPlanID, String strDate){
        ContentValues oVals = new ContentValues();
        oVals.put(Steps.COL_PLANID, nPlanID);
        oVals.put(Steps.COL_STARTTIME, strDate);
        oVals.put(Steps.COL_STATUS, Steps.STATUS.READY);
        //前一天打+1
        oVals.put(Steps.COL_SUCCESS, getSuccessiveSteps(getOffsetDateStr(strDate, -1), nPlanID)+1);
        SQLiteDatabase oDb = mDbHelper.getWritableDatabase();
        return oDb.insert(Steps.TABLE_NAME, "", oVals);
    }

    public boolean finishStep(int nStepID, int nStatus){
        ContentValues oVals = new ContentValues();
        oVals.put(Steps.COL_STATUS, nStatus);
        if(nStatus==Steps.STATUS.FAIL)
        oVals.put(Steps.COL_SUCCESS, 0);
        String strWhere = Steps._ID + " = ?";
        String[] arrArgs = {
                String.format("%d", nStepID)
        };
        SQLiteDatabase oDb = mDbHelper.getWritableDatabase();
        oDb.update(Steps.TABLE_NAME, oVals, strWhere, arrArgs);
        return true;
    }

    public List<Map<String, String>> getPlansList(String strWhere, String[] args){
        List<Map<String,String>> oList = new ArrayList<>();

        String[] selection = {
                Plans._ID, Plans.COL_CONTENT, Plans.COL_STATUS
        };
        SQLiteDatabase oDb = mDbHelper.getReadableDatabase();
        Cursor c = oDb.query(Plans.TABLE_NAME, selection, strWhere, args, null, null, null);
        if(c.getCount()>0) {
            c.moveToFirst();
            int nIndex = 1;
            do {
                Map<String, String> m = new HashMap<String, String>();
                m.put(KEY_PLAN_INDEX, String.valueOf(nIndex));
                m.put(Plans.COL_CONTENT,
                        c.getString(c.getColumnIndexOrThrow(Plans.COL_CONTENT)));
                m.put(Plans._ID,
                        c.getString(c.getColumnIndexOrThrow(Plans._ID)));
                m.put(KEY_PALN_STATUS,
                        c.getInt(c.getColumnIndexOrThrow(Plans.COL_STATUS))==Plans.STATUS.YES?"有效":"无效");
                oList.add(m);
                nIndex++;
            } while (c.moveToNext());
        }
        c.close();
        oDb.close();
        return oList;
    }

    public Map<String,String> getPlanDetail(int nPlanID){
        Map<String, String> m = new HashMap<String, String>();
        String[] cols = {
            Plans._ID, Plans.COL_CONTENT, Plans.COL_MEMO, Plans.COL_STATUS, Plans.COL_TYPE
        };
        String selection = Plans._ID + " = ?";
        String[] selectionArgs = {
            String.format("%d", nPlanID)
        };
        SQLiteDatabase oDb = mDbHelper.getReadableDatabase();
        Cursor c = oDb.query(Plans.TABLE_NAME, cols, selection, selectionArgs, null, null, null);
        if(c.getCount()>0) {
            c.moveToFirst();
            m.put(Plans._ID,
                    c.getString(c.getColumnIndexOrThrow(Plans._ID)));
            m.put(Plans.COL_CONTENT,
                    c.getString(c.getColumnIndexOrThrow(Plans.COL_CONTENT)));
            m.put(Plans.COL_MEMO,
                    c.getString(c.getColumnIndexOrThrow(Plans.COL_MEMO)));
            m.put(Plans.COL_STATUS,
                    c.getString(c.getColumnIndexOrThrow(Plans.COL_STATUS)));
            m.put(Plans.COL_TYPE,
                    c.getString(c.getColumnIndexOrThrow(Plans.COL_TYPE)));
        }
        c.close();
        oDb.close();
        return m;
    }

    public long addPlan(String strContent, String strMemo, int nStatus, int nType){
        ContentValues oVals = new ContentValues();
        oVals.put(Plans.COL_CONTENT, strContent);
        oVals.put(Plans.COL_MEMO, strMemo);
        oVals.put(Plans.COL_STATUS, nStatus);
        oVals.put(Plans.COL_TYPE, nType);
        oVals.put(Plans.COL_CREATETIME, getCurDateStr());
        SQLiteDatabase oDb = mDbHelper.getWritableDatabase();
        return oDb.insert(Plans.TABLE_NAME, "", oVals);
    }

    public int updatePlanDetail(int nPlanID, String strContent, String strMemo, int nStatus, int nType){
        ContentValues oVals = new ContentValues();
        oVals.put(Plans.COL_CONTENT, strContent);
        oVals.put(Plans.COL_MEMO, strMemo);
        oVals.put(Plans.COL_STATUS, nStatus);
        oVals.put(Plans.COL_TYPE, nType);
        String strWhere = Plans._ID + " = ?";
        String[] arrArgs = {
                String.format("%d", nPlanID)
        };
        SQLiteDatabase oDb = mDbHelper.getWritableDatabase();
        oDb.update(Plans.TABLE_NAME, oVals, strWhere, arrArgs);
        return 1;
    }

    public boolean deletePlan(int nPlanID, boolean bDeteteSteps){
        String strWhere = Plans._ID + " = ?";
        String[] arrArgs = {
                String.format("%d", nPlanID)
        };
        SQLiteDatabase oDb = mDbHelper.getWritableDatabase();
        oDb.delete(Plans.TABLE_NAME, strWhere, arrArgs);

        if(bDeteteSteps){
            strWhere = Steps.COL_PLANID + " = ?";
            oDb.delete(Steps.TABLE_NAME, strWhere, arrArgs);
        }
        oDb.close();
        return true;
    }

    public int count(String tableName, String where){
        String sql = String.format("select count(*) from %s where %s", tableName, where);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        if(c.getCount()>0){
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            db.close();
            return count;
        }
        return 0;
    }

}
