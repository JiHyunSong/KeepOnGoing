package com.secsm.keepongoing.Alarm;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.secsm.keepongoing.R;

import java.util.ArrayList;

/**
 * Created by KMINSU-PC-W1 on 2014-08-11.
 */
public class AlramAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<AlramData> infoList = null;
    private ViewHolder viewHolder = null;
    public Context mContext = null;

    public AlramAdapter(Context c, ArrayList<AlramData> arrays) {
        this.mContext = c;
        this.inflater = LayoutInflater.from(c);
        this.infoList = arrays;
    }

    // Adapter가 관리할 Data의 개수를 설정 합니다.
    @Override
    public int getCount() {
        return infoList.size();
    }

    // Adapter가 관리하는 Data의 Item 의 Position을 <객체> 형태로 얻어 옵니다.
    @Override
    public AlramData getItem(int position) {
        return infoList.get(position);
    }

    // Adapter가 관리하는 Data의 Item 의 position 값의 ID 를 얻어 옵니다.
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ListView의 뿌려질 한줄의 Row를 설정 합니다.
    @Override
    public View getView(final int position, View convertview, ViewGroup parent) {

//        DBContactHelper helper = new DBContactHelper(convertview.getContext());
//        helper.addContact(new Contact("Name","Phone-number"));
/*
        DBContactHelper helper = new DBContactHelper(convertview.getContext());
        Contact contact;
        contact=helper.getContact(position);
        contact.gethour();
        contact.getminute();*/
        View v = convertview;
        final View temp=v;

        if (v == null) {
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.list_row, null);
            CheckBox vibratemode = (CheckBox) v.findViewById(R.id.vibratemode);
            vibratemode.setChecked(Preference.getBoolean(v.getContext(), "vibratemode"));
            if (position == 0) {


                vibratemode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        Preference.putBoolean(mContext, "vibratemode",isChecked);

                    }
                });
            }

            if (position == 1) {

                vibratemode.setVisibility(v.GONE);
            }



            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        Intent intent = new Intent(v.getContext(), Alarm_main.class);
                        intent.putExtra("position", position);
                        v.getContext().startActivity(intent);
                        //   Toast.makeText(v.getContext(), "선택된자의 이름은 기상시간", 2).show();
                    }
                    if (position == 1) {
                        Intent intent = new Intent(v.getContext(), Alarm_main.class);
                        intent.putExtra("position", position);
                        v.getContext().startActivity(intent);
                        //     Toast.makeText(v.getContext(), "선택된자의 이름은 목표시간", 2).show();
                    }



                }
            });

            viewHolder.tv_title = (TextView) v.findViewById(R.id.child_textview);
            viewHolder.tv_title.setText(infoList.get(position).name);
            v.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        return v;

    }


    // Adapter가 관리하는 Data List를 교체 한다.
    // 교체 후 Adapter.notifyDataSetChanged() 메서드로 변경 사실을
    // Adapter에 알려 주어 ListView에 적용 되도록 한다.
    public void setArrayList(ArrayList<AlramData> arrays) {
        this.infoList = arrays;
    }

    public ArrayList<AlramData> getArrayList() {
        return infoList;
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    /*
     * ViewHolder
     * getView의 속도 향상을 위해 쓴다.
     * 한번의 findViewByID 로 재사용 하기 위해 viewHolder를 사용 한다.
     */
    class ViewHolder {
        public TextView tv_title = null;
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    private void free() {
        inflater = null;
        infoList = null;
        viewHolder = null;
        mContext = null;
    }
}
