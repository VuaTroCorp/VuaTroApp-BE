package fpt.ntu.vuatrovn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String email;

    private String phone;

    private String newEmail;
    private String newPhone;
}
