package fpt.ntu.vuatrovn.service.sms;

public interface SmsSender {

    String sendSms(String phone, String message);

}
