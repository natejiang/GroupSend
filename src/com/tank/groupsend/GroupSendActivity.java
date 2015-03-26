package com.tank.groupsend;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.ContactsContract;

public class GroupSendActivity extends ActionBarActivity {
	EditText numbers,content;
	Button select,send;
	SmsManager sManager;
	//记录需要群发的号码列表
	ArrayList<ArrayList<String>> sendList=new ArrayList<ArrayList<String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_send);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		sManager=SmsManager.getDefault();
		select=(Button)findViewById(R.id.select);
		numbers=(EditText) findViewById(R.id.number);
		send=(Button) findViewById(R.id.send);
		content=(EditText) findViewById(R.id.content);
		//为send按钮绑定监听器
		send.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				for (ArrayList<String> s1:sendList)
				{
					//创建一个PendingIntent对象
					PendingIntent pi=PendingIntent.getActivity(GroupSendActivity.this, 0, new Intent(), 0);
					//发送短信
					sManager.sendTextMessage(s1.get(1), null, content.getText().toString().replace("0828", s1.get(0)), pi, null);
				}
				//提示短信群发完成
				Toast.makeText(GroupSendActivity.this, "短信群发完成", 8000).show();
			}
		});
		
		
		//为SELECT按钮绑定监听器
		select.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				final Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,"sort_key COLLATE LOCALIZED asc");
				//编写BaseAdapter适配器
				BaseAdapter adapter= new BaseAdapter()
				{
					public int getCount()
					{
						return cursor.getCount();
					}
					public Object getItem(int position)
					{
						return position;
					}
					public long getItemId(int position)
					{
						return position;
					}
					public View getView(int position,View convertView,ViewGroup parent)
					{
						cursor.moveToPosition(position);
						
						final ArrayList<String> detail=new ArrayList<String>();
						CheckBox rb=new CheckBox(GroupSendActivity.this);
						
						
						//获取联系人的电话号码，并去掉中间的中划线
						final String number= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
								.replace("-","").replace("+", "");		
						//获取联系人的姓名
						final String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						
						rb.setText(name+" "+number);

						rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() 
						{
							
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
							{
								if(isChecked)
								{
									if(!isChecked(number))
									{
										detail.add(name);
										detail.add(number);
										sendList.add(detail);
									}	
								}
								else
								{
									detail.add(name);
									detail.add(number);
									sendList.remove(detail);
								}
								// TODO Auto-generated method stub
								
							}
						});
						
						
						
						if (isChecked(number))
						{
							rb.setChecked(true);
						}
						
						return rb;
					}
				};//编写BaseAdapter适配器结束
				//加载list.xml布局文件对应的View
				View selectView=getLayoutInflater().inflate(R.layout.list,null);
				//获取selectView中的名为list的listView组件
				final ListView listView=(ListView)selectView.findViewById(R.id.list);
				listView.setAdapter(adapter);
				new AlertDialog.Builder(GroupSendActivity.this).setView(selectView).setPositiveButton("确定",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,int which)
							{
								numbers.setText("");
								numbers.setText(sendList.toString().replace("[", "").replace("]", ""));
							}	
						}).show();
				
				
				
				
							
			}
			
			
		});	
		
	}
	//判断某个电话号码是否已在群发范围内
	public boolean isChecked(String phone)
	{
		for (ArrayList<String> s1:sendList)
		{
			if (s1.contains(phone))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_send, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_group_send,
					container, false);
			return rootView;
		}
	}

}