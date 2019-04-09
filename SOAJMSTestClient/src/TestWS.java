
//import java.util.Random;

import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import SOAService.JMSAdapter.JMSMQAdapter;
import SOAService.Model.SOAKomunikat;
//import javax.jws.WebParam;

//import SOAService.JMSAdapter.JMSMQAdapter;
//import SOAService.Model.SOAKomunikat;

@SuppressWarnings("unused")
public class TestWS {

	// private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// http://localhost:8080/TestWSModel/services/TestWS?wsdl
	// @WebMethod(operationName = "TestWS")
	// public String SOAP_WS_JMS(@WebParam(name = "Id") final long Id,
	// @WebParam(name = "Kolejka") String Kolejka,
	// @WebParam(name = "MSISDN") String MSISDN, @WebParam(name = "Komunikat")
	// String Komunikat,
	// @WebParam(name = "Tryb") String Tryb) {

	public String SOAP_WS_JMS(Connection conn, Long Id, String Kolejka, String MSISDN, String Komunikat, String Tryb) {

		String wynikIn = ""; // wynik operacji wys≈Çania
		String wynikOut = ""; // wynik operacji pobrania
		String collerationId;
		Integer licznikErr = 0;
		Integer MAX_LICZBA_BLEDOW = 150;

		SOAKomunikat XMLKom = new SOAKomunikat();
		XMLKom.setId(Id);
		XMLKom.setKolejka(Kolejka);
		XMLKom.setMSISDN(MSISDN);
		XMLKom.setKomunikat(Komunikat);
		XMLKom.setTryb(Tryb);

		try {
			JMSMQAdapter ja = new JMSMQAdapter(conn);
			collerationId = randomString(6);
			wynikIn = ja.wyslijKomJMSTest("JMS In", XMLKom.getMSISDN(), XMLKom.parsowanieClassnaXML(), XMLKom.getTryb(),
					collerationId);
			if (!wynikIn.equals("ok")) {
				System.out.println("Nie uda≥o siÍ wys≥aÊ komunikatu: " + wynikIn);
			}
			wynikOut = ja.pobierzKomJMSTest(XMLKom.getMSISDN() + "_Out", ja.getTryb(), collerationId);// pobiera
																										// komunikat
																										// zgodny z
																										// collerationId
			while (!ja.getCollerationId().equals(collerationId)) { // po
																	// wprowadzeniu
																	// collerationId
																	// sekcja
																	// nadmiarowa
				wynikIn = ja.wyslijKomJMSTest(XMLKom.getMSISDN() + "_Out", XMLKom.getMSISDN(), wynikOut, ja.getTryb(),
						ja.getCollerationId());
				// wys≈Çanie obcego komunikatu z powrotem do kolejki
				licznikErr += 1;
				System.out.println("Odebrano b≈Çƒôdny komunikat Collaboration_id: " + ja.getCollerationId());
				if (licznikErr >= MAX_LICZBA_BLEDOW) {// sprawdzenie max liczby
														// komunikat√≥w do
														// sprawdzenia
					// wynikIn = ja.wyslijKomJMS(XMLKom.getMSISDN() + "_Out",
					// XMLKom.getMSISDN(), wynikOut, ja.getCollerationId());
					return "error";
				}
				wynikOut = ja.pobierzKomJMSTest(XMLKom.getMSISDN() + "_Out", ja.getTryb(), collerationId);// powt√≥rne
																											// odebranie
																											// komunikatu
			}
		} catch (Exception e) {

		}
		return wynikOut;
	}

	private static String randomString(int max) {
		Random ran = new Random();

		char data = ' ';
		String dat = "";

		for (int i = 0; i <= max; i++) {
			data = (char) (ran.nextInt(25) + 97);
			dat = data + dat;
		}
		return dat;
	}
}
