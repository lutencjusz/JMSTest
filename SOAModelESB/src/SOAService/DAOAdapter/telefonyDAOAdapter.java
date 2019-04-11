package SOAService.DAOAdapter;

import java.util.List;

import SOAService.Model.telefon;

public class telefonyDAOAdapter {

	// SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	// ta instrukcja powoduje błąd w Hibernate

	public telefon pobierzTelefonyID(int Id) {

		telefon tel = null;
		// Session sesja = null;
		SQLAdapter sa = new SQLAdapter();

		if (sa.DAOMySQLUtworzPolaczenie().equals("ok")) {

			tel = sa.DAOMySQLKomunikatTelefon(Id);
		}
		sa.DAOMySQLZamknijPolaczenie();
		return tel;

		/*
		 * testowe dane tel = new telefon(); tel.setId(Id);
		 * tel.setKomunikat("Przykładowy komunikat");
		 * tel.setMSISDN("601135621");
		 */
		/*
		 * Sesja SQL + Hibernate nie działa try { sesja =
		 * sessionFactory.openSession(); sesja.beginTransaction();
		 * System.out.println
		 * ("------------------------Początek transakcji SQL---------"); tel =
		 * (telefon) sesja.createQuery("from telefony where telefony.Id = :Id")
		 * .setParameter("Id", Id).uniqueResult();
		 * sesja.getTransaction().commit();
		 * 
		 * } catch (Exception e) { if (sesja != null) {
		 * sesja.getTransaction().rollback(); } } finally { if (sesja != null) {
		 * sesja.close(); } }
		 */

	}

	public List<telefon> pobierzWszystkieTelefony() {
		List<telefon> telefony = null;
		//Session sesja = null;
		SQLAdapter sa = new SQLAdapter();

		if (sa.DAOMySQLUtworzPolaczenie().equals("ok")) {

			telefony = sa.DAOMySQLKomunikatyWszystkieTelefony();
		}
		sa.DAOMySQLZamknijPolaczenie();
		return telefony;	
		
		/*
		 * Sesja SQL + Hibernate nie działa try { sesja =
		 * sessionFactory.openSession(); sesja.beginTransaction(); //telefony =
		 * sesja.createQuery("from telefony").list();
		 * 
		 * } catch (Exception e) { if (sesja != null) {
		 * sesja.getTransaction().rollback(); } } finally { if (sesja != null) {
		 * sesja.close(); } }
		 */
	}
}
