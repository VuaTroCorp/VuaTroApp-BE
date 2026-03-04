package fpt.ntu.vuatrovn.entity;

import java.time.LocalDateTime;

import fpt.ntu.vuatrovn.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long payment_id;

    private Float amount;
    private String payment_method;
    private String transaction_code;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime payment_time;

    @ManyToOne
    @JoinColumn(name = "upgrade_id", nullable = false)
    private Upgrade upgrade;


    public long getId() {
        return this.payment_id;
    }

    public void setId(long payment_id) {
        this.payment_id = payment_id;
    }

    public Float getAmount() {
        return this.amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getPayment_method() {
        return this.payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getTransaction_code() {
        return this.transaction_code;
    }

    public void setTransaction_code(String transaction_code) {
        this.transaction_code = transaction_code;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPayment_time() {
        return this.payment_time;
    }

    public void setPayment_time(LocalDateTime payment_time) {
        this.payment_time = payment_time;
    }

    public Upgrade getUpgrade_id() {
        return this.upgrade;
    }

    public void setUpgrade_id(Upgrade upgrade_id) {
        this.upgrade = upgrade_id;
    }

}
