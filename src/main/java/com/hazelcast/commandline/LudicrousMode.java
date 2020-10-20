package com.hazelcast.commandline;

import com.hazelcast.cluster.Member;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LudicrousMode {
    ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private String message = "";
    private HazelcastInstance hazelcastInstance;

    public void start() {
        Config config = new Config();
        config.setClusterName("ludicrousMode");
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        IMap<Integer, Ludicrous> ludicrousPositionsIMap = hazelcastInstance.getMap("ludicrous");
        threadPool.execute(() -> {
            int self = 0;
            int memberCounter = 0;
            for (Member member : hazelcastInstance.getCluster().getMembers()) {
                if (member.equals(hazelcastInstance.getCluster().getLocalMember())){
                    self = memberCounter;
                    break;
                }
                memberCounter++;
            }

            if (self == 0) {
                ludicrousPositionsIMap.set(1, new Ludicrous());
            }

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

                boolean gameStarted = ludicrousPositionsIMap.get(1).gameStarted;
                printLines(screen, linePos, (screen.length - 5) / 3);
                printLines(screen, linePos, 2 * (screen.length - 5) / 3);
                if (gameStarted) {
                    linePos += speed;
                }

                int car1RelPos = ludicrousPositionsIMap.get(1).pos[0] - ludicrousPositionsIMap.get(1).pos[self];
                int car2RelPos = ludicrousPositionsIMap.get(1).pos[1] - ludicrousPositionsIMap.get(1).pos[self];
                int car3RelPos = ludicrousPositionsIMap.get(1).pos[2] - ludicrousPositionsIMap.get(1).pos[self];

                printCar(screen, xPos + car1RelPos, (screen.length - 5) / 6 - 3);
                printCar(screen, xPos + car2RelPos, (screen.length - 5) / 2 - 3);
                printCar(screen, xPos + car3RelPos, 5 * (screen.length - 5) / 6 - 3);
                if (gameStarted && xPos <= screen[0].length / 2) {
                    xPos += speed;
                }

                printMessage(screen, message);

                if (!gameStarted) {
                    printWelcomeMessage(screen, hazelcastInstance.getCluster().getMembers(), hazelcastInstance.getCluster().getLocalMember());
                }

                printScreen(screen);

                sleep(100);
                System.out.print("\033["+ (maxY + 1) +"A");

//                LudicrousPositions ludicrousPositions = ludicrousPositionsIMap.get(1);
//                if (self == 0) {
//                    ludicrousPositions.pos[0] += 1;
//                }
//                if (self == 1) {
//                    ludicrousPositions.pos[1] += 2;
//                }
//                if (self == 2) {
//                    ludicrousPositions.pos[2] += 3;
//                }
//                ludicrousPositionsIMap.set(1, ludicrousPositions);
            } while (true);

        });

        threadPool.execute(() -> {
            sleep(1000);
            message = "5 + 15 = ";
            String input = "";
            while (true) {
                Ludicrous ludicrous = ludicrousPositionsIMap.get(1);
                boolean gameStarted = ludicrous.gameStarted;
                Scanner scanner = new Scanner(System.in);
                input = scanner.nextLine();
                if (!gameStarted){
                    if (hazelcastInstance.getCluster().getMembers().iterator().next().equals(hazelcastInstance.getCluster().getLocalMember()) && input.equalsIgnoreCase("y")) {
                        ludicrous.gameStarted = true;
                        ludicrousPositionsIMap.set(1, ludicrous);
                    }
                }
            }
        });
    }

    private void printWelcomeMessage(char[][] screen, Set<Member> members, Member localMember) {
        String welcome = "#########################################################################################################################################\n"
                       + "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                __  __                           ___                            __    __                                               #\n"
                       + "#               /\\ \\/\\ \\                         /\\_ \\                          /\\ \\__/\\ \\                                              #\n"
                       + "#               \\ \\ \\_\\ \\     __     ____      __\\//\\ \\     ___     __      ____\\ \\ ,_\\ \\/      ____                                    #\n"
                       + "#                \\ \\  _  \\  /'__`\\  /\\_ ,`\\  /'__`\\\\ \\ \\   /'___\\ /'__`\\   /',__\\\\ \\ \\/\\/      /',__\\                                   #\n"
                       + "#                 \\ \\ \\ \\ \\/\\ \\L\\.\\_\\/_/  /_/\\  __/ \\_\\ \\_/\\ \\__//\\ \\L\\.\\_/\\__, `\\\\ \\ \\_      /\\__, `\\                                  #\n"
                       + "#                  \\ \\_\\ \\_\\ \\__/.\\_\\ /\\____\\ \\____\\/\\____\\ \\____\\ \\__/.\\_\\/\\____/ \\ \\__\\     \\/\\____/                                  #\n"
                       + "#                   \\/_/\\/_/\\/__/\\/_/ \\/____/\\/____/\\/____/\\/____/\\/__/\\/_/\\/___/   \\/__/      \\/___/                                   #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                __                  __                                                                    __                           #\n"
                       + "#               /\\ \\                /\\ \\  __                                           /'\\_/`\\            /\\ \\                          #\n"
                       + "#               \\ \\ \\      __  __   \\_\\ \\/\\_\\    ___   _ __   ___   __  __    ____    /\\      \\    ___    \\_\\ \\     __                  #\n"
                       + "#                \\ \\ \\  __/\\ \\/\\ \\  /'_` \\/\\ \\  /'___\\/\\`'__\\/ __`\\/\\ \\/\\ \\  /',__\\   \\ \\ \\__\\ \\  / __`\\  /'_` \\  /'__`\\                #\n"
                       + "#                 \\ \\ \\L\\ \\ \\ \\_\\ \\/\\ \\L\\ \\ \\ \\/\\ \\__/\\ \\ \\//\\ \\L\\ \\ \\ \\_\\ \\/\\__, `\\   \\ \\ \\_/\\ \\/\\ \\L\\ \\/\\ \\L\\ \\/\\  __/                #\n"
                       + "#                  \\ \\____/\\ \\____/\\ \\___,_\\ \\_\\ \\____\\\\ \\_\\\\ \\____/\\ \\____/\\/\\____/    \\ \\_\\\\ \\_\\ \\____/\\ \\___,_\\ \\____\\               #\n"
                       + "#                   \\/___/  \\/___/  \\/__,_ /\\/_/\\/____/ \\/_/ \\/___/  \\/___/  \\/___/      \\/_/ \\/_/\\/___/  \\/__,_ /\\/____/               #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#                                      Current Players:                                                                                 #\n";
        int memberCount = 1;
        for (Member member : members) {
              welcome += "                                       " + memberCount++ + "- " + member.getSocketAddress().getAddress() + ":" + member.getSocketAddress().getPort();
              if (member.equals(localMember)) {
                  welcome += " <=== This player";
              }
              welcome += "\n";
        }
              welcome += "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#########################################################################################################################################";
        printString(screen, welcome, (screen[0].length / 2) - 48, (screen.length / 2) - 10);
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
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
