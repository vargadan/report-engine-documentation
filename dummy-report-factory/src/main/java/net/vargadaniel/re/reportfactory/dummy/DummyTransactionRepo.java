package net.vargadaniel.re.reportfactory.dummy;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import net.vargadaniel.re.reportfactory.dummy.model.DummyTransaction;

@Component
public interface DummyTransactionRepo extends CrudRepository<DummyTransaction, Long> {

	@Query("from DummyTransaction dt where dt.cif=:cif and dt.date between :fromDate and :toDate")
	public Iterable<DummyTransaction> findByCifAndDate(@Param("cif") String cif, @Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);
	
	@Query("from DummyTransaction dt where dt.cif=:cif and dt.date = :date")
	public Iterable<DummyTransaction> findByCifAndDate(@Param("cif") String cif, @Param("date") LocalDate date);

}
