package handlers.ws.handler;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.RequestTimeView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;
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
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.NodeList;

import com.sun.xml.ws.api.wsdl.parser.XMLEntityResolver.Parser;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.BadTicketRequest_Exception;
import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;
//import helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext>{
	
	public static final String CONTEXT_PROPERTY = "my.property";
	private static RequestTime requestTime;
	private CipheredView requestTimeCiphered;
	
	//SOAP
	private SOAPEnvelope se;
	private SOAPHeader sh;
	private SOAPBody sb;
	Name nome;
	//SOAPMessage msg;
	//SOAPPart sp;
	


	private Name nameRequestTime;
	private SOAPHeaderElement elementRequestTime;
	private byte[] valueRequestTime;

	private String valueTime64;
	private byte[] valueTime;

	SessionKey sessionKey;
	
	//Key
	private String server_email = "binas@A30.binas.org";
	private String server_password = "Y5p7yBzp";
	
	@Override
	public void close(MessageContext context) {
		// nothing to clean up
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		System.out.println("Ignoring fault message...SERVER");
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		String ticket;
		String auth;
		CipherClerk ck = new CipherClerk();
		CipheredView cv = new CipheredView();
		
		Boolean outboundElement = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		System.out.println("Welcome to the handleMessage in KerberosServer: ");
		try {
			if (outboundElement.booleanValue()) {				
				
				// get SOAP envelope
				SOAPMessage msg = context.getMessage();
				SOAPPart sp = msg.getSOAPPart();		
				this.se = sp.getEnvelope();		
				this.sh = se.getHeader();
				if (this.sh == null) {
					this.sh = se.addHeader();				
				this.sb = se.getBody();
				if (this.sb == null)
					this.sb = se.addBody();
				}
				
				QName name = (QName) context.get(MessageContext.WSDL_OPERATION);
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
						System.out.println("outbound SOAP message...");

						System.out.println("New header element for RequestTime");
						this.nameRequestTime = se.createName("requestTime", "r", "http://requestTime");
						this.elementRequestTime = sh.addHeaderElement(nameRequestTime);

						this.elementRequestTime.addTextNode(valueTime64);
					}
				}
			}else {
				System.out.println("A  ler o SOAP");
				// get SOAP envelope
				SOAPMessage msg = context.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				
				this.se = sp.getEnvelope();
				this.sh = se.getHeader();
				this.sb = se.getBody();	
				if (this.sh == null) {
						this.sh = se.addHeader();
				}
				
				QName name = (QName) context.get(MessageContext.WSDL_OPERATION);
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
							ticket = getTicketSOAP(context);
							auth = getAuthSOAP(context);
							decifrarEmail(argument, ticket, auth, context);
						} 
						catch (NoSuchAlgorithmException | InvalidKeySpecException  | KerbyException | JAXBException e1) {
							throw new RuntimeException("Encontrada SOAP Exception no KerberosClient:" + e1);
						}
						
						msg.saveChanges();
					
						return true;
					}
				}
			}
		}
		catch (SOAPException e) {
			throw new RuntimeException("Encontrada SOAP Exception no KerberosServer:" + e);
		} 
	
	return true;
}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
	
/*----------------------------------------------AUX Functions---------------------------------------------------------*/
	private void decifrarEmail(Node argument, String ticketServer, String authServer,SOAPMessageContext context ) throws NoSuchAlgorithmException, InvalidKeySpecException, KerbyException, JAXBException {
		CipherClerk ck = new CipherClerk();
		System.out.println("Decifrar info from email...");

		String email = argument.getTextContent();
		
		//key do server
		Key serverKey = getKey(server_password);
		CipheredView cipheredTicket = new CipheredView();
		CipheredView cipheredAuth= new CipheredView();
		//SessionKey sessionKey;
		long timeDiff;
			
		// decoding string in base 64
	    System.out.println("Decoding ticket from Base64 ...");
	    byte[] ticketByte = parseBase64Binary(ticketServer);
	    cipheredTicket = ck.cipherFromXMLBytes(ticketByte);
		Ticket ticket = new Ticket(cipheredTicket, serverKey);
		
		timeDiff = ticket.getTime2().getTime() - ticket.getTime1().getTime();
		
		// decoding string in base 64
	    System.out.println("Decoding auth from Base64 ...");
	    byte[] authByte = parseBase64Binary(authServer);
	    cipheredAuth = ck.cipherFromXMLBytes(authByte); 

	    Key sessionKey = ticket.getKeyXY(); 
				
		context.put("sessionKeyTag", sessionKey);
				
		Auth auth = new Auth(cipheredAuth,  sessionKey);
		auth.validate();
				
		this.requestTime = new RequestTime(auth.getTimeRequest());		
		
		this.requestTimeCiphered = requestTime.cipher(sessionKey);
		
		this.valueTime = ck.cipherToXMLBytes(requestTimeCiphered, "time");
		
		// encoding binary data with base 64
	    System.out.println("Encoding RequestTime to Base64 ...");
	    this.valueTime64 = printBase64Binary(valueTime);
	}
	
	private static Key getKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return SecurityHelper.generateKeyFromPassword(password);
	}
	
	private String getTicketSOAP(SOAPMessageContext context) throws SOAPException {
		Iterator it;
		String ticket;

		System.out.println("Geting ticket from SOAP");
		
		this.nome = this.se.createName("ticket", "t", "http://ticket");
		it = this.sh.getChildElements(this.nome);
		// check header element
		if (!it.hasNext()) {
			System.out.println("Nao encontrado: ticket missing");
			throw new RuntimeException("Nao encontrado: ticket missing");
		}
		
		SOAPElement element = (SOAPElement) it.next();	
		ticket = element.getValue();	
		
		return ticket;
	}
	
	private String getAuthSOAP(SOAPMessageContext context) throws SOAPException {
		Iterator it;
		String auth;
		
		System.out.println("Geting auth from SOAP");
		
		this.nome = this.se.createName("auth", "a", "http://auth");
		it = this.sh.getChildElements(nome);
		// check header element
		if (!it.hasNext()) {
			System.out.println("Nao encontrado: auth missing");
			throw new RuntimeException("Nao encontrado: auth missing");
		}
		SOAPElement element = (SOAPElement) it.next();
		auth = element.getValue();
		
		return auth;
	}
}
