package com.decryptor.entity;

/**
 * Класс для удобного хранения и передачи параметров шифрования
 */
public class CryptJob {

    /**
     * Ключ шифрования
     */
    private String key;

    /**
     * Путь до входного файла
     */
    private String input;

    /**
     * Путь до выходного файла
     */
    private String output;

    /**
     * Признак шифрования/дешифрования
     */
    private boolean isEncrypt;

    public CryptJob(String key, String input, String output, boolean isEncrypt) {
        this.key = key;
        this.input = input;
        this.output = output;
        this.isEncrypt = isEncrypt;
    }

    public CryptJob() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }
}
