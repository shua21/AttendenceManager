package com.example.shua21.attendancemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class studentAddActivity extends Activity {
    String stuid;
    int classid;
    int sid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);
        Intent i = this.getIntent();
        stuid = i.getStringExtra("stuid");
        try{
            sid = Integer.valueOf(i.getStringExtra("sid"));
        }
        catch(Exception e){
            sid = 0;
        }
        if(stuid==null)stuid="";
        classid = i.getIntExtra("classid",0);
        if(!stuid.equals(""))
        {
            try {
                Cursor c = MainActivity.helper.db1.rawQuery("select * from StudentList where (classid=" + classid + " and stuid='" + stuid + "')", null);
                c.moveToPosition(0);
                ((EditText)findViewById(R.id.add_edit_name)).setText(c.getString(2));
                ((EditText)findViewById(R.id.add_edit_phone)).setText(c.getString(3));
                ((EditText)findViewById(R.id.add_edit_stuid)).setText(stuid);
            }
            catch(Exception e){
                stuid="";
            }
        }
        findViewById(R.id.add_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("name", ((EditText) findViewById(R.id.add_edit_name)).getText().toString());
                i.putExtra("phone", ((EditText) findViewById(R.id.add_edit_phone)).getText().toString());
                i.putExtra("stuid", ((EditText) findViewById(R.id.add_edit_stuid)).getText().toString());
                i.putExtra("pre_stuid",stuid);
                setResult(1, i);
                finish();
            }
        });

        findViewById(R.id.add_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCancel();

            }
        });
        findViewById(R.id.add_button_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sid!=0)
                    MainActivity.helper.db1.execSQL("delete from StudentList where id=" + sid);
                finish();

            }
        });
    }
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            addCancel();
        }
        return false;
    }

    private void addCancel() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
