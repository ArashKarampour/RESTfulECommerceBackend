package com.SpringBootRESTAPIs.store.repositories;

import com.SpringBootRESTAPIs.store.entities.Profile;
import org.springframework.data.repository.CrudRepository;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}