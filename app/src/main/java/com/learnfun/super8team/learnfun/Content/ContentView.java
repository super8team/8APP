package com.learnfun.super8team.learnfun.Content;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

import com.learnfun.super8team.learnfun.Activity.ContentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KIM on 2017-05-12.
 */

abstract public class ContentView {
//    protected
    protected int id;
    protected String name;
    protected int contentNum;



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
    public Intent setActionScript(JSONObject code , Intent intent, int contentNumber, ContentActivity contentActivity){
        try{

                Log.i("액션 스크립트 길이 :", String.valueOf(code.names()));
                if (code.has("out_txt")) {
                    //해당 액션 코드가 있는지 검사후 있으면 추출후 인텐트에 넣음
                    String text = code.getString("out_txt");
                    intent.putExtra("text", text);
                }
                if (code.has("out_img")) {
                    String image = code.getString("out_img");
                    intent.putExtra("image", image);
                }
                if (code.has("end")) {
                    intent.putExtra("number", contentNumber);
                    intent.putExtra("end", "true");
                }
                if (code.has("config")) {
                    JSONArray jobj = code.getJSONArray("config");

                    //설정값 변경코드 콘텐츠명, 비전, 클릭, 생명여부순
                    //인텐트에 데이터를 담지않는다.
                    for (int i = 0; i < jobj.length(); i++) {
                        contentActivity.setContentStatus(jobj.getJSONObject(i).getString("target_name"),
                                jobj.getJSONObject(i).getBoolean("visionable"),
                                jobj.getJSONObject(i).getBoolean("clickable"),
                                jobj.getJSONObject(i).getBoolean("disable"));
                    }
                }
                if (code.has("toast")) {
                    String message = code.getString("toast");
                    intent.putExtra("toast", message);
                }
                if (code.has("quest")) {
                    Log.i("액션코드부분", "체크됨");
                    //데이터베이스에 현재상태 저장
                    String message = code.getString("quest");
                    contentActivity.getDB().insert("quest", message);

                    contentActivity.onQuestButton();
                }
                if (code.has("endQuest")) {
                    contentActivity.closeQuestButton();
                }
                if (code.has("bingo")) {
                    int pointer = code.getInt("bingo");
                    //데이터베이스에 현재상태 저장
                    contentActivity.getDB().insert("bingo", String.valueOf(pointer));
                    //빙고실행
                    contentActivity.onBingoButton();
                }
                if (code.has("endBingo")) {

                    //빙고종료
                    contentActivity.closeBingoButton();
                }
//                if (code.has("collection")) {
//                    contentActivity.onCollectButton();
//
////                contentActivity.getDB().insert("collect","on");
//                    code.remove("collection");
//                }
//                if (code.has("endCollection")) {
//                    contentActivity.closeCollectButton();
//                    code.remove("endCollection");
//                }
                if (code.has("openMap")) {
                    contentActivity.onMapButton();
                    //데이터 베이스에 현재상태 저장
                    contentActivity.getDB().insert("map", "on");
                }
                if (code.has("closeMap")) {
                    contentActivity.closeMapButton();
                }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return intent;
    }
}
