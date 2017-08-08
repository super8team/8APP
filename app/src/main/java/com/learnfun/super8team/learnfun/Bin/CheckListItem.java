package com.learnfun.super8team.learnfun.Bin;

public class CheckListItem {

    public String substance;
    private boolean isSelected;
    public int no;
    public int respond;

    public CheckListItem(String substance, int no,int respond){

        this.substance = substance;
        this.no = no;
        this.respond = respond;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


}
