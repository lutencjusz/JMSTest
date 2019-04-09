import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


public class PobierzJMSTest {

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static String subject = "JMSTest"; //Queue Name;

	/*
	public static void main(String[] args) throws Exception {
		Watek(new PobierzJMSConsumer(), false);
	}
*/
	public PobierzJMSTest(){
		Watek(new PobierzJMSConsumer(), false);
	}
	public static void Watek(Runnable runnable, boolean daemon) {
		Thread brokerWatek = new Thread(runnable);
		brokerWatek.setDaemon(daemon);
		brokerWatek.start();
		try {
			brokerWatek.join();
			//oczekiwanie g³ównego programu na zakoñczenie w¹tku
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    public static class PobierzJMSConsumer implements Runnable, ExceptionListener {
        public void run() {
            try {
 
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
                Connection connection = connectionFactory.createConnection();
                connection.start();
 
                connection.setExceptionListener(this);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(subject);
 
                MessageConsumer consumer = session.createConsumer(destination);
                Message message = consumer.receive(1000);
 
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Odebrano tekst: " + text);
                } else {
                    System.out.println("Odebrano message: " + message);
                }
 
                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
 
        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }
    }
}
