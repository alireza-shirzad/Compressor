import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Decompressor {

    private int v;
    private int b;
    private int maxWindowLength;
    private StringBuffer searchBuffer;


    public int decompress(String inputFile, String password){

        // ----------- Creating Logger File ------------ //
        Logger logger = new Logger('d' , inputFile);
        // --------------------------------------------- //


        // ----------- Initializing Reading Operations ------------ //
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ----------------------------------------------------//



        // ----------- Header Parsing ----------------- //
        try {
            byte header;
            header = (byte) inputStream.read();
            if ((char) header=='0'){
                String hashedPassToStoreInFile = new String(ByteHandler.hashPassword(password));
                byte[] passHash = new byte[hashedPassToStoreInFile.length()];
                for (int i = 0; i < hashedPassToStoreInFile.length(); i++) {
                    passHash[i] = (byte) inputStream.read();
                }
                String readHash = new String(passHash);
                if (!readHash.equals(hashedPassToStoreInFile)) return 1;
                header = (byte) inputStream.read();
            }
            int[] vb = parseHeader(header);
            this.v = vb[0];
            this.b = vb[1];
            logger.log("b = " + b,true);
            logger.log("v = " + v, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        maxWindowLength = (int) Math.pow(2,v);
        searchBuffer = new StringBuffer(maxWindowLength);
        // ---------------------------------------------//


        // ----------- Decompression Algorithm ----------------- //
        char nextchar;
            try {
                OutputStream outputStream = new FileOutputStream(inputFile+".uncompressed");
                int codeLength = (int) Math.ceil(((double)this.v+(double)this.b)/8);
                boolean coded = false;
                int codeCounter = 0;
                int nextCharacterCode;
                String toWrite;
                byte[] code = new byte[codeLength];
                int a;
                int t;
                int[] ta;
                while ((nextCharacterCode = inputStream.read()) != -1) {
                    nextchar = (char) nextCharacterCode;
                    if (coded==true){
                        codeCounter++;
                        code[codeCounter-1] = (byte) nextchar;
                        if (codeCounter==codeLength){
                            coded=false;
                            ta = decode(code,b,v);
                            a= ta[0];
                            t= ta[1];
                            toWrite = this.searchBuffer.substring(this.searchBuffer.length()-t,this.searchBuffer.length()-t+a);
                            outputStream.write(toWrite.getBytes(Charset.forName("ASCII")));
                            searchBuffer.append(toWrite);
                            checkSearchWindow();
                        }
                    }
                    else if (nextchar == '0') {
                        coded = true;
                        codeCounter = 0;
                    }
                    else{
                        outputStream.write(nextchar);
                        this.searchBuffer.append(nextchar);
                        checkSearchWindow();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        // ------------------------------------------------------//
    return 0;
    }


    private int[] decode(byte[] code,int b, int v){
        int[] output = {1,0};
        byte tMask, aMask;
        aMask = (byte) (2^b);
        int a = aMask & code[(code.length)-1];
        int t = (int) ((ByteHandler.byteArrayToLong(code)-a) >> b);
        output[0] = a;
        output[1] = t;
        return output;
    }


    private int[] parseHeader(byte headerByte){
        byte vMask, bMask;
        bMask = 7;
        vMask = (byte) (31<<3);
        int b = bMask & headerByte;
        int v = (vMask & headerByte) >>3;
        int[] output = new int[2];
        output[0] = v;
        output[1] = b;
        return output;
    }

    private boolean checkSearchWindow(){
        if (searchBuffer.length() > maxWindowLength) {
            searchBuffer = searchBuffer.delete(0,  searchBuffer.length() - maxWindowLength);
            return true;
        }
        return false;
    }

}
