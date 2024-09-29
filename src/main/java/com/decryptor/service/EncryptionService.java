package com.decryptor.service;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.HexFormat;

@Service
public class EncryptionService {

    public void crypt(String password, String input, String output, boolean isEncrypt) {

        try {
            //Генерация 256-битного AES ключа на основе переданного
            byte[] salt = HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d");
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            //Запуск процесса шифрования/дешифрования
            if (isEncrypt) {
                encrypt(input, output, secret);
            } else {
                decrypt(input, output, secret);
            }
        } catch (Exception ex) {
            handle(ex);
        }
    }

    void encrypt(String input, String output, SecretKey secretKey) {
        try {
            //Задаем шифр с нужными параметрами
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            try (
                    //Создаем буфферный шифрующий поток вывода
                    FileOutputStream fileOut = new FileOutputStream(output);
                    CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
                    BufferedOutputStream bos = new BufferedOutputStream(cipherOut)
            ) {
                //Записываем IV в префикс файла
                fileOut.write(iv);
                try (
                        //Создаем буфферный поток чтения
                        FileInputStream fin = new FileInputStream(input);
                        BufferedInputStream bis = new BufferedInputStream(fin)
                ) {
                    //Задаем размер буффера, читаем потоком в буффер,
                    //и записываем в результирующий файл шифрующим потоком, пока читаемый файл не закончится
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, count);
                    }
                }
                bos.flush();
            }
        } catch (Exception ex) {
            handle(ex);
        }
    }

    void decrypt(String input, String output, SecretKey secretKey) {
        try {
            //Создаем шифр
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            //Читаем IV из файла (для AES шифрования размер IV 16 байтов)
            try (FileInputStream fileIn = new FileInputStream(input)) {
                byte[] fileIv = new byte[16];
                fileIn.read(fileIv);
                //Инициализируем шифр
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));
                try (
                        //Создаем буфферный шифрующий поток чтения
                        CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
                        BufferedInputStream bis = new BufferedInputStream(cipherIn);
                ) {
                    try (
                            //Создаем буфферный поток записи
                            FileOutputStream fos = new FileOutputStream(output);
                            BufferedOutputStream bos = new BufferedOutputStream(fos)
                    ) {
                        //Процесс чтения/записи
                        byte[] buffer = new byte[8192];
                        int count;
                        while ((count = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, count);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            handle(ex);
        }
    }

    //Образец обработчика ошибок
    private void handle(Exception ex) {
        if (ex instanceof NoSuchAlgorithmException) {
            System.out.println("Wrong encryption algorithm!");
        } else if (ex instanceof InvalidKeyException) {
            System.out.println("Given secret key is inappropriate for selected cipher!");
        } else if (ex instanceof NoSuchPaddingException) {
            System.out.println("Wrong padding for chosen algorithm or padding does not exist!");
        } else if (ex instanceof FileNotFoundException) {
            System.out.println("Trying to access file that does not exist!");
        } else if (ex instanceof IOException) {
            System.out.println("There was an exception while reading the file!");
        } else {
            System.out.println("An unknown exception has occurred!");
        }
    }
}
