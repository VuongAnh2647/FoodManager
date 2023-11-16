package com.example.foodmanager.model;

import java.io.Serializable;

public class Table  {
    private String idTable;
    private String nameTable;
    private String Status ;
    private boolean isHidden;


    public Table() {
    }

    public Table(String idTable, String nameTable,  boolean isHidden) {
        this.idTable = idTable;
        this.nameTable = nameTable;
        this.isHidden = isHidden;
    }

    public String getIdTable() {
        return idTable;
    }

    public void setIdTable(String idTable) {
        this.idTable = idTable;
    }

    public String getNameTable() {
        return nameTable;
    }

    public void setNameTable(String nameTable) {
        this.nameTable = nameTable;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
