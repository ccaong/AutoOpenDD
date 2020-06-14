package com.ccaong.autoopendd;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.api.BasicCallback;

/**
 * 绑定用户
 */
public class BindUserActivity extends AppCompatActivity {

    EditText etName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_user);
        etName = findViewById(R.id.et_execute_id);

        findViewById(R.id.btn_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindUser();
            }
        });
    }

    /**
     * 绑定客户端手机账号
     */
    private void bindUser() {
        String userId = etName.getText().toString();

        if (userId.equals("")) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
        }
        ContactManager.sendInvitationRequest(userId, "6ee7a41c067f1d1ba608f8de", "hello", new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                if (0 == responseCode) {
                    //好友请求请求发送成功
                    Toast.makeText(BindUserActivity.this, "好友请求请求发送成功", Toast.LENGTH_SHORT).show();
                } else {
                    //好友请求发送失败
                    Toast.makeText(BindUserActivity.this, "好友请求发送失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
