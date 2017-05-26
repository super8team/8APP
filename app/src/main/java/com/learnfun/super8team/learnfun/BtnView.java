package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KIM on 2017-05-12.
 */

public class BtnView extends ContentView{
    private String fill;
    private String color;
    private int width;
    private int height;
    private int size;
    private Button btnView;

    BtnView(JSONObject jobj,View view) throws JSONException {
        this.id     = jobj.getInt("id");
        this.name   = jobj.getString("name");
        this.fill   = jobj.getString("fill");
        this.color  = jobj.getString("color");
        this.width  = jobj.getInt("width");
        this.height = jobj.getInt("height");
        this.size   = jobj.getInt("size");

        btnView = (Button) view;

        Log.i("뷰 설정완료 -----", this.name);
    }

    @Override
    public void setContentView() {
        //버튼 이름변경
        btnView.setText(this.name);

        //버튼 색상변경
        btnView.setBackgroundColor(Color.parseColor(this.fill));

        //버튼 크기설정
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnView.getLayoutParams();
        params.height = height;
        params.width  = width;
        btnView.setLayoutParams(params);

        //버튼 폰트 크기 변경
        btnView.setTextSize(size);

        //버튼 폰트색상 변경
        btnView.setTextColor(Color.parseColor(this.color));

        //버튼 출력
        btnView.setVisibility(View.VISIBLE);
    }

    @Override
    public void unsetContentView() {

    }

    @Override
    public void setClickAction(final JSONObject code, final ContentActivity contentActivity) {
        //클릭리스너 달고 인텐트로 코드값들 넘기기
        Log.i("리스너 생성","리스너 생성");
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("리스너 클릭","버튼 클릭");
                Intent intent = new Intent(contentActivity,Dialog.class);
                try {
                    if(code.has("out_txt")){
                        //해당 액션 코드가 있는지 검사후 있으면 추출후 인텐트에 넣음
                        String text = code.getString("out_txt");
                        intent.putExtra("text",text);
                    }
                    if (code.has("out_img")){
                        String image= code.getString("out_img");
                        intent.putExtra("image",image);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                contentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void setCheckEditAction(final EditText editview, final String answer, final JSONObject ooo, final JSONObject xxx, final ContentActivity contentActivity) {
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contentActivity,Dialog.class);

                try{

                    if (editview.getText().equals(answer)){
                        if(ooo.has("out_txt")){
                            String text = ooo.getString("out_txt");
                            intent.putExtra("text",text);
                        }
                        if (ooo.has("out_img")){
                            String image= ooo.getString("out_img");
                            intent.putExtra("image",image);
                        }
                    }else {
                        if(xxx.has("out_txt")){
                            String text = xxx.getString("out_txt");
                            intent.putExtra("text",text);
                        }
                        if (xxx.has("out_img")){
                            String image= xxx.getString("out_img");
                            intent.putExtra("image",image);
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                contentActivity.startActivity(intent);
            }
        });
    }

}
