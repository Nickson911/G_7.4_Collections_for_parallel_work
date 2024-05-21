package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static int text = 10_000;
    public static int length = 100_000;
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    public static Thread creatingText;

    public static void main(String[] args) throws InterruptedException {
        creatingText = new Thread(() -> {
            for (int i = 0; i < text; i++) {
                String text = generateText("abc", length);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        creatingText.start();

        Thread a = maxCharCount(queueA, 'a');
        Thread b = maxCharCount(queueB, 'b');
        Thread c = maxCharCount(queueC, 'c');

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();
    }

    public static Thread maxCharCount(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int count = 0;
            int max = 0;
            try {
                while (creatingText.isAlive()) {
                    String text = queue.take();
                    for (char c : text.toCharArray()) {
                        if (c == letter) count++;
                    }
                    if (count > max) max = count;
                    count = 0;
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " был прерван");
            }
            System.out.printf("Максимальное количество букв " + letter + " в тексте - %d \n", max);
        });
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}