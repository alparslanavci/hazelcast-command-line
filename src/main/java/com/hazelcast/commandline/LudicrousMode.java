package com.hazelcast.commandline;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LudicrousMode {
    ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private String message = "";

    public void start() {
        threadPool.execute(() -> {
//            HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
//            IMap<Integer, LudicrousPositions> ludicrousPositionsIMap = hazelcastInstance.getMap("ludicrous");

            int maxX;
            int maxY;
            try {
                exec("resize", "-s", "5000", "5000");
            } catch (Exception ignored) {
            } finally {
                try {
                    maxX = Integer.parseInt(exec("tput", "cols"));
                    maxY = Integer.parseInt(exec("tput", "lines"));
                } catch (Exception ex) {
                    maxX = 150;
                    maxY = 150;
                }
            }

            char[][] screen = new char[maxY][maxX];

            int xPos = 0;
            int linePos = 0;
            int speed = 5;
            do {
                setClearScreen(screen);

                printLines(screen, linePos, (screen.length - 5) / 3);
                printLines(screen, linePos, 2 * (screen.length - 5) / 3);
                linePos += speed;

                printCar(screen, xPos, (screen.length - 5) / 6 - 3);
                printCar(screen, xPos, (screen.length - 5) / 2 - 3);
                printCar(screen, xPos, 5 * (screen.length - 5) / 6 - 3);
                if (xPos <= screen[0].length / 2) {
                    xPos += speed;
                }

                printMessage(screen, message);

                printScreen(screen);

                sleep();
                System.out.print("\033["+ (maxY + 1) +"A");
            } while (true);

        });

//        threadPool.execute(() -> {
//            message = "5 + 15 = ";
//            Scanner scanner = new Scanner(System.in);
//            message = scanner.nextLine();
//        });
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private void printLines(char[][] screen, int xPos, int yPos) {
//        int y = -yPos;
//        String line = "==========          ";
//        int lineLength = line.length();
//        for (int i = 0; i < screen[0].length + yPos; i = i + lineLength) {
//            int newY = y + i;
//            if (newY + lineLength > 0) {
//                printString(screen, line, xPos, newY);
//            }
//        }
//    }

    private void printLines(char[][] screen, int xPos, int yPos) {
        int x = -xPos;
        String line = "==========          ";
        int lineLength = line.length();
        for (int i = 0; i < screen[0].length + xPos; i = i + lineLength) {
            int newX = x + i;
            if (newX + lineLength > 0) {
                printString(screen, line, newX, yPos);
            }
        }
    }

    private void printCar(char[][] screen, int xPos, int yPos) {
        int x = 5 + xPos;
        String car =
                          "  HHHH        HHHH\n"
                        + "HHHHHHHHHHHHHHHHHHHHH\n"
                        + "HH                 HHHH\n"
                        + "HH     Hello!      HHHHHHH\n"
                        + "HH                 HHHH\n"
                        + "HHHHHHHHHHHHHHHHHHHHH\n"
                        + "  HHHH        HHHH\n";
        printString(screen, car, x, yPos);
    }

    private void printMessage(char[][] screen, String message) {
        int y = screen.length - 3;
        int x = 5;
        printString(screen, message, x, y);
    }

    private void printString(char[][] screen, String string, int x, int y) {
        int a = 0;
        int b = 0;
        for (int i = 0; i < string.length(); i++) {
            char charAt = string.charAt(i);
            if (charAt == '\n') {
                a++;
                b = 0;
                continue;
            }
            int newY = y + a;
            int newX = x + b++;
            if (newY > 0 && newY < screen.length && newX > 0 && newX < screen[0].length){
                screen[newY][newX] = charAt;
            }
        }
    }

    private void setClearScreen(char[][] screen) {
        for (int y = 1; y < screen.length; y++) {
            for (int x = 0; x < screen[y].length; x++) {
                if (y == 1 || y == screen.length - 5 /*|| x == 0 || x == screen[y].length - 1*/) {
                    screen[y][x] = '#';
                }else {
                    screen[y][x] = 0;
                }
            }
        }
    }

    private void printScreen(char[][] screen) {
        for (char[] strings : screen) {
            for (char pixel : strings) {
                System.out.print((pixel == 0) ? " " : pixel);
            }
            System.out.println();
        }
    }

    public String exec(String... commands) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder builder = new StringBuilder();
        process.waitFor();
        String line;
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}
