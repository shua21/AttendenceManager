package com.example.shua21.attendancemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AttendActivity extends Activity {
    ListView l1;
    ArrayAdapter<String> la1;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothOneshot b = null;
    private int classid;
    String classname;
    TextView txStatus;
    int timenum;
    String today="";
    CheckBox checkBox;
    int logid[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);
        checkBox = (CheckBox)findViewById(R.id.attend_checkBox);
        txStatus = (TextView)findViewById(R.id.attend_textview_status);
        l1 = (ListView)findViewById(R.id.attend_listview);
        la1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        l1.setAdapter(la1);
        Intent i = this.getIntent();
        classname = i.getStringExtra("classname");
        timenum = i.getIntExtra("timenum",0);
        classid = i.getIntExtra("classid", 0);
        Calendar cal = Calendar.getInstance();
        today = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        ((TextView)findViewById(R.id.attend_Textview_timenum)).setText(timenum + "교시");
        ((TextView)findViewById(R.id.attend_textview_date)).setText(today);
        listLoad();
        bluetoothConnect();
        findViewById(R.id.attend_button_resfresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConnect();
                txStatus.setText("대기중");
            }
        });
        findViewById(R.id.attend_button_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.helper.db1.execSQL("delete from attendlog where classid=" + classid + " and nday = '" +
                        today + "' and ntime=" + timenum);
                listLoad();
                bluetoothConnect();
            }
        });

        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(AttendActivity.this)
                        .setTitle("기록 수정하기")
                        .setItems(R.array.attendmodify,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        int status=0;
                                        switch (which) {
                                            case 0:
                                                status=0;
                                                break;
                                            case 1:
                                                status=1;
                                                break;
                                            case 2:
                                                status=2;
                                                break;
                                            case 3:
                                                status=3;
                                                break;
                                        }
                                        MainActivity.helper.db1.execSQL("insert into AttendLog values (null," + classid +
                                                ","+logid[position]+",'" + today + "'," + timenum + ","+status+")");
                                        listLoad();
                                    }

                                })

                        .setNegativeButton("취소", null)
                        .show();
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            bluetoothConnect();
        }
    }

    private void bluetoothConnect() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(AttendActivity.this, "블루투스 기기가 없습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 3);

        }
        else
        {
            if (mBluetoothAdapter.getScanMode() !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000);
                startActivity(discoverableIntent);

            }

            //b = new bluetooth(mHandler);
            try{
                if(b!=null) {
                    b.interrupt();
                    b=null;
                }
            }
            catch(Exception e){

            }
            b = new BluetoothOneshot(mHandler);

        }
    }
    public final CountDownTimer mTimer = new CountDownTimer(2000,2000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            txStatus.setText("대기중");
        }
    };
    public final Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            mTimer.cancel();
            switch (msg.what)
            {
                case 1:

                    txStatus.setText("전화번호 비교중");
                    int w = listcount();

                    //Toast.makeText(AttendActivity.this,w+"",Toast.LENGTH_LONG).show();

                    byte[] readBuf = (byte[]) msg.obj;
                    boolean isok=false;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    for(int i=0;i<la1.getCount();i++)
                    {
                        if(la1.getItem(i).split("\n")[1].replace("-","").equals(readMessage.replace("-",""))) {
                            try {
                                b.okmsg((classname + " " +la1.getItem(i).split("\n")[0] + " 출석완료").getBytes());

                            }catch(Exception e){}
                            txStatus.setText(la1.getItem(i).replace("\n", " ") + " 출석");
                            mTimer.start();
                            la1.remove(la1.getItem(i));
                            isok=true;
                            MainActivity.helper.db1.execSQL("insert into AttendLog values (null," + classid +
                                    ",(select id from Studentlist where classid="+classid+" and replace(phone,'-','')='" +
                                    readMessage.replace("-","")  + "'),'" +
                                    today + "'," + timenum + ",'" + (checkBox.isChecked()?1:2) +"')");
                            break;
                        }
                    }
                    if(isok==false)
                    {
                        try {
                            txStatus.setText("이미 출석하였거나 정보가 없습니다.");
                            b.okmsg("정보가없습니다".getBytes());
                            mTimer.start();
                        }catch(Exception e){}
                    }
                    break;
                case 2:

                    txStatus.setText("연결됨");

                    break;
                case 4:

                    txStatus.setText("오류발생");
                    break;
                case 5:
                    b = new BluetoothOneshot(mHandler);
                    break;
            }
        }
    };
    public int listcount()
    {
        return la1.getCount();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attend, menu);
        return true;
    }

    void listLoad()
    {
        la1.clear();
        Cursor c = MainActivity.helper.db1.rawQuery("select * from StudentList where classid=" + classid,null);
        Cursor d=null;
        try {
             d = MainActivity.helper.db1.rawQuery("select * from AttendLog where classid=" + classid + " and nday = '" +
                    today + "' and ntime=" + timenum, null);
        }
        catch(Exception e){
            Log.i("err", e.toString());
        }
        //select * from AttendLog where classid = 1 and nday = '2015-10-28' and ntime=1
        String name,phone,stuid;
        int stnum;
        boolean stexist=false;
        logid = new int[c.getCount()];
        int icount=0;
        for(int i=0;i<c.getCount();i++)
        {
            c.moveToPosition(i);

            stnum = c.getInt(0);
            name =  c.getString(2);
            phone = c.getString(3);
            stuid = c.getString(4);
            for(int ii=0;ii<d.getCount();ii++)
            {
                d.moveToPosition(ii);
                if(stnum==d.getInt(2)){
                    stexist=true;
                    break;
                }
            }
            if(!stexist) {
                logid[icount++]= stnum;
                la1.add(stuid + name + "\n" + phone);

            }
            stexist = false;
        }
        c.close();
    }
    public void onDestroy()
    {
        try {
            b.interrupt();
        }catch(Exception e){}
         super.onDestroy();
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
