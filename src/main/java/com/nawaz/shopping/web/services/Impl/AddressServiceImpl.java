package com.nawaz.shopping.web.services.Impl;

import com.nawaz.shopping.web.Entity.Address;
import com.nawaz.shopping.web.Entity.User;
import com.nawaz.shopping.web.exceptions.APIException;
import com.nawaz.shopping.web.exceptions.ResourceNotFoundException;
import com.nawaz.shopping.web.payloads.AddressDTO;
import com.nawaz.shopping.web.repositories.AddressRepo;
import com.nawaz.shopping.web.repositories.UserRepo;
import com.nawaz.shopping.web.services.AddressService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {


    private AddressRepo addressRepo;
    private UserRepo userRepo;
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {

        String country = addressDTO.getCountry();
        String state = addressDTO.getState();
        String city = addressDTO.getCity();
        String pincode = addressDTO.getPincode();
        String street = addressDTO.getStreet();
        String buildingName = addressDTO.getBuildingName();
        Address addressFromDB = addressRepo.findByCountryAndStateAndCityAndPinCodeAndStreetAndBuildingName(country,
                state, city, pincode, street, buildingName);
        if (addressFromDB != null) {
            throw new APIException("Address already exists with addressId: " + addressFromDB.getAddressId());
        }
        Address address = modelMapper.map(addressDTO, Address.class);
        Address savedAddress = addressRepo.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepo.findAll();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddress(Long addressId, Address address) {
        Address addressFromDB = addressRepo.findByCountryAndStateAndCityAndPinCodeAndStreetAndBuildingName(
                address.getCountry(), address.getState(), address.getCity(), address.getPinCode(), address.getStreet(),
                address.getBuildingName());
        if (addressFromDB == null) {
            addressFromDB = addressRepo.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
            addressFromDB.setCountry(address.getCountry());
            addressFromDB.setState(address.getState());
            addressFromDB.setCity(address.getCity());
            addressFromDB.setPinCode(address.getPinCode());
            addressFromDB.setStreet(address.getStreet());
            addressFromDB.setBuildingName(address.getBuildingName());
            Address updatedAddress = addressRepo.save(addressFromDB);
            return modelMapper.map(updatedAddress, AddressDTO.class);
        } else {
            List<User> users = userRepo.findByAddress(addressId);
            final Address a = addressFromDB;
            users.forEach(user -> user.getAddresses().add(a));
            deleteAddress(addressId);
            return modelMapper.map(addressFromDB, AddressDTO.class);
        }
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDB = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        List<User> users = userRepo.findByAddress(addressId);
        users.forEach(user -> {
            user.getAddresses().remove(addressFromDB);
            userRepo.save(user);
        });
        addressRepo.deleteById(addressId);
        return "Address deleted succesfully with addressId: " + addressId;
    }

}
