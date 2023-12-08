package com.nawaz.shopping.web.services.Impl;

import com.nawaz.shopping.web.Entity.Address;
import com.nawaz.shopping.web.Entity.Cart;
import com.nawaz.shopping.web.Entity.Role;
import com.nawaz.shopping.web.Entity.User;
import com.nawaz.shopping.web.Entity.CartItem;
import com.nawaz.shopping.web.config.AppConstants;
import com.nawaz.shopping.web.exceptions.APIException;
import com.nawaz.shopping.web.exceptions.ResourceNotFoundException;
import com.nawaz.shopping.web.payloads.*;
import com.nawaz.shopping.web.repositories.AddressRepo;
import com.nawaz.shopping.web.repositories.RoleRepo;
import com.nawaz.shopping.web.repositories.UserRepo;
import com.nawaz.shopping.web.services.CartService;
import com.nawaz.shopping.web.services.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private UserRepo userRepo;
	private RoleRepo roleRepo;
	private AddressRepo addressRepo;
	private CartService cartService;
	private PasswordEncoder passwordEncoder;
	private ModelMapper modelMapper;

	@Override
	public UserDTO registerUser(UserDTO userDTO) {
		try {
			User user = modelMapper.map(userDTO, User.class);
			Cart cart = new Cart();
			user.setCart(cart);
			Role role = roleRepo.findById(AppConstants.USER_ID).orElseThrow();
			user.getRoles().add(role);
			String country = userDTO.getAddress().getCountry();
			String state = userDTO.getAddress().getState();
			String city = userDTO.getAddress().getCity();
			String pinCode = userDTO.getAddress().getPincode();
			String street = userDTO.getAddress().getStreet();
			String buildingName = userDTO.getAddress().getBuildingName();
			Address address = addressRepo.findByCountryAndStateAndCityAndPinCodeAndStreetAndBuildingName(country, state,
					city, pinCode, street, buildingName);
			if (address == null) {
				address = new Address(country, state, city, pinCode, street, buildingName);
				address = addressRepo.save(address);
			}
			user.setAddresses(List.of(address));
			User registeredUser = userRepo.save(user);
			cart.setUser(registeredUser);
			userDTO = modelMapper.map(registeredUser, UserDTO.class);
			userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
			return userDTO;
		} catch (DataIntegrityViolationException e) {
			throw new APIException("User already exists with emailId: " + userDTO.getEmail());
		}

	}

	@Override
	public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<User> pageUsers = userRepo.findAll(pageDetails);
		List<User> users = pageUsers.getContent();
		if (users.isEmpty()) {
			throw new APIException("No User exists !!!");
		}
		List<UserDTO> userDTOs = users.stream().map(user -> {
			UserDTO dto = modelMapper.map(user, UserDTO.class);
			if (!user.getAddresses().isEmpty()) {
				dto.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
			}
			CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);
			List<ProductDTO> products = user.getCart().getCartItems().stream()
					.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());
			dto.setCart(cart);
			dto.getCart().setProducts(products);
			return dto;
		}).collect(Collectors.toList());
		UserResponse userResponse = new UserResponse();
		userResponse.setContent(userDTOs);
		userResponse.setPageNumber(pageUsers.getNumber());
		userResponse.setPageSize(pageUsers.getSize());
		userResponse.setTotalElements(pageUsers.getTotalElements());
		userResponse.setTotalPages(pageUsers.getTotalPages());
		userResponse.setLastPage(pageUsers.isLast());
		return userResponse;
	}

	@Override
	public UserDTO getUserById(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
		CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);
		List<ProductDTO> products = user.getCart().getCartItems().stream()
				.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());
		userDTO.setCart(cart);
		userDTO.getCart().setProducts(products);
		return userDTO;
	}

	@Override
	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		String encodedPass = passwordEncoder.encode(userDTO.getPassword());
		user.setUserName(userDTO.getUserName());
		user.setMobileNumber(userDTO.getMobileNumber());
		user.setEmail(userDTO.getEmail());
		user.setPassword(encodedPass);
		if (userDTO.getAddress() != null) {
			String country = userDTO.getAddress().getCountry();
			String state = userDTO.getAddress().getState();
			String city = userDTO.getAddress().getCity();
			String pinCode = userDTO.getAddress().getPincode();
			String street = userDTO.getAddress().getStreet();
			String buildingName = userDTO.getAddress().getBuildingName();
			Address address = addressRepo.findByCountryAndStateAndCityAndPinCodeAndStreetAndBuildingName(country, state,
					city, pinCode, street, buildingName);
			if (address == null) {
				address = new Address(country, state, city, pinCode, street, buildingName);
				address = addressRepo.save(address);
				user.setAddresses(List.of(address));
			}
		}
		userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
		CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);
		List<ProductDTO> products = user.getCart().getCartItems().stream()
				.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());
		userDTO.setCart(cart);
		userDTO.getCart().setProducts(products);
		return userDTO;
	}

	@Override
	public String deleteUser(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		List<CartItem> cartItems = user.getCart().getCartItems();
		Long cartId = user.getCart().getCartId();
		cartItems.forEach(item -> {
			Long productId = item.getProduct().getProductId();
			cartService.deleteProductFromCart(cartId, productId);
		});
		userRepo.delete(user);
		return "User with userId " + userId + " deleted successfully!!!";
	}

}
