package edu.dhu.auction.web.bean.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountVo implements Serializable {

    private static final long serialVersionUID = -879701411389299511L;

    private Long id;
    private String username;
    private String email;
    private String realName;
    private String idCard;
    private String avatar;
    private LocalDateTime loginTime;
    private Boolean isPwdFirstChange;
    private UUID accountIdentifier;

    public AccountVo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public Boolean getPwdFirstChange() {
        return isPwdFirstChange;
    }

    public void setPwdFirstChange(Boolean pwdFirstChange) {
        isPwdFirstChange = pwdFirstChange;
    }

    public UUID getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(UUID accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }
}
