package com.narify.awarm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.narify.awarm.models.Alarm;
import com.narify.awarm.R;
import com.narify.awarm.databinding.ItemAlarmBinding;
import com.narify.awarm.utilities.DateTimeUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    List<Alarm> mList;
    ListItemEventListeners mItemEventListeners;

    public AlarmAdapter(List<Alarm> alarmList, ListItemEventListeners itemEventListeners) {
        mList = alarmList;
        mItemEventListeners = itemEventListeners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = mList.get(position);
        String time = DateTimeUtils.formatTime(alarm.getHour(), alarm.getMinute());
        String ampm = DateTimeUtils.getAmPm(alarm.getHour(), alarm.getMinute());

        holder.mBinding.cbActivateAlarm.setChecked(alarm.isActivated());
        holder.mBinding.tvAlarmClock.setText(time);
        holder.mBinding.tvAlarmAMPM.setText(ampm);
        holder.mBinding.tvAlarmLabel.setText(alarm.getLabel());
    }

    public List<Alarm> getList() {
        return mList;
    }

    public void setList(List<Alarm> alarmList) {
        mList = alarmList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface ListItemEventListeners {
        void OnAlarmItemClicked(int position);

        void OnAlarmItemCheckedChange(int position, boolean isChecked);

        void OnAlarmItemDeleted(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemAlarmBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemAlarmBinding.bind(itemView);

            itemView.setOnClickListener(this);
            mBinding.cbActivateAlarm.setOnClickListener(this);
            mBinding.ibDeleteAlarmItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            switch (view.getId()) {
                case R.id.cl_alarm_item_root:
                    mItemEventListeners.OnAlarmItemClicked(position);
                    break;
                case R.id.cb_activate_alarm:
                    boolean isChecked = ((CheckBox) view).isChecked();
                    mItemEventListeners.OnAlarmItemCheckedChange(position, isChecked);
                    break;
                case R.id.ib_delete_alarm_item:
                    mItemEventListeners.OnAlarmItemDeleted(position);
                    break;
            }
        }


    }
}
