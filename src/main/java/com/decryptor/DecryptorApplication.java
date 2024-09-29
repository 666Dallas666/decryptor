package com.decryptor;

import com.decryptor.entity.CryptJob;
import com.decryptor.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DecryptorApplication implements CommandLineRunner {
    @Autowired
    private EncryptionService encryptionService;

    public static void main(String[] args) {
        SpringApplication.run(DecryptorApplication.class, args);
    }

    @Override
    public void run(String... args) {

        List<CryptJob> jobs = new ArrayList<>();

        //Интерфейс взаимодействия с приложением, сделан при помощи сканера и циклов
        System.out.println("Please enter necessary data for encryption:");
        System.out.println("Escape word is \"stop\"\n");
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        int counter = 1;
        mainLoop:
        while (true) {
            CryptJob job = new CryptJob();
            String line;
            while (true) {
                System.out.println("Enter key for crypt job " + counter + ":");
                line = scanner.nextLine();
                if (line.equalsIgnoreCase("stop")) {
                    break mainLoop;
                } else if (!line.isBlank()) {
                    job.setKey(line);
                    break;
                } else {
                    System.out.println("Key must be not empty!");
                }
            }
            while (true) {
                System.out.println("Please enter absolute path to input file for crypt job " + counter + ":");
                line = scanner.nextLine();
                if (line.equalsIgnoreCase("stop")) {
                    break mainLoop;
                } else {
                    if (new File(line).isFile()) {
                        job.setInput(line);
                        break;
                    } else {
                        System.out.println("The path is invalid or file does not exist! Please try again\n");
                    }
                }
            }
            while (true) {
                System.out.println("Please enter absolute path to output file for crypt job " + counter + ":");
                line = scanner.nextLine();
                if (line.equalsIgnoreCase("stop")) {
                    break mainLoop;
                } else {
                    if (new File(line).isFile()) {
                        job.setOutput(line);
                        break;
                    } else {
                        System.out.println("The path is invalid or file does not exist! Please try again\n");
                    }
                }
            }
            while (true) {
                System.out.println("Do you want to encrypt or decrypt file?");
                line = scanner.nextLine();
                if (line.equalsIgnoreCase("stop")) {
                    break mainLoop;
                } else {
                    if (line.equalsIgnoreCase("encrypt")) {
                        job.setEncrypt(true);
                        break;
                    } else if (line.equalsIgnoreCase("decrypt")) {
                        job.setEncrypt(false);
                        break;
                    } else {
                        System.out.println("Incorrect input! Please try again\n");
                    }
                }
            }
            jobs.add(job);
            counter++;
        }

        //Создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //Для каждой джобы создаем таску и отправляем ее на обработку в пул потоков
        for (CryptJob job : jobs) {
            Runnable task = () -> encryptionService
                    .crypt(job.getKey(), job.getInput(), job.getOutput(), job.isEncrypt());
            executorService.submit(task);
        }
        //Убираем возможность добавлять новые таски в пул потоков
        executorService.shutdown();
        //Ждем пока все текущие таски завершатся
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Awaiting tasks were terminated!");
        }
    }
}
