package com.blackcowmoo.moomark.auth.repository;

import com.blackcowmoo.moomark.auth.model.entity.PassportKey;
import org.springframework.data.repository.CrudRepository;

public interface PassportKeyRedisRepository extends CrudRepository<PassportKey, String> {

}
