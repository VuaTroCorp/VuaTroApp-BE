package fpt.ntu.vuatrovn.service;

import fpt.ntu.vuatrovn.dto.UserRequest;
import fpt.ntu.vuatrovn.dto.UserResponse;
import fpt.ntu.vuatrovn.entity.OtpVerifications;
import fpt.ntu.vuatrovn.entity.User;
import fpt.ntu.vuatrovn.enums.OtpType;
import fpt.ntu.vuatrovn.repository.OtpVerificationRepository;
import fpt.ntu.vuatrovn.repository.UserRepository;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final OtpVerificationRepository otpVerificationRepository;

    private final SmsService smsService;

    private final EmailService emailService;

    public UserResponse ConvertUserToDTo(User user){
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .provider(user.getProvider())
                .status(user.getStatus())
                .build();
    }

    public UserResponse getUserInfo(String email){
        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Khong tim thay user")
        );

        return ConvertUserToDTo(user);

    }

    public String generatePhoneOtp(UserRequest request){

        /*Lấy thông tin đăng nhập của hệ thống*/
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        String phoneOtp = String.format(
                "%06d",new SecureRandom().nextInt(1_000_000)
        );

        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"khong tim thay user")
        );

        /*Check phone nhập xem trùng ko*/
        /*Check trùng với phone hiện tại*/
        if (user.getPhone().equals(request.getNewPhone())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ban da nhap phone hien tai");
        }
        /*Check có trùng vs phone nào trong hệ thống*/
        if (userRepository.existsByPhone(request.getNewPhone())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"so dien thoai da duoc sư dung vui long su dung so dien thoai khac");
        }

        Optional<OtpVerifications> otpPhoneVerifications = this.otpVerificationRepository.findByTargetValue(request.getNewPhone());
        /*Check tồn tại*/
        if (otpPhoneVerifications.isPresent()){
            if (Instant.now().isAfter(otpPhoneVerifications.get().getExpired_at())){
                otpVerificationRepository.delete(otpPhoneVerifications.get());
                OtpVerifications otpVerifications = new OtpVerifications();
                otpVerifications.setOtp(phoneOtp);
                otpVerifications.setUser(user);
                otpVerifications.setExpired_at(
                        Instant.now().plus(1,ChronoUnit.MINUTES)
                );
                otpVerifications.setType(OtpType.PHONE);
                otpVerifications.setTargetValue(request.getNewPhone());
                otpVerifications.setNewName(request.getNewUserName());
                otpVerificationRepository.save(otpVerifications);
                return "OTP đã được gửi tới số điện thoại " + request.getNewPhone() + "Vui long kiem tra SMS";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "OTP đã được gửi tới số điện thoại " + request.getNewPhone() +
                            ". Vui lòng kiểm tra SMS hoặc đợi OTP hết hạn để gửi lại."
            );
        }
        OtpVerifications newOtp = new OtpVerifications();
        newOtp.setOtp(phoneOtp);
        newOtp.setUser(user);
        newOtp.setType(OtpType.PHONE);
        newOtp.setTargetValue(request.getNewPhone());
        newOtp.setNewName(request.getNewUserName());
        newOtp.setExpired_at(Instant.now().plus(1, ChronoUnit.MINUTES));

        otpVerificationRepository.save(newOtp);


////        Gửi SMS
//        String result = smsService.sendSms(
//                request.getNewPhone(),phoneOtp
//        );

        return "OTP đã được gửi tới số điện thoại " + request.getNewPhone() + "Vui long kiem tra SMS" ;
    }

    public String generateEmailOtp(UserRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUserName = authentication.getName();

        String emailOtp = String.format(
                "%06d",new SecureRandom().nextInt(1_000_000)
        );

        User user = this.userRepository.findByEmail(emailUserName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "khong tim thay user")
        );

        /*Check email nhập xem có nhập hoặc trùng ko*/
        /*Check trùng với email hiện tại*/
        if (user.getEmail().equals(request.getNewEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ban da nhap lai email hien tai");
        }

        /*Check ko trùng vs email hiện tại nhưng trùng với email khác*/
        if (userRepository.existsByEmail(request.getNewEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,request.getNewEmail() + "da duoc su dung");
        }

        Optional<OtpVerifications> otpEmailVerifications = this.otpVerificationRepository.findByTargetValue(request.getNewEmail());

        /*Check xem có bản ghi chưa*/
        if (otpEmailVerifications.isPresent()){


            if (Instant.now().isAfter(otpEmailVerifications.get().getExpired_at())){
                /*Có nhưng hết hạn*/
                this.otpVerificationRepository.delete(otpEmailVerifications.get());

                OtpVerifications otpVerifications = new OtpVerifications();
                otpVerifications.setOtp(emailOtp);
                otpVerifications.setUser(user);
                otpVerifications.setType(OtpType.EMAIL);
                otpVerifications.setNewName(request.getNewUserName());
                otpVerifications.setExpired_at(Instant.now().plus(1,ChronoUnit.MINUTES));
                otpVerifications.setTargetValue(request.getNewEmail());
                otpVerificationRepository.save(otpVerifications);

                emailService.confirmEmailChangeOtp(request.getNewEmail(),emailOtp);

                return  "OTP đã được gửi tới email " + request.getNewEmail() +
                        ". Vui lòng kiểm tra email";
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "OTP đã được gửi tới email " + request.getNewEmail() +
                            ". Vui lòng kiểm tra email hoặc đợi OTP hết hạn để gửi lại."
            );
        }

        OtpVerifications otpVerifications = new OtpVerifications();
        otpVerifications.setOtp(emailOtp);
        otpVerifications.setType(OtpType.EMAIL);
        otpVerifications.setTargetValue(request.getNewEmail());
        otpVerifications.setNewName(request.getNewUserName());
        otpVerifications.setUser(user);
        otpVerifications.setExpired_at(
                Instant.now().plus(1,ChronoUnit.MINUTES)
        );
        otpVerificationRepository.save(otpVerifications);

//        gửi mail
        emailService.confirmEmailChangeOtp(request.getNewEmail(),emailOtp);
        return  "OTP đã được gửi tới email " + request.getNewEmail() +
                ". Vui lòng kiểm tra email.";
    }

//    Check Otp Email;
    public String verifyOtp(String targetEmail,String otp){

        OtpVerifications otpVerifications = this.otpVerificationRepository.findByTargetValueAndOtpAndType(targetEmail,otp,OtpType.EMAIL).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Sai otp hoac chua co otp")
        );

        /*Có thì check xem token đã hết hạn chưa*/
        if (Instant.now().isAfter(otpVerifications.getExpired_at())){

            otpVerificationRepository.delete(otpVerifications);
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Otp het hạn vui lòng thực hiện lại");
        }
        /*Con han thi check xem truong new name co data ko*/

        User user = otpVerifications.getUser();

        if (otpVerifications.getNewName() == null || otpVerifications.getNewName().isBlank()){
            otpVerifications.setNewName(user.getUsername());
        }

        user.setEmail(targetEmail);
        user.setUsername(otpVerifications.getNewName());
        this.userRepository.save(user);
        this.otpVerificationRepository.delete(otpVerifications);

        return "xac thuc thanh cong";
    }
}
