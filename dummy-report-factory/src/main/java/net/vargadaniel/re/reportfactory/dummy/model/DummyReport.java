package net.vargadaniel.re.reportfactory.dummy.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.SortedSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DummyReport extends TransactionStatistics {
	
	public class DailiyDummyReportStats extends TransactionStatistics implements Comparable<DailiyDummyReportStats> {
		
		private LocalDate date;
		
		@XmlElement
		public LocalDate getDate() {
			return date;
		}

		public void setDate(LocalDate date) {
			this.date = date;
		}

		@Override
		public int compareTo(DailiyDummyReportStats o) {
			return this.date.compareTo(o.date);
		}
		
	}
	
	private String cif;
	
	private LocalDate fromDate;
	
	private LocalDate toDate;
	
	private SortedSet<DailiyDummyReportStats> dailyStats;

	@XmlElement
	public SortedSet<DailiyDummyReportStats> getDailyStats() {
		return dailyStats;
	}

	public void setDailyStats(SortedSet<DailiyDummyReportStats> dailyStats) {
		this.dailyStats = dailyStats;
	}

	@XmlElement
	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	@XmlElement
	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	@XmlElement
	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}	
	

}

abstract class TransactionStatistics {
	
	private BigDecimal Sum;
	
	private BigDecimal Average;
	
	private Long count; 
	
	private BigDecimal min;
	
	private BigDecimal max;

	@XmlElement
	public BigDecimal getSum() {
		return Sum;
	}

	public void setSum(BigDecimal Sum) {
		this.Sum = Sum;
	}

	@XmlElement
	public BigDecimal getAverage() {
		return Average;
	}

	public void setAverage(BigDecimal Average) {
		this.Average = Average;
	}

	@XmlElement
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@XmlElement
	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	@XmlElement
	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}
	
}