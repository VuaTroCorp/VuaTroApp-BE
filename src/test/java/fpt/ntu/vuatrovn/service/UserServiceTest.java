package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.UserRequest;
import fpt.ntu.vuatrovn.dto.UserResponse;
import fpt.ntu.vuatrovn.entity.OtpVerifications;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.OtpType;
import fpt.ntu.vuatrovn.repository.OtpVerificationRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;
import fpt.ntu.vuatrovn.service.email.EmailService;
import fpt.ntu.vuatrovn.service.sms.SmsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpVerificationRepository otpVerificationRepository;

    @Mock
    private SmsService smsService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;



    private User user;

    @BeforeEach
    void setup(){
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@gmail.com");
        user.setPhone("0123456789");
    }

    /*Hàm giả lập Security*/
    private void mockSecurity(){
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("test@gmail.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /*reset security sau mỗi test (tránh test sau bị ảnh hưởng).*/
    @AfterEach
    void clearSecurity(){
        SecurityContextHolder.clearContext();
    }


    /*Test case 1: lấy thông tin người dùng thành công*/
    @Test
    void getUserInfo_success(){
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserInfo("test@gmail.com");
        assertEquals("testUser",response.getUsername());
        assertEquals("test@gmail.com",response.getEmail());
    }

    /*Test case 2: không tìm thấy người dùng*/
    @Test
    void getUserInfo_UserNotFound(){
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,() -> userService.getUserInfo("test@gmail.com"));
    }
/*----------------------------------- Sinh Otp Phone -------------------------------------*/
    /*Test case 3: Tạo otp cho phone thành công*/
    @Test
    void generatePhoneOtp_success(){
        mockSecurity();

        UserRequest request = new UserRequest();
        request.setNewPhone("0999999999");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(userRepository.existsByPhone("0999999999")).thenReturn(false);

        when(otpVerificationRepository.findByTargetValue("0999999999")).thenReturn(Optional.empty());

        String result = userService.generatePhoneOtp(request);

        assertTrue(result.contains("OTP"));

        verify(otpVerificationRepository).save(any());

    }

    /*Test Case 4: phone tồn tại*/
    @Test
    void generatePhoneOtp_phoneAlreadyExists(){
        mockSecurity();

        UserRequest request = new UserRequest();
        request.setNewPhone("0999999999");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(userRepository.existsByPhone("0999999999")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.generatePhoneOtp(request));

    }

    /*Test case 5: Otp còn hạn */
    @Test
    void generatePhoneOtp_otpNotExpired(){
        mockSecurity();

        UserRequest request = new UserRequest();
        request.setNewPhone("0999999999");

        OtpVerifications otp = new OtpVerifications();
        otp.setExpired_at(Instant.now().plus(60, ChronoUnit.SECONDS));

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(userRepository.existsByPhone("0999999999")).thenReturn(false);

        when(otpVerificationRepository.findByTargetValue("0999999999")).thenReturn(Optional.of(otp));

        assertThrows(ResponseStatusException.class,() -> userService.generatePhoneOtp(request));
    }

    /*------------------------------------ Sinh Otp Email --------------------------------*/
    /*Test case 6: Sinh email otp thành công*/
    @Test
    void generateEmailOtp_success(){
        mockSecurity();
        UserRequest request = new UserRequest();
        request.setNewEmail("new@gmail.com");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);

        when(otpVerificationRepository.findByTargetValue("new@gmail.com")).thenReturn(Optional.empty());

        String result = userService.generateEmailOtp(request);

        assertTrue(result.contains("OTP"));

        verify(emailService).confirmEmailChangeOtp(eq("new@gmail.com"),any());

    }

    /*Test case 7: Email đã tồn tại*/
    @Test
    void generateEmailOtp_emailAlreadyExists(){
        mockSecurity();

        UserRequest request = new UserRequest();
        request.setNewEmail("new@gmail.com");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class,() -> userService.generateEmailOtp(request));
    }

    /*Test case 8: otp email chưa hết hạn*/
    @Test
    void generateEmailOtp_otpNotExpired(){
        mockSecurity();

        UserRequest request = new UserRequest();
        request.setNewEmail("new@gmail.com");

        OtpVerifications otp = new OtpVerifications();
        otp.setExpired_at(Instant.now().plus(60,ChronoUnit.SECONDS));

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);

        when(otpVerificationRepository.findByTargetValue("new@gmail.com")).thenReturn(Optional.of(otp));

        assertThrows(ResponseStatusException.class,() -> userService.generateEmailOtp(request));
    }

    /*------------------------------------ Xác thực otp --------------------------------*/
    /*Test case 9: Xác thực otp thành công */
    @Test
    void verifyOtp_success(){
        OtpVerifications otp = new OtpVerifications();
        otp.setOtp("123456");
        otp.setTargetValue("new@gmail.com");
        otp.setExpired_at(Instant.now().plusSeconds(60));
        otp.setUser(user);

        when(otpVerificationRepository.findByTargetValueAndOtpAndType("new@gmail.com","123456", OtpType.EMAIL)).thenReturn(Optional.of(otp));

        String result = userService.verifyOtp("new@gmail.com","123456");

        assertEquals("xac thuc thanh cong", result);

        verify(userRepository).save(any());
    }

    /*Test case 10: Nhập sai otp*/
    @Test
    void verifyOtp_wrongOtp(){
        when(otpVerificationRepository.findByTargetValueAndOtpAndType("new@gmail.com","123456",OtpType.EMAIL)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,() -> userService.verifyOtp("new@gmail.com","123456"));
    }

    /*Test case 11: Otp hết hạn trong lúc xác thực*/
    @Test
    void verifyOtp_NotExpired(){
        OtpVerifications otp = new OtpVerifications();
        otp.setOtp("123456");
        otp.setTargetValue("new@gmail.com");
        otp.setType(OtpType.EMAIL);
        otp.setExpired_at(Instant.now().minusSeconds(60));
        otp.setUser(user);

        when(otpVerificationRepository.findByTargetValueAndOtpAndType("new@gmail.com","123456",OtpType.EMAIL)).thenReturn(Optional.of(otp));

        assertThrows(ResponseStatusException.class, () -> userService.verifyOtp("new@gmail.com","123456"));

        verify(otpVerificationRepository).delete(otp);
    }


}
