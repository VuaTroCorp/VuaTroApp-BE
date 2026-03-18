package fpt.ntu.vuatrovn.service.user;

import fpt.ntu.vuatrovn.dto.UserRequest;
import fpt.ntu.vuatrovn.dto.UserResponse;
import fpt.ntu.vuatrovn.entity.User;

public interface UserService {
    public UserResponse ConvertUserToDTo(User user);
    public UserResponse getUserInfo(String email);
    public String generatePhoneOtp(UserRequest request);
    public String generateEmailOtp(UserRequest request);
    public String verifyOtp(String targetEmail,String otp);
}
