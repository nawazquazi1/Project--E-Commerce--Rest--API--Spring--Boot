package com.nawaz.shopping.web.repositories;


import com.nawaz.shopping.web.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {

	Address findByCountryAndStateAndCityAndPinCodeAndStreetAndBuildingName(String country, String state, String city,
																		   String pinCode, String street, String buildingName);

}
