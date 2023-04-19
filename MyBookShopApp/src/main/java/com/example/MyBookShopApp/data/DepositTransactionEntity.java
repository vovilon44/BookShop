package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.security.BookstoreUser;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "deposit_transaction")
public class DepositTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String signature;
    private String signatureSecond;
    private LocalDateTime time;

    private Integer invId;

    private Boolean approve;

    private Double value;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private BookstoreUser user;

    public DepositTransactionEntity() {
    }

    public DepositTransactionEntity(String signature, String signatureSecond, Double value, BookstoreUser user, Integer invId) {
        this();
        this.signature = signature;
        this.signatureSecond = signatureSecond;
        this.user = user;
        this.invId = invId;
        this.value = value;
        this.time = LocalDateTime.now();
        this.approve = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public BookstoreUser getUser() {
        return user;
    }

    public void setUser(BookstoreUser user) {
        this.user = user;
    }

    public String getSignatureSecond() {
        return signatureSecond;
    }

    public void setSignatureSecond(String signatureSecond) {
        this.signatureSecond = signatureSecond;
    }

    public Integer getInvId() {
        return invId;
    }

    public void setInvId(Integer invId) {
        this.invId = invId;
    }
}
