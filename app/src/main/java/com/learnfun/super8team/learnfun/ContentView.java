package com.learnfun.super8team.learnfun;

import android.widget.EditText;

import org.json.JSONObject;

/**
 * Created by KIM on 2017-05-12.
 */

abstract public class ContentView {
    final static boolean used_interface = false;
    protected int id;
    protected String name;



    //컨텐츠 화면 출력
    abstract public void setContentView() throws InterruptedException;

    //컨텐츠 화면 삭제
    abstract public void unsetContentView();

    //클릭 액션 코드 등록
    abstract public void setClickAction(final JSONObject code,final ContentActivity contentActivity);

    //에디트 검사 액션 코드 등록
    abstract public void setCheckEditAction(final EditText editview,final String answer,final JSONObject ooo, final JSONObject xxx,final ContentActivity contentActivity);

}
