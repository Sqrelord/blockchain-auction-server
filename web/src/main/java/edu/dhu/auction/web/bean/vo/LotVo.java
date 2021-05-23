package edu.dhu.auction.web.bean.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LotVo {
    private Long id;
    private String name;
    private String description;
    private String imgUrl;
    private String introduction;
    private BigDecimal deposit;
    private BigDecimal scale;
    private BigDecimal commission;
    private BigDecimal basePrice;
    private BigDecimal highestPrice;
    private LocalDateTime createTime;
    private UUID assetIdentifier;
    private UUID auctionIdentifier;
    private LocalDateTime auctionStartTime;
    private LocalDateTime auctionEndTime;
    private CategoryVo category;
    private List<AuctionDetailVo> auctionDetails;
    private List<CommentVo> comments;

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

    public CategoryVo getCategory() {
        return category;
    }

    public void setCategory(CategoryVo category) {
        this.category = category;
    }

    public List<AuctionDetailVo> getAuctionDetails() {
        return auctionDetails;
    }

    public void setAuctionDetails(List<AuctionDetailVo> auctionDetails) {
        this.auctionDetails = auctionDetails;
    }

    public List<CommentVo> getComments() {
        return comments;
    }

    public void setComments(List<CommentVo> comments) {
        this.comments = comments;
    }
}
