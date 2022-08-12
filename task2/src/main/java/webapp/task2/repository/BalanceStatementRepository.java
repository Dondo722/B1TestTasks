package webapp.task2.repository;

import org.springframework.data.repository.CrudRepository;
import webapp.task2.model.BankBalanceStatement;

public interface BalanceStatementRepository extends CrudRepository<BankBalanceStatement,Long> {
}
