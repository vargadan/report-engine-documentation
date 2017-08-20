package net.vargadaniel.re.ordermanager;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import net.vargadaniel.re.ordermanager.model.Product;

@Component
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
	
	Product findByName(@Param("name") String name);

}