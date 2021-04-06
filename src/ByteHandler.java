import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class ByteHandler {
    static byte[] longToByteArray(long toBeConverted , int length){
        byte[] result = new byte[length];
        int mask = 0xFF;
        for (int i =  result.length-1; i>=0; i--) {
            result[i] = (byte) (toBeConverted & mask);
            toBeConverted >>= 8;
        }
        return result;
    }
    static String byteArrayToBinaryString (byte[] value){
        StringBuilder result = new StringBuilder();
        result.append("{");
        for (byte b : value){
            result.append(String.format(" %s,", byteToBinaryString(b)));
        }
        result.setLength((result.length()-1));
        result.append("}");

        return result.toString();
    }

    static String byteToBinaryString(byte toBeConverted){
        StringBuilder result = new StringBuilder();
        int mask = (1<<(Byte.SIZE-1));
        do {
            char c = (toBeConverted & mask) != 0 ? '1' : '0';
            result.append(c);
        }
        while((mask>>=1)>0);
        return result.toString();
    }
    static byte[] intToByteArray(int toBeConverted){
        byte[] result = new byte[Integer.BYTES];
        int mask = 0xFF;
        for (int i =  result.length-1; i>=0; i--) {
            result[i] = (byte) (toBeConverted & mask);
            toBeConverted >>= 8;
        }
        return result;
    }
    static long byteArrayToLong(byte[] byteArray){
        long value = 0;
        for (int i = 0; i < byteArray.length; i++)
        {
            value = (value << 8) + (byteArray[i] & 0xff);
        }
        return value;
    }
    public static byte[] hashPassword(String password) {
        byte[] salt = new byte[16];
        byte[] hash = null;
        for (int i = 0; i < 16; i++) {
            salt[i] = (byte) i;
        }
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();

        } catch (NoSuchAlgorithmException nsale) {
            nsale.printStackTrace();

        } catch (InvalidKeySpecException ikse) {
            ikse.printStackTrace();
        }
        return hash;
    }
}
