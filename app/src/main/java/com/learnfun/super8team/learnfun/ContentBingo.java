package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import static android.view.Window.FEATURE_NO_TITLE;

public class ContentBingo extends AppCompatActivity {
    private ArrayList<ImageView> bingoBoard = new ArrayList<>();
    private final int ON = 1;
    private final int OFF = 0;
    private int boardStatus[] = {OFF,OFF,OFF,OFF,OFF,OFF,OFF,OFF,OFF};
    @Override
    public View findViewById(@IdRes int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_bingo);


        //빙고 뷰 선언받기

        bingoBoard.add(0,(ImageView) findViewById(R.id.bingoBoard1));
        bingoBoard.add(1,(ImageView) findViewById(R.id.bingoBoard2));
        bingoBoard.add(2,(ImageView) findViewById(R.id.bingoBoard3));
        bingoBoard.add(3,(ImageView) findViewById(R.id.bingoBoard4));
        bingoBoard.add(4,(ImageView) findViewById(R.id.bingoBoard5));
        bingoBoard.add(5,(ImageView) findViewById(R.id.bingoBoard6));
        bingoBoard.add(6,(ImageView) findViewById(R.id.bingoBoard7));
        bingoBoard.add(7,(ImageView) findViewById(R.id.bingoBoard8));
        bingoBoard.add(8,(ImageView) findViewById(R.id.bingoBoard9));


        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        //문자열로된 숫자들을 /를 기준으로 자른다 첫째문자열버림

        String pointer[] = data.substring(1).split("/");

        //가지고있는 빙고판을 활성화
        for (int i=0;i<pointer.length;i++){
            switch (pointer[i]){
                case "1":
                    bingoBoard.get(0).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[0] = ON;
                    break;
                case "2":
                    bingoBoard.get(1).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[1] = ON;
                    break;
                case "3":
                    bingoBoard.get(2).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[2] = ON;
                    break;
                case "4":
                    bingoBoard.get(3).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[3] = ON;
                    break;
                case "5":
                    bingoBoard.get(4).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[4] = ON;
                    break;
                case "6":
                    bingoBoard.get(5).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[5] = ON;
                    break;
                case "7":
                    bingoBoard.get(6).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[6] = ON;
                    break;
                case "8":
                    bingoBoard.get(7).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[7] = ON;
                    break;
                case "9":
                    bingoBoard.get(8).setBackgroundResource(R.drawable.bingocheck);
                    boardStatus[8] = ON;
                    break;
            }
        }
//        Log.i("빙고뷰 상태", String.valueOf(bingoBoard.get(0).getVisibility()));
        for(int i=0,j=0;j<3;i+=3,j++){
            if(     boardStatus[0+i] == ON &&
                    boardStatus[1+i] == ON &&
                    boardStatus[2+i] == ON){
                bingoBoard.get(0+i).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(1+i).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(2+i).setBackgroundResource(R.drawable.bingoclear);
            }
            if(     boardStatus[0+j] == ON &&
                    boardStatus[3+j] == ON &&
                    boardStatus[6+j] == ON ){
                bingoBoard.get(0+j).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(3+j).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(6+j).setBackgroundResource(R.drawable.bingoclear);
            }
            if(     boardStatus[0] == ON &&
                    boardStatus[4] == ON &&
                    boardStatus[8] == ON  ){
                bingoBoard.get(0).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(4).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(8).setBackgroundResource(R.drawable.bingoclear);
            }
            if(     boardStatus[2] == ON  &&
                    boardStatus[4] == ON  &&
                    boardStatus[6] == ON  ){
                bingoBoard.get(2).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(4).setBackgroundResource(R.drawable.bingoclear);
                bingoBoard.get(6).setBackgroundResource(R.drawable.bingoclear);
            }
        }
        setResult(4132);
    }
}

