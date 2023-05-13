package com.middleware.wyd.Services;

import com.middleware.wyd.DTO.ResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.*;
import java.util.Scanner;
import java.nio.file.*;
import java.util.stream.IntStream;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

@Service
public class IntegrationAPIService {

    @Value("${ingame}")
    private String inGamePath;

    @Value("${dbsrv}")
    private String dbSrvPath;

    @Value("${common}")
    private String commonPath;

    @Value("${pincode}")
    private String pinCodePath;

    @Value("${droplist}")
    private String dropListPath;

    @Value("${ranking}")
    private String rankingPath;


    public String convertToUpperCaseOrSetEtcForSpecialChars(String initial) {
        if (initial.matches(".*[a-zA-Z].*")) {
            initial = initial.toUpperCase();
        } else {
            initial = "etc";
        }
        return initial;
    }

    public ResponseDTO addAccount( String user,  String pass) throws IOException {
        Path path = Paths.get(inGamePath + dbSrvPath + "baseusuario");
        byte[] bytesAccount = Files.readAllBytes(path);
        String destDir = inGamePath + dbSrvPath + "run/account/" + convertToUpperCaseOrSetEtcForSpecialChars(user.substring(0, 1)) + "/" + user;

        if(user.length() > 12 || pass.length() > 12 ){
            throw new IOException("Usuario ou senha muito grandes, tamanho máximo 12 caracteres.");
        }

        if(Files.exists(Paths.get(destDir))){
            throw new IOException("Essa Conta ja existe.");
        }

        // zera o usuario antigo byte por byte
        for(int i = 0 ; i<= "baseusuario".length() - 1; i ++){
            bytesAccount[i] = 0;
        }

        // inicia no 0 e substitui byte por byte até 12 posicoes
        for(int i = 0; i<= user.length() - 1; i ++){
            bytesAccount[i] = user.getBytes()[i];
        }


        // zera a senha antiga byte por byte
        for(int i = 0 ; i<= "basesenha".length(); i ++){
            bytesAccount[i + 16] = 0;
        }

        // inicia no 16 e substitui byte por byte até 12 posicoes
        for(int i = 0; i<= pass.length() - 1; i ++){
            bytesAccount[16 + i] = pass.getBytes()[i];
        }


        writeAccount(destDir, bytesAccount);

        return ResponseDTO.builder().title("Sucesso").description("Conta criada.").build();
    }

    public boolean writeAccount(String destDir, byte[] bytesAccount) {
        try{
            File accountFile = new File(destDir);
            FileOutputStream fos = new FileOutputStream(accountFile);
            fos.write(bytesAccount);
            fos.close();
            return true;
        }
        catch (IOException ex){
            return false;
        }
    }

    public ResponseDTO accountExists( String user) {
        String filePath = inGamePath + dbSrvPath + "run/account/" + convertToUpperCaseOrSetEtcForSpecialChars(user.substring(0, 1)) + "/" + user;
        File accountFile = new File(filePath);
        return ResponseDTO.builder().title("Sucesso").description("Conta encontrada.").build();
    }

    public ResponseDTO alterPass( String user,  String pass,  String newPass) throws Exception {

            Path account = Paths.get(inGamePath + dbSrvPath + "run/account/" + convertToUpperCaseOrSetEtcForSpecialChars(user.substring(0, 1)) + "/" + user);
            byte[] bytesAccount = Files.readAllBytes(account);
            String contaString = new String(bytesAccount, "Cp1252");

            if(!contaString.contains(pass) && newPass.length() <= 12){
                throw new Exception("Senha incorreta");
            }

            // Zera a senha antiga byte por byte
            IntStream.range(0, pass.length() + 1)
                    .forEach(i -> bytesAccount[i + 16] = 0);

            // Substmanixitui byte por byte até 12 posições
            IntStream.range(0, Math.min(newPass.length(), 12))
                    .forEach(i -> bytesAccount[16 + i] = newPass.getBytes()[i]);

          Files.write(account, bytesAccount);
        return ResponseDTO.builder().title("Sucesso").description("Senha alterada com sucesso").build();

    }

