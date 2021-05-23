package edu.dhu.auction.web.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "LOT", indexes = {@Index(columnList = "assetIdentifier"), @Index(columnList = "auctionIdentifier")})
public class Lot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Lob
    private String imgUrl;
    @Lob
    private String introduction;
    private BigDecimal basePrice;
    private BigDecimal highestPrice;
    private BigDecimal deposit; //保证金
    private BigDecimal scale; // 竞价阶梯
    private BigDecimal commission; //佣金
    private LocalDateTime createTime = LocalDateTime.now();
    @Type(type = "uuid-char")
    @Column(unique = true, nullable = false)
    private UUID assetIdentifier;
    @Type(type = "uuid-char")
    @Column(unique = true, nullable = false)
    private UUID auctionIdentifier;
    private LocalDateTime auctionStartTime;
    private LocalDateTime auctionEndTime;
    @JsonProperty("isActive")
    private Boolean isActive = true;
    @ManyToOne(optional = false)
    private Account owner;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @OneToMany(mappedBy = "lot", cascade = ALL)
    @OrderBy("bidTime DESC")
    private List<AuctionDetail> auctionDetails = Lists.newArrayList();
    @OneToMany(mappedBy = "lot", cascade = ALL)
    private List<Comment> comments = Lists.newArrayList();
    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Order order;
    @ManyToMany
    private Set<Account> participants = Sets.newHashSet();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(BigDecimal highestPrice) {
        this.highestPrice = highestPrice;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public UUID getAssetIdentifier() {
        return assetIdentifier;
    }

    public void setAssetIdentifier(UUID assetIdentifier) {
        this.assetIdentifier = assetIdentifier;
    }

    public UUID getAuctionIdentifier() {
        return auctionIdentifier;
    }

    public void setAuctionIdentifier(UUID auctionIdentifier) {
        this.auctionIdentifier = auctionIdentifier;
    }

    public LocalDateTime getAuctionStartTime() {
        return auctionStartTime;
    }

    public void setAuctionStartTime(LocalDateTime auctionStartTime) {
        this.auctionStartTime = auctionStartTime;
    }

    public LocalDateTime getAuctionEndTime() {
        return auctionEndTime;
    }

    public void setAuctionEndTime(LocalDateTime auctionEndTime) {
        this.auctionEndTime = auctionEndTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<AuctionDetail> getAuctionDetails() {
        return auctionDetails;
    }

    public void setAuctionDetails(List<AuctionDetail> auctionDetails) {
        this.auctionDetails = auctionDetails;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Set<Account> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Account> participants) {
        this.participants = participants;
    }
}
