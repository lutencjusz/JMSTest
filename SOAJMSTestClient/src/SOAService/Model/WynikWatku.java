package SOAService.Model;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "XMLWynikWatku")
public class WynikWatku {

	private Integer a;
	private Integer iloscBlednychKom;
	private Integer iloscKom;
	private Long minCzas;
	private Long maxCzas;
	private Double sreCzas;
	private Long medCzas;
	
	@XmlElement (name="IloscBlednychKom")
	public Integer getIloscBlednychKom() {
		return iloscBlednychKom;
	}
	public void setIloscBlednychKom(Integer iloscBlednychKom) {
		this.iloscBlednychKom = iloscBlednychKom;
	}
	
	@XmlElement (name="IloscKom")
	public Integer getIloscKom() {
		return iloscKom;
	}
	public void setIloscKom(Integer iloscKom) {
		this.iloscKom = iloscKom;
	}
	
	@XmlElement (name="MinCzas")
	public Long getMinCzas() {
		return minCzas;
	}
	public void setMinCzas(Long minCzas) {
		this.minCzas = minCzas;
	}
	
	@XmlElement (name="MaxCzas")
	public Long getMaxCzas() {
		return maxCzas;
	}
	public void setMaxCzas(Long maxCzas) {
		this.maxCzas = maxCzas;
	}
	
	@XmlElement (name="SreCzas")
	public Double getSreCzas() {
		return sreCzas;
	}
	public void setSreCzas(Double sreCzas) {
		this.sreCzas = sreCzas;
	}
	
	@XmlElement (name="MedCzas")
	public Long getMedCzas() {
		return medCzas;
	}
	public void setMedCzas(Long medCzas) {
		this.medCzas = medCzas;
	}
	
	@XmlElement (name="A")
	public Integer getA() {
		return a;
	}
	public void setA(Integer a) {
		this.a = a;
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
	
	public WynikWatku(String kom) throws Exception {

		java.io.StringReader sr = new StringReader(kom);
		JAXBContext jaxbContext = JAXBContext.newInstance(WynikWatku.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		WynikWatku soaKom = (WynikWatku) jaxbUnmarshaller.unmarshal(sr);

		this.setA(soaKom.a);
		this.setIloscBlednychKom(soaKom.iloscBlednychKom);
		this.setIloscKom(soaKom.iloscKom);
		this.setMaxCzas(soaKom.maxCzas);
		this.setMedCzas(soaKom.medCzas);;
		this.setMinCzas(soaKom.minCzas);
		this.setSreCzas(soaKom.sreCzas);
	}
	
	public WynikWatku() {
		
	}
	
}
