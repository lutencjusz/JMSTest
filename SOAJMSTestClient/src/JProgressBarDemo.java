import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class JProgressBarDemo extends JDialog {

	private static final long serialVersionUID = 1L;
	int a; // numer w¹tku
	JPanel panelPaskaPostepu;
	JProgressBar pasekPostepu;
	JTextArea textPaskaPostepu;
	JScrollPane JSCPtextPaskaPostepu;
	Border ramkaPaskaPostepu;
	JButton stop;
	JButton szczegoly;
	Boolean czyWcisnietyStop = false;
	Boolean czySzczegoly = false;
	private static int OPOZNIENIE_WYLACZENIA = 5; // w sek.

	public JProgressBarDemo(int a, int min, int max, int podzialKola) {
		this.a = a;
		pasekPostepu = new JProgressBar(min, max);
		ramkaPaskaPostepu = BorderFactory.createTitledBorder("Przetwarza " + a + " watek...");
		pasekPostepu.setBorder(ramkaPaskaPostepu);
		// pasekPostepu.setSize(200, 50);
		stop = new JButton("STOP");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				czyWcisnietyStop = true;
			}
		});
		szczegoly = new JButton("Poka¿ szczegó³y");
		szczegoly.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				czySzczegoly = (czySzczegoly) ? false : true;
				if (czySzczegoly) {
					panelPaskaPostepu.add(JSCPtextPaskaPostepu, BorderLayout.CENTER);
					szczegoly.setText("Usuñ szczegó³y");
					setSize(400, 300);
					revalidate();
					repaint();
				} else {
					panelPaskaPostepu.remove(JSCPtextPaskaPostepu);
					szczegoly.setText("Poka¿ szczegó³y");
					// setSize(400, 91);
					revalidate();
					repaint();
					pack();
				}
			}
		});

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// setSize(400, 91);
		panelPaskaPostepu = new JPanel();
		textPaskaPostepu = new JTextArea();
		textPaskaPostepu.setSize(400, 100);
		textPaskaPostepu.setLineWrap(true);
		JSCPtextPaskaPostepu = new JScrollPane(textPaskaPostepu);
		JSCPtextPaskaPostepu.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
		pasekPostepu.setValue(0);
		pasekPostepu.setStringPainted(true);

		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.add(pasekPostepu);
		p.add(stop);
		p.add(szczegoly);

		panelPaskaPostepu.setLayout(new BorderLayout());
		panelPaskaPostepu.add(p, BorderLayout.SOUTH);

		setContentPane(panelPaskaPostepu);
		setType(Type.UTILITY);
		setLocation(this.ustawLuk(a, podzialKola));
		pack();
		setVisible(true);
	}

	public Point ustawLuk(int m, int p) {
		int r = 300;
		int x0 = 600;
		int y0 = 400;
		double x = x0 + r * Math.cos(Math.toRadians((360 / p) * m));
		double y = y0 + r * Math.sin(Math.toRadians((360 / p) * m));
		return new Point((int) x, (int) y);
	}

	public boolean czyStop() {
		if (czyWcisnietyStop) {
			return true;
		} else
			return false;
	}

	public void ustawPasekPostepu(int a) {
		pasekPostepu.setValue(a);
	}

	public int pobierzPasekPostepu() {
		return pasekPostepu.getValue();
	}

	public void wstawText(String s) {
		textPaskaPostepu.append(s);
	}

	public void wylacz() {
		stop.setEnabled(false);
		int licznik_sek = OPOZNIENIE_WYLACZENIA;
		while (licznik_sek-- > 1) {
			pasekPostepu.setBorder(BorderFactory.createTitledBorder(a + " watek oczekuje: " + licznik_sek + " sek."));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.setVisible(false);
	}

}