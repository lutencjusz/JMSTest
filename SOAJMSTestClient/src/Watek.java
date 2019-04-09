import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.jms.Connection;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;

import SOAService.Model.ParametryWatku;
import SOAService.Model.WynikWatku;

class Watek implements Callable<String> {

	private JFrame p;
	private int znaka = KeyEvent.VK_A; // kod znaku "a"
	private int iloscLiter = KeyEvent.VK_Z - KeyEvent.VK_A; // iloœæ liter w alfabecie ASCII
	public List<Long> listaWynik = new ArrayList<>();// lista czasów trwania operacji

	private int podzialKola;
	private int a; // numer w¹tku
	private Connection conn; // po³aczenie z JMS MQ
	private String tryb = "JDBC"; // (JDBC/REST)
	private String zapSQL = "select * from telefony"; // Zapytanie SQL
	private int iloscPowtorzen = 100;// maksymalna iloœæ powtórzeñ pêtli g³ównej
	private int iloscWatkow = 3;
	private int iloscWszystkichWatkow = 6;
	private DefaultListModel<String> modelListyTelefonow;
	private String wynikWatku = "";

	public Watek(ParametryWatku pW) {
		this.a = pW.getA();
		this.podzialKola = pW.getPodzialKola();
		this.modelListyTelefonow = pW.getModelListyTelefonow();
		this.conn = pW.getConn();
		this.tryb = pW.getTryb();
		this.zapSQL = pW.getZapSQL();
		this.iloscPowtorzen = pW.getIloscPowtorzen();
		this.iloscWatkow = pW.getIloscWatkow();
		this.iloscWszystkichWatkow = pW.getIloscWszystkichWatkow();
	}

	public String call() {

		JProgressBarDemo frame = new JProgressBarDemo(a, 0, iloscPowtorzen * modelListyTelefonow.size(), podzialKola);
		/*
		 * int i = 0; while (i < 200) { if (!frame.czyStop()) { try { Thread.sleep(100);
		 * } catch (InterruptedException e) { } frame.ustawPasekPostepu(i); } else {
		 * frame.setVisible(false); return a; } i += 1; }
		 * 
		 * }
		 */
		String wynik = "";// wynik zwrócony przez serwis
		Long czp = Long.parseLong("0"); // czas pocz¹tkowy
		Long czk = Long.parseLong("0"); // czas koñcowy
		Integer iloscBlednychKom = 0;
		WynikWatku wW = new WynikWatku();

		try {
			TestWS ws = new TestWS();
			String wzorzecWyniku = ""; // zmienna zworca wyniku
			// System.out.println("MAX_ILOSC_POWT: " + MAX_ILOSC_POWT + "; listaMSISDN: " +
			// listaMSISDN.size());
			for (int i = 0; i < iloscPowtorzen; i++) {
				int j = 0; // zmienna pomicnicza do zapisu w JProgressBar pierwszego wyniku
				// for (String t : modelListyTelefonow) {
				for (int k = 0; k < modelListyTelefonow.size(); k++) {
					czp = System.currentTimeMillis();
					if (!frame.czyStop()) {
						if (tryb.equalsIgnoreCase("JDBC")) {
							wynik = ws.SOAP_WS_JMS(conn, Long.parseLong("1"), "JMS In",
									modelListyTelefonow.getElementAt(k), zapSQL, tryb);
						} else {
							wynik = ws.SOAP_WS_JMS(conn, Long.parseLong("1"), "JMS In",
									modelListyTelefonow.getElementAt(k),
									"Client test" + randomString(8, iloscLiter, znaka), "REST");
						}

						if (i == 0 && j++ == 0 && !wynik.equals("error")) { // dodwanie pierwszej odpowidzi do
																			// JProgressBar
							frame.wstawText(wynik + "\n");
							wzorzecWyniku = wynik;
						}
						czk = System.currentTimeMillis();
						if (wynik.equals("error")) {
							System.out.println(
									"\nW¹tek " + a + " odebra³ b³êdny komunikat w:" + (czk - czp) + " ms: " + wynik);
							iloscBlednychKom += 1;
							listaWynik.add(czk - czp);
						} else {
							// System.out.println("W¹tek " + a + " odebra³ w " + (czk - czp) + " ms:
							// "+wynik);
							// System.out.print(".");
							listaWynik.add(czk - czp);
						}
						if (wynik.equals(wzorzecWyniku) || wzorzecWyniku.equals("")) { // czy wynik prawid³owy
							frame.wstawText(".");
						} else {
							frame.wstawText(wynik + "\n");
						}
						frame.ustawPasekPostepu(frame.pobierzPasekPostepu() + 1); // inc paska postêpu
					}
				}

			}

			Collections.sort(listaWynik);
			OptionalDouble avg = listaWynik.stream().mapToLong(val -> val).average();

			wW.setA(a);
			wW.setIloscBlednychKom(iloscBlednychKom);
			wW.setIloscKom(listaWynik.size());
			wW.setMaxCzas(Collections.max(listaWynik));
			wW.setMinCzas(Collections.min(listaWynik));
			wW.setSreCzas(avg.getAsDouble());
			wW.setMedCzas(listaWynik.get(listaWynik.size() / 2));

			System.out.println("\n---------Podsumowanie------------");
			System.out.println("W¹tek: " + a);
			System.out.println("Iloœæ b³êdnych komunikatów = " + iloscBlednychKom);
			System.out.println("Iloœæ komunikatów = " + listaWynik.size());
			System.out.println("Min czas = " + Collections.min(listaWynik) + " ms");
			System.out.println("Max czas = " + Collections.max(listaWynik) + " ms");
			System.out.println("Œredni czas = " + avg.getAsDouble() + " ms");
			System.out.println("Mediana czasu = " + listaWynik.get(listaWynik.size() / 2) + " ms");
			System.out.println("---------Koniec podsumowania--------------");

		} catch (

		Exception e) {
			e.printStackTrace();
		}
		String wynikWatku = "";
		try {
			wynikWatku = wW.parsowanieClassnaXML();
		} catch (Exception e) {
			System.out.println("Nie uda³o siê parsowanie wyniku dla: " + a);
			e.printStackTrace();
		}
		frame.wylacz();
		// System.out.println("Watek: "+a+": "+wynikWatku);
		return wynikWatku;

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
