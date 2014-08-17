package com.secsm.keepongoing.Quiz;

/**
 * Created by kim on 2014-08-16.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.secsm.keepongoing.R;

import java.util.ArrayList;


/**
 * Created by KMINSU-PC-W1 on 2014-08-11.
 */
public class listAdapter_Solve extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<Quiz_data> infoList = null;
    private ViewHolder viewHolder = null;
    public Context mContext = null;

    public listAdapter_Solve(Context c, ArrayList<Quiz_data> arrays) {
        this.mContext = c;
        this.inflater = LayoutInflater.from(c);
        this.infoList = arrays;
    }

    public void addQuizData(Quiz_data data) {
        infoList.add(data);
        refresh();
    }

    public void refresh() {
        this.notifyDataSetChanged();
    }

    // Adapter가 관리할 Data의 개수를 설정 합니다.
    @Override
    public int getCount() {
        return infoList.size();

    }

    // Adapter가 관리하는 Data의 Item 의 Position을 <객체> 형태로 얻어 옵니다.
    @Override
    public Quiz_data getItem(int position) {
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
        View v = convertview;

        Log.i("minsu)", "minsu) check position : " + position);

        if (v == null) {
            //뷰생성
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.quiz_list_row, null);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            viewHolder.check1 = (CheckBox) v.findViewById(R.id.check1);


            viewHolder.check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //      viewHolder = (ViewHolder) v.getTag();

                    int position = (Integer) buttonView.getTag();
                    infoList.get(position).chk1 = isChecked;
                    //    Log.e("minsu)", "minsu) check conditon : " + "check1 with postion"+position+" set check1"+infoList.get(position).chk1);

                }
            });
            viewHolder.check2 = (CheckBox) v.findViewById(R.id.check2);

            viewHolder.check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (Integer) buttonView.getTag();
                    infoList.get(position).chk2 = isChecked;
                    //        Log.e("minsu)", "minsu) check conditon : " + "check2 with postion"+position+" set check2"+infoList.get(position).chk2);
                }
            });
            viewHolder.check3 = (CheckBox) v.findViewById(R.id.check3);
            viewHolder.check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (Integer) buttonView.getTag();
                    infoList.get(position).chk3 = isChecked;
                    //        Log.e("minsu)", "minsu) check conditon : " + "check3 with postion"+position+" set check3"+infoList.get(position).chk3);
                }
            });
            viewHolder.check4 = (CheckBox) v.findViewById(R.id.check4);
            viewHolder.check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (Integer) buttonView.getTag();
                    infoList.get(position).chk4 = isChecked;
                    // Log.e("minsu)", "minsu) check conditon : " + "check4 with postion"+position+" set check4"+infoList.get(position).chk4);
                }
            });
            viewHolder.check5 = (CheckBox) v.findViewById(R.id.check5);
            viewHolder.check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    int position = (Integer) buttonView.getTag();
                    infoList.get(position).chk5 = isChecked;
                    //      Log.e("minsu)", "minsu) check conditon : " + "check5 with postion"+position+" set check5"+infoList.get(position).chk5);

                }
            });


            viewHolder.essay = (EditText) v.findViewById(R.id.essay);
            viewHolder.essay.requestFocus();
            viewHolder.essay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        int position = (Integer) v.getTag();
                        infoList.get(position).essay = ((EditText) v).getText().toString();
                        Log.e("minsu)", "minsu) essay condition : " + "essay with position" + position + " set " + infoList.get(position).essay);
                    }
                }
            });

            //     viewHolder.TFGroup=(RadioGroup)v.findViewById(R.id.tfgroup);
            viewHolder.Truebtn = (RadioButton) v.findViewById(R.id.True);
            viewHolder.Falsebtn = (RadioButton) v.findViewById(R.id.False);
            viewHolder.TFGroup = (RadioGroup) v.findViewById(R.id.tfgroup);

