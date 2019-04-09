package SOAService.Model;

import javax.jms.Connection;
import javax.swing.DefaultListModel;

public class ParametryWatku {

	public int a; // numer w¹tku
	private Connection conn; // po³aczenie z JMS MQ
	private String tryb = "JDBC"; // (JDBC/REST)
	public String zapSQL = "select * from telefony"; // Zapytanie SQL
	public int iloscPowtorzen = 20;
	private int iloscWatkow = 3;
	private int iloscWszystkichWatkow=6;
	private int podzialKola;
	private DefaultListModel<String> modelListyTelefonow;
	
	
	public int getIloscPowtorzen() {
		return iloscPowtorzen;
	}
	public void setIloscPowtorzen(int iloscPowtorzen) {
		this.iloscPowtorzen = iloscPowtorzen;
	}
	public int getIloscWatkow() {
		return iloscWatkow;
	}
	public void setIloscWatkow(int iloscWatkow) {
		this.iloscWatkow = iloscWatkow;
	}
	public int getIloscWszystkichWatkow() {
		return iloscWszystkichWatkow;
	}
	public void setIloscWszystkichWatkow(int iloscWszystkichWatkow) {
		this.iloscWszystkichWatkow = iloscWszystkichWatkow;
	}

	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public String getTryb() {
		return tryb;
	}
	public void setTryb(String tryb) {
		this.tryb = tryb;
	}
	public String getZapSQL() {
		return zapSQL;
	}
	public void setZapSQL(String zapSQL) {
		this.zapSQL = zapSQL;
	}
	public int getPodzialKola() {
		return podzialKola;
	}
	public void setPodzialKola(int podzialKola) {
		this.podzialKola = podzialKola;
	}
	public DefaultListModel<String> getModelListyTelefonow() {
		return modelListyTelefonow;
	}
	public void setModelListyTelefonow(DefaultListModel<String> modelListyTelefonow) {
		this.modelListyTelefonow = modelListyTelefonow;
	}
	
}
