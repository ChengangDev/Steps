package com.free.cg.steps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.free.cg.steps.DbMap.Plans;
import com.free.cg.steps.DbMap.Steps;
/**
 * Created by Administrator on 2017/3/2.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = "DbHelper";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "db.sqlite";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_PLANS =
            "CREATE TABLE " + Plans.TABLE_NAME + " (" +
                    Plans._ID + " INTEGER PRIMARY KEY," +
                    Plans.COL_CONTENT + TEXT_TYPE + COMMA_SEP +
                    Plans.COL_TYPE + INT_TYPE + COMMA_SEP +
                    Plans.COL_STATUS + INT_TYPE + COMMA_SEP +
                    Plans.COL_MEMO + TEXT_TYPE + COMMA_SEP +
                    Plans.COL_CREATETIME + DATE_TYPE +
                    ")";

    private static final String SQL_CREATE_STEPS =
            "CREATE TABLE " + Steps.TABLE_NAME + " (" +
                    Steps._ID + " INTEGER PRIMARY KEY," +
                    Steps.COL_PLANID + INT_TYPE + COMMA_SEP +
                    Steps.COL_STATUS + INT_TYPE + COMMA_SEP +
                    Steps.COL_STARTTIME + DATE_TYPE + COMMA_SEP +
                    Steps.COL_DUETIME + DATE_TYPE +
                    ")";

    public DbHelper(Context context, String strDbPath){
        super(context, strDbPath, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLANS);
        db.execSQL(SQL_CREATE_STEPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        final String strTempTbl = "tempUpdTbl101";
        DropTable(db, strTempTbl);

        onCreate(db);
    }

    private void DropTable(SQLiteDatabase db, String strTbl){
        String strSql = String.format("DROP TABLE IF EXISTS %s", strTbl);
        db.execSQL(strSql);
    }

    private void CopyCreateTable(SQLiteDatabase db, String strFrom, String strTo){
        String strSql;
    }
}
