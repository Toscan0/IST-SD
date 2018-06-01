package handlers.ws.handler;

import static javax.xml.bind.DatatypeConverter.printHexBinary;


import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class MACHandler implements SOAPHandler<SOAPMessageContext>{
	private SOAPEnvelope se;
	private SOAPHeader sh;
	private SOAPBody sb;
	private Name nameBody;
	private Name nome;
	Key chaveSecreta;
	
	/** Symmetric cryptography algorithm. */
	final static String SYM_ALGO = "AES";
	/** Plain text to protect with the message authentication code. */
	private String plainText;
	/** Plain text bytes. */
	private byte[] plainBytes;
	
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
	
	@Override
	public void close(MessageContext arg0) {
		// nothing to clean up
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		System.out.println("Ignoring fault message...MAC ");
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		String timeRequest;
		String stringBody;
		String ticket;
		Boolean result;
		
		System.out.print("Welcome to the handleMessage in MacHandler ");
		
		Boolean outboundElement = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		if(outboundElement.booleanValue()){
			try{
				
				System.out.println("in protecting side");			
				SOAPMessage msg = context.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				this.se = sp.getEnvelope();
				this.sh = this.se.getHeader();
				this.sb = this.se.getBody();
				
				if (this.sh == null) {
					this.sh = this.se.addHeader();
				}
				
				QName name = (QName) context.get(MessageContext.WSDL_OPERATION);
				//activateUser rentBina returnBina getCredit
				if(!name.getLocalPart().equals("activateUser") && !name.getLocalPart().equals("rentBina") && 
						!name.getLocalPart().equals("returnBina") && !name.getLocalPart().equals("getCredit")){
					System.out.println("Ignoring function...");
					return true;
				}
				
				//Cria a key unica
				Key key = setKey(context);
				chaveSecreta = key;

				//converter o SOAPBODY para string
				stringBody = SOAPBodyConvertToString(msg.getSOAPBody());
				//converter para bytes
				byte[] bytesSBody = stringBody.getBytes();
				//converter HexBinary 
				printHexBinary(bytesSBody);
				
				//cria o mac para o body
				byte[] cipherDigest = makeMAC(bytesSBody, key);
								
				//envia o cipherDigest em HexBinary
				this.nameBody = this.se.createName("BodyCipherDigest", "b", "http://BodyCipherDigest");
				SOAPElement elementDigest = this.sh.addHeaderElement(nameBody);
				elementDigest.addTextNode(printHexBinary(cipherDigest));
				
			}
			catch(Exception e){
				throw new RuntimeException("Encontrada SOAP Exception no MacHandler:" + e);
			}
		}
		else {
			//server side
			try {
				SOAPMessage msg = context.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				this.se = sp.getEnvelope();
				this.sh = this.se.getHeader();
				this.sb = this.se.getBody();
				
				if (this.sh == null) {
					this.sh = this.se.addHeader();
				}
				
				QName name = (QName) context.get(MessageContext.WSDL_OPERATION);
				//activateUser rentBina returnBina getCredit
				if(!name.getLocalPart().equals("activateUser") && !name.getLocalPart().equals("rentBina") && 
						!name.getLocalPart().equals("returnBina") && !name.getLocalPart().equals("getCredit")){
					System.out.println("Ignoring function...");
					return true;
				}
				
				String stringBody2 = SOAPBodyConvertToString(msg.getSOAPBody());
				//converter para bytes
				byte[] bytesSBody2 = stringBody2.getBytes();
				//converter HexBinary 
				printHexBinary(bytesSBody2);
				
				byte[] bodyCipherDigest = getBody();
				
				boolean result2 = MACTextVerify(bodyCipherDigest, bytesSBody2, setKey(context));
				if(result2 == false) {
					throw new RuntimeException("O body foi corrompido, em MAChandler!");
				}
				else {
					System.out.println("TUDO A BATER CERTO");
				}
			
				
			}
			catch (Exception e) {
				throw new RuntimeException("Encontrada SOAP Exception no MacHandler:" + e);
			}
			
		}
		return true;
	}


	@Override
	public Set<QName> getHeaders() {
		return null;
	}

/*----------------------------------------------AUX Functions---------------------------------------------------------*/
	/** Makes a message authentication code. */
	private static byte[] makeMAC(byte[] bytes, Key key) throws Exception {

		Mac cipher = Mac.getInstance(MAC_ALGO);
		cipher.init(key);
		byte[] cipherDigest = cipher.doFinal(bytes);

		return cipherDigest;
	}

	/**
	 * Calculates new digest from text and compare it to the to deciphered
	 * digest.
	 */
	private static boolean verifyMAC(byte[] cipherDigest, byte[] bytes, Key key) throws Exception {

		Mac cipher = Mac.getInstance(MAC_ALGO);
		cipher.init(key);
		byte[] cipheredBytes = cipher.doFinal(bytes);
		return Arrays.equals(cipherDigest, cipheredBytes);
	}
				 
	
	private String SOAPBodyConvertToString(SOAPBody soapBody) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		String stringBody;
		System.out.println("...A converter o SOAPBody para string...");
		
		DOMSource source = new DOMSource(soapBody);
		StringWriter stringResult = new StringWriter();
		
		TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
		
		stringBody = stringResult.toString();
		System.out.println("...SOAPBody convertido para string..." );
		
		return stringBody;
	}
	
	private Key setKey(SOAPMessageContext context) throws Exception {
		Key key;
		
		//vai buscar chave
		key = (Key) context.get("sessionKeyTag");
		
		return key;
	}
	
	private String getKey() throws SOAPException {
		Iterator it;
		SecretKey key;
		String keyString;
		
		System.out.println("Geting timeRequest from SOAP");
		
			
		this.nome = this.se.createName("keyMAC", "key", "http://keyMAC");
		it = this.sh.getChildElements(nome);
		
		// check header element
		if (!it.hasNext()) {
			System.out.println("Nao encontrado: BodyCipherDigest missing");
			throw new RuntimeException("Nao encontrado: BodyCipherDigest missing");
		}
		SOAPElement element = (SOAPElement) it.next();
		
		keyString = element.getValue();
		
		return keyString;
		
	}
	
	private byte[] getBody() throws SOAPException {
		Iterator it;
		String bodyString;
		
		System.out.println("Geting timeRequest from SOAP");
		
		this.nome = this.se.createName("BodyCipherDigest", "b", "http://BodyCipherDigest");
		it = this.sh.getChildElements(nome);
		// check header element
		if (!it.hasNext()) {
			System.out.println("Nao encontrado: BodyCipherDigest missing");
			throw new RuntimeException("Nao encontrado: BodyCipherDigest missing");
		}
		SOAPElement element = (SOAPElement) it.next();
		bodyString = element.getValue();
		
		byte[] bodyByte = DatatypeConverter.parseHexBinary(bodyString);
		
		return bodyByte;
	}

	private boolean MACTextVerify(byte[] body, byte[] bodyRecived, Key key) throws Exception {
		//byte[] cipherDigest;
		//SecretKey key;
		System.out.println("Starting verify the message(body of soap)");
		
		//cipherDigest = body.getBytes();
		System.out.println("Calling verifyMAC");
		
		boolean result = verifyMAC(body, bodyRecived, key);
		
		System.out.println("MAC is " + (result ? "right" : "wrong"));
		
		return result;
	 }
	
}