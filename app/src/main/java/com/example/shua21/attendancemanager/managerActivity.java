package com.example.shua21.attendancemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class managerActivity extends Activity {

    ListView l1;
    ArrayList<clist> la1;
    clistApapter cl;
    Boolean isLog=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Intent intent = this.getIntent();
        isLog = intent.getBooleanExtra("isLog",false);
        l1 = (ListView)findViewById(R.id.Manager_listview);

        la1 = new ArrayList<>();
        cl = new clistApapter(getApplicationContext(), R.layout.clist,la1);
        l1.setAdapter(cl);
        //la1.add("월1234 안드로이드 프로그래밍");
        listLoad();

        findViewById(R.id.manager_button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cursor c= MainActivity.helper.db1.rawQuery("insert into ClassList values(null,'')", null);
                MainActivity.helper.db1.execSQL("insert into ClassList values(null,'',0)");
                //
                Cursor c = MainActivity.helper.db1.rawQuery("select last_insert_rowid()", null);
                c.moveToPosition(0);


                Intent i = new Intent(managerActivity.this, studentActivity.class);
                i.putExtra("classid", c.getInt(0));
                i.putExtra("classname", "");
                i.putExtra("isNew",true);
                c.close();
                startActivityForResult(i, 0);
            }
        });

        ((ListView)findViewById(R.id.Manager_listview)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                if(isLog)
                {
                    i = new Intent(managerActivity.this, AttendLogActivity.class);
                }
                else {
                    i = new Intent(managerActivity.this, studentActivity.class);
                }
                i.putExtra("classid", la1.get(position).cid);
                i.putExtra("classname",  la1.get(position).classname);
                i.putExtra("isNew",false);
                i.putExtra("img",la1.get(position).img);



                startActivityForResult(i, 1);
            }
        });


    }


    void listLoad()
    {
        int a0,a2;
        String a1;
        la1.clear();
        Cursor c = MainActivity.helper.db1.rawQuery("select * from ClassList",null);
        for(int i=0;i<c.getCount();i++)
        {
            c.moveToPosition(i);
            a0=c.getInt(0);
            a1=c.getString(1);
            a2=c.getInt(2);
            la1.add(new clist(a0,a1,a2));

        }
        l1.setAdapter(cl);
        c.close();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        listLoad();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manager, menu);
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
