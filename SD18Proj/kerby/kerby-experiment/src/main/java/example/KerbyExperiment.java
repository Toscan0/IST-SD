package example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.SecureRandom;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyView;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;
import pt.ulisboa.tecnico.sdis.kerby.TicketView;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import javax.crypto.Mac;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class KerbyExperiment {
	/** Symmetric cryptography algorithm. */
	final static String SYM_ALGO = "AES";
	/** Plain text to protect with the message authentication code. */
	final static  String plainText = "This is the plain text!";
	/** Plain text bytes. */
	final static  byte[] plainBytes = plainText.getBytes();
	
	/** Symmetric algorithm key size. */
	final static int SYM_KEY_SIZE = 128;
	/** Length of initialization vector. */
	final int SYM_IV_LEN = 16;
	/** Number generator algorithm. */
	final String NUMBER_GEN_ALGO = "SHA1PRNG";

	/** Message authentication code algorithm. */
	final static String MAC_ALGO = "HmacSHA256";

	/**
	 * Symmetric cipher: combination of algorithm, block processing, and
	 * padding.
	 */
	final String SYM_CIPHER = "AES/CBC/PKCS5Padding";
	/** Digest algorithm. */
	final String DIGEST_ALGO = "SHA-256";
	
	private static Key getKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return SecurityHelper.generateKeyFromPassword(password);
	}
	
	private static SecretKey generateMACKey(int keySize) throws Exception {
		// generate an AES secret key
		KeyGenerator keyGen = KeyGenerator.getInstance(SYM_ALGO);
		keyGen.init(keySize);
		SecretKey key = keyGen.generateKey();

		return key;
	}
	
	/** Makes a message authentication code. */
	private static byte[] makeMAC(byte[] bytes, SecretKey key) throws Exception {

		Mac cipher = Mac.getInstance(MAC_ALGO);
		cipher.init(key);
		byte[] cipherDigest = cipher.doFinal(bytes);

		return cipherDigest;
	}

	/**
	 * Calculates new digest from text and compare it to the to deciphered
	 * digest.
	 */
	private static boolean verifyMAC(byte[] cipherDigest, byte[] bytes, SecretKey key) throws Exception {

		Mac cipher = Mac.getInstance(MAC_ALGO);
		cipher.init(key);
		byte[] cipheredBytes = cipher.doFinal(bytes);
		return Arrays.equals(cipherDigest, cipheredBytes);
	}
	
    public static void main(String[] args) throws Exception {
    	
    	SecureRandom randomGenerator = new SecureRandom();
    	String VALID_CLIENT_NAME = "alice@A30.binas.org";
    	String VALID_CLIENT_PASSWORD = "88AXnSX6d";
    	String VALID_SERVER_NAME = "binas@A30.binas.org";
    	String VALID_SERVER_PASSWORD = "Y5p7yBzp";
    	int VALID_DURATION = 30;
    	
        System.out.println("Hi!");

        System.out.println();

        // receive arguments
        System.out.printf("Received %d arguments%n", args.length);

        System.out.println();

        // load configuration properties
        try {
            InputStream inputStream = KerbyExperiment.class.getClassLoader().getResourceAsStream("config.properties");
            // variant for non-static methods:
            // InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");

            Properties properties = new Properties();
            properties.load(inputStream);
            //printig properties
            //properties.list(System.out);
            
            System.out.printf("Loaded %d properties%n", properties.size());
            
        } catch (IOException e) {
            System.out.printf("Failed to load configuration: %s%n", e);
        }

        System.out.println();

		// client-side code experiments
        System.out.println("Experiment with Kerberos client-side processing");
		System.out.println("...TODO...");
		
		//Cria um kerbyClient
		KerbyClient kclient = new KerbyClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby");
		
		//faz o getKey do client e do servidor
		final Key clientKey = getKey(VALID_CLIENT_PASSWORD);
		final Key serverKey = getKey(VALID_SERVER_PASSWORD);
		//gera um numero random
		long nounce = randomGenerator.nextLong();
		
		//faz o requestTocket do client
		SessionKeyAndTicketView result = kclient.requestTicket(VALID_CLIENT_NAME, VALID_SERVER_NAME, nounce, VALID_DURATION);
		
		//getSessionKey 
		CipheredView cipheredSessionKey = result.getSessionKey();
		//getTicket
		CipheredView cipheredTicket = result.getTicket();
		
		SessionKey sessionKey = new SessionKey(cipheredSessionKey, clientKey);
		
		SecretKey key = generateMACKey(SYM_KEY_SIZE);
		printHexBinary(plainBytes);
		byte[] cipherDigest = makeMAC(plainBytes, key);
		
		//Auth(String x, Date timeRequest)
		Auth authClient = new Auth("client", new Date());
		CipheredView cipheredAuth = authClient.cipher(sessionKey.getKeyXY());
		
        System.out.println();

		// server-side code experiments
        System.out.println("Experiment with Kerberos server-side processing");
		System.out.println("...TODO...");
		
		Ticket ticket = new Ticket(cipheredTicket, serverKey);
		long timeDiff = ticket.getTime2().getTime() - ticket.getTime1().getTime();
		
		Auth authServer = new Auth(cipheredAuth, sessionKey.getKeyXY());
		authServer.validate();
		
		RequestTime requestTime = new RequestTime(authServer.getTimeRequest());
		
		boolean result2 = verifyMAC(cipherDigest, plainBytes, key);
		System.out.println("MAC is " + (result2 ? "right" : "wrong"));
		
        System.out.println();
		
		System.out.println("Bye!");
    }
}
