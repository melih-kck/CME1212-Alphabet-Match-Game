import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.print("Enter player name: ");
        String playerName = scanner.nextLine();

        Queue highScoreNames = new Queue(10);
        Queue highScoreScores = new Queue(10);
        loadHighScores(highScoreNames, highScoreScores);

        System.out.println("THE GAME STARTS NOW!...");

        Queue sets = new Queue(5);
        int totalInitialTiles = 0;

        for (int i = 0; i < 5; i++) {
            Stack set = new Stack(10);
            int numberOfTiles = random.nextInt(10) + 1;
            fillSetWithUniqueLetters(set, numberOfTiles, random);
            sets.enqueue(set);
            totalInitialTiles += numberOfTiles;
        }

        int maxSteps = (int) (totalInitialTiles * 1.2);

        Queue reserveQueue = new Queue(30);
        Queue supplementaryQueue = new Queue(30);

        fillQueueWithAlphabet(reserveQueue, random);
        fillQueueWithAlphabet(supplementaryQueue, random);

        int score = 0;
        int remainingShifts = random.nextInt(5) + 1;
        int stepCount = 0;
        boolean gameOver = false;

        displayFullState(sets, reserveQueue, supplementaryQueue,
                score, remainingShifts, stepCount, maxSteps);

        while (!gameOver && stepCount < maxSteps && !allSetsEmpty(sets)) {
            System.out.print(">> ");
            String command = scanner.nextLine();
            String trimmedCommand = command.replace(" ", "");
            boolean validStep = false;

            if (trimmedCommand.startsWith("Match(")) {
                try {
                    int comma = trimmedCommand.indexOf(',');
                    int lastParenthesis = trimmedCommand.indexOf(')');

                    if (comma < 7 || lastParenthesis != trimmedCommand.length() - 1) {
                        throw new Exception();
                    }

                    int firstIndex = Integer.parseInt(trimmedCommand.substring(6, comma)) - 1;
                    int secondIndex = Integer.parseInt(
                            trimmedCommand.substring(comma + 1, lastParenthesis)) - 1;

                    if (firstIndex >= 0 && firstIndex < 5
                            && secondIndex >= 0 && secondIndex < 5
                            && firstIndex != secondIndex) {
                        Stack firstSet = getSet(sets, firstIndex);
                        Stack secondSet = getSet(sets, secondIndex);

                        if (firstSet.isEmpty() || secondSet.isEmpty()) {
                            System.out.println("One or both sets are empty. Try again.");
                        } else if (firstSet.peek().equals(secondSet.peek())) {
                            firstSet.pop();
                            secondSet.pop();
                            score += 5;
                            System.out.println("Match successful! +5 points.");
                            validStep = true;
                        } else {
                            System.out.println("Tiles do not match. Try again.");
                        }
                    } else {
                        System.out.println("Invalid set numbers. Please enter values between 1 and 5, and choose two different sets.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid Match command format.");
                }
            } else if (trimmedCommand.startsWith("AddSet(")) {
                try {
                    int lastParenthesis = trimmedCommand.indexOf(')');

                    if (lastParenthesis != trimmedCommand.length() - 1) {
                        throw new Exception();
                    }

                    int setIndex = Integer.parseInt(
                            trimmedCommand.substring(7, lastParenthesis)) - 1;

                    if (setIndex >= 0 && setIndex < 5) {
                        Stack selectedSet = getSet(sets, setIndex);

                        if (reserveQueue.isEmpty()) {
                            System.out.println("Reserve queue is empty! Cannot add tile.");
                        } else if (selectedSet.isFull()) {
                            Object tile = reserveQueue.dequeue();
                            reserveQueue.enqueue(tile);
                            System.out.println("Set" + (setIndex + 1)
                                    + " is full! Tile returned to Reserve Queue.");
                        } else {
                            selectedSet.push(reserveQueue.dequeue());

                            if (remainingShifts == 0) {
                                score -= 2;
                                System.out.println("No shift rights left. AddSet penalty: -2 points.");
                            }

                            validStep = true;
                        }
                    } else {
                        System.out.println("Invalid set number. Please enter a value between 1 and 5.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid AddSet command format.");
                }
            } else if (trimmedCommand.equals("ShiftQueue")) {
                if (remainingShifts > 0) {
                    if (reserveQueue.isEmpty()) {
                        System.out.println("Reserve queue is empty! Cannot shift.");
                    } else {
                        reserveQueue.enqueue(reserveQueue.dequeue());
                        remainingShifts--;
                        System.out.println("Reserve queue shifted.");
                        validStep = true;
                    }
                } else {
                    System.out.println("No shift rights left!");
                }
            } else if (trimmedCommand.equals("F")) {
                gameOver = true;
            } else {
                System.out.println("Invalid command. Please try again.");
            }

            if (validStep) {
                stepCount++;

                if (stepCount % 3 == 0 && !supplementaryQueue.isEmpty()) {
                    int minSetIndex = getMinSetIndex(sets);
                    Stack minSet = getSet(sets, minSetIndex);
                    Object tile = supplementaryQueue.dequeue();

                    if (minSet.isFull()) {
                        supplementaryQueue.enqueue(tile);
                    } else {
                        minSet.push(tile);
                        System.out.println("[Auto] Tile added to Set"
                                + (minSetIndex + 1) + " from Supplementary Queue.");
                    }
                }

                displayFullState(sets, reserveQueue, supplementaryQueue,
                        score, remainingShifts, stepCount, maxSteps);
            }
        }

        System.out.println("END OF THE GAME!...\n");
        updateAndDisplayHighScores(playerName, score,
                highScoreNames, highScoreScores);

        System.out.println("Play again?");
        String playAgain = scanner.nextLine().trim();

        if (playAgain.equalsIgnoreCase("Y")) {
            System.out.println("Please restart the program to play again.");
        }

        scanner.close();
    }

    public static void fillSetWithUniqueLetters(Stack targetStack,
                                                 int count, Random random) {
        Queue alphabetQueue = new Queue(26);

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            alphabetQueue.enqueue(letter);
        }

        int remainingLetters = 26;

        for (int i = 0; i < count; i++) {
            int rotations = random.nextInt(remainingLetters);

            for (int j = 0; j < rotations; j++) {
                alphabetQueue.enqueue(alphabetQueue.dequeue());
            }

            targetStack.push(alphabetQueue.dequeue());
            remainingLetters--;
        }
    }

    public static void fillQueueWithAlphabet(Queue targetQueue, Random random) {
        Queue alphabetQueue = new Queue(26);

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            alphabetQueue.enqueue(letter);
        }

        int remainingLetters = 26;

        while (!alphabetQueue.isEmpty()) {
            int rotations = random.nextInt(remainingLetters);

            for (int i = 0; i < rotations; i++) {
                alphabetQueue.enqueue(alphabetQueue.dequeue());
            }

            targetQueue.enqueue(alphabetQueue.dequeue());
            remainingLetters--;
        }
    }

    public static Stack getSet(Queue sets, int index) {
        Stack selectedSet = null;
        int numberOfSets = sets.size();

        for (int i = 0; i < numberOfSets; i++) {
            Stack currentSet = (Stack) sets.dequeue();

            if (i == index) {
                selectedSet = currentSet;
            }

            sets.enqueue(currentSet);
        }

        return selectedSet;
    }

    public static void collectStackLines(Stack stack,
                                         String setName, Queue lineQueue) {
        Stack temporaryStack = new Stack(stack.size() + 1);
        String prefix = setName + ": Top -> ";

        if (stack.isEmpty()) {
            lineQueue.enqueue(prefix + "Empty");
            return;
        }

        int itemsLeft = stack.size();
        boolean firstItem = true;

        while (!stack.isEmpty()) {
            Object item = stack.pop();
            temporaryStack.push(item);
            itemsLeft--;

            if (firstItem) {
                if (itemsLeft == 0) {
                    lineQueue.enqueue(prefix + item + " <- Bottom");
                } else {
                    lineQueue.enqueue(prefix + item);
                }
                firstItem = false;
            } else if (itemsLeft == 0) {
                lineQueue.enqueue("             " + item + " <- Bottom");
            } else {
                lineQueue.enqueue("             " + item);
            }
        }

        while (!temporaryStack.isEmpty()) {
            stack.push(temporaryStack.pop());
        }
    }

    public static String buildQueueLine(Queue queue) {
        if (queue.isEmpty()) {
            return "  Front -> (empty) <- Rear";
        }

        String line = "  Front -> ";
        int queueSize = queue.size();

        for (int i = 0; i < queueSize; i++) {
            Object item = queue.dequeue();

            if (i > 0) {
                line += " ";
            }

            line += item;
            queue.enqueue(item);
        }

        return line + " <- Rear";
    }

    public static void displayFullState(Queue sets, Queue reserveQueue,
                                        Queue supplementaryQueue, int score,
                                        int remainingShifts, int stepCount,
                                        int maxSteps) {
        System.out.println("----------------------------------------------------------------------");

        Queue leftLines = new Queue(60);
        int numberOfSets = sets.size();

        for (int i = 0; i < numberOfSets; i++) {
            Stack currentSet = (Stack) sets.dequeue();
            collectStackLines(currentSet, "Set" + (i + 1), leftLines);
            sets.enqueue(currentSet);
        }

        Queue rightLines = new Queue(10);
        rightLines.enqueue("Reserve Queue:");
        rightLines.enqueue(buildQueueLine(reserveQueue));
        rightLines.enqueue("Supplementary Queue:");
        rightLines.enqueue(buildQueueLine(supplementaryQueue));
        rightLines.enqueue("----------------------------------------------------------------------");
        rightLines.enqueue("Score: " + score + " | Remaining Shifts: "
                + remainingShifts + " | Step: " + stepCount + "/" + maxSteps);

        while (!leftLines.isEmpty() || !rightLines.isEmpty()) {
            String left = leftLines.isEmpty() ? "" : (String) leftLines.dequeue();
            String right = rightLines.isEmpty() ? "" : (String) rightLines.dequeue();
            System.out.printf("%-39s%s%n", left, right);
        }

        System.out.println();
    }

    public static boolean allSetsEmpty(Queue sets) {
        boolean allEmpty = true;
        int numberOfSets = sets.size();

        for (int i = 0; i < numberOfSets; i++) {
            Stack currentSet = (Stack) sets.dequeue();

            if (!currentSet.isEmpty()) {
                allEmpty = false;
            }

            sets.enqueue(currentSet);
        }

        return allEmpty;
    }

    public static int getMinSetIndex(Queue sets) {
        int minIndex = 0;
        int minSize = 11;
        int numberOfSets = sets.size();

        for (int i = 0; i < numberOfSets; i++) {
            Stack currentSet = (Stack) sets.dequeue();

            if (currentSet.size() < minSize) {
                minSize = currentSet.size();
                minIndex = i;
            }

            sets.enqueue(currentSet);
        }

        return minIndex;
    }

    public static void loadHighScores(Queue namesQueue, Queue scoresQueue) {
        try {
            File file = new File("HighScoreTable.txt");

            if (!file.exists()) {
                return;
            }

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine() && scoresQueue.size() < 10) {
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                int spaceIndex = line.lastIndexOf(' ');

                if (spaceIndex == -1) {
                    continue;
                }

                try {
                    int score = Integer.parseInt(line.substring(spaceIndex + 1));
                    String name = line.substring(0, spaceIndex);
                    namesQueue.enqueue(name);
                    scoresQueue.enqueue(score);
                } catch (NumberFormatException e) {
                    continue;
                }
            }

            fileScanner.close();
        } catch (Exception e) {
            System.out.println("Error reading High Score Table: " + e.getMessage());
        }
    }

    public static void updateAndDisplayHighScores(String playerName,
                                                   int finalScore,
                                                   Queue namesQueue,
                                                   Queue scoresQueue) {
        Queue updatedNames = new Queue(10);
        Queue updatedScores = new Queue(10);
        boolean playerAdded = false;

        while (!scoresQueue.isEmpty() && !updatedScores.isFull()) {
            String name = (String) namesQueue.dequeue();
            int score = (Integer) scoresQueue.dequeue();

            if (!playerAdded && finalScore >= score) {
                updatedNames.enqueue(playerName);
                updatedScores.enqueue(finalScore);
                playerAdded = true;
            }

            if (!updatedScores.isFull()) {
                updatedNames.enqueue(name);
                updatedScores.enqueue(score);
            }
        }

        if (!playerAdded && !updatedScores.isFull()) {
            updatedNames.enqueue(playerName);
            updatedScores.enqueue(finalScore);
        }

        try {
            PrintWriter writer = new PrintWriter(
                    new FileWriter("HighScoreTable.txt"));

            System.out.println("High Score Table");

            while (!updatedNames.isEmpty()) {
                String name = (String) updatedNames.dequeue();
                int score = (Integer) updatedScores.dequeue();
                System.out.println(name + " " + score);
                writer.println(name + " " + score);
            }

            writer.close();
        } catch (Exception e) {
            System.out.println("Error managing High Score Table: "
                    + e.getMessage());
        }
    }
}
