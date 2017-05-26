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

    TxtView(JSONObject jobj,View view) throws JSONException {
        this.id           = jobj.getInt("id");
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

    }

    @Override
    public void setClickAction(final JSONObject code, final ContentActivity contentActivity) {
        textView.setOnClickListener(new View.OnClickListener() {
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
        textView.setOnClickListener(new View.OnClickListener() {
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
