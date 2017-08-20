package net.vargadaniel.re.reportrepository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;

import net.vargadaniel.re.reportrepository.model.Report;

@Component
@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository extends PagingAndSortingRepository<Report, Long> {
	
	List<Report> findByOrderId(Long orderId);

	@Override
	@RestResource(exported=false)
	void delete(Long id);
	
	@Override
	@RestResource(exported=false)
	void delete(Report report);
	
	@SuppressWarnings("unchecked")
	@Override
	@RestResource(exported=false)
	Report save(Report report);

}