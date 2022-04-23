package ait.a00231910.microservices.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ait.a00231910.microservices.dto.Seller;


@Repository
public interface SellerRepository extends CrudRepository<Seller, Long>{
	Optional<Seller> findByName(String name);

}
