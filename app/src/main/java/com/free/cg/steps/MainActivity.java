package com.free.cg.steps;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private boolean mShowSteps = true;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //updateStepsListView();
        updateMainAct(mShowSteps);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            Intent intent = new Intent(this, PlanActivity.class);
            intent.putExtra(DbMap.Plans._ID, -1);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_switch){
            updateMainAct(!mShowSteps);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMainAct(mShowSteps);
    }

    public void onClickToggle(View view){
        Log.d(TAG, "onClickToggle");
        DbOperator op = new DbOperator(this);
        ToggleButton tg = (ToggleButton)view;
        int nStepID = Integer.valueOf(view.getTag().toString());
        if(tg.isChecked()){
            op.finishStep(nStepID, DbMap.Steps.STATUS.OK);
        }else{
            op.finishStep(nStepID, DbMap.Steps.STATUS.READY);
        }
    }

    private void updateMainAct(boolean bShowSteps){
        mShowSteps = bShowSteps;
        if(mShowSteps){
            if(mMenu!=null)
                mMenu.findItem(R.id.action_switch).setTitle("Steps");
            updateStepsListView();
        } else{
            if(mMenu!=null)
                mMenu.findItem(R.id.action_switch).setTitle("Plans");
            updatePlansListView();
        }
    }

    private void updateStepsListView(){

        //s
        StepsChecker chk = new StepsChecker(this);
        chk.refresh();

        ListView lv = (ListView)findViewById(R.id.listview_steps);
        DbOperator dbOp = new DbOperator(this);
        final List<Map<String,String>> oStepsList = dbOp.getStepsList(dbOp.getCurDateStr());
        List<StepItem> items = new ArrayList<>(oStepsList.size());
        for(int i = 0; i < oStepsList.size(); ++i){
            Map<String, String> m = oStepsList.get(i);
            StepItem item = new StepItem();
            item.mStepID = Integer.valueOf(m.get(DbMap.Steps._ID));
            item.mPlanID = Integer.valueOf(m.get(DbMap.Steps.COL_PLANID));
            item.mDate = m.get(DbMap.Steps.COL_STARTTIME);
            //包含待命的次数
            item.mCompleted = dbOp.count(DbMap.Steps.TABLE_NAME,
                    String.format("%s != %d and %s = %d",
                            DbMap.Steps.COL_STATUS, DbMap.Steps.STATUS.FAIL,
                            DbMap.Steps.COL_PLANID, item.mPlanID));
            item.mTotal = dbOp.count(DbMap.Steps.TABLE_NAME,
                    String.format("%s = %d", DbMap.Steps.COL_PLANID, item.mPlanID));
            item.mSuccess = Integer.valueOf(m.get(DbMap.Steps.COL_SUCCESS));
            item.mStepStatus = Integer.valueOf(m.get(DbMap.Steps.COL_STATUS));
            item.mPlanContent = m.get(DbMap.Plans.COL_CONTENT);
            item.mPlanMemo = m.get(DbMap.Plans.COL_MEMO);
            items.add(item);
        }

        StepAdapter adpt = new StepAdapter(this, R.layout.item_of_steps_list, items);
        lv.setAdapter(adpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0 && position < oStepsList.size())
                {
                    int nPlanID = Integer.valueOf(oStepsList.get(position).get(DbMap.Steps.COL_PLANID));
                    Intent intent = new Intent(MainActivity.this, PlanActivity.class);
                    intent.putExtra(DbMap.Plans._ID, nPlanID);
                    startActivity(intent);
                }
            }
        });
    }

    private void updatePlansListView(){

        ListView lv = (ListView)findViewById(R.id.listview_steps);
        DbOperator dbOp = new DbOperator(this);
        final List<Map<String,String>> oPlansList = dbOp.getPlansList(null, null);
        SimpleAdapter adpt = new SimpleAdapter(this, oPlansList, R.layout.item_of_plans_list,
                new String[]{
                        DbOperator.KEY_PLAN_INDEX,
                        DbOperator.KEY_PALN_STATUS,
                        DbMap.Plans.COL_CONTENT
                },
                new int[]{
                        R.id.textview_index,
                        R.id.textview_status,
                        R.id.textview_content
                });

        lv.setAdapter(adpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0 && position < oPlansList.size())
                {
                    int nPlanID = Integer.valueOf(oPlansList.get(position).get(DbMap.Plans._ID));
                    Intent intent = new Intent(MainActivity.this, PlanActivity.class);
                    intent.putExtra(DbMap.Plans._ID, nPlanID);
                    startActivity(intent);
                }
            }
        });
    }
}
