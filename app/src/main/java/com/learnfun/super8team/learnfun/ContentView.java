package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KIM on 2017-05-12.
 */

abstract public class ContentView {
//    protected
    protected int id;
    protected String name;
    protected String ContentName;



    //컨텐츠 화면 출력
    abstract public void setContentView() throws InterruptedException;

    //컨텐츠 화면 삭제
    abstract public void unsetContentView();

    //클릭 액션 코드 등록
    abstract public void setClickAction(final JSONObject code,final ContentActivity contentActivity);

    //에디트 검사 액션 코드 등록
    abstract public void setCheckEditAction(final EditText editview,final String answer,final JSONObject ooo, final JSONObject xxx,final ContentActivity contentActivity);

    //액션 코드 (클릭리스너 초기화)
    abstract public void actionClear();

    //액션코드 구분 코드
    public Intent setActionScript(JSONObject code , Intent intent, String name){
        try{
            intent.putExtra("name",name);

            if(code.has("out_txt")){
                //해당 액션 코드가 있는지 검사후 있으면 추출후 인텐트에 넣음
                String text = code.getString("out_txt");
                intent.putExtra("text",text);
            }
            if (code.has("out_img")){
                String image= code.getString("out_img");
                intent.putExtra("image",image);
            }
            if (code.has("end")){
//                boolean end = code.getBoolean("end");
                intent.putExtra("end","true");
            }
            if (code.has("config")){

            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return intent;
    }
}
