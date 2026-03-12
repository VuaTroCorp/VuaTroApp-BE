package fpt.ntu.vuatrovn.dto;

import fpt.ntu.vuatrovn.entity.OtpVerifications;
import fpt.ntu.vuatrovn.enums.Provider;
import fpt.ntu.vuatrovn.enums.Role;
import fpt.ntu.vuatrovn.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Provider provider;
    private UserStatus status;
    private Role role;
}
