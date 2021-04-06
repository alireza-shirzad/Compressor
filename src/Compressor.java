import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Compressor {

    private int b;
    private int v;
    private int maxWindowLength;
    private int maxBufferLength;
    private StringBuffer searchBuffer;
    public Compressor(int b , int v){

        // ----------- Compression Parameters Settings ------------ //
        this.b=b;
        this.v=v;
        this.maxBufferLength = (int) Math.pow(2,b);
        this.maxWindowLength = (int) Math.pow(2,v);
        searchBuffer = new StringBuffer(maxWindowLength);
        // -------------------------------------------------------- //

        System.out.println("File Compressor created with maximum buffer size of " + this.maxBufferLength + " and maximum window length of " + this.maxWindowLength);
    }

    public void compress(String inputFile, String password) throws IOException {

        // ----------- Creating Logger File ------------ //
        Logger logger = new Logger('c' , inputFile);
        // --------------------------------------------- //

        // ----------- Initializing IO operations ------------ //
        Reader input = new BufferedReader(new FileReader(inputFile));
        FileOutputStream output = new FileOutputStream(inputFile + ".shirzad");
        logger.log("Output file " + inputFile + ".shirzad" + " creted", true);
        // ----------------------------------------------------//

        // ----------- Password Hash Construction ------------ //
        if (password.length()!=0){
            output.write('0');
            String hashedPassToStoreInFile = new String(ByteHandler.hashPassword(password));
            for (int i = 0; i < hashedPassToStoreInFile.length(); i++) {
                output.write(hashedPassToStoreInFile.charAt(i));
            }
            System.out.println(password);
            System.out.println(hashedPassToStoreInFile);
        }
        // ---------------------------------------------//



        // ----------- Header Construction ------------ //
        Integer header = new Integer((this.v<<3 ) + this.b);
        output.write(header.byteValue());
        logger.log("Header created", true);
        // ---------------------------------------------//


        // --------------- Algorithm Variable Initialization --------------- //
        int nextCharacterCode,matchIndex, a , t;
        char nextCharacter;
        String lookAheadString="", toWriteString;
        long codeNumber;
        byte[] code;
        boolean trimmed, stringNotFinished = true, inputnotfinished=true;
        boolean read = true;
        int threshold = (int) Math.ceil(((double)this.v+(double)this.b)/8) + 1;
        // -----------------------------------------------------------//

        // ----------- Algorithm Procedure ------------ //
        logger.log("Compression Started with b=" + b + " and v=" + v + " thus threshold=" + threshold, true);

        while (inputnotfinished | stringNotFinished) {
            logger.log("---------------------------------------------------------------------------------", true);
            if (read){
                if((nextCharacterCode = input.read()) == -1){
                    inputnotfinished = false;
                }
                nextCharacter = (char) nextCharacterCode;
                logger.log("Character \"" + nextCharacter + "\" read", true);
                lookAheadString = lookAheadString + nextCharacter;
            } else{
                logger.log("No characters read", true);
                read = true;
            }
            logger.log("Look Ahead String is \"" + lookAheadString + "\"", true);
            matchIndex  = searchBuffer.indexOf(lookAheadString);
            logger.log("Search Buffer:  \"" + searchBuffer + "\"", true);
            logger.log("Match Index is " + matchIndex, true);

            if ((matchIndex==-1) | ((lookAheadString.length()-1)>b)){
                logger.log("Write Process Started ", true);

                if ((lookAheadString.length()-1)<=threshold){
                    logger.log("Less than threshold ", true);

                    if (lookAheadString.length()>1){
                        toWriteString = lookAheadString.substring(0,lookAheadString.length()-1);
                        for (int i = 0; i < toWriteString.length(); i++) {
                            output.write(toWriteString.charAt(i));
                        }
                        logger.log("raw string \"" + toWriteString + "\" written", true);
                        lookAheadString = lookAheadString.substring(lookAheadString.length()-1);
                        read = false;
                        searchBuffer.append(toWriteString);
                        logger.log("Look Ahead String became \"" + lookAheadString + "\"", true);
                        logger.log("Search Buffer became \"" + searchBuffer + "\"", true);
                    }
                    else{
                        toWriteString = lookAheadString;
                        for (int i = 0; i < toWriteString.length(); i++) {
                            output.write(toWriteString.charAt(i));
                        }
                        searchBuffer.append(toWriteString);
                        logger.log("raw string \"" + toWriteString + "\" written", true);
                        lookAheadString = "";
                        logger.log("Look Ahead String became \"" + lookAheadString + "\"", true);
                        logger.log("Search Buffer became \"" + searchBuffer + "\"", true);
                    }
                } else{
                    logger.log("Above the threshold and Coding Starts " + matchIndex, true);
                    toWriteString = lookAheadString.substring(0,lookAheadString.length()-1);
                    read = false;
                    a = toWriteString.length();
                    t = searchBuffer.length()-searchBuffer.lastIndexOf(toWriteString);
                    logger.log("a= " + a + " and t=" + t, true);
                    codeNumber = (a) + (t<<b);
                    code = ByteHandler.longToByteArray(codeNumber,threshold-1);

                    output.write('0');
                    logger.log("\"0\" written", true);
                    logger.log(code.length + "bytes written:", true);
                    logger.log(ByteHandler.byteArrayToBinaryString(code),true);
                    logger.log(String.valueOf(code.length),true);
                    for (byte codeByte : code) {
                        output.write(codeByte);
                        logger.log(ByteHandler.byteToBinaryString(codeByte),true);
                    }
                    lookAheadString = lookAheadString.substring(lookAheadString.length()-1);
                    searchBuffer.append(toWriteString);
                    logger.log("Look Ahead String became \"" + lookAheadString + "\"", true);
                    logger.log("Search Buffer became \"" + searchBuffer + "\"", true);
                }
            }
            else{
                logger.log("Look Ahead String became \"" + lookAheadString + "\"", true);
            }

            trimmed = checkSearchWindow();
            if (trimmed) {
                logger.log("Search Buffer became \"" + searchBuffer + "\" after checking the window size", true);
            }
            if (lookAheadString.length()==0) stringNotFinished=false;
            logger.log("---------------------------------------------------------------------------------", true);
        }
        // -----------------------------------------------------------//



        input.close();
        output.flush();
        output.close();

        }


        private boolean checkSearchWindow(){
            if (searchBuffer.length() > maxWindowLength) {
                searchBuffer = searchBuffer.delete(0,  searchBuffer.length() - maxWindowLength);
                return true;
            }
            return false;
        }

}
