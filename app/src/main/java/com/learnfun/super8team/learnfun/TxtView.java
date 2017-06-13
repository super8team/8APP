package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KIM on 2017-05-12.
 */

public class TxtView extends ContentView{
    private String description;
    private int size;
    private String color;
    private TextView textView;

    TxtView(JSONObject jobj,View view,String name) throws JSONException {
        this.id           = jobj.getInt("id");
        this.ContentName = name;
        this.name         = jobj.getString("name");
        this.description = jobj.getString("description");
        this.size         = jobj.getInt("size");
        this.color        = jobj.getString("color");

        textView = (TextView) view;

        Log.i("뷰 설정완료 -----", this.name);
    }

    @Override
    public void setContentView() {
        //텍스트 내용
        textView.setText(this.description);

        //텍스트 크기
        textView.setTextSize(this.size);

        //텍스트 컬러
        textView.setTextColor(Color.parseColor(this.color));

        //텍스트뷰 출력
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public void unsetContentView() {
        textView.setVisibility(View.GONE);
    }

    @Override
    public void setClickAction(final JSONObject code, final ContentActivity contentActivity) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("리스너 클릭","버튼 클릭");
                Intent intent = new Intent(contentActivity,Dialog.class);

                contentActivity.startActivityForResult(setActionScript(code,intent,ContentName),3203);
            }
        });
    }

    @Override
    public void setCheckEditAction(final EditText editview, final String answer, final JSONObject ooo, final JSONObject xxx, final ContentActivity contentActivity) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contentActivity,Dialog.class);

                //정답을 맞췄을경우 출력화면
                if (editview.getText().toString().equals(answer)){
                    intent = setActionScript(ooo,intent,ContentName);
                }else { //틀렷을경우 출력화면
                    intent = setActionScript(xxx,intent,ContentName);
                }

                contentActivity.startActivityForResult(intent,3203);
            }
        });
    }

    @Override
    public void actionClear() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아무것도 하지 않음
                Log.i("클리어 액션","아무것도 하지않는다");
            }
        });
    }
}
