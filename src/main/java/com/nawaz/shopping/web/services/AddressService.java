package com.nawaz.shopping.web.services;


import com.nawaz.shopping.web.Entity.Address;
import com.nawaz.shopping.web.payloads.AddressDTO;

import java.util.List;

public interface AddressService {
	
	AddressDTO createAddress(AddressDTO addressDTO);
	
	List<AddressDTO> getAddresses();
	
	AddressDTO getAddress(Long addressId);
	
	AddressDTO updateAddress(Long addressId, Address address);
	
	String deleteAddress(Long addressId);
}
