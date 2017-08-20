package net.vargadaniel.re.reportfactory.dummy.model;

import java.time.LocalDate;
import java.util.Map;

import net.vargadaniel.re.reportfactory.dummy.DummyReportMeta;

public class DummyReportOrder {
	
	private Long id;
	
	private Map<String, String> properties;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public String getCif() {
		return properties.get(DummyReportMeta.RPOP_CIF);
	}
	
	public LocalDate getFromDate() {
		String sDate = properties.get(DummyReportMeta.PROP_FROM_DATE);
		return LocalDate.parse(sDate);
	}

	public LocalDate getToDate() {
		String sDate = properties.get(DummyReportMeta.PROP_TO_DATE);
		return LocalDate.parse(sDate);
		
	}
	

}