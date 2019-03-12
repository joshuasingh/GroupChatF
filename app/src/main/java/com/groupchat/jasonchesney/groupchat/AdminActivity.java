package com.groupchat.jasonchesney.groupchat;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {

    EditText adnum;
    Button adlogin;
    final static String ADMIN = "9568301049";
    String getnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // this 5 line code is just to change the color of the status bar
        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));

            adlogin = (Button) findViewById(R.id.alogin);
            adnum = (EditText) findViewById(R.id.adminauth);
        }
    }

    //onStart has the login details i would recommend ou to change the number to your own
    @Override
    protected void onStart() {
        super.onStart();

        adlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getnum = adnum.getText().toString().trim();

                if(getnum.equals("")){
                    Toast.makeText(AdminActivity.this, "Please enter admin code", Toast.LENGTH_SHORT).show();
                }
                else if(getnum.equals(ADMIN)){
                    Intent i = new Intent(AdminActivity.this, AdminVerifiyActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("adnumber", "+91"+getnum);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else{
                    Toast.makeText(AdminActivity.this, "Code invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