    public ResponseDTO getRanking() throws FileNotFoundException {
            File file = new File(inGamePath + rankingPath);
            Scanner scanner = new Scanner(file);
            String ranking = scanner.nextLine();
            scanner.close();
            return ResponseDTO.builder().title("Ranking").description(ranking).build();
    }

    public ResponseDTO getDroplist() throws FileNotFoundException {
            File file = new File(inGamePath + dropListPath);
            Scanner scanner = new Scanner(file);
            String droplist = scanner.nextLine();
            scanner.close();
            return ResponseDTO.builder().title("Droplist").description(droplist).build();
    }

    public ResponseDTO dologin(String user, String pass) throws Exception {


            Path account = Paths.get(inGamePath + dbSrvPath + "run/account/" + convertToUpperCaseOrSetEtcForSpecialChars(user.substring(0, 1)) + "/" + user);

            String dirTest = inGamePath + dbSrvPath + "run/account/" + convertToUpperCaseOrSetEtcForSpecialChars(user.substring(0, 1)) + "/" + user;

            byte[] bytesAccount = Files.readAllBytes(account);
            String contaString = new String(bytesAccount, "Cp1252");

            if(
                            !contaString.substring(0, 12).contains(user) &&
                            !contaString.substring(0, 12).contains(user.toUpperCase())
                            || !contaString.substring(16, 28).contains(pass)
            ){
                System.out.println("[GAMECMS] Login senha incorreta - Usuario: " + user + " - Senha: " + pass);
                throw new IllegalArgumentException("Senha incorreta");
            }

        System.out.println("[GAMECMS] Login com sucesso - Usuario: " + user + " - Senha: " + pass);
        return ResponseDTO.builder().title("Sucesso").description(user).build();
    }



    /*TODO: Revisar para usos futuros*/
    public String getPass( String user) throws IOException {
        String filePath = inGamePath + dbSrvPath + "run/account/" + convertToUpperCaseOrSetEtcForSpecialChars(user.substring(0, 1)) + "/" + user;
        File accountFile = new File(filePath);
        if (accountFile.exists()) {
            FileInputStream fis = new FileInputStream(accountFile);
            byte[] accBytes = fis.readAllBytes();
            fis.close();

            String pass = new String(accBytes, 16, 12);
            StringBuilder pass2 = new StringBuilder();
            for (int i = 0; i < pass.length(); i++) {
                if (pass.charAt(i) == 0x00) {
                    break;
                }

                pass2.append(pass.charAt(i));
            }

            return pass2.toString() + ";" + new String(accBytes, 202, 6);
        } else {
            return "0";
        }
    }

    public String getAccount( String user) {
        try {
            File file = new File(inGamePath + dbSrvPath + "run/account/" + convertToUpperCaseOrSetEtcForSpecialChars(user.substring(0, 1)) + "/" + user);
            Scanner scanner = new Scanner(file);
            String account = scanner.nextLine();
            scanner.close();
            return account;
        } catch (Exception e) {
            return "0";
        }
    }

    public int checker( String check,  String checktype) {
        try {
            File file = new File(check);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(hexStringToByteArray(checktype));
            fos.close();
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }


    public String verifyPincode( String user,  String pin) throws IOException {
        String filePath = inGamePath + pinCodePath + user + ".txt";
        File pincodeFile = new File(filePath);
        FileOutputStream fos = new FileOutputStream(pincodeFile);
        fos.write(pin.getBytes());
        fos.close();

        return String.valueOf(pincodeFile.exists());
    }

    public int addPincode( String user,  String value) {
        try {
            File file = new File("C:/Server/Common/ImportDonate/" + user + ".txt");
            FileWriter writer = new FileWriter(file);
            writer.write(value);
            writer.close();
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }


}
