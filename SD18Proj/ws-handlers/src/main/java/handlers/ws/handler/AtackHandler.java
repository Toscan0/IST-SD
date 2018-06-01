package handlers.ws.handler;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;

public class AtackHandler implements SOAPHandler<SOAPMessageContext>{

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		System.out.println("Welcome to the handleMessage in AtackHandler");

		Boolean outboundElement = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

			try {
				if (outboundElement.booleanValue()) {
					
					// get SOAP envelope
					SOAPMessage msg = context.getMessage();
					SOAPPart sp = msg.getSOAPPart();
					SOAPEnvelope se = sp.getEnvelope();
					SOAPBody sb = se.getBody();

					// add header
					SOAPHeader sh = se.getHeader();
					if (sh == null)
						sh = se.addHeader();
					
					QName name = (QName) context.get(MessageContext.WSDL_OPERATION);
					//activateUser rentBina returnBina getCredit
					if(!name.getLocalPart().equals("activateUser") && !name.getLocalPart().equals("rentBina") && 
							!name.getLocalPart().equals("returnBina") && !name.getLocalPart().equals("getCredit")){
						System.out.println("Ignoring function...");
						return true;
					}
					
					NodeList children;
					try{
						children = ((Node) sb).getFirstChild().getFirstChild().getChildNodes();
					} catch(NullPointerException e){
						return true;
					}

					for (int i = 0; i < children.getLength(); i++) {
						Node argument = (Node) children.item(i);
						if (argument.getNodeName().equals("email")) {

							System.out.println("Changing email in outbound SOAP message...");					

							//String email = argument.getTextContent();
							//changig email  in outbound SOAP message
							argument.setTextContent("sd@tecnico.ulisboa.pt");
						}
					}

				}
				else{
					
					return true;
					
				}
			} catch (DOMException e) {
				throw new RuntimeException("DOM Exception caught in AtackHandler: " + e);
			} catch (SOAPException e){
				throw new RuntimeException("SOAP Exception caught in AtackHandler: " + e);
			}


		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		// nothing to clean up
		
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

}
