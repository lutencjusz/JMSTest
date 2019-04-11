package SOAService.DAOAdapter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import SOAService.DAOAdapter.DOAParsowanie;
import SOAService.Model.telefon;

public class SQLAdapter {

	private Statement mySQLStatement;

	public String DAOMySQLUtworzPolaczenie() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();  
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/serwisoli", "lutencjusz",
					"Aleks07$");
			mySQLStatement = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			return "Błąd nawiązania połączenia z bazą danych";
		}
		return "ok";
	}

	@SuppressWarnings("unused")
	public String DAOMySQLKomunikatyTelefonyString(String zapytanieSQL,
			String tryb) {

		String odp = "";
		final DOAParsowanie DAOParsowanie = new DOAParsowanie();

		try {
			ResultSet rs = mySQLStatement.executeQuery(zapytanieSQL);

			List<telefon> telefony = new ArrayList<>();
			while (rs.next()) {
				telefon DAOTel = new telefon();
				DAOTel.setMSISDN(rs.getString("MSISDN"));
				DAOTel.setKomunikat(rs.getString("Komunikat"));
				DAOTel.setId(rs.getInt("ID"));
				if (tryb.equalsIgnoreCase("XML")) {

					String linia = DAOTel.parsowanieClassnaXML();
					odp += linia;
				} else {
					telefony.add(DAOTel);
				}
			}
			if (tryb.equalsIgnoreCase("JSON")) {
				odp = DOAParsowanie.parsowanieDAOListNaJSON(telefony);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Błędny odczyt z bazy danych";
		}
		return odp;
	}

	public List<telefon> DAOMySQLKomunikatyWszystkieTelefony() {

		List<telefon> telefony = new ArrayList<>();

		try {
			ResultSet rs = mySQLStatement.executeQuery("select * from telefony");

			while (rs.next()) {
				telefon DAOTel = new telefon();
				DAOTel.setMSISDN(rs.getString("MSISDN"));
				DAOTel.setKomunikat(rs.getString("Komunikat"));
				DAOTel.setId(rs.getInt("ID"));
				telefony.add(DAOTel);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return telefony;
	}

	public telefon DAOMySQLKomunikatTelefon(int Id) {

		telefon DAOTel = new telefon();
		try {
			ResultSet rs = mySQLStatement
					.executeQuery("select * from telefony where Id='" + Id
							+ "'");
			DAOTel.setMSISDN(rs.getString("MSISDN"));
			DAOTel.setKomunikat(rs.getString("Komunikat"));
			DAOTel.setId(rs.getInt("ID"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return DAOTel;
	}

	public String DAOMySQLZamknijPolaczenie() {
		try {
			mySQLStatement.closeOnCompletion();
		} catch (Exception e) {
			return "Błąd zamknięcia połączenia z bazą danych";
		}
		return "ok";
	}
}
