package com.blackcowmoo.moomark.auth.repository;

import org.springframework.data.repository.CrudRepository;
import com.blackcowmoo.moomark.auth.model.entity.Token;

public interface TokenRepository extends CrudRepository<Token, String> {

}
