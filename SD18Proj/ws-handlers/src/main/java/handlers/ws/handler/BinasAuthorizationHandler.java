package handlers.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.Set;

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
import pt.ulisboa.tecnico.sdis.kerby.CipherClerk;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.KerbyException;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext>{
	//SOAP
		private SOAPEnvelope se;
		private SOAPHeader sh;
		private SOAPBody sb;
		Name nome;

		private String server_password = "Y5p7yBzp";
	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		System.out.println("AddHeaderHandler: Handling message.");
		String email;
		Boolean outboundElement = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			Key serverKey = getKey(server_password);
			CipherClerk ck = new CipherClerk();
			if (outboundElement.booleanValue()) {
				System.out.println("Writing header to OUTbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = context.getMessage();
				SOAPPart sp = msg.getSOAPPart();		
				this.se = sp.getEnvelope();		
				this.sh = se.getHeader();
				if (this.sh == null)
					this.sh = se.addHeader();			
				this.sb = se.getBody();
				if (this.sb == null)
					this.sb = se.addBody();
				
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
						email = argument.getValue();
						/*if(!equalEmails(email, context)) {
						throw new RuntimeException("Email do body diferente do encontrado no header");
					}*/
					String ticketSOAP;
					
					ticketSOAP = getTicketSOAP(context);
				
		
				    byte[] ticketByte = parseBase64Binary(ticketSOAP);
				    
				    CipheredView cipheredTicket = ck.cipherFromXMLBytes(ticketByte);
					Ticket ticket = new Ticket(cipheredTicket, serverKey);
					if(email.equals(ticket.getX())) {
						String authSOAP = getAuthSOAP(context);
			
					    byte[] authByte = parseBase64Binary(authSOAP);
					    
					    CipheredView cipheredAuth = ck.cipherFromXMLBytes(authByte);
						Auth auth = new Auth(cipheredAuth, serverKey);
						if(!email.equals(auth.getX())) {
							throw new RuntimeException("Email do body diferente do encontrado no header");
						}else {
							return true;
						}
					}else{
						throw new RuntimeException("Email do body diferente do encontrado no header");
					}
						
					}
				}
				
			} else {
				System.out.println("Reading header from INbound SOAP message...");
				// get SOAP envelope
				SOAPMessage msg = context.getMessage();
				SOAPPart sp = msg.getSOAPPart();		
				this.se = sp.getEnvelope();		
				this.sh = se.getHeader();
				if (this.sh == null)
					this.sh = se.addHeader();			
				this.sb = se.getBody();
				if (this.sb == null)
					this.sb = se.addBody();
				
				
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
						email = argument.getValue();
						/*if(!equalEmails(email, context)) {
							throw new RuntimeException("Email do body diferente do encontrado no header");
						}*/
						String ticketSOAP;
						
						ticketSOAP = getTicketSOAP(context);
					
			
					    byte[] ticketByte = parseBase64Binary(ticketSOAP);
					    
					    CipheredView cipheredTicket = ck.cipherFromXMLBytes(ticketByte);
						Ticket ticket = new Ticket(cipheredTicket, serverKey);
						if(email.equals(ticket.getX())) {
							String authSOAP = getAuthSOAP(context);
				
						    byte[] authByte = parseBase64Binary(authSOAP);
						    
						    CipheredView cipheredAuth = ck.cipherFromXMLBytes(authByte);
							Auth auth = new Auth(cipheredAuth, serverKey);
							if(!email.equals(auth.getX())) {
								throw new RuntimeException("Email do body diferente do encontrado no header");
							}else {
								return true;
							}
						}else{
							throw new RuntimeException("Email do body diferente do encontrado no header");
						}
					}
				}

			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}
	
	
	
	/*----------------------------------------------AUX Functions---------------------------------------------------------*/
	private static Key getKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return SecurityHelper.generateKeyFromPassword(password);
	}
	
	
	/*public Boolean equalEmails(String email, SOAPMessageContext context) {
		try {
			Key serverKey = getKey(server_password);
			CipherClerk ck = new CipherClerk();
			
			String ticketSOAP;
			
				ticketSOAP = getTicketSOAP(context);
			
	
		    byte[] ticketByte = parseBase64Binary(ticketSOAP);
		    
		    CipheredView cipheredTicket = ck.cipherFromXMLBytes(ticketByte);
			Ticket ticket = new Ticket(cipheredTicket, serverKey);
			if(email.equals(ticket.getX())) {
				String authSOAP = getAuthSOAP(context);
	
			    byte[] authByte = parseBase64Binary(authSOAP);
			    
			    CipheredView cipheredAuth = ck.cipherFromXMLBytes(authByte);
				Auth auth = new Auth(cipheredAuth, serverKey);
				if(!email.equals(auth.getX())) {
					return false;
				}else {
					return true;
				}
			}else{
				return false;
			}
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KerbyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}*/
	
	
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
