package org.wyf.game.login.repository;

import org.wyf.game.login.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
