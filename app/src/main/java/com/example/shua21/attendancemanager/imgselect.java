package com.example.shua21.attendancemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by shua21 on 2015-11-10.
 */
public class imgselect extends Dialog implements View.OnClickListener{
    public imgselect(Context context) {
        super(context);
    }
    public int imgnum;
    public int getimgnum()
    {
        return imgnum;
    }
    ImageButton img1,img2,img3,img4,img5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imgselect); // 커스텀 다이얼로그 레이아웃
        img1 =(ImageButton)findViewById(R.id.imgselect_img1);img1.setOnClickListener(this);
        img2 =(ImageButton)findViewById(R.id.imgselect_img2);img2.setOnClickListener(this);
        img3 =(ImageButton)findViewById(R.id.imgselect_img3);img3.setOnClickListener(this);
        img4 =(ImageButton)findViewById(R.id.imgselect_img4);img4.setOnClickListener(this);
        img5 =(ImageButton)findViewById(R.id.imgselect_img5);img5.setOnClickListener(this);
        findViewById(R.id.imgselect_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==img1)
            imgnum = R.drawable.classimg1;
        else if(v==img2)
            imgnum = R.drawable.classimg2;
        else if(v==img3)
            imgnum = R.drawable.classimg3;
        else if(v==img4)
            imgnum = R.drawable.classimg4;
        else if(v==img5)
            imgnum = R.drawable.classimg5;
        else
            cancel();
        dismiss();
    }



}
