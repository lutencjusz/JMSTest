package SOAModelESBPackage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import SOAService.DAOAdapter.SQLAdapter;
import SOAService.JMSAdapter.JMSMQAdapter;
import SOAService.Model.SOAKomunikat;


//import java.util.concurrent.*;


//import org.apache.activemq.*;


public class SOAModelESBTest {

	private static Integer ILOSC_WATKOW = 3;

	public static void main(String[] args) {

		System.out.println("--- Start pojedynczego egzekutora -------------");

		// 2 
		final ExecutorService nowyEgzekutor = Executors
				.newFixedThreadPool(ILOSC_WATKOW);
		// new ThreadPoolExecutor(ILOSC_WATKOW, ILOSC_WATKOW * 3, ILOSC_WATKOW *
		// 4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),new
		// ThreadPoolExecutor.CallerRunsPolicy());

		for (int i = 1; i <= ILOSC_WATKOW; i++) {
			nowyEgzekutor.execute(new listenerSOA(i));
		}

		try {
			nowyEgzekutor.awaitTermination(20 * 60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		nowyEgzekutor.shutdown();

		System.out.println("--- Koniec pojedynczego egzekutora -------------");

	}
}

class listenerSOA implements Runnable {

	private int a;
	private static String TRYB_ODPOWIEDZI = "JSON"; // JSON lub XML w jakim
													// formacie ma być zwracana
													// odpowiedź JDBC SQL

	public listenerSOA(int a) {
		this.a = a;
	}

	@Override
	public void run() {

		Long wczp; // 
		Long wczk; // 
		String komunikatJMS;
		String komunikatSQL;
		SQLAdapter mySQL = new SQLAdapter();

		System.out.println("--- Start wątku: " + a);
		System.out.println("--- Tryb odpowiedzi SQL: " + TRYB_ODPOWIEDZI);

		try {
			while (true) { 
				do { 
					JMSMQAdapter ja = new JMSMQAdapter();
					wczp = System.currentTimeMillis();
					komunikatJMS = ja.pobierzKomJMS("JMS In", "", "");
					
					if (komunikatJMS.equals("error")) {
						// pobranie komunikatu
						// JMS, oczekuje 10s
						//System.out.println("Nie ma komunikatu JMS z kolejki: "
						// + KOLEJKA_IN);
					} else {
						//do {//dbierania zapytania do bazy 
							if (ja.getTryb().equalsIgnoreCase("JDBC")) {// jeżeli
																		// komunikat
																		// w
																		// trybie
																		// JBDB
								// 
								// mysql-connector-java-5.1.44-bin.jar
								if (mySQL.DAOMySQLUtworzPolaczenie().equals(
										"ok")) {
									komunikatSQL = mySQL.DAOMySQLKomunikatyTelefonyString(
													new SOAKomunikat(
															komunikatJMS)
															.getKomunikat(),
													TRYB_ODPOWIEDZI);
									// parsowanie z XML na SOAKomunikat i
									// pobranie
									// komunikatu
									mySQL.DAOMySQLZamknijPolaczenie();
								} else {
									komunikatSQL = "Nie udało się połączyć z MySQL: "+(new SOAKomunikat(komunikatJMS)).getKomunikat();
									mySQL.DAOMySQLZamknijPolaczenie();
								}

							} else {
								// komunikatSQL =
								// dr.pobierzRESTSQLXML();//pobranie komunikatu
								// REST
								komunikatSQL = "błędna próba pobrania komunikatu REST";
							}
							if (ja.wyslijKomJMS(ja.getMSISDN() + "_Out",
									ja.getMSISDN(), komunikatSQL, ja.getTryb(),
									ja.getCollerationId()).equalsIgnoreCase(
									"ok")) {
								// wysłanie komunikatu REST lub JDBC
								wczk = System.currentTimeMillis();
								System.out.println("Wątek: " + a
										+ " ;wysłano komunikat JMS w: "
										+ (wczk - wczp));
							} else {
								komunikatSQL = "error";// wysłanie błedu, jeżeli
														// odpowiedzi nie było
														// po 10s
								System.out
										.println("Nie wysłano komunikatu JMS: Błędny komunikat SQL");
							}
						//} while (komunikatSQL.equals("error"));// koniec pętli
																// odbierającej
																// zapytanie do
																// bazy
					}

				} while (komunikatJMS.equals("error"));// koniec pętli
														// nasłuchującej kolejkę
														// JMS
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		// System.out.println("Koniec wątku: ");
	}

}

