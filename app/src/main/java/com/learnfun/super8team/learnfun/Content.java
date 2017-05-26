package com.learnfun.super8team.learnfun;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KIM on 2017-05-12.
 */

public class Content {
    public static boolean CONTENT_USED = false;
    private String name;
    private String vertical;
    private String horizontal;
    private boolean visionable;
    private boolean clickable;
    private boolean disable;
    private double latitude, longitude;
    private JSONArray scriptCode;
    private EditText editview;
    private boolean hasEditview = false;
    private ArrayList<ContentView> views = new ArrayList<>();

//    private ArrayList<ImgView> imgView = new ArrayList<>();
//    private ArrayList<TxtView> txtView = new ArrayList<>();
//    private ArrayList<BtnView> btnView = new ArrayList<>();
    private int[] imgviews = {R.id.img_1,R.id.img_2,R.id.img_3,R.id.img_4};
    private int[] btnviews = {R.id.btn_1,R.id.btn_2,R.id.btn_3,R.id.btn_4};

    private LinearLayout outform;
    private LinearLayout inform;

    //생성자 초기설정
    Content(JSONObject jobj,ContentActivity contentActivity) throws JSONException, InterruptedException {
        this.name        = jobj.getString("name");
        this.vertical   = jobj.getString("vertical");
        this.horizontal = jobj.getString("horizontal");
        this.visionable = jobj.getBoolean("visionable");
        this.clickable  = jobj.getBoolean("clickable");
        this.disable    = jobj.getBoolean("disable");
        this.latitude   = jobj.getInt("latitude");
        this.longitude  = jobj.getInt("longitude");

        Log.i("컨텐츠 멤버 값 입력완료 -----", this.name);
        //컨텐츠 배열들을 ArrayList에 저장
        //이미지 컨텐츠 배열
        for(int i=0;i<jobj.getJSONArray("image").length();i++){
            ImgView imgView = new ImgView(jobj.getJSONArray("image").getJSONObject(i), contentActivity.findViewById(imgviews[i]));

            this.views.add(imgView);

        }

        for(int i=0;i<jobj.getJSONArray("text").length();i++){
            Log.i("텍스트 컨텐츠 길이 ::",Integer.toString(jobj.getJSONArray("text").length()));
            if(jobj.getJSONArray("text").getJSONObject(i).getInt("id") == 1){
                //텍스트 컨텐츠 헤더
                Log.i("텍스트 컨텐츠 헤더 값 ::",Integer.toString(jobj.getJSONArray("text").getJSONObject(i).getInt("id")));
                TxtView txtView = new TxtView(jobj.getJSONArray("text").getJSONObject(i), contentActivity.findViewById(R.id.header_text));

                this.views.add(txtView);
            }else{
                //텍스트 컨텐츠 바텀
                Log.i("텍스트 컨텐츠 바텀 값 ::",Integer.toString(jobj.getJSONArray("text").getJSONObject(i).getInt("id")));
                TxtView txtView = new TxtView(jobj.getJSONArray("text").getJSONObject(i), contentActivity.findViewById(R.id.bottom_text));

                this.views.add(txtView);
            }
        }
        Log.i("텍스트 컨텐츠 배열 저장완료 -----",views.toString());
        //에디트뷰 있을시
        if(jobj.has("edit")){
            hasEditview = true;
            editview = (EditText) contentActivity.findViewById(R.id.edit_text);
            editview.setText(jobj.getJSONObject("edit").getString("text"));
            editview.setHint(jobj.getJSONObject("edit").getString("hint"));
            editview.setTextSize(jobj.getJSONObject("edit").getInt("size"));
        }

        //버튼 컨텐츠 배열
        for(int i=0;i<jobj.getJSONArray("button").length();i++){
            Log.i("버튼 컨텐츠 길이 ::",Integer.toString(jobj.getJSONArray("button").length()));
            BtnView btnView = new BtnView(jobj.getJSONArray("button").getJSONObject(i), contentActivity.findViewById(btnviews[i]));

            this.views.add(btnView);
        }
        Log.i("버튼 컨텐츠 배열 저장완료 -----",views.toString());
        //전체적인 컨텐츠 위치 설정
        outform = (LinearLayout) contentActivity.findViewById(R.id.horizon_layout);
        LinearLayout.LayoutParams params_horizontal = (LinearLayout.LayoutParams) outform.getLayoutParams();
        //호라이즌 폼 설정
        switch (this.horizontal){
            case "left":
                params_horizontal.gravity = Gravity.LEFT;
                break;
            case "center":
                params_horizontal.gravity = Gravity.CENTER;
                break;
            case "right":
                params_horizontal.gravity = Gravity.RIGHT;
                break;
            default:
                params_horizontal.gravity = Gravity.CENTER;
                break;
        }
        outform.setLayoutParams(params_horizontal);

        //버티컬 폼 설정
        inform  = (LinearLayout) contentActivity.findViewById(R.id.vertical_layout);
        LinearLayout.LayoutParams params_vertical = (LinearLayout.LayoutParams) inform.getLayoutParams();

        switch (this.vertical){
            case "top":
                params_vertical.gravity = Gravity.TOP;
                break;
            case "center":
                params_vertical.gravity = Gravity.CENTER;
                break;
            case "bottom":
                params_vertical.gravity = Gravity.BOTTOM;
                break;
            default:
                params_vertical.gravity = Gravity.CENTER;
                break;
        }

        inform.setLayoutParams(params_vertical);

        //스크립트 읽어들이기
            if(disable == false){
                scriptCode = jobj.getJSONArray("script");
                for (int i=0;i<scriptCode.length();i++){
                    //존재하는 스크립트수만큼 반복
                    //이름이 같은 뷰에 해당 타입의 액션을 추가한다.

                    //스크립트 명세 추출
                    String type = scriptCode.getJSONObject(i).getString("type");

                    //타입에 따른 액션스크립트 구분
                    if(type.equals("CLICK")){
                        String name = scriptCode.getJSONObject(i).getString("name");
                        JSONObject action = scriptCode.getJSONObject(i).getJSONObject("action");
                        Log.i("액션 제이슨값  ",action.toString());
                        //컨텐츠 ArrayList에서 이름이 같은 컨텐츠 검색
                        for(int j=0;j<views.size();j++){
                            Log.i("제이슨 네임  ",name);
                            Log.i("뷰 네임 ", views.get(j).name);
                            if(name.equals(views.get(j).name)){
                                //이름이 같은 컨텐츠 발견시 액션코드를 해당 컨텐츠에 삽입
                                //반복종료
                                views.get(j).setClickAction(action, contentActivity);

                                break;
                            }
                        }
                    }else if(type.equals("CHECKEDIT")){
                        String answer = scriptCode.getJSONObject(i).getString("answer");
                        String name = scriptCode.getJSONObject(i).getString("name");
                        JSONObject ooo = scriptCode.getJSONObject(i).getJSONObject("true");
                        JSONObject xxx = scriptCode.getJSONObject(i).getJSONObject("false");
                        Log.i("체크","스크립트");
                        for (int j=0;j<views.size();j++){
                            Log.i("체크1-------",views.get(j).name);
                            Log.i("체크1-------",views.get(j).name);
                            if(name.equals(views.get(j).name)){
                                views.get(j).setCheckEditAction(editview,answer,ooo,xxx,contentActivity);

                                break;
                            }
                        }

                    }


                }

            }


    }//end of constructor

    //컨텐츠 실행 조건체크-쓰레드로 체크될 것
    public boolean checkCondition(double lat, double lng){
        double val = 0.000100;
        if(lat+val > latitude && lng-val < longitude && lat-val < latitude && lng-val > longitude){
            return true;
        }else{
            return false;
        }
    }
    //컨텐트 뷰 표시
    public void setContentView() throws InterruptedException {
        if(!CONTENT_USED){
            CONTENT_USED = true;
            //컨텐츠 뷰 활성화 - 테스트용
            for(int i=0;i<views.size();i++){
                views.get(i).setContentView();
            }
            if (hasEditview) editview.setVisibility(View.VISIBLE);
        }

    }

    //컨텐트 뷰 비활성화
    public void unsetContentView(){
        for(int i=0;i<views.size();i++){
            views.get(i).unsetContentView();
        }
        if (hasEditview) editview.setVisibility(View.VISIBLE);
        CONTENT_USED = false;
    }
    //컨텐츠 스크립트
    private void runScript(){

    }
    //컨텐츠 지우기
}

