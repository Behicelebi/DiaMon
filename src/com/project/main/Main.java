package com.project.main;

import com.project.view.Frame;

public class Main {
    public static String url = "jdbc:sqlserver://localhost:1433;databaseName=PROJE3;encrypt=true;trustServerCertificate=true";
    public static String username = "sa";
    public static String password = "diyabet1234";
    public static String enUserName, enPassword;
    public static Frame frame;
    public static void main(String[] args) {
        frame = new Frame();
    }
}