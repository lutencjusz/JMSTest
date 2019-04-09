
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.swing.JOptionPane;

import SOAService.JMSAdapter.JMSMQAdapter;

public class Main {

	private static Integer MAX_ILOSC_WATKOW = 30;
	private static Integer MAX_ILOSC_POWTORZEN = 200;
	private static String DOMYSLNE_ZAP_SQL = "select * from telefony"; // zapytanie SQL

	public static void main(String[] args) {

		JMSMQAdapter jmsAd;
		jmsAd = new JMSMQAdapter(); // Utworzenie dapatera do JMS MQ
				
		String zapSQL = "select * from telefony";
		Integer iloscWatkow = 0;
		Integer iloscPowtorzen = 0;

		iloscWatkow = wprowadzPoprawneWartosciInteger("Podaj ilo�� watk�w", "3", MAX_ILOSC_WATKOW);
		iloscPowtorzen = wprowadzPoprawneWartosciInteger("Podaj ilo�� powtorze� w watkach", "20", MAX_ILOSC_POWTORZEN);
		
		Object[] options = { "JDBC", "REST" };
		int result = JOptionPane.showOptionDialog(null, "Kt�ry tryb komunikacji wybierasz?", "",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		String tryb = (result == 0) ? "JDBC" : "REST";
		if (result == 0) {
			zapSQL = JOptionPane.showInputDialog("Podaj zapytanie SQL?", DOMYSLNE_ZAP_SQL);
		} else {
			zapSQL = "REST";
		}

		System.out.println("--- Start pojedynczego egzekutora -------------");

		if (!jmsAd.nawiazPolaczenie().equals("ok")) {
			System.out.println("Nie mo�na nawi�za� po�aczenie JMS");
			System.exit(1);
		}

		// ILOSC_WATKOW startuj�, reszta czeka w kolejce
		final ExecutorService nowyEgzekutor = new ThreadPoolExecutor(MAX_ILOSC_WATKOW, MAX_ILOSC_WATKOW,
				MAX_ILOSC_WATKOW, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
				new ThreadPoolExecutor.CallerRunsPolicy());

		int i = 1;
		List<Future<Integer>> listaWatkow = new ArrayList<>();
		while (i <= iloscWatkow) {
			final Future<Integer> Fi = nowyEgzekutor.submit(new Watek(jmsAd.getConnection(), i, tryb, zapSQL, iloscPowtorzen));	
			listaWatkow.add(Fi);
			i += 1;
		}

		
		for (Future<Integer> w: listaWatkow) { //p�tla oczekiwanie na zako�czenie watkow
			try {
				w.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("\n--- Koniec pojedynczego egzekutora  -------------");
		if (!jmsAd.zakonczPolaczenie().equals("ok")) {
			System.out.println("Nie mo�na zako�czy� po�aczenia JMS");
			System.exit(1);
		} else {
			System.out.println("Zako�czono po�aczenie z "+jmsAd.getURL());
		}
		
		try { //wstrzymanie na 15 sek. w celu obejrzenia wynik�w
			nowyEgzekutor.awaitTermination(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		nowyEgzekutor.shutdown();

	}

	public Main() {
		super();
	}
	
	public static Integer wprowadzPoprawneWartosciInteger(String kom, String domyslne, Integer max) {

		Integer i = 0;

		while (i == 0) {
			String wartoscString = JOptionPane.showInputDialog(kom + " (<" + max + ")?", domyslne);
			try {
				i = Integer.parseInt(wartoscString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "To nie jest liczba... podaj jeszcze raz");
				i = 0;
			}
			if (i < 0 || i > max) {
				JOptionPane.showMessageDialog(null,
						"Licba nie mie�ci si� w zakresie (1:" + max + ">... podaj jescze raz");
				i = 0;
			}
		}
		return i;
	}
}

class Watek implements Callable <Integer>{
	
	private int a; // numer w�tku
	private Connection conn; //po�aczenie z JMS MQ
	private String tryb; // (JDBC/REST)
	private String zapSQL; // Zapytanie SQL
//	private int znak0 = 48; // kod znaku "0"
	private int znaka = 97; // kod znaku "a"
	private int iloscLiter = 24; // ilo�� liter w alfabecie ASCII
//	private int iloscCyfr = 10; // ilo�� cyfr
	private int MAX_ILOSC_POWT;// maksymalna ilo�� powt�rze� p�tli
									// g��wnej
	// private String TRYB_TESTU = "JDBC"; // JDBC lub REST

	public Watek(Connection conn, int a, String tryb, String zapSQL, int MAX_ILOSC_POWT) {
		this.a = a;
		this.tryb = tryb;
		this.zapSQL = zapSQL;
		this.conn = conn;
		this.MAX_ILOSC_POWT = MAX_ILOSC_POWT;
		System.out.println("Start watku: " + a);
	}

	@Override
	public Integer call() {

		List<String> listaMSISDN = new ArrayList<>(); // lista MSISDN'�w
		List<Long> listaWynik = new ArrayList<>();// lista czas�w trwania
													// operacji
		// listaMSISDN.add("601" + randomString(6, iloscCyfr, znak0));
		// listaMSISDN.add("601" + randomString(6, iloscCyfr, znak0));
		// listaMSISDN.add("601" + randomString(6, iloscCyfr, znak0));
		// listaMSISDN.add("601" + randomString(6, iloscCyfr, znak0));
		listaMSISDN.add("601135623");
		listaMSISDN.add("601135622");
		listaMSISDN.add("601135621");
		listaMSISDN.add("601135620");

		String wynik = "";// wynik zwr�cony przez serwis
		Long czp; // czas pocz�tkowy
		Long czk; // czas ko�cowy
		Integer iloscBlednychKom = 0;

		try {
			TestWS ws = new TestWS();
			for (int i = 0; i < MAX_ILOSC_POWT; i++) {
				for (String t : listaMSISDN) {
					czp = System.currentTimeMillis();
					if (tryb.equalsIgnoreCase("JDBC")) {
						wynik = ws.SOAP_WS_JMS(conn, Long.parseLong("1"), "JMS In", t, zapSQL, tryb);
					} else {
						wynik = ws.SOAP_WS_JMS(conn, Long.parseLong("1"), "JMS In", t,
								"Client test" + randomString(8, iloscLiter, znaka), "REST");
					}
					czk = System.currentTimeMillis();

					if (wynik.equals("error")) {
						System.out.println("\nW�tek " + a + " odebra� b��dny komunikat w:" + (czk - czp) + " ms: "+wynik);
						iloscBlednychKom += 1;
						listaWynik.add(czk - czp);
					} else {
						//System.out.println("W�tek " + a + " odebra� w " + (czk - czp) + " ms: "+wynik);
						System.out.print(".");
						listaWynik.add(czk - czp);
					}
				}
			}

			Collections.sort(listaWynik);
			OptionalDouble avg = listaWynik.stream().mapToLong(val -> val).average();

			System.out.println("\n---------Podsumowanie------------");
			System.out.println("W�tek: " + a);
			System.out.println("Ilo�� b��dnych komunikat�w = " + iloscBlednychKom);
			System.out.println("Ilo�� komunikat�w = " + listaWynik.size());
			System.out.println("Min czas = " + Collections.min(listaWynik) + " ms");
			System.out.println("Max czas = " + Collections.max(listaWynik) + " ms");
			System.out.println("�redni czas = " + avg.getAsDouble() + " ms");
			System.out.println("Mediana czasu = " + listaWynik.get(listaWynik.size() / 2) + " ms");
			System.out.println("---------Koniec podsumowania--------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0; //przekazanie sygna�u o zako�czeniu w�tku Callable
	}

	private String randomString(int max, int zakres, int znakStart) {
		Random ran = new Random();

		char data = ' ';
		String dat = "";

		for (int i = 0; i < max; i++) {
			// data = (char) (ran.nextInt(25) + 97);
			data = (char) (ran.nextInt(zakres) + znakStart);
			dat = data + dat;
		}
		return dat;
	}

}
