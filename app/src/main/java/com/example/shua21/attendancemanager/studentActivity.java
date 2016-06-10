package com.example.shua21.attendancemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Enumeration;
import java.util.Vector;

public class studentActivity extends Activity {

    int classid;
    String classname;
    TextView t1;
    ListView l1;
    ArrayAdapter<String> la1;
    boolean isNew;
    int imgnum;
    ImageView imageview;
    imgselect imgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        imageview = (ImageView)findViewById(R.id.student_imageview);
        final Intent i = this.getIntent();
        classid = i.getIntExtra("classid",0);
        classname = i.getStringExtra("classname");
        imgnum = i.getIntExtra("img",R.drawable.classimg1);
        imageview.setImageResource(imgnum);
        l1 = (ListView)findViewById(R.id.student_listview);
        la1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        l1.setAdapter(la1);
        t1 = (TextView)findViewById(R.id.student_edit);
        t1.setText(classname);
        isNew = i.getBooleanExtra("isNew", false);
        //if(isNew==false)
        //    findViewById(R.id.student_button_cancel).setVisibility(View.INVISIBLE);
        listLoad();
        findViewById(R.id.student_button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(studentActivity.this, studentAddActivity.class);
                startActivityForResult(i, 0);
            }
        });
        findViewById(R.id.student_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = studentActivity.this;
                imgs = new imgselect(mContext);
                imgs.setTitle("그림선택");
                imgs.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        imgnum = imgs.getimgnum();
                        imageview.setImageResource(imgnum);
                    }
                });
                try {
                    imgs.show();
                }catch (Exception e){
                    Log.i("imgserr",e.toString());}


            }
        });
        findViewById(R.id.student_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t1.getText().equals(""))
                    t1.setText("제목없음");
                MainActivity.helper.db1.execSQL("update ClassList set classname='" + t1.getText() + "',img='"+imgnum+"' where id=" + classid);

                Intent i = new Intent();
                setResult(0, i);
                finish();
                //String[][] s = Student.vectorToStringss(Sv);
                //i.putExtra("names",s[0] );
                //i.putExtra("phones",s[1] );
                //i.putExtra("StdIDs",s[2] );
            }
        });
        findViewById(R.id.student_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCancel();

            }
        });
        findViewById(R.id.student_button_attend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(studentActivity.this)
                        .setTitle("시간 선택")
                        .setItems(R.array.timelist,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent i= new Intent(studentActivity.this,AttendActivity.class);
                                        i.putExtra("classid", classid);
                                        i.putExtra("classname",classname);
                                        i.putExtra("timenum",(which+1));
                                        startActivityForResult(i, 1);
                                    }

                                })

                        .setNegativeButton("취소", null)
                        .show();


            }
        });

        findViewById(R.id.student_Button_Del).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                MainActivity.helper.db1.execSQL("delete from StudentList where classid=" + classid);
                MainActivity.helper.db1.execSQL("delete from ClassList where id=" + classid);
                finish();
            }
        });

        ((ListView) findViewById(R.id.student_listview)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(studentActivity.this, studentAddActivity.class);
                i.putExtra("sid", la1.getItem(position).split("\n")[0].split(" ")[0]);
                i.putExtra("stuid", la1.getItem(position).split("\n")[0].split(" ")[1]);
                i.putExtra("classid",classid);




                startActivityForResult(i, 0);
            }
        });
    }

    private void addCancel() {
        if(isNew) {
            MainActivity.helper.db1.execSQL("delete from ClassList where id=" + classid);
            MainActivity.helper.db1.execSQL("delete from StudentList where classid=" + classid);
        }
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if (resultCode == 1) {
                String pre_stuid;
                String s1, s2, s3;
                s1 = data.getStringExtra("name");
                s2 = data.getStringExtra("phone");
                s3 = data.getStringExtra("stuid");
                pre_stuid = data.getStringExtra("pre_stuid");
                if (s3 != null)
                    if (!s3.equals(""))
                        if (pre_stuid.equals(""))
                            MainActivity.helper.db1.execSQL("insert into StudentList values (null," + classid + ",'" + s1 + "','" + s2 + "'," + s3 + ")");
                        else
                            MainActivity.helper.db1.execSQL("update StudentList set name='" + s1 + "', phone='" + s2 + "', stuid='" + s3 + "'  where (stuid='" + pre_stuid + "' and classid='" + classid + "')");
            }
        }
        listLoad();
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
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            addCancel();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student, menu);
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
/*
class Student {
    String Name, Phone, StdID;

    Student(String n, String p, String id)
    {
        Name = n;
        Phone = p;
        StdID = id;
    }

    static String[][] vectorToStringss(Vector<Student> v)
    {
        Enumeration<Student> e = v.elements();
        String s[][] = new String[v.capacity()][3];
        int i=0;
        Student imsi;
        while(e.hasMoreElements())
        {
            imsi = e.nextElement();
            s[0][i] = imsi.Name;
            s[1][i] = imsi.Phone;
            s[2][i] = imsi.StdID;

        }
        return s;
    }

    static void dbAddClass(dbHelper h)
    {
        //h.db1.execSQL("INSERT INTO " + h.ProgramName + "VALUES (null," + );
    }
}
*/
