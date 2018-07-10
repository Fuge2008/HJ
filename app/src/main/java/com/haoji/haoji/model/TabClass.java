package com.haoji.haoji.model;

/**
 * Created by Administrator on 2016/11/30.
 */

public class TabClass {

    private int Row;
    private int Col;
    private int ClassLength;
    private String ClassName;
    private Object obj;

    public TabClass(){}

    public TabClass(int Row, int Col, int ClassLength, String ClassName){
        this.Row=Row;
        this.Col=Col;
        this.ClassLength=ClassLength;
        this.ClassName=ClassName;
    }

    public int getRow() {
        return Row;
    }

    public int getCol() {
        return Col;
    }

    public String getClassName() {
        return ClassName;
    }

    public Object getObj() {
        return obj;
    }

    public void setRow(int row) {
        Row = row;
    }

    public void setCol(int col) {
        Col = col;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public int getClassLength() {
        return ClassLength;
    }

    public void setClassLength(int classLength) {
        ClassLength = classLength;
    }
}
