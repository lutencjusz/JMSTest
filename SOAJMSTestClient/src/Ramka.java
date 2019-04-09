import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import SOAService.JMSAdapter.JMSMQAdapter;
import SOAService.Model.ParametryWatku;
import SOAService.Model.WynikWatku;

public class Ramka extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Integer MAX_ILOSC_WATKOW = 30;
	private static Integer MAX_ILOSC_POWTORZEN = 200;
	private static String DOMYSLNE_ZAP_SQL = "select * from telefony"; // zapytanie SQL
	private static Integer MAX_PODZIAL_KOLA = 15;
	private static boolean czyUruchomicWatki = false;

	Integer iloscWatkow = 0;
	Integer iloscPowtorzen = 0;
	String zapSQL = "select * from telefony";
	static String tryb = "JDBC"; // JDBC lub REST
	//static public List<String> listaMSISDN = new ArrayList<>(); // lista MSISDN'ów
	static DefaultListModel<String> modelListyTelefonow;
	private JFrame frame;
	private static JTextField JTFIloscWatkow;
	private static JTextField JTFIloscPowtorzen;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private static JTextField JTFZapytanieSQL;
	private static JTextField JTFIloscWszystkichWatkow;
	private static int podzialKola;
	private static JButton JBUruchomTest;
	private static Ramka oknoGlowne;
	private static JTextField JTFTelefonyDodaj;
	private static JList<String> JLLT;
	private static DefaultTableModel modelTablicy;
	private static JScrollPane JTWynikiWatkow;

	static JPanel panel = new JPanel();

	public static void main(String[] args) {

		modelTablicy = new DefaultTableModel();
		modelTablicy.addColumn("Nr w¹tku"); //tworzenie tablicy wyników
		modelTablicy.addColumn("Iloœæ b³ednych kom.");
		modelTablicy.addColumn("Iloœæ komunikatów");
		modelTablicy.addColumn("Min. czas");
		modelTablicy.addColumn("Max. czas");
		modelTablicy.addColumn("Mediana czasu");
		modelTablicy.addColumn("Œredni czas");
		
		modelListyTelefonow = new DefaultListModel<String>();
		modelListyTelefonow.addElement("601135622");
		modelListyTelefonow.addElement("601135623");
		modelListyTelefonow.addElement("601135624");
		modelListyTelefonow.addElement("601135625");
		modelListyTelefonow.addElement("601135626");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					oknoGlowne = new Ramka();
					oknoGlowne.frame.setVisible(true);
					//JTWynikiWatkow.setVisible(false);
					oknoGlowne.frame.revalidate();
					oknoGlowne.frame.pack();
					oknoGlowne.frame.repaint();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		while (true) {
			czyUruchomicWatki = false;
			// pêtla umo¿liwia uruchomienie JProgressBarów w Watkach prawid³owe bez pustych
			// pól
			// nie mo¿na tego zrobiæ z poziomu podprocedury
			while (!czyUruchomicWatki) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			JBUruchomTest.setEnabled(false); // wy³¹czenie powtórnego uruchomienia w¹tków w trakcie pracy programu

			JMSMQAdapter jmsAd;
			jmsAd = new JMSMQAdapter(); // Utworzenie dapatera do JMS MQ

			if (!jmsAd.nawiazPolaczenie().equals("ok")) {
				System.out.println("Nie mo¿na nawi¹zaæ po³aczenie JMS");
				System.exit(1);
			}

			final ExecutorService nowyEgzekutor = new ThreadPoolExecutor(Integer.parseInt(JTFIloscWatkow.getText()),
					Integer.parseInt(JTFIloscWszystkichWatkow.getText()), MAX_ILOSC_WATKOW, TimeUnit.SECONDS,
					new ArrayBlockingQueue<>(20), new ThreadPoolExecutor.CallerRunsPolicy());

			ParametryWatku p = new ParametryWatku();
			p.setA(1); // numer w¹tku
			p.setConn(jmsAd.getConnection()); // po³aczenie z JMS MQ
			p.setTryb(tryb); // (JDBC/REST)
			p.setZapSQL(JTFZapytanieSQL.getText()); // Zapytanie SQL
			p.setIloscPowtorzen(Integer.parseInt(JTFIloscPowtorzen.getText()));
			p.setIloscWatkow(Integer.parseInt(JTFIloscWatkow.getText()));
			p.setIloscWszystkichWatkow(Integer.parseInt(JTFIloscWszystkichWatkow.getText()));
			podzialKola = Integer.parseInt(JTFIloscWszystkichWatkow.getText());
			podzialKola = (podzialKola > MAX_PODZIAL_KOLA) ? MAX_PODZIAL_KOLA : podzialKola; // ogranicza podzia³ ko³a
			p.setPodzialKola(podzialKola);
			p.setModelListyTelefonow(modelListyTelefonow);

			int i = 1;
			List<Future<String>> listaWatkow = new ArrayList<>();
			while (i <= p.getIloscWszystkichWatkow()) {
				p.setA(i);
				final Future<String> Fi = nowyEgzekutor.submit(new Watek(p));
				listaWatkow.add(Fi);
				i += 1;
			}

			WynikWatku wynikW;
			
			List<Integer> iloscBlednychKomW = new ArrayList<>();
			List<Integer> iloscKomW = new ArrayList<>();
			List<Long> minCzasW = new ArrayList<>();
			List<Long> maxCzasW = new ArrayList<>();
			List<Long> medCzasW = new ArrayList<>();
			List<Double> sreCzasW = new ArrayList<>();

			String s;
			for (Future<String> w : listaWatkow) { // pêtla oczekiwanie na zakoñczenie watkow
				try {
					s = w.get();
					// System.out.println("Otrzymano: "+s);
					wynikW = new WynikWatku(s);
					dodajWynikWatkuDoTablicy(modelTablicy, wynikW);
					iloscBlednychKomW.add(wynikW.getIloscBlednychKom());
					iloscKomW.add(wynikW.getIloscKom());
					minCzasW.add(wynikW.getMinCzas());
					maxCzasW.add(wynikW.getMaxCzas());
					medCzasW.add(wynikW.getMedCzas());
					sreCzasW.add(wynikW.getSreCzas());
					//System.out.println("Zakoñczono watek: " + wynikW.getA());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Vector suma = new Vector();
			suma.add("");
			suma.add(Collections.min(iloscBlednychKomW)+"..."+Collections.max(iloscBlednychKomW));
			suma.add(Collections.min(iloscKomW)+"..."+Collections.max(iloscKomW));
			suma.add(Collections.min(minCzasW)+"..."+Collections.max(minCzasW));
			suma.add(Collections.min(maxCzasW)+"..."+Collections.max(maxCzasW));
			suma.add(Collections.min(medCzasW)+"..."+Collections.max(medCzasW));
			suma.add(Collections.min(sreCzasW)+"..."+Collections.max(sreCzasW));
			modelTablicy.addRow(suma);
			
			
			//JTWynikiWatkow.setVisible(true);
			oknoGlowne.frame.revalidate();
			oknoGlowne.frame.repaint();
			oknoGlowne.frame.pack();

			if (!jmsAd.zakonczPolaczenie().equals("ok")) {
				System.out.println("Nie mo¿na zakoñczyæ po³aczenia JMS");
				System.exit(1);
			} else {
				System.out.println("Zakoñczono po³aczenie z " + jmsAd.getURL());
			}

			JBUruchomTest.setEnabled(true); // wy³¹czenie powtórnego uruchomienia w¹tków
		}
	}

	public Ramka() {
		frame = new JFrame();
		frame.setBounds(100, 100, 680, 511);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel JLiloscWatkow = new JLabel("Ilo\u015B\u0107 jednoczesnych w\u0105tk\u00F3w:");

		JTFIloscWatkow = new JTextField("3");
		JTFIloscWatkow.setToolTipText("Nie wi\u0119cej ni\u017C " + MAX_ILOSC_WATKOW);
		JTFIloscWatkow.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (!czyLiczbaMiesciWZakresie(0, MAX_ILOSC_POWTORZEN,
						Integer.parseInt(((JTextField) arg0.getSource()).getText()))) {
					((JTextField) arg0.getSource()).requestFocus();
				} else if (Integer.parseInt(JTFIloscWszystkichWatkow.getText()) < Integer
						.parseInt(((JTextField) arg0.getSource()).getText())) {
					JTFIloscWszystkichWatkow.setText(((JTextField) arg0.getSource()).getText());
				}
			}
		});
		JTFIloscWatkow.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
					e.consume(); // blokowanie wkopiowywania
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (!czyJestLiczba((int) e.getKeyChar()))
					e.consume();
			}
		});
		JTFIloscWatkow.setToolTipText("Nie wi\u0119cej ni\u017C 30");
		JTFIloscWatkow.setColumns(10);

		JLabel JLIloscPowtorzen = new JLabel("Ilo\u015B\u0107 powt\u00F3rze\u0144:");

		JTFIloscPowtorzen = new JTextField("20");
		JTFIloscPowtorzen.setToolTipText("Nie wi\u0119cej ni\u017C " + MAX_ILOSC_POWTORZEN);
		JTFIloscPowtorzen.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (!czyLiczbaMiesciWZakresie(0, MAX_ILOSC_POWTORZEN,
						Integer.parseInt(((JTextField) arg0.getSource()).getText())))
					((JTextField) arg0.getSource()).requestFocus();
			}
		});
		JTFIloscPowtorzen.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
					e.consume(); // blokowanie wkopiowywania
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (!czyJestLiczba((int) e.getKeyChar()))
					e.consume();
			}
		});
		JTFIloscPowtorzen.setColumns(10);

		JLabel lblTelefonyTesowe = new JLabel("Telefony tesowe:");

		JRadioButton JRBJDBC = new JRadioButton("JDBC");
		JRBJDBC.setSelected(true);
		JRBJDBC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tryb = "JDBC";
				JTFZapytanieSQL.setEnabled(true);
			}
		});
		buttonGroup.add(JRBJDBC);

		JRadioButton JRBREST = new JRadioButton("REST");
		JRBREST.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tryb = "REST";
				JTFZapytanieSQL.setEnabled(false);
			}
		});
		buttonGroup.add(JRBREST);

		JBUruchomTest = new JButton("Uruchom test");
		JBUruchomTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				czyUruchomicWatki = true;
			}
		});

		JTable JTWW = new JTable(modelTablicy);
		JScrollPane JTWynikiWatkow = new JScrollPane(JTWW);

		JLabel lblPodajZapytanieSql = new JLabel("Podaj zapytanie SQL:");

		JTFZapytanieSQL = new JTextField(DOMYSLNE_ZAP_SQL);
		JTFZapytanieSQL.setColumns(10);

		JLabel lblIloWszystkichWtkw = new JLabel("Ilo\u015B\u0107 wszystkich w\u0105tk\u00F3w:");

		JTFIloscWszystkichWatkow = new JTextField();
		JTFIloscWszystkichWatkow.setText("6");
		JTFIloscWszystkichWatkow.setColumns(10);
		JTFIloscWszystkichWatkow.setToolTipText(
				"Nie mniej ni¿ iloœæ jednoczesnych w¹tków i nie wi\u0119cej ni\u017C " + MAX_ILOSC_WATKOW);
		JTFIloscWszystkichWatkow.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (!czyLiczbaMiesciWZakresie(Integer.parseInt(JTFIloscWatkow.getText()), MAX_ILOSC_POWTORZEN,
						Integer.parseInt(((JTextField) arg0.getSource()).getText())))
					((JTextField) arg0.getSource()).requestFocus();
			}
		});
		JTFIloscWszystkichWatkow.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
					e.consume(); // blokowanie wkopiowywania
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (!czyJestLiczba((int) e.getKeyChar()))
					e.consume();
			}
		});
		JButton btnWyjcie = new JButton("Wyj\u015Bcie");
		btnWyjcie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JTFTelefonyDodaj = new JTextField();
		JTFTelefonyDodaj.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					boolean czyDubel = false;
					for (int i = 0; i < modelListyTelefonow.size(); i++) {
						if (((JTextField) arg0.getSource()).getText().equals(modelListyTelefonow.get(i))) {
							czyDubel = true;
						}
					}
					if (!czyDubel) {
						modelListyTelefonow.addElement(((JTextField) arg0.getSource()).getText());
						JLLT.setModel(modelListyTelefonow);
					}
				}
			}
		});
		JTFTelefonyDodaj.setColumns(10);

		JButton JbTelefonyUsun = new JButton("Usu\u0144");
		JbTelefonyUsun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!JLLT.isSelectionEmpty()) {
					int sL[] = JLLT.getSelectedIndices();
					for (int i = 0; i < sL.length; i++) {
						modelListyTelefonow.remove(sL[i]);
					}
				}
			}
		});

		JLLT = new JList<String>(modelListyTelefonow);
		JLLT.setVisibleRowCount(5);
		JScrollPane JLTelefony = new JScrollPane(JLLT);

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(JTWynikiWatkow, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(JBUruchomTest)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(btnWyjcie, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE))
								.addComponent(JTFZapytanieSQL)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(JLiloscWatkow)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(JTFIloscWatkow, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
										.addGap(213))
									.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
											.addGroup(groupLayout.createSequentialGroup()
												.addComponent(lblIloWszystkichWtkw)
												.addGap(18)
												.addComponent(JTFIloscWszystkichWatkow, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE))
											.addGroup(groupLayout.createSequentialGroup()
												.addComponent(JLIloscPowtorzen)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(JTFIloscPowtorzen, 151, 151, 151)))
										.addGap(88)
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
											.addComponent(lblTelefonyTesowe)
											.addComponent(JbTelefonyUsun)))
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(lblPodajZapytanieSql)
										.addGap(78)
										.addComponent(JRBREST)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(JRBJDBC))))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(JTFTelefonyDodaj, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
								.addComponent(JLTelefony, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))))
					.addGap(29))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(16)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(24)
							.addComponent(JTFTelefonyDodaj, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(JLTelefony, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTelefonyTesowe)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(JbTelefonyUsun)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblPodajZapytanieSql)
										.addComponent(JRBREST)
										.addComponent(JRBJDBC))
									.addGap(8))
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(JLiloscWatkow)
										.addComponent(JTFIloscWatkow, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(JTFIloscWszystkichWatkow, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblIloWszystkichWtkw))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(JLIloscPowtorzen)
										.addComponent(JTFIloscPowtorzen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(37)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(JTFZapytanieSQL, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(JBUruchomTest, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnWyjcie, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addComponent(JTWynikiWatkow, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}

	private boolean czyJestLiczba(int znak) {
		if (znak >= KeyEvent.VK_0 && znak <= KeyEvent.VK_9) {
			return true;
		} else
			return false;
	}

	private boolean czyLiczbaMiesciWZakresie(int min, int max, Integer liczba) {
		if (min <= liczba && liczba <= max) {
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Licba nie mieœci siê w zakresie (1:" + max + ">... podaj jescze raz");
			return false;
		}
	}
	
	private static void dodajWynikWatkuDoTablicy(DefaultTableModel m, WynikWatku w) {
		
		Vector v = new Vector();
		v.add(w.getA());
		v.add(w.getIloscBlednychKom());
		v.add(w.getIloscKom());
		v.add(w.getMinCzas());
		v.add(w.getMaxCzas());
		v.add(w.getMedCzas());
		v.add(w.getSreCzas());
	
		m.addRow(v);	
	}
}
