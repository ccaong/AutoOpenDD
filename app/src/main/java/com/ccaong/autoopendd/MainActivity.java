package com.ccaong.autoopendd;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static cn.jpush.im.android.api.event.ContactNotifyEvent.Type.contact_deleted;

public class MainActivity extends AppCompatActivity {

    String friendName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JMessageClient.registerEventReceiver(this);


        loadData();
        loadFriend();
        findViewById(R.id.tv_open_dd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(friendName)) {
                    Toast.makeText(MainActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();

                } else {
                    JMessageClient.createSingleTextMessage(friendName, "6ee7a41c067f1d1ba608f8de", "OPEN_DINGDING");
                }
            }
        });

        findViewById(R.id.tv_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BindUserActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loadData() {
        JMessageClient.getUserInfo("ccaong", "", new GetUserInfoCallback() {

            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                Log.e("", i + "" + s);
                Log.e("", userInfo.getAddress());
            }
        });
    }

    /**
     * 打开dd
     */
    private void openDD() {
        PackageManager packageManager = getPackageManager();
        String packageName = "com.alibaba.android.rimet";//要打开应用的包名,以钉钉为例
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntentForPackage != null)
            startActivity(launchIntentForPackage);
        else
            Toast.makeText(this, "手机未安装该应用", Toast.LENGTH_SHORT).show();
    }


    public void onEvent(ContactNotifyEvent event) {
        final String reason = event.getReason();
        String fromUsername = event.getFromUsername();
        String appkey = event.getfromUserAppKey();

        switch (event.getType()) {
            case invite_received://收到好友邀请

                ContactManager.acceptInvitation(fromUsername, appkey, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage) {
                        Log.e("打印信息", "错误码" + responseCode + responseMessage);
                        if (0 == responseCode) {
                            //接收好友请求成功
                            Toast.makeText(MainActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "绑定失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //...
                break;
            case invite_accepted://对方接收了你的好友邀请

                //...
                break;
            case invite_declined://对方拒绝了你的好友邀请

                //...
                break;
            case contact_deleted://对方将你从好友中删除

                //...
                break;
            default:
                break;
        }
    }


    private void loadFriend() {
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                if (0 == responseCode) {
                    //获取好友列表成功
                    if (userInfoList.size() != 0) {
                        Log.e("好友", "数量" + userInfoList.size());
                        Log.e("好友", userInfoList.get(0).getUserName());
                        friendName = userInfoList.get(0).getUserName();
                    } else {
                        Toast.makeText(MainActivity.this, "列表空", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //获取好友列表失败
                    Toast.makeText(MainActivity.this, "错误" + responseMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 接收在线消息
     **/
    /**
     * 收到消息
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        Log.e("消息", "接收到消息");
        final UserInfo userInfo = (UserInfo) msg.getTargetInfo();
        String targetId = userInfo.getUserName();
        Conversation conv = JMessageClient.getSingleConversation(targetId, userInfo.getAppKey());
        if (conv != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, Bitmap avatarBitmap) {
                            if (responseCode == 0) {
                                // TODO: 2020/6/14 收到消息
                                Log.e("消息", "接收到消息:" + responseMessage);

                            }
                        }
                    });

                }
            });
//            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
        }

    }


    /**
     * 接收离线消息。
     * 类似MessageEvent事件的接收，上层在需要的地方增加OfflineMessageEvent事件的接收
     * 即可实现离线消息的接收。
     **/
    public void onEvent(OfflineMessageEvent event) {
        //获取事件发生的会话对象
        Conversation conversation = event.getConversation();
        List<Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        System.out.println(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
    }


    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }


}
