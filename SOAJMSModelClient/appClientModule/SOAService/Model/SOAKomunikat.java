package SOAService.Model;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "XMLKomunikat")
public class SOAKomunikat {

	private Long Id;
	private String Kolejka;
	private String MSISDN;
	private String Komunikat;
	private String Tryb;
	
	@XmlElement (name="Id")
	public Long getId() {
		return Id;
	}
	public void setId(Long Id) {
		this.Id = Id;
	}
	
	@XmlElement (name="Kolejka")
	public String getKolejka() {
		return Kolejka;
	}
	public void setKolejka(String Kolejka) {
		this.Kolejka = Kolejka;
	}

	@XmlElement (name="MSISDN")
	public String getMSISDN() {
		return MSISDN;
	}
	public void setMSISDN(String MSISDN) {
		this.MSISDN = MSISDN;
	}
	
	@XmlElement (name="Komunikat")
	public String getKomunikat() {
		return Komunikat;
	}
	public void setKomunikat(String Komunikat) {
		this.Komunikat = Komunikat;
	}
	
	@XmlElement (name="Tryb")
	public String getTryb() {
		return Tryb;
	}
	public void setTryb(String Tryb) {
		this.Tryb = Tryb;
	}

	public String parsowanieClassnaXML() throws Exception {

		java.io.StringWriter sw = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(this, sw);
		} catch (Exception e) {
			System.out.println("Coœ posz³o nie tak w parsowanieClassnaXML");
			e.printStackTrace();
		}
		return sw.toString();
	}

	public SOAKomunikat(String kom) throws Exception {

		//super();
		java.io.StringReader sr = new StringReader(kom);
		JAXBContext jaxbContext = JAXBContext.newInstance(SOAKomunikat.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		SOAKomunikat soaKom = (SOAKomunikat) jaxbUnmarshaller.unmarshal(sr);

		this.setId(soaKom.Id);
		this.setKolejka(soaKom.Kolejka);
		this.setMSISDN(soaKom.MSISDN);
		this.setKomunikat(soaKom.Komunikat);
		this.setTryb(soaKom.Tryb);
	}

	public SOAKomunikat() {
		super();
	}
}
