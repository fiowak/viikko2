package poikkeus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import henkilo.Henkilo;

public class HenkiloDAO {
	
	/**
	 * Konstruktori
	 * lataa tietokantayhteyden ajurin
	 */
	public HenkiloDAO() throws DAOPoikkeus {
		try {
			Class.forName(DBConnectionProperties.getInstance().getProperty("driver")).newInstance();
		} catch(Exception e) {
			throw new DAOPoikkeus("Tietokannan ajuria ei kyetty lataamaan.", e);
		}
	}
	
	/**
	 * Avaa tietokantayhteyden
	 * @return avatun tietokantayhteyden
	 * @throws Exception Mik�li yhteyden avaaminen ei onnistu
	 */
	private Connection avaaYhteys() throws DAOPoikkeus {
		
		try {
			return DriverManager.getConnection(
					DBConnectionProperties.getInstance().getProperty("url"), 
					DBConnectionProperties.getInstance().getProperty("username"),
					DBConnectionProperties.getInstance().getProperty("password"));
		} catch (Exception e) {
			throw new DAOPoikkeus("Tietokantayhteyden avaaminen ep�onnistui", e);
		}
	}
	
	/**
	 * Sulkee tietokantayhteyden
	 * @param yhteys Suljettava yhteys
	 */
	private void suljeYhteys(Connection yhteys) throws DAOPoikkeus {
		try {
			if (yhteys != null && !yhteys.isClosed())
				yhteys.close();
		} catch(Exception e) {
			throw new DAOPoikkeus("Tietokantayhteys ei jostain syyst� suostu menem��n kiinni.", e);
		}
	}
	
	/**
	 * Hakee kaikki henkil�t kannasta
	 * @return listallinen henkil�it�
	 */
	public List<Henkilo> haeKaikki() throws DAOPoikkeus{		
		
		ArrayList<Henkilo> henkilot = new ArrayList<Henkilo>();
		
		//avataan yhteys
		Connection yhteys = avaaYhteys();
		
		try {
			
			//suoritetaan haku
			String sql = "select id, etunimi, sukunimi from henkilo";
			Statement haku = yhteys.createStatement();
			ResultSet tulokset = haku.executeQuery(sql);
			
			//k�yd��n hakutulokset l�pi
			while(tulokset.next()) {
				int id = tulokset.getInt("id");
				String etunimi = tulokset.getString("etunimi");
				String sukunimi = tulokset.getString("sukunimi");
				
				//lis�t��n henkil� listaan
				Henkilo h = new Henkilo(id, etunimi, sukunimi);
				henkilot.add(h);
			}
			
		} catch(Exception e) {
			//JOTAIN VIRHETT� TAPAHTUI
			throw new DAOPoikkeus("Tietokantahaku aiheutti virheen", e);
		}finally {
			//LOPULTA AINA SULJETAAN YHTEYS
			suljeYhteys(yhteys);
		}
		
		System.out.println("HAETTIIN TIETOKANNASTA HENKIL�T: " +henkilot.toString());
		
		return henkilot;
	}

	
	/**
	 * Lis�� henkil�n tietokantaan
	 * @param h Lis�tt�v�n henkil�n tiedot
	 */
	public void lisaa(Henkilo h) throws DAOPoikkeus{
			
		//avataan yhteys
		Connection yhteys = avaaYhteys();
		
		try {
			
			//suoritetaan haku
			
			//alustetaan sql-lause
			String sql = "insert into henkilo(etunimi, sukunimi) values(?,?)";
			PreparedStatement lause = yhteys.prepareStatement(sql);
			
			//t�ytet��n puuttuvat tiedot
			lause.setString(1, h.getEtunimi());
			lause.setString(2, h.getSukunimi());
			
			//suoritetaan lause
			lause.executeUpdate();
			System.out.println("LIS�TTIIN HENKIL� TIETOKANTAAN: "+h);
		} catch(Exception e) {
			//JOTAIN VIRHETT� TAPAHTUI
			throw new DAOPoikkeus("Henkil�n lis��misyritys aiheutti virheen", e);
		}finally {
			//LOPULTA AINA SULJETAAN YHTEYS
			suljeYhteys(yhteys);
		}

	}

}
