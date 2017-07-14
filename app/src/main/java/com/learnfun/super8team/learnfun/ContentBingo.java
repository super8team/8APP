package com.learnfun.super8team.learnfun;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import static android.view.Window.FEATURE_NO_TITLE;

public class ContentBingo extends AppCompatActivity {
    private ArrayList<ImageView> bingoBoard = new ArrayList<>();

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
                    bingoBoard.get(0).setVisibility(View.VISIBLE);
                    break;
                case "2":
                    bingoBoard.get(1).setVisibility(View.VISIBLE);
                    break;
                case "3":
                    bingoBoard.get(2).setVisibility(View.VISIBLE);
                    break;
                case "4":
                    bingoBoard.get(3).setVisibility(View.VISIBLE);
                    break;
                case "5":
                    bingoBoard.get(4).setVisibility(View.VISIBLE);
                    break;
                case "6":
                    bingoBoard.get(5).setVisibility(View.VISIBLE);
                    break;
                case "7":
                    bingoBoard.get(6).setVisibility(View.VISIBLE);
                    break;
                case "8":
                    bingoBoard.get(7).setVisibility(View.VISIBLE);
                    break;
                case "9":
                    bingoBoard.get(8).setVisibility(View.VISIBLE);
                    break;
            }
        }

        setResult(4132);
    }
}
