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

	@XmlElement
	private Long Id;
	@XmlElement
	private String Kolejka;
	@XmlElement
	private String MSISDN;
	@XmlElement
	private String Komunikat;
	@XmlElement
	private String Tryb;


	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}

	
	public String getKolejka() {
		return Kolejka;
	}

	public void setKolejka(String Kolejka) {
		this.Kolejka = Kolejka;
	}

	public String getMSISDN() {
		return MSISDN;
	}

	public void setMSISDN(String MSISDN) {
		this.MSISDN = MSISDN;
	}

	@XmlElement(name = "Komunikat")
	public String getKomunikat() {
		return Komunikat;
	}

	public void setKomunikat(String Komunikat) {
		this.Komunikat = Komunikat;
	}

	public String getTryb() {
		return Tryb;
	}

	public void setTryb(String Tryb) {
		this.Tryb = Tryb;
	}

	public String parsowanieClassnaXML() throws Exception {

		java.io.StringWriter sw = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(SOAKomunikat.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(this, sw);

		return sw.toString();
	}

	public SOAKomunikat(String kom) throws Exception {

		super();
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
