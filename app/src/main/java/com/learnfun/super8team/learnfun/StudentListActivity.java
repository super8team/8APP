package com.learnfun.super8team.learnfun;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity {
    private StudentHelper helper;
    private ArrayList<Student> data;
    private ListView listView;
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mClassStudent = null;
    private BaseExpandableAdapter adapter;
    NetworkAsync requestNetwork;
    // 로그인한 유저 정보를 담을 객체
    UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        setLayout();

        userPreferences = UserPreferences.getUserPreferences(this);

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        helper = new StudentHelper(this,"NameDB",null,7);

        int tableCheck = helper.tableCheck();

        if(tableCheck==0) setStudentList();


         ArrayList<Integer> classes = helper.selectClasses();
        ArrayList<Student> student = helper.selectAll();
        //Log.d("StudentList's name",data.get(0).name);
//        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
//        listView = (ListView)findViewById(R.id.listView);
//        listView.setAdapter(adapter);
        Log.d("classesSize", String.valueOf(classes.size()));

        for(int i =0; i < classes.size();i++){
            mClassStudent = new ArrayList<String>();
            mGroupList.add(classes.get(i)+"반");

            for(int j =0; j < student.size();j++){
                if(classes.get(i) == student.get(j).className ){
                    mClassStudent.add(student.get(j).name);
                }

            }
            mChildList.add(mClassStudent);
        }


        adapter = new BaseExpandableAdapter(this, mGroupList, mChildList);
        mListView.setAdapter(adapter);

        // 그룹 클릭 했을 경우 이벤트
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
//                Toast.makeText(getApplicationContext(), "g click = " + groupPosition,
//                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 차일드 클릭 했을 경우 이벤트
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(getApplicationContext(),"g click = "+groupPosition  +"c click = " + childPosition,
//                        Toast.LENGTH_SHORT).show();

                int count =0;
                //counting for student num
                for(int i =0; i <= groupPosition;i++){

                    if(i==groupPosition){
                        for(int j=0; j <=childPosition;j++){
                            count++;
                        }
                    }else{
                        for(int j=0; j < mChildList.get(i).size();j++){
                            count++;
                        }
                    }

                }

                //학생 이름을 클릭시 새창으로 색상을 선택할수 있도록 한다
//                Intent intent = new Intent(getApplicationContext(), SelectColorActivity.class);
//                startActivityForResult(intent,REQUEST_CODE_COLOR);
                AlertDialog dialog = createDialogBox(groupPosition,childPosition,count);
                dialog.show();

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
//                Toast.makeText(getApplicationContext(), "g Collapse = " + groupPosition,
//                        Toast.LENGTH_SHORT).show();
            }
        });

        // 그룹이 열릴 경우 이벤트
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition,
//                        Toast.LENGTH_SHORT).show();
            }
        });



    } //onCreate function end

    public void setStudentList(){ // 학생리스트를 서버에서 받아와서 db에 넣음
        String userid = userPreferences.getUserId();

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("userID",userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestNetwork = new NetworkAsync(this, "getStudentList",  NetworkAsync.POST, sendData);

        try {
            // 네트워크 통신 후 json 획득
            String returnString = requestNetwork.execute().get();
            Log.e("planResult", "result is "+returnString);
            JSONObject studentList = new JSONObject(returnString);
            JSONObject classList = new JSONObject(studentList.getString("school"));

            for(int i = 0 ; i < classList.length();i++) { // class1 class2 class3

                String className = "class" + (i+1) ;
                JSONObject studentListArray = classList.getJSONObject(className);

                for(int j = 0 ; j < studentListArray.length();j++){
                    String studentName = "student" + (j+1) ;
                    //제이슨배열을 만든것을 하나씩 제이슨 객체로 만듬
                    JSONObject dataJsonObject = studentListArray.getJSONObject(studentName);

                    helper.insert(dataJsonObject.getString("id"),dataJsonObject.getString("name"),i+1);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private AlertDialog createDialogBox(final int groupPosition, final int childPosition, final int count){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("마커색상선택");
        //builder.setMessage("이 길을 계속 가시겠습니까?");
//        Toast.makeText(getApplicationContext(),String.valueOf(count),
//                Toast.LENGTH_SHORT).show();
        builder.setItems(R.array.color_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) { //0:red, 1:blue, 2:yellow

                            case 0:
                                //DB에 groupPosition과 childPosition에 맞게 색상을 저장한다.
                                helper.update(count,"red");
                                Log.d("color = ","red");
                                break;
                            case 1:
                                helper.update(count,"blue");
                                Log.d("color = ","blue");
                                break;
                            case 2:
                                helper.update(count,"yellow");
                                Log.d("color = ","yellow");
                                break;
                        }
                    }
                });
            //builder.setIcon(R.drawable.socon);  <-- 다이얼로그 나왔을때 '안내'라는 글자 옆에 생성되는 아이콘

            // msg 는 그저 String 타입의 변수, tv 는 onCreate 메서드에 글을 뿌려줄 TextView
//        builder.setPositiveButton("그래", new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int whichButton){
//
//                }
//        });

//        builder.setNeutralButton("보류", new DialogInterface.OnClickListener(){
//            public void onClick(DialogInterface dialog, int whichButton){
//
//            }
//        });
//
//        builder.setNegativeButton("싫어!!!", new DialogInterface.OnClickListener(){
//            public void onClick(DialogInterface dialog, int whichButton){
//
//            }
//        });

        AlertDialog dialog = builder.create();

        return dialog;

    };



    /*
         * Layout
         */
    private ExpandableListView mListView;

    private void setLayout(){
        mListView = (ExpandableListView) findViewById(R.id.expandableListView);
    }
}
