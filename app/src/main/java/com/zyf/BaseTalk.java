package com.zyf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.jpush.Gson;
import com.google.gson.jpush.reflect.TypeToken;
import com.qiniu.android.jpush.storage.Recorder;
import com.zyf.util.ItemBean;
import com.zyf.util.MsgAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

public class BaseTalk extends AppCompatActivity {

    UserInfo info;
    EditText editText1 = null ;
    ListView showTxt = null;
    Conversation conversation = null;

    String tagetUserName= "";
    List<ItemBean> itemBeanList = null;

    protected void  init(){
        JMessageClient.registerEventReceiver(this);

        itemBeanList  = new ArrayList<>();
        editText1 =  (EditText)findViewById(R.id.txt);
        showTxt = (ListView)findViewById(R.id.showTxt);
        showTxt.setAdapter(new MsgAdapter(this,itemBeanList));

        info = JMessageClient.getMyInfo();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_talk);
        init();

        //设置ListView的数据适配器
       //

        String userName = "";
        if(info.getUserName()=="zyf0744"){
            userName ="llb520";
        }
        else {
            userName ="zyf0744";
        }
        tagetUserName = userName;
        //第一种方式
        Button Btn1 = (Button)findViewById(R.id.button);//获取按钮资源
        Btn1.setOnClickListener(new Button.OnClickListener(){//创建监听
            public void onClick(View v) {
                String userName = "";
                if(info.getUserName()=="zyf0744"){
                    userName ="llb520";
                }
                else {
                    userName ="zyf0744";
                }
                editText1 =  (EditText)findViewById(R.id.txt);
                String msg= "test";
                msg = editText1.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    Toast.makeText(getApplicationContext(), "请输入消息", Toast.LENGTH_SHORT).show();
                    return;
                }
                Conversation mConversation = JMessageClient.getSingleConversation(userName, "da3a531d69e0ab66664f3493");
                if (mConversation == null) {
                    mConversation = Conversation.createSingleConversation(userName, "da3a531d69e0ab66664f3493");
                }
                Message m = JMessageClient.createSingleTextMessage(userName, msg);
               // mConversation.createSendMessage(m.getContent(), userName);
                m.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Log.i("发送成功", "JMessageClient.createSingleTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("发送失败", "JMessageClient.createSingleTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
                            Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //设置消息发送时的一些控制参数
                MessageSendingOptions options = new MessageSendingOptions();
                options.setNeedReadReceipt(true);//是否需要对方用户发送消息已读回执
                options.setRetainOffline(true);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
                options.setShowNotification(true);//是否让对方展示sdk默认的通知栏通知
                options.setCustomNotificationEnabled(true);//是否需要自定义对方收到这条消息时sdk默认展示的通知栏中的文字
                if (true) {
                    options.setNotificationTitle("上新啦！");//自定义对方收到消息时通知栏展示的title
                    options.setNotificationAtPrefix("淘宝推荐");//自定义对方收到消息时通知栏展示的@信息的前缀
                    options.setNotificationText("亲，有新的商品推荐，快来看看吧");//自定义对方收到消息时通知栏展示的text
                }

                //发送消息
                JMessageClient.sendMessage(m, options);
               // showTxt.append("我:"+ msg+"\n");
                itemBeanList.add(new ItemBean(R.mipmap.ic_launcher, userName , msg,new Date()));
                //设置ListView的数据适配器
                showTxt.setAdapter(new MsgAdapter(getApplicationContext(),itemBeanList));
                showTxt.setSelection(showTxt.getBottom());
                editText1.setText("");
                //HideKeyboard(editText1);
            }

        });
    }

    public static void HideKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext( ).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken() , 0 );
        }
    }
    //接受消息的事件
    public void onEventMainThread(MessageEvent event) {
        Message msg = event.getMessage();
        switch (msg.getContentType()) {
            case text:
                // 处理文字消息
                TextContent textContent = (TextContent) msg.getContent();
                String username= msg.getFromUser().getUserName();
                String msgText = textContent.getText().toString();
                itemBeanList.add(new ItemBean(R.mipmap.ic_launcher, username,msgText,new Date()));
                //设置ListView的数据适配器
                showTxt.setAdapter(new MsgAdapter(getApplicationContext(),itemBeanList));
                showTxt.setSelection(showTxt.getBottom());
                break;
        }
    }
    public void onEvent(NotificationClickEvent event) {
        Intent notificationIntent = new Intent(this,BaseTalk.class);
        this.startActivity(notificationIntent);// 自定义跳转到指定页面
    }
    public void onEvent(OfflineMessageEvent event) {
        //获取事件发生的会话对象
        conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        Toast.makeText(BaseTalk.this, newMessageList.size(),Toast.LENGTH_SHORT).show();
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
    }


    /**
     如果在JMessageClient.init时启用了消息漫游功能，则每当一个会话的漫游消息同步完成时
     sdk会发送此事件通知上层。
     **/
    public void onEvent(ConversationRefreshEvent event) {
        //获取事件发生的会话对象
         conversation = event.getConversation();
        //获取事件发生的原因，对于漫游完成触发的事件，此处的reason应该是
        //MSG_ROAMING_COMPLETE
        ConversationRefreshEvent.Reason reason = event.getReason();
        List<Message> msgs =  conversation.getAllMessage();

        for(Message m : msgs){
            Toast.makeText(BaseTalk.this,m.getContent().toString(),Toast.LENGTH_SHORT).show();
            itemBeanList.add(new ItemBean(R.mipmap.ic_launcher, m.getFromUser().getUserName(),m.getContent().toJson().toString(),new Date()));
            //设置ListView的数据适配器
            showTxt.setAdapter(new MsgAdapter(getApplicationContext(),itemBeanList));
        }
     // Toast.makeText(BaseTalk.this, reason.toString(),Toast.LENGTH_SHORT).show();

        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
        System.out.println("事件发生的原因 : " + reason);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        info = JMessageClient.getMyInfo();
        if (null == info) {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }

      //  tv_header.append("版本号：" + JMessageClient.getSdkVersionString());
        Intent intent = getIntent();
        Gson gson = new Gson();
        List<DeviceInfo> deviceInfos = gson.fromJson(intent.getStringExtra("deviceInfos"), new TypeToken<List<DeviceInfo>>() {}.getType());
        if (deviceInfos != null) {
            for (DeviceInfo deviceInfo : deviceInfos) {
               // Toast.makeText(BaseTalk.this, deviceInfo.toString(),Toast.LENGTH_SHORT).show();
              //  Log.i("1111",deviceInfo.toString());
              //  tv_deviceInfo.append("设备登陆记录:\n");
//                tv_deviceInfo.append("设备ID: " + deviceInfo.getDeviceID() + " 平台：" + deviceInfo.getPlatformType()
//                        + " 上次登陆时间:" + deviceInfo.getLastLoginTime() + "登陆状态:" + deviceInfo.isLogin() + "在线状态:" + deviceInfo.getOnlineStatus()
//                        + " flag:" + deviceInfo.getFlag());
            }
        }
        if(conversation!=null){
        List<Message>  list =  conversation.getAllMessage();
        for(Message m : list){
            itemBeanList.add(new ItemBean(R.mipmap.ic_launcher, tagetUserName, m.getContent().toString(),new Date()));
            showTxt.setAdapter(new MsgAdapter(getApplicationContext(),itemBeanList));
            showTxt.setSelection(showTxt.getBottom());
            editText1.setText("");
          }
        }
    }


    private long firstTime=0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime>2000){
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                firstTime=System.currentTimeMillis();
            }else{
                JMessageClient.logout();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
