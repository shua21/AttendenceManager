package com.example.shua21.attendancemanager;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AttendLogActivity extends AppCompatActivity {
    ListView li;
    ArrayAdapter<String> la1;
    int classid;
    String classname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_log);
        Intent i = this.getIntent();
        classid = i.getIntExtra("classid", 0);
        classname = i.getStringExtra("classname");
        if(classname==null)classname="";
        ((TextView)findViewById(R.id.attendlog_textview)).setText(classname);
        li = (ListView)findViewById(R.id.AttendLog_listview);
        la1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        li.setAdapter(la1);
        listLoad();
        ((ListView) findViewById(R.id.AttendLog_listview)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(AttendLogActivity.this, AttendLogDetailActivity.class);
                i.putExtra("sid", la1.getItem(position).split("\n")[0].split(" ")[0]);
                i.putExtra("stuid", la1.getItem(position).split("\n")[0].split(" ")[1]);
                i.putExtra("sname", la1.getItem(position).split("\n")[0].split(" ")[2]);
                i.putExtra("phone", la1.getItem(position).split("\n")[1]);
                i.putExtra("classid", classid);


                startActivityForResult(i, 0);
            }
        });
    }
    void listLoad()
    {
        la1.clear();
        Cursor c = MainActivity.helper.db1.rawQuery("select * from StudentList where classid=" + classid,null);
        String name,phone,stuid;
        int sid;
        for(int i=0;i<c.getCount();i++)
        {
            c.moveToPosition(i);
            sid = c.getInt(0);
            name =  c.getString(2);
            phone = c.getString(3);
            stuid = c.getString(4);
            la1.add(sid + " " + stuid + " "  + name + "\n" + phone);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attend_log, menu);
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
