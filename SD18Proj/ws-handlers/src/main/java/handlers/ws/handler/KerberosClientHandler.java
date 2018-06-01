package handlers.ws.handler;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.security.Key;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.NodeList;
import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.RequestTimeView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;
//import helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext>{
	

	public static final String CONTEXT_PROPERTY = "my.property";
	//SOAP
	private SOAPEnvelope se;
	private SOAPHeader sh;
	private SOAPBody sb;
	//Kerby
	private String user1_email = "alice@A30.binas.org";
	private String user1_pass = "88AXnSX6d";
	private String user2_email = "charlie@A30.binas.org";
	private String user2_pass = "AaBsyJVG";
	private String user3_email = "eve@A30.binas.org";
	private String user3_pass = "GMt9hP4";
	private String server_email = "binas@A30.binas.org";
	private String server_pass = "Y5p7yBzp";
	private int VALID_DURATION = 90;
	private SecureRandom randomGenerator = new SecureRandom();
	private KerbyClient kclient;
	//TAG
	int tag = 0;
	//Cifrar
	private Key clientKey;
	private Key sk;
	private SessionKey sessionKey;
	private Name nameTicket;
	private Name nameAuth;
	
	private SOAPHeaderElement elementTicket;
	private SOAPHeaderElement elementAuth;
	
	private byte[] valueTicket;
	private byte[] valueAuth;
	
	private String ticket64;
	private String auth64;
	
	private long nounce;
	private SessionKeyAndTicketView result;
	
	private CipheredView cipheredSessionKey;
	private CipheredView cipheredTicket;
	private CipheredView cipheredAuth;
	
	
	private Auth authClient;
	private Name nome;
	
	@Override
	public void close(MessageContext arg0) {
		// nothing to clean up
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext arg0) {
		System.out.println("Welcome to the handleMessage in KerberosClient");
		SOAPMessage msg;
		SOAPPart sp;
		String timeRequest;
		Boolean outboundElement = (Boolean) arg0.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		try {
			if(outboundElement.booleanValue()) {
				// get SOAP envelope
				 msg = arg0.getMessage();
				 sp = msg.getSOAPPart();
				this.se = sp.getEnvelope();
				this.sh = se.getHeader();
				this.sb = se.getBody();
				if (this.sh == null)
					this.sh = se.addHeader();
				
				QName name = (QName) arg0.get(MessageContext.WSDL_OPERATION);
				//activateUser rentBina returnBina getCredit
				if(!name.getLocalPart().equals("activateUser") && !name.getLocalPart().equals("rentBina") && 
						!name.getLocalPart().equals("returnBina") && !name.getLocalPart().equals("getCredit")){
					System.out.println("Ignoring function...");
					return true;
				}
				
					NodeList children = ((Node) sb).getFirstChild().getChildNodes();
					
					//Cria um kerbyClient
					try {
						this.kclient = new KerbyClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby");
					} catch (KerbyClientException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					for (int i = 0; i < children.getLength(); i++) {
						Node argument = (Node) children.item(i);
						if (argument.getNodeName().equals("email")) {
							
							System.out.println("cifrar email in outbound SOAP message...");
							
							
							try {
								cifrarEmail(argument, arg0);
								//arg0.put("sessionKeyTag", sessionKey);
							} catch (NoSuchAlgorithmException | InvalidKeySpecException | SOAPException
									| BadTicketRequest_Exception | KerbyException | JAXBException e1) {
								throw new RuntimeException("Encontrada SOAP Exception no KerberosClient:" + e1);
							}
							
								msg.saveChanges();
							
							
							return true;
						}
					}
			}else {
				
				// get SOAP envelope
				msg = arg0.getMessage();
				sp = msg.getSOAPPart();
				
				this.se = sp.getEnvelope();	
				
				this.sh = se.getHeader();
				if (this.sh == null) {
					this.sh = se.addHeader();
					
				this.sb = se.getBody();
				if (this.sb == null)
					this.sb = se.addBody();
				}
				
				QName name = (QName) arg0.get(MessageContext.WSDL_OPERATION);
				//activateUser rentBina returnBina getCredit
				if(!name.getLocalPart().equals("activateUser") && !name.getLocalPart().equals("rentBina") && 
						!name.getLocalPart().equals("returnBina") && !name.getLocalPart().equals("getCredit")){
					System.out.println("Ignoring function...");
					return true;
				}
	
				NodeList children = ((Node) sb).getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = (Node) children.item(i);
					if (argument.getNodeName().equals("email")) {
						
						System.out.println("cifrar email in outbound SOAP message...");
						
						try {
							timeRequest = getTimeRequest(arg0);
							decifrarTimeRequest(argument, timeRequest, arg0);
							
						} catch (NoSuchAlgorithmException | InvalidKeySpecException  | KerbyException e1) {
							throw new RuntimeException("Encontrada SOAP Exception no KerberosClient:" + e1);
						} catch (SOAPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msg.saveChanges();
						return true;
					}
				}
			} 
		}catch (SOAPException e) {
			throw new RuntimeException("Encontrada SOAP Exception no KerberosClient:" + e);
		}
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
	
/*----------------------------------------------AUX Functions---------------------------------------------------------*/
	private void cifrarEmail(Node argument, SOAPMessageContext arg0) throws NoSuchAlgorithmException, InvalidKeySpecException, SOAPException, BadTicketRequest_Exception, KerbyException, JAXBException{
		System.out.println("Cifrar info from email...");
		String email = argument.getTextContent();
		
		if(email.equals(user1_email)) {
			System.out.println("Email do user1->alice");
			nounce = randomGenerator.nextLong();
			kerbyConectCli(email, user1_pass, nounce, arg0);
		}
		else if(email.equals(user2_email)) {
			System.out.println("Email do user2->charlie");
			nounce = randomGenerator.nextLong();
			kerbyConectCli(email, user2_pass, nounce, arg0);
		}
		else if(email.equals(user3_email)) {
			System.out.println("Email do user3->eve");
			nounce = randomGenerator.nextLong();
			kerbyConectCli(email, user3_pass, nounce, arg0);
		}
		else {
			System.out.println("No client with that email: " + email);
		}
		System.out.println("End email cifring");
	}
	
	private static Key getKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return SecurityHelper.generateKeyFromPassword(password);
	}
	
	private void kerbyConectCli(String email, String user_pass, long nounce, SOAPMessageContext context) throws SOAPException, BadTicketRequest_Exception, NoSuchAlgorithmException, InvalidKeySpecException, KerbyException, JAXBException {
		CipherClerk ck = new CipherClerk();
		
		System.out.println("Starting kerby conect in client side");
		//Key do client
		this.clientKey = getKey(user_pass);
		//gera um numero random
		this.nounce = randomGenerator.nextLong();
		//faz o requestTocket do client
		this.result = kclient.requestTicket(email, server_email, nounce, VALID_DURATION);
		//getSessionKey 
		this.cipheredSessionKey = result.getSessionKey();
		//getTicket
		this.cipheredTicket = result.getTicket();
		this.sessionKey = new SessionKey(cipheredSessionKey, clientKey);
		context.put("sessionKeyTag", this.sessionKey.getKeyXY());
		
		// add header element (name, namespace prefix, namespace)
		System.out.println("New header element for ticket");
		this.nameTicket = se.createName("ticket", "t", "http://ticket");
		this.elementTicket = sh.addHeaderElement(nameTicket);
		// add header element value
		this.valueTicket = ck.cipherToXMLBytes(cipheredTicket, "ticket");
		
		// encoding binary data with base 64
	    System.out.println("Encoding to Base64 ...");
	    this.ticket64 = printBase64Binary(valueTicket);
		this.elementTicket.addTextNode(ticket64);		
		
		//Auth(String x, Date timeRequest)
		this.authClient = new Auth(email, new Date());
		this.cipheredAuth = authClient.cipher(sessionKey.getKeyXY());
		
		// add header element (name, namespace prefix, namespace)
		System.out.println("New header element for auth");
		this.nameAuth = se.createName("auth", "a", "http://auth");
		this.elementAuth = sh.addHeaderElement(nameAuth);
		// add header element value
		this.valueAuth = ck.cipherToXMLBytes(cipheredAuth, "auth");
		// encoding binary data with base 64
	    System.out.println("Encoding to Base64 ...");
	    this.auth64 = printBase64Binary(valueAuth);
		this.elementAuth.addTextNode(auth64);
	    
		System.out.println("Auth and ticket passed to base 64 binary :)");
	}
	
	private String getTimeRequest(SOAPMessageContext context) throws SOAPException {
		Iterator it;
		String timeRequest;
		
		System.out.println("Geting timeRequest from SOAP");
		
		this.nome = this.se.createName("requestTime", "r", "http://requestTime");
		it = this.sh.getChildElements(this.nome);
		// check header element
		if (!it.hasNext()) {
			System.out.println("Nao encontrado: timeRequest missing");
			throw new RuntimeException("Nao encontrado: timeRequest missing");
		}
		SOAPElement element = (SOAPElement) it.next();
		timeRequest = element.getValue();

		
		return timeRequest;
	}
	
	private void decifrarTimeRequest(Node argument, String timeRequest, SOAPMessageContext context ) throws NoSuchAlgorithmException, InvalidKeySpecException, KerbyException {
		System.out.println("Decifrar info from timeRequest...");
		CipherClerk ck = new CipherClerk();
			
		// decoding string in base 64
	    System.out.println("Decoding RequestTime from Base64 ...");
	    byte[] timeRequestByte = parseBase64Binary(timeRequest);
	    try {
			CipheredView cipheredTimeRequest = ck.cipherFromXMLBytes(timeRequestByte);

			RequestTime rt = new RequestTime(cipheredTimeRequest, this.sessionKey.getKeyXY());
			rt.validate();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		
	}
}
