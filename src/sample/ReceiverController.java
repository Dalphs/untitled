package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.SocketHandler;

public class ReceiverController {
    public TextArea log;
    private final String PATH_TO_FILE = "C:\\Users\\simon\\IdeaProjects\\3. semester\\untitled\\fileReceived\\test.txt";

    public void initialize(){
        log.setEditable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("localhost", 8000);
                    byte[] contents = new byte[10000];

                    FileOutputStream fos = new FileOutputStream(PATH_TO_FILE);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

                    int bytesRead = 0;
                    System.out.println("Waiting for File");

                    while((bytesRead = in.read(contents)) > 0){
                        bos.write(contents);
                        log.setText("File received");
                    }

                    bos.flush();
                    System.out.println("File saved");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void viewContent(ActionEvent actionEvent) {
        String document = "";
        try (FileReader fileReader = new FileReader(PATH_TO_FILE)) {
            int ch = fileReader.read();
            while (ch != -1) {
                document += (char) ch;
                ch = fileReader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.setText(document);
    }

    public void decrypt(ActionEvent actionEvent) {
        final String encryptionKey = "ABCDEFGHIJKLMNOP";
        final String cipherTransformation = "AES/CBC/PKCS5PADDING";
        final String aesEncryptionAlgorithem = "AES";

        String encryptedText = log.getText();
        String decrypted = "";
        try{
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(encryptedText.getBytes(StandardCharsets.UTF_8));
            decrypted = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
        }
        log.setText(decrypted);
        writeTextToDocument(decrypted);
    }

    public void writeTextToDocument(String text){
        String absolutePath = PATH_TO_FILE;

        try(FileWriter fileWriter = new FileWriter(absolutePath, false)){
            fileWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
