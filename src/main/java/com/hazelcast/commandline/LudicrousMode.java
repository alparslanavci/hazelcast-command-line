package com.hazelcast.commandline;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.cluster.Member;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LudicrousMode {
    ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private String message = "";
    private HazelcastInstance hazelcastInstance;

    public void start() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        //        JoinConfig join = config.getNetworkConfig().getJoin();
        //        join.getMulticastConfig().setEnabled(false);
        //        join.getTcpIpConfig().setEnabled(true).addMember("10.212.134.150").addMember("10.212.134.151").addMember("10.212.134.152");
        hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);

//        PNCounter counter = hazelcastInstance.getPNCounter("ludicrous");
//        long self = counter.getAndIncrement();
        String name = hazelcastInstance.getName();
        int self = Integer.parseInt(name.charAt(name.length() - 1) + "") - 1;
        hazelcastInstance.getList("clientList").add(hazelcastInstance.getName());

        IMap<Integer, Ludicrous> ludicrousMap = hazelcastInstance.getMap("ludicrous");
        IMap<Integer, List<LudicrousQuestion>> ludicrousQuestions = hazelcastInstance.getMap("ludicrousQuestions");

        if (self == 0) {
            prepareQuestions(ludicrousQuestions);
            ludicrousMap.set(1, new Ludicrous());
        }
//        int self = 0;
//        int memberCounter = 0;
//        for (Member member : hazelcastInstance.getCluster().getMembers()) {
//            if (member.equals(hazelcastInstance.getCluster().getLocalMember())){
//                self = memberCounter;
//                break;
//            }
//            memberCounter++;
//        }
        if (self == 0) {
            ludicrousMap.set(1, new Ludicrous());
        }
        int finalSelf = (int) self;
        System.out.println("------------------------" + finalSelf);
        threadPool.execute(() -> {
//            int maxX = 270;
//            int maxY = 76;
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

                boolean gameStarted = ludicrousMap.get(1).gameStarted;
                printLines(screen, linePos, (screen.length - 5) / 3);
                printLines(screen, linePos, 2 * (screen.length - 5) / 3);
                if (gameStarted) {
                    linePos += speed;
                }

//                int car1RelPos = 0;
//                if (ludicrousMap.get(1).pos[0] - ludicrousMap.get(1).pos[finalSelf] > 0) {
//                    car1RelPos++;
//                } else if (ludicrousMap.get(1).pos[0] - ludicrousMap.get(1).pos[finalSelf] < 0) {
//                    car1RelPos--;
//                }
//
//                int car2RelPos = 0;
//                if (ludicrousMap.get(1).pos[1] - ludicrousMap.get(1).pos[finalSelf] > 0) {
//                    car2RelPos++;
//                } else if (ludicrousMap.get(1).pos[1] - ludicrousMap.get(1).pos[finalSelf] < 0) {
//                    car2RelPos--;
//                }
//
//                int car3RelPos = 0;
//                if (ludicrousMap.get(1).pos[2] - ludicrousMap.get(1).pos[finalSelf] > 0) {
//                    car3RelPos++;
//                } else if (ludicrousMap.get(1).pos[2] - ludicrousMap.get(1).pos[finalSelf] < 0) {
//                    car3RelPos--;
//                }

                int car1RelPos = ludicrousMap.get(1).pos[0] - ludicrousMap.get(1).pos[finalSelf];
                int car2RelPos = ludicrousMap.get(1).pos[1] - ludicrousMap.get(1).pos[finalSelf];
                int car3RelPos = ludicrousMap.get(1).pos[2] - ludicrousMap.get(1).pos[finalSelf];

                printCar(screen, xPos + car1RelPos, (screen.length - 5) / 6 - 3);
                printCar(screen, xPos + car2RelPos, (screen.length - 5) / 2 - 3);
                printCar(screen, xPos + car3RelPos, 5 * (screen.length - 5) / 6 - 3);
                if (gameStarted && xPos <= screen[0].length / 2) {
                    xPos += speed;
                }

                printMessage(screen, message);

                if (!gameStarted) {
                    printWelcomeMessage(screen, hazelcastInstance.getCluster().getMembers(), hazelcastInstance.getName());
                }

                printScreen(screen);

                sleep(100);
                System.out.print("\033["+ (maxY + 1) +"A");

//                Ludicrous ludicrousPositions = ludicrousMap.get(1);
//                if (self == 0) {
//                    ludicrousPositions.pos[0] += 1;
//                }
//                if (self == 1) {
//                    ludicrousPositions.pos[1] += 2;
//                }
//                if (self == 2) {
//                    ludicrousPositions.pos[2] += 3;
//                }
//                ludicrousMap.set(1, ludicrousPositions);
            } while (true);

        });

        threadPool.execute(() -> {
            sleep(1000);
            message = "";
            String input = "";
            int questionNumber = 0;
            List<LudicrousQuestion> questions = ludicrousQuestions.get(1);
            while (true) {
                Ludicrous ludicrous = ludicrousMap.get(1);
                Scanner scanner = new Scanner(System.in);
                input = scanner.nextLine();
                if (!ludicrous.gameStarted){
                    if (self == 0 && input.equalsIgnoreCase("y")) {
                        ludicrous.gameStarted = true;
                        ludicrousMap.set(1, ludicrous);
                    }
                }
                if (ludicrous.gameStarted) {
                    LudicrousQuestion question = questions.get(questionNumber);
                    if (input.equalsIgnoreCase(question.answer)) {
                        ludicrousMap.executeOnKey(1, (EntryProcessor<Integer, Ludicrous, Object>) entry -> {
                            Ludicrous value = entry.getValue();
                            value.pos[finalSelf] += 10;
                            entry.setValue(value);
                            return null;
                        });
                        question = questions.get(++questionNumber);
                    }
                    message = question.question;
                }
            }
        });
    }

    private void prepareQuestions(IMap<Integer, List<LudicrousQuestion>> ludicrousQuestions) {
        Random random = new Random();
        ArrayList<LudicrousQuestion> questions = new ArrayList<>();
        int bound = 10;
        for (int i = 0; i < 100; i++) {
            int first = random.nextInt(bound);
            int second = random.nextInt(bound);
            int answer = first + second;
            LudicrousQuestion question = new LudicrousQuestion();
            question.question = first + " + " + second + " = ?";
            question.answer = String.valueOf(answer);
            questions.add(question);
        }
        ludicrousQuestions.set(1, questions);
    }

    private void printWelcomeMessage(char[][] screen, Set<Member> members, String localMember) {
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
              if (memberCount == Integer.parseInt((localMember.charAt(localMember.length() - 1) + 1) + "")) {
//              if (member.equals(localMember)) {
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
