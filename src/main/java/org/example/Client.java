package org.example;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Dirección IP del servidor
    private static final int SERVER_PORT = 8888; // Puerto del servidor

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Conexión establecida con el servidor.");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingrese el mensaje que desea enviar al servidor:");
            String message = scanner.nextLine();
            // Guardar mensaje en un archivo
            String filePath = "messages/message.txt";
            writeTextToFile(message, filePath);
            // Enviar archivo al servidor
            encryptAndSend(socket, filePath);
            System.out.println("Mensaje enviado al servidor");
        } catch (IOException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException |
                 NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private static void encryptAndSend(Socket socket, String filePath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        FileInputStream fis = new FileInputStream(filePath);
        try (OutputStream os = socket.getOutputStream()) {
            byte[] key = "0123456789abcdef".getBytes(); // 16 bytes for AES-128
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] encryptedBytes = cipher.doFinal(buffer, 0, bytesRead);
                os.write(encryptedBytes);
            }
        }
    }

    private static void writeTextToFile(String text, String filePath) {
        File directory = new File("messages");
        if (!directory.exists()) {
            directory.mkdir();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

