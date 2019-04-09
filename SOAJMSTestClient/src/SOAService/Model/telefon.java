package SOAService.Model;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "telefony")
public class telefon {

	@XmlElement
	private int Id;
	@XmlElement
	private String MSISDN;
	@XmlElement
	private String Komunikat;

	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	
	public String getMSISDN() {
		return MSISDN;
	}
	public void setMSISDN(String mSISDN) {
		MSISDN = mSISDN;
	}
	
	public String getKomunikat() {
		return Komunikat;
	}
	public void setKomunikat(String komunikat) {
		Komunikat = komunikat;
	}
    public String parsowanieClassnaXML() throws Exception {
        
        java.io.StringWriter sw = new StringWriter();
        JAXBContext jaxbContext = JAXBContext.newInstance(telefon.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(this, sw);
        
        return sw.toString();
    }
    
    public telefon(String kom) throws Exception {
        
        super();
        java.io.StringReader sr = new StringReader(kom);
        JAXBContext jaxbContext = JAXBContext.newInstance(telefon.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        telefon tel = (telefon) jaxbUnmarshaller.unmarshal(sr);
        
        this.setId(tel.Id);
        //this.setKolejka(soaKom.Kolejka);
        this.setMSISDN(tel.MSISDN);
        this.setKomunikat(tel.Komunikat);
        //this.setTryb(soaKom.Tryb);
    }
    
    public telefon() {
        super();
    }
}
