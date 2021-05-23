package edu.dhu.auction.web.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "ACCOUNT", indexes = {@Index(columnList = "accountIdentifier"), @Index(columnList = "username")})
public class Account implements UserDetails {

    private static final long serialVersionUID = -3007139673167823822L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    private String realName;
    private String idCard;
    private String avatar;
    private LocalDateTime loginTime = LocalDateTime.now();
    @JsonProperty("isPwdFirstChange")
    private Boolean isPwdFirstChange = false;
    @Type(type = "uuid-char")
    @Column(unique = true, nullable = false)
    private UUID accountIdentifier;
    @OneToMany(mappedBy = "account", cascade = ALL)
    private List<Address> addresses = Lists.newArrayList();
    @OneToMany(mappedBy = "owner", cascade = {PERSIST, MERGE, DETACH, REFRESH})
    private List<Lot> ownLots = Lists.newArrayList();
    @ManyToMany(mappedBy = "participants")
    private Set<Lot> joinAuctions = Sets.newHashSet();
    @OneToMany(mappedBy = "account", cascade = ALL)
    private List<Comment> comments = Lists.newArrayList();
    @OneToMany(mappedBy = "bidder")
    private List<AuctionDetail> bids = Lists.newArrayList();
    @OneToMany(mappedBy = "owner", cascade = ALL)
    private List<Order> ownOrders = Lists.newArrayList();

    @Transient
    private List<GrantedAuthority> authorities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
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

    public void setEmail(String email) {
        this.email = email;
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

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Lot> getOwnLots() {
        return ownLots;
    }

    public void setOwnLots(List<Lot> ownLots) {
        this.ownLots = ownLots;
    }

    public Set<Lot> getJoinAuctions() {
        return joinAuctions;
    }

    public void setJoinAuctions(Set<Lot> joinAuctions) {
        this.joinAuctions = joinAuctions;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<AuctionDetail> getBids() {
        return bids;
    }

    public void setBids(List<AuctionDetail> joinedAuctions) {
        this.bids = joinedAuctions;
    }

    public List<Order> getOwnOrders() {
        return ownOrders;
    }

    public void setOwnOrders(List<Order> ownOrders) {
        this.ownOrders = ownOrders;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
