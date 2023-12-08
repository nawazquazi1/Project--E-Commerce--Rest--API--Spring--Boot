package com.nawaz.shopping.web.payloads;


import com.nawaz.shopping.web.Entity.Role;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
	
	private Long userId;
	private String userName;
	private String mobileNumber;
	private String email;
	private String password;
	private Set<Role> roles = new HashSet<>();
	private AddressDTO address;
	private CartDTO cart;
}
