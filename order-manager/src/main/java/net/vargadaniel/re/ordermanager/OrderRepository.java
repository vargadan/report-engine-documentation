package net.vargadaniel.re.ordermanager;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import net.vargadaniel.re.ordermanager.model.Order;

@Component
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
}
