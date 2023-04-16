package pers.mr.ft.inventory.ui.html;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
  private SecretKey key;
  private static final int KEY_SIZE = 128;
  private static final int DATA_LENGTH = 128;
  private static byte IV_LENGTH = 12;
  private static final int SALT_LENGTH_BYTE = 16;
  private Cipher encryptionCipher;
  private String algo = "AES/GCM/NoPadding";
  
  public void init() throws Exception {
    //KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    //keyGenerator.init(KEY_SIZE);
    //key = keyGenerator.generateKey();
    // TODO: comment stocker la clé secrète ?
    char[] password = new char[] { 0x1b, 0x0a, 0x23, 0x7d, 0x0f, 0x63, 0x22, 0x1a, 0x08, 0x11, 0x45, 0x35 }; // TODO: pwd paramétré
    // TODO: mettre le sel dans un fichier de config. Si le sel est changé les cookies persistant ne sont plus bons.
    byte[] salt = new byte[] {58, -36, -68, -59, -101, 126, 123, -13, -87, -34, -74, 9, 82, -96, 119, 98 };
    key = getAESKeyFromPassword(password, salt);
  }
  
  public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    int iterationCount = 65536;
    KeySpec spec = new PBEKeySpec(password, salt, iterationCount, KEY_SIZE);
    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    return secret;
  }
  
  public static byte[] getRandomNonce(int length) {
    byte[] nonce = new byte[length];
    new SecureRandom().nextBytes(nonce);
    return nonce;
  }
  
  public String encrypt(String data) throws Exception {
    byte [] iv = getRandomNonce(IV_LENGTH);
    GCMParameterSpec spec = new GCMParameterSpec(DATA_LENGTH, iv);

    encryptionCipher = Cipher.getInstance(algo);
    encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
    
    byte[] dataInBytes = data.getBytes();
    byte[] encryptedBytes = encryptionCipher.doFinal(dataInBytes);
    byte[] cipherTextWithIv = ByteBuffer.allocate(IV_LENGTH+encryptedBytes.length+1)
        .put(IV_LENGTH)
        .put(iv)
        .put(encryptedBytes)
        .array();

    return encode(cipherTextWithIv);
  }
  
  public String decrypt(String encryptedData) throws Exception {
    byte[] encryptedBytes = decode(encryptedData);
    ByteBuffer bb = ByteBuffer.wrap(encryptedBytes);
    
    // Longueur iv
    byte ivLength = bb.get();
    byte[] iv = new byte[ivLength];
    bb.get(iv);
    byte[] cipherText = new byte[bb.remaining()];
    bb.get(cipherText);
    
    Cipher decryptionCipher = Cipher.getInstance(algo);
    GCMParameterSpec spec = new GCMParameterSpec(DATA_LENGTH, iv);
    decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
    byte[] decryptedBytes = decryptionCipher.doFinal(cipherText);
    return new String(decryptedBytes);
  }

  private String encode(byte[] data) {
    return Base64.getEncoder().encodeToString(data);
  }
  
  private byte[] decode(String data) {
    return Base64.getDecoder().decode(data);
  }
}
