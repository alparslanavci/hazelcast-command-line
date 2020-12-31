package com.hazelcast.commandline;

import com.hazelcast.cluster.Member;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DoYouLoveMe {
    ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private String message = "";
    private HazelcastInstance hazelcastInstance;
    private int HIGHEST_CYCLE = 1050;
    private int SLEEP = 100;
    private int secondsPassed;
    private int remainingSeconds = 0;
    private final int TOTAL_SECONDS = HIGHEST_CYCLE / (1000 / SLEEP);
    private boolean botEnabled;

    public void start() {
        Config config = new Config();
        config.setClusterName("doyouloveme");
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");
//        System.setProperty("java.net.preferIPv4Stack", "true");
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        IMap<Integer, Ludicrous> ludicrousMap = hazelcastInstance.getMap("ludicrous");
        IMap<Integer, List<LudicrousQuestion>> ludicrousQuestions = hazelcastInstance.getMap("ludicrousQuestions");
        if (hazelcastInstance.getCluster().getMembers().iterator().next().equals(hazelcastInstance.getCluster().getLocalMember())) {
            prepareQuestions(ludicrousQuestions);
            ludicrousMap.set(1, new Ludicrous());
        }
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
            ludicrousMap.set(1, new Ludicrous());
        }
        int finalSelf = self;
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
            Random random = new Random();
            int car1RelPos = 0;
            int car2RelPos = 0;
            int car3RelPos = 0;

            int actualDifference1 = 0;
            int latestDifference1 = 0;
            int difference1 = 0;
            int actualDifference2 = 0;
            int latestDifference2 = 0;
            int difference2 = 0;
            int actualDifference3 = 0;
            int latestDifference3 = 0;
            int difference3 = 0;

            int cycle = 0;
            do {
                setClearScreen(screen);

//                boolean countDownStarted = ludicrousMap.get(1).countDownStarted;
//                boolean gameStarted = ludicrousMap.get(1).gameStarted;
//                printLines(screen, linePos, (screen.length - 5) / 3);
//                printLines(screen, linePos, 2 * (screen.length - 5) / 3);
//                if (gameStarted) {
//                    linePos += speed;
//                }

//                actualDifference1 = ludicrousMap.get(1).pos[0] - ludicrousMap.get(1).pos[finalSelf];
//                if (latestDifference1 != actualDifference1){
//                    difference1 += (actualDifference1 - latestDifference1);
//                    latestDifference1 = actualDifference1;
//                }
//                if (difference1 > 0) {
//                    car1RelPos++;
//                    difference1--;
//                } else if (difference1 < 0) {
//                    car1RelPos--;
//                    difference1++;
//                }

//                actualDifference2 = ludicrousMap.get(1).pos[1] - ludicrousMap.get(1).pos[finalSelf];
//                if (latestDifference2 != actualDifference2){
//                    difference2 += (actualDifference2 - latestDifference2);
//                    latestDifference2 = actualDifference2;
//                }
//                if (difference2 > 0) {
//                    car2RelPos++;
//                    difference2--;
//                } else if (difference2 < 0) {
//                    car2RelPos--;
//                    difference2++;
//                }

//                actualDifference3 = ludicrousMap.get(1).pos[2] - ludicrousMap.get(1).pos[finalSelf];
//                if (latestDifference3 != actualDifference3){
//                    difference3 += (actualDifference3 - latestDifference3);
//                    latestDifference3 = actualDifference3;
//                }
//                if (difference3 > 0) {
//                    car3RelPos++;
//                    difference3--;
//                } else if (difference3 < 0) {
//                    car3RelPos--;
//                    difference3++;
//                }

                Iterator<Member> memberIterator = hazelcastInstance.getCluster().getMembers().iterator();
                Member member1 = memberIterator.next();
                String member1name = member1.getSocketAddress().getAddress() + ":" + member1.getSocketAddress().getPort();
                if (cycle % 6 == 0 || cycle % 6 == 1 || cycle % 6 == 2) {
                    printDancer1(screen, member1name, 10, 10);
                } else {
                    printDancer2(screen, member1name, 10, 10);
                }
                String member2name = "<Available>";
                if (memberIterator.hasNext()) {
                    Member member2 = memberIterator.next();
                    member2name = member2.getSocketAddress().getAddress() + ":" + member2.getSocketAddress().getPort();
                    if (cycle % 6 == 0 || cycle % 6 == 1 || cycle % 6 == 2) {
                        printDancer4(screen, member2name, 50, 10);
                    } else {
                        printDancer3(screen, member2name, 50, 10);
                    }
                }
//                printCar(screen, xPos + car2RelPos, (screen.length - 5) / 2 - 3, member2name, gameStarted);
                String member3name = "<Available>";
                if (memberIterator.hasNext()) {
                    Member member3 = memberIterator.next();
                    member3name = member3.getSocketAddress().getAddress() + ":" + member3.getSocketAddress().getPort();
                }
//                printCar(screen, xPos + car3RelPos, 5 * (screen.length - 5) / 6 - 3, member3name, gameStarted);


//                if (gameStarted && xPos <= screen[0].length / 2 - 21) {
//                    xPos += speed;
//                }

//                printMessage(screen, message);

//                if (!countDownStarted) {
//                    printWelcomeMessage(screen, hazelcastInstance.getCluster().getMembers(), hazelcastInstance.getCluster().getLocalMember(),finalSelf == 0);
//                }
//
//                if(countDownStarted && !gameStarted){
//                    LudicrousQuestion question = ludicrousQuestions.get(1).get(0);
//                    message = question.question;
//                    printCountDown(screen, cycle, finalSelf == 0, ludicrousMap);
//                }
//
//                if (gameStarted){
//                    printClock(screen, cycle);
//                }

                printScreen(screen);

                sleep(SLEEP);
                System.out.print("\033["+ (maxY + 1) +"A");

//                Ludicrous ludicrousPositions = ludicrousMap.get(1);
//                if (self == 0) {
//                    ludicrousPositions.pos[0] += 1;
//                }

//                if (botEnabled && gameStarted) {
//                    if (finalSelf == 1) {
//                        ludicrousPositions.pos[1] += random.nextInt(2);
//                    }
//                }
//                if (self == 2) {
//                    ludicrousPositions.pos[2] += 3;
//                }
//                ludicrousMap.set(1, ludicrousPositions);

//                if (countDownStarted || gameStarted) {
                    cycle++;
//                }
            } while (cycle <= HIGHEST_CYCLE);

            printFinish(screen, ludicrousMap);

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
                if (!ludicrous.countDownStarted && !ludicrous.gameStarted){
                    if (hazelcastInstance.getCluster().getMembers().iterator().next().equals(hazelcastInstance.getCluster().getLocalMember()) && input.equalsIgnoreCase("y")) {
                        ludicrous.countDownStarted = true;
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
                    if (input.equalsIgnoreCase("b")) {
                        botEnabled = true;
                    }
                    message = question.question;
                }
            }
        });
    }

    private void printDancer1(char[][] screen, String member1name, int x, int y) {
        String dancer = "############################\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#    "+member1name+"       #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "############################\n"
                      + "             ##             \n"
                      + "      #      ##             \n"
                      + "      ################      \n"
                      + "             ##      #      \n"
                      + "             ##             \n"
                      + "             ##             \n"
                      + "       ##############       \n"
                      + "       #            #       \n"
                      + "       #           #        \n"
                      + "      #           #         \n"
                      + "      #          #          \n"
                      + "      #           #         \n"
                      + "       #           #        \n"
                      + "       #            #       \n";

        printString(screen, dancer, x, y);

    }

    private void printDancer2(char[][] screen, String member1name, int x, int y) {
        String dancer = "############################\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#    "+member1name+"       #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "############################\n"
                      + "             ##             \n"
                      + "             ##      #      \n"
                      + "      ################      \n"
                      + "      #      ##             \n"
                      + "             ##             \n"
                      + "             ##             \n"
                      + "       ##############       \n"
                      + "       #            #       \n"
                      + "       #             #      \n"
                      + "      #               #     \n"
                      + "      #                #    \n"
                      + "      #               #     \n"
                      + "       #             #      \n"
                      + "       #            #       \n";

        printString(screen, dancer, x, y);

    }

    private void printDancer3(char[][] screen, String member1name, int x, int y) {
        String dancer = "############################\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#    "+member1name+"       #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "############################\n"
                      + "             ##             \n"
                      + "      #      ##             \n"
                      + "      ################      \n"
                      + "             ##      #      \n"
                      + "             ##             \n"
                      + "             ##             \n"
                      + "       ##############       \n"
                      + "       #            #       \n"
                      + "      #             #       \n"
                      + "     #             #        \n"
                      + "    #              #        \n"
                      + "     #             #        \n"
                      + "      #             #       \n"
                      + "       #            #       \n";

        printString(screen, dancer, x, y);

    }

    private void printDancer4(char[][] screen, String member1name, int x, int y) {
        String dancer = "############################\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#    "+member1name+"       #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "#                          #\n"
                      + "############################\n"
                      + "             ##             \n"
                      + "             ##      #      \n"
                      + "      ################      \n"
                      + "      #      ##             \n"
                      + "             ##             \n"
                      + "             ##             \n"
                      + "       ##############       \n"
                      + "       #            #       \n"
                      + "        #           #       \n"
                      + "         #         #        \n"
                      + "          #        #        \n"
                      + "         #         #        \n"
                      + "        #           #       \n"
                      + "       #            #       \n";

        printString(screen, dancer, x, y);

    }

    private void printCountDown(char[][] screen, int cycle, boolean master, IMap<Integer, Ludicrous> ludicrousMap) {
        int max = 5;
        
        if (cycle % 10 == 0){
            secondsPassed = cycle / 10;
            remainingSeconds = max - secondsPassed;
        }
        
        if (remainingSeconds == 0){
            secondsPassed = 0;
            if (master) {
                Ludicrous ludicrous = ludicrousMap.get(1);
                ludicrous.gameStarted = true;
                ludicrousMap.set(1, ludicrous);
            }
            remainingSeconds = TOTAL_SECONDS - 5;
            return;
        }

        String welcome = "#########################################################################################################################################\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                   ########     ###     ######  ########     ######  ########    ###    ########  ########  ######                     #\n"
                + "#                   #     ##   ## ##   ##    ## ##          ##    ##    ##      ## ##   ##     ##    ##    ##    ##                     #\n"
                + "#                   #     ##  ##   ##  ##       ##          ##          ##     ##   ##  ##     ##    ##    ##                           #\n"
                + "#                   #######  ##     ## ##       ######       ######     ##    ##     ## ########     ##     ######                      #\n"
                + "#                   #   ##   ######### ##       ##                ##    ##    ######### ##   ##      ##          ##                     #\n"
                + "#                   #    ##  ##     ## ##    ## ##          ##    ##    ##    ##     ## ##    ##     ##    ##    ##                     #\n"
                + "#                   #     ## ##     ##  ######  ########     ######     ##    ##     ## ##     ##    ##     ######                      #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                           #### ##    ##                                                               #\n"
                + "#                                                            ##  ###   ##                                                               #\n"
                + "#                                                            ##  ####  ##                                                               #\n"
                + "#                                                            ##  ## ## ##                                                               #\n"
                + "#                                                            ##  ##  ####                                                               #\n"
                + "#                                                            ##  ##   ###                                                               #\n"
                + "#                                                           #### ##    ##                                                               #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#                                                                                                                                       #\n"
                + "#########################################################################################################################################\n";

        printString(screen, welcome, (screen[0].length / 2) - 48, (screen.length / 2) - 18);
        printString(screen, NUMBERS[remainingSeconds],screen[0].length / 2 + 14 , (screen.length / 2) + 5);
    }

    private void printClock(char[][] screen, int cycle) {

        printString(screen, NUMBERS[0],screen[0].length - 42, 3);
        printString(screen, NUMBERS[10],screen[0].length - 31, 3);
        if (cycle % 10 == 0){
            secondsPassed = cycle / 10;
            remainingSeconds = TOTAL_SECONDS - secondsPassed;
        }
        printString(screen, NUMBERS[remainingSeconds /10],screen[0].length - 27, 3);
        printString(screen, NUMBERS[remainingSeconds %10],screen[0].length - 16, 3);

    }

    String[] NUMBERS =  {"   #####   \n"
                       + "  ##   ##  \n"
                       + " ##     ## \n"
                       + " ##     ## \n"
                       + " ##     ## \n"
                       + "  ##   ##  \n"
                       + "   #####   \n",
                       "     ##    \n"
                     + "   ####    \n"
                     + "     ##    \n"
                     + "     ##    \n"
                     + "     ##    \n"
                     + "     ##    \n"
                     + "   ######  \n",
                       "  #######  \n"
                     + " ##     ## \n"
                     + "        ## \n"
                     + "  #######  \n"
                     + " ##        \n"
                     + " ##        \n"
                     + " ######### \n",
                       "  #######  \n"
                     + " ##     ## \n"
                     + "        ## \n"
                     + "  #######  \n"
                     + "        ## \n"
                     + " ##     ## \n"
                     + "  #######  \n",
                       " ##        \n"
                     + " ##    ##  \n"
                     + " ##    ##  \n"
                     + " ##    ##  \n"
                     + " ######### \n"
                     + "       ##  \n"
                     + "       ##  \n",
                       "  ######## \n"
                     + "  ##       \n"
                     + "  ##       \n"
                     + "  #######  \n"
                     + "        ## \n"
                     + "  ##    ## \n"
                     + "   ######  \n",
                       "  #######  \n"
                     + " ##     ## \n"
                     + " ##        \n"
                     + " ########  \n"
                     + " ##     ## \n"
                     + " ##     ## \n"
                     + "  #######  \n",
                       "  ######## \n"
                     + "  ##    ## \n"
                     + "      ##   \n"
                     + "     ##    \n"
                     + "    ##     \n"
                     + "    ##     \n"
                     + "    ##     \n",
                       "  #######  \n"
                     + " ##     ## \n"
                     + " ##     ## \n"
                     + "  #######  \n"
                     + " ##     ## \n"
                     + " ##     ## \n"
                     + "  #######  \n",
                       "  #######  \n"
                     + " ##     ## \n"
                     + " ##     ## \n"
                     + "  ######## \n"
                     + "        ## \n"
                     + " ##     ## \n"
                     + "  #######  \n",
                       "    \n"
                     + "    \n"
                     + " ## \n"
                     + "    \n"
                     + " ## \n"
                     + "    \n"
                     + "    \n", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
            , "", "", "", "", "", "", "", ""};

    private void printFinish(char[][] screen, IMap<Integer, Ludicrous> ludicrousMap) {
        setClearScreen(screen);

        String finishString = "             ____                             __                     \n"
                + " _______    /\\  _`\\   __          __         /\\ \\          _______   \n"
                + "/\\______\\   \\ \\ \\L\\_\\/\\_\\    ___ /\\_\\    ____\\ \\ \\___     /\\______\\  \n"
                + "\\/______/_   \\ \\  _\\/\\/\\ \\ /' _ `\\/\\ \\  /',__\\\\ \\  _ `\\   \\/______/_ \n"
                + "  /\\______\\   \\ \\ \\/  \\ \\ \\/\\ \\/\\ \\ \\ \\/\\__, `\\\\ \\ \\ \\ \\    /\\______\\\n"
                + "  \\/______/    \\ \\_\\   \\ \\_\\ \\_\\ \\_\\ \\_\\/\\____/ \\ \\_\\ \\_\\   \\/______/\n"
                + "                \\/_/    \\/_/\\/_/\\/_/\\/_/\\/___/   \\/_/\\/_/            \n"
                + "                                                                     \n"
                + "                                                                     \n"
                + "                             ----- RESULTS -----                     \n"
                + "                                                                     \n";
        printString(screen, finishString, (screen[0].length / 2) - 38, 25);

        int[] posArray = ludicrousMap.get(1).pos;
        int[] copyOf = Arrays.copyOf(posArray, posArray.length);
        Arrays.sort(copyOf);

        for (int i = copyOf.length - 1; i >= 0; i--) {
            int pos = copyOf[i];
            int posIndex = 0;
            for (int j = 0; j < posArray.length; j++) {
                int unsortedPos = posArray[j];
                if (unsortedPos == pos){
                    posIndex = j;
                }
            }
            Set<Member> memberIterator = hazelcastInstance.getCluster().getMembers();
            int count = 0;
            for (Member member : memberIterator) {
                if (count++ == posIndex) {
                    String name = (3 - i) + " - " + member.getSocketAddress().getAddress() + ":" + member.getSocketAddress().getPort() + "        " + (pos * 10) + " pts\n";
                    printString(screen, name, (screen[0].length / 2) - (name.length() / 2), 37 + (-i));
                }
            }
        }

        printScreen(screen);
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

    private void printWelcomeMessage(char[][] screen, Set<Member> members, Member localMember, boolean master) {
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
                       + "#                                                                                                                                       #\n";
        if(master) {
              welcome += "#                                     Please press 'y' and then 'Enter' to start the race!                                              #\n";
        }
        else {
              welcome += "#                                     Please wait Master Member to start the race!                                                      #\n";
        }
              welcome += "#                                                                                                                                       #\n"
                       + "#                                      Current Players:                                                                                 #\n";
        int memberCount = 1;
        for (Member member : members) {
              welcome += "                                       " + memberCount++ + "- " + member.getSocketAddress().getAddress() + ":" + member.getSocketAddress().getPort();
              if (member.equals(localMember)) {
                  welcome += " <=== This player";
              }
              welcome += "                                                            \n";
        }
              welcome += "#                                                                                                                                       #\n"
                       + "#                                                                                                                                       #\n"
                       + "#########################################################################################################################################";
        printString(screen, welcome, (screen[0].length / 2) - 48, (screen.length / 2) - 18);
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

    private void printCar(char[][] screen, int xPos, int yPos, String name, boolean gameStarted) {
        int x = 5 + xPos;
        if (x > screen[0].length - 5) {
            String ahead = name + " >>>";
            printString(screen, ahead, screen[0].length - ahead.length(), yPos + 3);
        } else if (x < 0){
            String behind = "<<< " + name;
            printString(screen, behind, behind.length(), yPos + 3);
        } else {
            String car = name + "\n"
                    + "                  .\n"
                    + "    __            |\\\n"
                    + " __/__\\___________| \\_\n"
                    + "|   ___    |  ,|   ___`-.\n"
                    + "|  /   \\   |___/  /   \\  `-.\n"
                    + "|_| (O) |________| (O) |____|\n"
                    + "   \\___/          \\___/\n";
            if (gameStarted) {
                car =     "         " + name + "\n"
                        + " ----                     .\n"
                        + "      ---   __            |\\\n"
                        + "----     __/__\\___________| \\_\n"
                        + "        |   ___    |  ,|   ___`-.\n"
                        + "  ---   |  /   \\   |___/  /   \\  `-.\n"
                        + "        |_| (O) |________| (O) |____|\n"
                        + "   _______ \\___/          \\___/\n";
            }
            printString(screen, car, x, yPos);
        }
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
