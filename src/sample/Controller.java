package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Controller {
    public TextArea log;
    private File file;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private OutputStream out;

    public void initialize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(8000);
                    Socket socket = serverSocket.accept();

                    file = new File("C:\\Users\\simon\\IdeaProjects\\3. semester\\untitled\\src\\sample\\test.txt");
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);

                    out = socket.getOutputStream();

                    System.out.println("Connection established");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendFile(ActionEvent actionEvent) throws IOException {
        writeTextToDocument(log.getText());

        byte[] contents;
        long fileLength = file.length();
        long current = 0;

        System.out.println("Starting file transfer");
        while(current != fileLength){
            int size = 10000;
            if(file.length() - current >= size)
                current += size;
            else{
                size = (int)(file.length() - current);
                current = fileLength;
            }
            contents = new byte[size];
            bis.read(contents, 0, size);
            out.write(contents);
        }
        out.flush();
        log.setText("File sent");
    }

    public void encrypt(ActionEvent actionEvent) {
        final String encryptionKey = "ABCDEFGHIJKLMNOP";
        final String cipherTransformation = "AES/CBC/PKCS5PADDING";
        final String aesEncryptionAlgorithem = "AES";

        String text = log.getText();
        String encrypted = "";
        try{
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            Base64.Encoder encoder = Base64.getEncoder();
            encrypted = encoder.encodeToString(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.setText(encrypted);
    }

    public void writeTextToDocument(String text){
        String absolutePath = "C:\\Users\\simon\\IdeaProjects\\3. semester\\untitled\\src\\sample\\test.txt";

        try(FileWriter fileWriter = new FileWriter(absolutePath, false)){
            fileWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
