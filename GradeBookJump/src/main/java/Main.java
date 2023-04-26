package main.java;

import main.java.controller.Controller;
import main.java.utils.Database;
import main.java.view.Menu;
import java.util.Scanner;

public class Main {
        public static void main(String[] args){
            Scanner scanner = new Scanner(System.in);
            Controller controller = new Controller();
            Menu menu = new Menu(scanner,controller);
            menu.run();
            Database.closeConnection();
        }
    }

