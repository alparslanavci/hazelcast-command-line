package com.hazelcast.commandline;

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

            int yPos = 0;
            int linePos = 0;
            int speed = 5;
            do {
                setClearScreen(screen);

                printLines(screen, screen.length / 3, linePos);
                printLines(screen, 2 * screen.length / 3, linePos);
                linePos += speed;

                printCar(screen, -3, yPos);
                if (yPos <= screen[0].length / 2) {
                    yPos += speed;
                }

                printMessage(screen, message);

                printScreen(screen);

                sleep();
                System.out.print("\033["+ (maxY + 1) +"A");
            } while (true);

        });

        threadPool.execute(() -> {
            message = "5 + 15 = ";
            Scanner scanner = new Scanner(System.in);
            message = scanner.nextLine();
        });
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printLines(char[][] screen, int xPos, int yPos) {
        int y = -yPos;
        String line = "==========          ";
        int lineLength = line.length();
        for (int i = 0; i < screen[0].length + yPos; i = i + lineLength) {
            int newY = y + i;
            if (newY + lineLength > 0) {
                printString(screen, line, xPos, newY);
            }
        }
    }

    private void printCar(char[][] screen, int xPos, int yPos) {
        int x = screen.length / 2 + xPos;
        int y = 5 + yPos;
        String car =
                          "  HHHH        HHHH\n"
                        + "HHHHHHHHHHHHHHHHHHHHH\n"
                        + "HH                 HHHH\n"
                        + "HH     Hello!      HHHHHHH\n"
                        + "HH                 HHHH\n"
                        + "HHHHHHHHHHHHHHHHHHHHH\n"
                        + "  HHHH        HHHH\n";
        printString(screen, car, x, y);
    }

    private void printMessage(char[][] screen, String message) {
        int x = screen.length - 3;
        int y = 5;
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
            int newX = x + a;
            int newY = y + b++;
            if (newX > 0 && newX < screen.length && newY > 0 && newY < screen[0].length){
                screen[newX][newY] = charAt;
            }
        }
    }

    private void setClearScreen(char[][] screen) {
        for (int x = 1; x < screen.length; x++) {
            for (int y = 0; y < screen[x].length; y++) {
                if (x == 1 || x == screen.length - 5 /*|| y == 0 || y == screen[x].length - 1*/) {
                    screen[x][y] = '#';
                }else {
                    screen[x][y] = 0;
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
