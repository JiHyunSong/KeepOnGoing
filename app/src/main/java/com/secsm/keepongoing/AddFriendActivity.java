package com.secsm.keepongoing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.com.lanace.connecter.CallbackResponse;
import com.com.lanace.connecter.HttpAPIs;
import com.secsm.keepongoing.Shared.BaseActivity;
import com.secsm.keepongoing.Shared.KogPreference;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddFriendActivity extends BaseActivity {

    private BootstrapButton add_friend_add_btn;
    private BootstrapButton add_friend_go_back_btn;
    private EditText add_friend_name_et;
    String f_nickName;

    private static String LOG_TAG = "AddFriendActivity";
    private String rMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        add_friend_add_btn = (BootstrapButton) findViewById(R.id.add_friend_add_btn);
        add_friend_go_back_btn = (BootstrapButton) findViewById(R.id.add_friend_go_back_btn);
        add_friend_name_et = (EditText) findViewById(R.id.add_friend_name_et);
        add_friend_name_et.setFilters(new InputFilter[]{KogPreference.filterAlphaNumKor});

        add_friend_name_et.setPrivateImeOptions("defaultInputmode=english;");

        add_friend_add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                f_nickName = add_friend_name_et.getText().toString();
                if (isNicknameValid(f_nickName)) {

                    addFriendRequest(f_nickName);

                } else {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add_friend_go_back_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GoBackPage();
            }
        });

    }

    private boolean isNicknameValid(String nickName) {
        if(nickName.equals(KogPreference.getNickName(getBaseContext())))
        {
            Toast.makeText(getBaseContext(), "자기 자신은 초대할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return (nickName.length() >= 4) && (nickName.length() <= 10);
    }

    /** base Handler for Enable/Disable all UI components */
    Handler baseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
//                TODO : implement setAllEnable()
//                setAllEnable();
            }
            else if(msg.what == -1){
//                TODO : implement setAllDisable()
//                setAllDisable();
            }
        }
    };

    Handler errorHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 1){
                Toast.makeText(getBaseContext(), "연결이 원활하지 않습니다.\n잠시후에 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    };


    /** getMyInfoRequest
     * statusCode == 200 => get My info, Update UI
     */
    Handler addFriendRequestHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle b = msg.getData();
                JSONObject result = new JSONObject(b.getString("JSONData"));
                int statusCode = Integer.parseInt(result.getString("status"));
                if (statusCode == 200) {
                    rMessage = result.getString("message");
                    // real action

                    Toast.makeText(getBaseContext(), "친구로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 1000) {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 1001) {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 1002) {
                    Toast.makeText(getBaseContext(), "이미 친구로 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 9001) {
                    Toast.makeText(getBaseContext(), "친구 추가할 아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "통신 에러", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "통신 에러 : " + result.getString("message"));
                }
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    private void addFriendRequest(final String mf_nickName) {
        try {
            baseHandler.sendEmptyMessage(-1);
            HttpRequestBase addFriendRequest = HttpAPIs.addFriendPost(
                    KogPreference.getNickName(AddFriendActivity.this),
                    mf_nickName);
            HttpAPIs.background(addFriendRequest, new CallbackResponse() {
                public void success(HttpResponse response) {
                    baseHandler.sendEmptyMessage(1);
                    JSONObject result = HttpAPIs.getHttpResponseToJSON(response);
                    Log.e(LOG_TAG, "응답: " + result.toString());
                    if(result != null) {
                        Message msg = addFriendRequestHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("JSONData", result.toString());
                        msg.setData(b);
                        addFriendRequestHandler.sendMessage(msg);
                    }
                }

                public void error(Exception e) {
                    baseHandler.sendEmptyMessage(1);
                    errorHandler.sendEmptyMessage(1);
                    Log.i(LOG_TAG, "Response Error: " +  e.toString());
                    e.printStackTrace();
                }
        });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            GoBackPage();
        }
        return false;
    }

    private void GoBackPage() {
//        Intent intent = new Intent(AddFriendActivity.this, TabActivity.class);
//        startActivity(intent);
        AddFriendActivity.this.finish();
    }
}
