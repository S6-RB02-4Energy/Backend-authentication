package com.Energy.BasicSpringAPI.repository;


import com.Energy.BasicSpringAPI.entity.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<userEntity, Long> {

}
