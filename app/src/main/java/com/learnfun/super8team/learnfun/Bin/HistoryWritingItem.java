package com.learnfun.super8team.learnfun.Bin;

import android.content.Context;

public class HistoryWritingItem {
    public String text;
    public String uri;
    public Context cont;
    public HistoryWritingItem(String text, String uri, Context cont){
        this.text = text;
        this.uri = uri;
        this.cont = cont;
    }
    public HistoryWritingItem(String uri, Context cont){
        this.uri = uri;
        this.cont = cont;
    }

}
