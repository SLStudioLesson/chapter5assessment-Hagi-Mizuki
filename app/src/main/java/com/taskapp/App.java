package com.taskapp;

import com.taskapp.exception.AppException;
import com.taskapp.ui.TaskUI;

public class App {

    public static void main(String[] args) throws AppException{  
        TaskUI ui = new TaskUI();
        ui.displayMenu();
    }
}
