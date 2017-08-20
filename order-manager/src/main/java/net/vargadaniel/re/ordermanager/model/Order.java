package net.vargadaniel.re.ordermanager.model;

import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.SortNatural;

@Entity(name="report_order")
public class Order {
	
	public Order() {
	}

	public Order(Long timestamp, Product product) {
		this.timestamp = timestamp;
		this.product = product;
	}

	Long id;
	
	Long timestamp;
	
	Product product;
	
	Map<String, String> properties;

	private SortedSet<OrderStatus> statuses = new TreeSet<>();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@NotNull
	@Column(updatable=false)
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@ManyToOne(fetch=FetchType.EAGER, optional=false, cascade=CascadeType.REFRESH)
	@NotNull
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	@ElementCollection(fetch=FetchType.EAGER)
	@MapKeyColumn(name="name", nullable=false)
	@Column(name="type")
	@CollectionTable(name="order_properties", joinColumns=@JoinColumn(name="order_id"))
	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

	@OneToMany(fetch=FetchType.EAGER, mappedBy="order", cascade = CascadeType.ALL)
	@SortNatural
	@OrderBy("timestamp DESC")
	public SortedSet<OrderStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(SortedSet<OrderStatus> statuses) {
		this.statuses = statuses;
	}
}
