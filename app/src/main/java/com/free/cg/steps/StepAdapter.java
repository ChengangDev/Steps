package com.free.cg.steps;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Created by ee on 17-6-6.
 */

public class StepAdapter extends ArrayAdapter<StepItem> {
    private static final String TAG = "StepAdapter";

    public StepAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<StepItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_of_steps_list, null);
        }

        StepItem item = getItem(position);
        TextView tvIndex = (TextView)convertView.findViewById(R.id.textview_index);
        tvIndex.setText(String.valueOf(position+1));

        TextView tvContent = (TextView)convertView.findViewById(R.id.textview_content);
        tvContent.setText(item.mPlanContent);
        TextView tvCount = (TextView)convertView.findViewById(R.id.textview_count);
        tvCount.setText(String.format("%d/%d(%.0f%%)", item.mCompleted, item.mTotal, new Double(item.mCompleted)/item.mTotal*100));
        TextView tvSucc = (TextView)convertView.findViewById(R.id.textview_success);
        tvSucc.setText(String.format("连续%d", item.mSuccess));
        ToggleButton tg = (ToggleButton)convertView.findViewById(R.id.button_status);
        tg.setChecked(item.mStepStatus== DbMap.Steps.STATUS.OK);
        tg.setTag(item.mStepID);
        return convertView;
    }
}
