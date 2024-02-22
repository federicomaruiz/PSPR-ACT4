package org.example;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Server {
    private static final int PORT = 8888;

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor FTP iniciado. Esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

                // Procesar archivo recibido del cliente
                processFile(clientSocket);
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private void processFile(Socket clientSocket) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try (InputStream is = clientSocket.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] data = baos.toByteArray();

            // Guardar archivo recibido
            try (FileOutputStream fos = new FileOutputStream("server_received_file.txt")) {
                fos.write(data);
            }

            // Leer contenido del archivo y mostrarlo
            try {
                String encryptedMessage = new String(data);
                System.out.println("Mensaje recibido del cliente (encriptado): " + encryptedMessage);

                // Desencriptar mensaje
                byte[] key = "0123456789abcdef".getBytes(); // 16 bytes for AES-128
                SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

                byte[] decryptedBytes = cipher.doFinal(data);
                String decryptedMessage = new String(decryptedBytes);
                System.out.println("Mensaje recibido del cliente (desencriptado): " + decryptedMessage);
            } catch (BadPaddingException e) {
                System.out.println("Error: La clave de desencriptación es incorrecta.");
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}