package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity {
    private StudentHelper helper;
    private ArrayList<Student> data;
    private ListView listView;
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mClass1Student = null;
    private ArrayList<String> mClass2Student = null;
    private ArrayList<String> mClass3Student = null;
    private BaseExpandableAdapter adapter;
    public static final int REQUEST_CODE_COLOR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        setLayout();

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        helper = new StudentHelper(this,"NameDB",null,2);

        helper.insert(1,"김",1);
        helper.insert(2,"조",1);

        helper.insert(3,"박",2);
        helper.insert(4,"이",2);

        helper.insert(5,"서",3);
        helper.insert(6,"오",3);

         ArrayList<Integer> classes = helper.selectClasses();
        ArrayList<Student> student = helper.selectAll();
        //Log.d("StudentList's name",data.get(0).name);
//        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
//        listView = (ListView)findViewById(R.id.listView);
//        listView.setAdapter(adapter);
        Log.d("classesSize", String.valueOf(classes.size()));
        for(int i =0; i < classes.size();i++){
            mClass1Student = new ArrayList<String>();
            mGroupList.add(classes.get(i)+"반");

            for(int j =0; j < student.size();j++){
                if(classes.get(i) == student.get(j).className ){
                    mClass1Student.add(student.get(j).name);
                }
            }
            mChildList.add(mClass1Student);
        }

//        mClass2Student = new ArrayList<String>();
//        mClass3Student = new ArrayList<String>();
//
//
//        mGroupList.add("2반");
//        mGroupList.add("3반");
//
//        mClass1Student.add("김");
//        mClass1Student.add("조");
//
//        mClass2Student.add("박");
//        mClass2Student.add("이");
//
//        mClass3Student.add("서");
//        mClass3Student.add("오");
//
//        mChildList.add(mClass1Student);
//        mChildList.add(mClass2Student);
//        mChildList.add(mClass3Student);
        adapter = new BaseExpandableAdapter(this, mGroupList, mChildList);
        mListView.setAdapter(adapter);

        // 그룹 클릭 했을 경우 이벤트
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), "g click = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 차일드 클릭 했을 경우 이벤트
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(),"g click = "+groupPosition  +"c click = " + childPosition,
                        Toast.LENGTH_SHORT).show();
                //학생 이름을 클릭시 새창으로 색상을 선택할수 있도록 한다
                Intent intent = new Intent(getApplicationContext(), SelectColorActivity.class);
                startActivityForResult(intent,REQUEST_CODE_COLOR);
//                Context context = getApplicationContext();
//                AlertDialog.Builder ad = new AlertDialog.Builder(context);
//                ad.set


                return false;
            }
        });

        // 그룹이 닫힐 경우 이벤트
        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(), "g Collapse = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 그룹이 열릴 경우 이벤트
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
            }
        });



    } //onCreate function end

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //색상을 클릭하고 다시 넘어올경우 색상을 저장해야함
        if(requestCode == REQUEST_CODE_COLOR){
            if(resultCode == RESULT_OK){
                String name = intent.getExtras().getString("name");
                Toast toast = Toast.makeText(getBaseContext(),
                        "응답으로 전달된 name: )" + name, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /*
         * Layout
         */
    private ExpandableListView mListView;

    private void setLayout(){
        mListView = (ExpandableListView) findViewById(R.id.expandableListView);
    }
}
