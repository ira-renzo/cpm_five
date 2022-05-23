package com.example.cpm_five;

import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        System.out.println("Path: " + Paths.get(".").toAbsolutePath().normalize());
        SensorApplication.main(args);
    }
}