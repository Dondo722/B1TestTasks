package webapp.task2.repository;

import org.springframework.data.repository.CrudRepository;
import webapp.task2.model.AccountSheet;
import webapp.task2.model.BankBalanceStatement;

import java.util.List;

public interface AccountSheetRepository extends CrudRepository<AccountSheet,Long> {

    List<AccountSheet> findByBalanceStatement(BankBalanceStatement statement);
}
