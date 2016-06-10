package com.example.shua21.attendancemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class AttendLogDetailActivity extends AppCompatActivity {
    ListView li;
    ArrayAdapter<String> la1;
    String stuid;
    int classid,sid;
    int logid[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_log_detail);
        li = (ListView)findViewById(R.id.detail_listView);
        la1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        li.setAdapter(la1);

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

        ((TextView)findViewById(R.id.detail_textview_stuid)).setText(i.getStringExtra("stuid")+"");
        ((TextView)findViewById(R.id.detail_textview_sname)).setText(i.getStringExtra("sname")+"");
        ((TextView)findViewById(R.id.detail_textview_phone)).setText(i.getStringExtra("phone")+"");


        listLoad();

        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(AttendLogDetailActivity.this)
                        .setTitle("기록 수정하기")
                        .setItems(R.array.attendmodify,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch(which)
                                        {
                                            case 0:
                                                MainActivity.helper.db1.execSQL("Update AttendLog set status=0 where id="+logid[position]);
                                                break;
                                            case 1:
                                                MainActivity.helper.db1.execSQL("Update AttendLog set status=1 where id="+logid[position]);
                                                break;
                                            case 2:
                                                MainActivity.helper.db1.execSQL("Update AttendLog set status=2 where id="+logid[position]);
                                                break;
                                            case 3:
                                                MainActivity.helper.db1.execSQL("Update AttendLog set status=3 where id="+logid[position]);
                                                break;
                                        }
                                        listLoad();
                                    }

                                })

                        .setNegativeButton("취소", null)
                        .show();
            }
        });
    }
    void listLoad()
    {
        la1.clear();
        Cursor c = MainActivity.helper.db1.rawQuery("select nday,ntime from attendlog where classid="+classid+" group by nday,ntime",null);
        String DayTime[] = new String[c.getCount()];
        for(int i=0;i<c.getCount();i++) {
            c.moveToPosition(i);
            DayTime[i] = c.getString(0)  + "/" + c.getInt(1);

        }
        c = MainActivity.helper.db1.rawQuery("select id,nday,ntime,status from attendlog where student=" + sid+" and classid=" + classid,null);
        String attendlog[][] = new String[2][c.getCount()];
        logid = new int[c.getCount()];
        for(int i=0;i<c.getCount();i++) {
            c.moveToPosition(i);
            logid[i] = c.getInt(0);
            attendlog[0][i] = c.getString(1)  + "/" + c.getInt(2);
            switch (c.getInt(3)){
                case 0:
                    attendlog[1][i] = "결석";
                    break;
                case 1:
                    attendlog[1][i] = "지각";
                    break;
                case 2:
                    attendlog[1][i] = "출석";
                    break;
                case 3:
                    attendlog[1][i] = "기타";
                    break;
            }

        }
        boolean att=false;
        for (int ii = 0; ii < attendlog[0].length; ii++){
             for(int i=0;i<DayTime.length;i++){
                 if (DayTime[i].equals(attendlog[0][ii])) {

                    la1.add(DayTime[i] + "교시 "+attendlog[1][ii]);
                    att = true;
                }

            }
            if(att==false)
                la1.add(attendlog[0][ii]+"교시 기록없음(결석)");
            else
                att=false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attend_log_detail, menu);
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