//
//
//
            viewHolder.Truebtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    infoList.get(position).truebtn = true;
                    infoList.get(position).falsebtn = false;
                    infoList.get(position).isChecked = true;
                    Log.e("minsu)", "minsu) check conditon : " + "rbtn  with postion" + position + " true condition: " + infoList.get(position).truebtn + " false condition: " + infoList.get(position).falsebtn);
                }
            });


            viewHolder.Falsebtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    infoList.get(position).truebtn = false;
                    infoList.get(position).falsebtn = true;
                    infoList.get(position).isChecked = true;
                    Log.e("minsu)", "minsu) check conditon : " + "rbtn  with postion" + position + " true condition: " + infoList.get(position).truebtn + " false condition: " + infoList.get(position).falsebtn);
                }
            });
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
            //재활용
        }
        viewHolder.essay.clearFocus();

        viewHolder.check1.setTag(position);
        viewHolder.check2.setTag(position);
        viewHolder.check3.setTag(position);
        viewHolder.check4.setTag(position);
        viewHolder.check5.setTag(position);
        viewHolder.essay.setTag(position);
        viewHolder.Falsebtn.setTag(position);
        viewHolder.Truebtn.setTag(position);


        viewHolder.check1.setChecked(infoList.get(position).chk1);
        viewHolder.check2.setChecked(infoList.get(position).chk2);
        viewHolder.check3.setChecked(infoList.get(position).chk3);
        viewHolder.check4.setChecked(infoList.get(position).chk4);
        viewHolder.check5.setChecked(infoList.get(position).chk5);
        viewHolder.essay.setText(infoList.get(position).essay);
        viewHolder.TFGroup.clearCheck();
        viewHolder.Truebtn.setChecked(infoList.get(position).truebtn);
        viewHolder.Falsebtn.setChecked(infoList.get(position).falsebtn);


        Log.e("minsu) : ", "minsu) tf init : position" + position + " t/f : " + infoList.get(position).truebtn + " / " + infoList.get(position).falsebtn);


        if (infoList.get(position).name == "essay") {
            EditText essay = (EditText) v.findViewById(R.id.essay);
            essay.setVisibility(View.VISIBLE);
            RelativeLayout multiplechoice = (RelativeLayout) v.findViewById(R.id.multiple);
            multiplechoice.setVisibility(View.GONE);

            RelativeLayout tflayout = (RelativeLayout) v.findViewById((R.id.tflayout));
            tflayout.setVisibility(View.GONE);
        } else if (infoList.get(position).name == "tf") {

            EditText essay = (EditText) v.findViewById(R.id.essay);
            essay.setVisibility(View.GONE);
            RelativeLayout multiplechoice = (RelativeLayout) v.findViewById(R.id.multiple);
            multiplechoice.setVisibility(View.GONE);
            RelativeLayout tflayout = (RelativeLayout) v.findViewById((R.id.tflayout));
            tflayout.setVisibility(View.VISIBLE);


        } else {
            EditText essay = (EditText) v.findViewById(R.id.essay);
            essay.setVisibility(View.GONE);
            RelativeLayout multiplechoice = (RelativeLayout) v.findViewById(R.id.multiple);
            multiplechoice.setVisibility(View.VISIBLE);
            RelativeLayout tflayout = (RelativeLayout) v.findViewById((R.id.tflayout));
            tflayout.setVisibility(View.GONE);
        }
/*        Log.e("minsu)", "minsu) check clear : " + "at postion"+position+
                infoList.get(position).chk1+
                infoList.get(position).chk2+
                infoList.get(position).chk3+
                infoList.get(position).chk4+
                infoList.get(position).chk5
        );*/


        return v;
    }


    // Adapter가 관리하는 Data List를 교체 한다.
    // 교체 후 Adapter.notifyDataSetChanged() 메서드로 변경 사실을
    // Adapter에 알려 주어 ListView에 적용 되도록 한다.
//    public void setArrayList(ArrayList<Quiz_data> arrays){
//        this.infoList = arrays;
//    }

//    public ArrayList<Quiz_data> getArrayList(){
//        return infoList;
//    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


    class ViewHolder {
        public EditText essay = null;
        public CheckBox check1;
        public CheckBox check2;
        public CheckBox check3;
        public CheckBox check4;
        public CheckBox check5;
        public RadioGroup TFGroup;
        public RadioButton Truebtn;
        public RadioButton Falsebtn;


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


    public void onItemSelected(AdapterView<?> listView, View view, int position, long id) {
        View v = view;
        if (position == 1) {
            // listView.setItemsCanFocus(true);

            // Use afterDescendants, because I don't want the ListView to steal focus
            listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            v.findViewById(R.id.essay).requestFocus();
        } else {
            if (!listView.isFocused()) {
                // listView.setItemsCanFocus(false);

                // Use beforeDescendants so that the EditText doesn't re-take focus
                listView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
                listView.requestFocus();
            }
        }
    }

    public void onNothingSelected(AdapterView<?> listView) {
        // This happens when you start scrolling, so we need to prevent it from staying
        // in the afterDescendants mode if the EditText was focused
        listView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    }


}
