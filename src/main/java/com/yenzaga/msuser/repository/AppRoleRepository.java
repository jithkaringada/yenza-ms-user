package com.yenzaga.msuser.repository;

import com.yenzaga.msuser.domain.AppRoleMappingDetail;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRoleRepository extends ReactiveMongoRepository<AppRoleMappingDetail, String> {
}
