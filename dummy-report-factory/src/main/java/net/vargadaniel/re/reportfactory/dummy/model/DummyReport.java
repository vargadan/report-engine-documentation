package net.vargadaniel.re.reportfactory.dummy.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.SortedSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DummyReport {
	
	public static class DailyStats {
		
		LocalDate date;
		
		BigDecimal dailySum;
		
		BigDecimal dailyAverage;

		@XmlElement
		public LocalDate getDate() {
			return date;
		}

		public void setDate(LocalDate date) {
			this.date = date;
		}

		@XmlElement
		public BigDecimal getDailySum() {
			return dailySum;
		}

		public void setDailySum(BigDecimal dailySum) {
			this.dailySum = dailySum;
		}

		@XmlElement
		public BigDecimal getDailyAverage() {
			return dailyAverage;
		}

		public void setDailyAverage(BigDecimal dailyAverage) {
			this.dailyAverage = dailyAverage;
		}
		
	}
	
	private String cif;
	
	private SortedSet<DailyStats> dailyStats;
	
	private BigDecimal totalSum;
	
	private BigDecimal totalAverage;

	@XmlElement
	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	@XmlElement
	public SortedSet<DailyStats> getDailyStats() {
		return dailyStats;
	}

	public void setDailyStats(SortedSet<DailyStats> dailyStats) {
		this.dailyStats = dailyStats;
	}

	@XmlElement
	public BigDecimal getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(BigDecimal totalSum) {
		this.totalSum = totalSum;
	}

	@XmlElement
	public BigDecimal getTotalAverage() {
		return totalAverage;
	}

	public void setTotalAverage(BigDecimal totalAverage) {
		this.totalAverage = totalAverage;
	}

}
