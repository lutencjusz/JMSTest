package SOAService.JMSAdapter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;



public class JMSMQAdapter {

	// public String KOLEJKA_IN = "JMS In";
	// public String KOLEJKA_OUT = "JMS Out";
	private String CollerationId; // Koleracja komunikatu
	private String MSISDN;
	private String Tryb;
	private Connection conn;
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	//private static String url = "failover:(tcp://localhost:61616)?timeout=15000";
	private static int CZAS_OCZEKIWANIA = 15; // czas oczekiwania kolejki JMS w
												// sek.
	public JMSMQAdapter(Connection conn) {
		super();
		this.conn = conn;
	}
	
	public JMSMQAdapter() {
		super();
	}
	
	public String nawiazPolaczenie() {
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			conn = connectionFactory.createConnection();
			conn.start();
		}catch (JMSException e) {
			return "error";
		}
		return "ok";
	}
	
	public String zakonczPolaczenie() {
		try {
			conn.close();
		} catch (JMSException e) {
			return "error";
		}
		return "ok";
	}
	
	public String wyslijKomJMSTest(String kolejka, String MSISDN, String komunikat, String tryb, String collerationId) {
		// Metoda do wysyłania wiadomości do kolejki MQ JMSTest
		// uruchomienie ActiveMQ: C:\apache-activemq-5.15.2\bin\win64\activemq

		this.CollerationId = collerationId;
		this.MSISDN = MSISDN;
		this.Tryb = tryb;

		try {
			//ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			//Connection connection;

			//connection = connectionFactory.createConnection();

			//connection.start();

			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(kolejka);
			MessageProducer producer = session.createProducer(destination);

			TextMessage message = session.createTextMessage(komunikat);
			message.setJMSCorrelationID(collerationId);
			message.setStringProperty("MSISDN", MSISDN);
			message.setStringProperty("Tryb", Tryb);
			producer.send(message);

			//System.out.println("Wyslano do kolejki: " + kolejka + " '" + message.getText() + "'");
			producer.close();
			session.close();
			//connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return "ok";
	}

	public String pobierzKomJMSTest(String Kolejka, String Tryb, String jmsCorellationId) {
		// Metoda odbierania z kolejki MQ JMSTest

		String wynik = null;
		@SuppressWarnings("unused")
		TextMessage text = null;
		MessageConsumer consumer;

		try {
			//ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			//Connection connection = connectionFactory.createConnection();
			//connection.start();

			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(Kolejka);

			if (jmsCorellationId.equalsIgnoreCase("")) {// jeżeli
														// jmsCorellationId jest
														// pusty, to bierze z
														// kolejki pierwszy
				consumer = session.createConsumer(destination);
			} else {
				//System.out.println("CorrelationID = " + jmsCorellationId);
				consumer = session.createConsumer(destination, "JMSCorrelationID='" + jmsCorellationId + "'");
				// wybiera z kolejki docelowej tylko komunikat z identyfikatorem
				// JMSCorrelationID
			}
			Message message = consumer.receive(CZAS_OCZEKIWANIA * 1000); // czeka
																			// na
																			// komunikat
																			// w
																			// sek.

			text = (TextMessage) message;

			consumer.close();
			session.close();
			//connection.close();

			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				wynik = textMessage.getText();
				this.CollerationId = textMessage.getJMSCorrelationID();
				this.MSISDN = textMessage.getStringProperty("MSISDN");
				this.Tryb = textMessage.getStringProperty("Tryb");
				//System.out.println("Odebrano tekst: " + wynik);
			} else {
				System.out.println("Odebrano message: " + message);
				wynik = "error";
			}

		} catch (JMSException e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
		return wynik;
	}

	public String getCollerationId() {
		return CollerationId;
	}

	public String getMSISDN() {
		return MSISDN;
	}

	public String getTryb() {
		return Tryb;
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public String getURL() {
		return url;
	}
}
