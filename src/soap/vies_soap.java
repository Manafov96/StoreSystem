package soap;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class vies_soap {

   
    public static SOAPMessage createSOAPRequest(String countryCode, String vatNumber) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://ec.europa.eu/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tns1", "urn:ec.europa.eu:taxud:vies:services:checkVat:types");
        envelope.addNamespaceDeclaration("impl", "urn:ec.europa.eu:taxud:vies:services:checkVat");

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        QName bodyQName = new QName("urn:ec.europa.eu:taxud:vies:services:checkVat:types",
                "checkVat", "tns1");
        SOAPElement soapBodyElem = soapBody.addChildElement(bodyQName);

        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(new QName("urn:ec.europa.eu:taxud:vies:services:checkVat:types",
                "countryCode", "tns1"));
        soapBodyElem1.addTextNode(countryCode);
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement(new QName("urn:ec.europa.eu:taxud:vies:services:checkVat:types",
                "vatNumber", "tns1"));
        soapBodyElem2.addTextNode(vatNumber);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI  + "checkVat");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

    public static String printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
        System.out.println(); 
        SOAPBody body = soapResponse.getSOAPBody();
        String isValid = body.getElementsByTagName("valid").item(0).getTextContent();
        System.out.println(isValid);
        
        return isValid;
        
    }
}