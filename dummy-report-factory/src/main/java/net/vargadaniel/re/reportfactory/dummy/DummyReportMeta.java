package net.vargadaniel.re.reportfactory.dummy;

import java.util.HashMap;
import java.util.Map;

public class DummyReportMeta {
	
	public final static String NAME = "dummyReport";
	
	final Map<String, String> properties = new HashMap<>();
	
	public final static String RPOP_CIF = "cif";
	
	public final static String PROP_FROM_DATE = "from";
	
	public final static String PROP_TO_DATE = "to";
	
	public DummyReportMeta() {
		properties.put(RPOP_CIF, "string");
		properties.put(PROP_FROM_DATE, "date");
		properties.put(PROP_TO_DATE, "date");
	}

	public String getName() {
		return NAME;
	}

	public Map<String, String> getProperties() {
		return properties;
	}


}
