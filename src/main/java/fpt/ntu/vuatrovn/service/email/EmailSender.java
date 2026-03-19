package fpt.ntu.vuatrovn.service.email;

public interface EmailSender {

    void sendVerificationEmail(String email, String token);

    void sendOtpEmail(String email, String otp);

    void confirmEmailChangeOtp(String email, String otp);

}
