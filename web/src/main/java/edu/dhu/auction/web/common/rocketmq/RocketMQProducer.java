package edu.dhu.auction.web.common.rocketmq;

import cn.hutool.core.text.StrFormatter;
import edu.dhu.auction.web.bean.AuctionDetail;
import edu.dhu.auction.web.bean.Lot;
import edu.dhu.auction.web.bean.dto.BidDTO;
import edu.dhu.auction.web.util.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProducer {

    private static final Logger log = LogManager.getLogger(RocketMQProducer.class);

    private String topic;
    private String tagOrder;
    private String tagSMS;
    private String tagBid;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendNewOrderMessage(Lot lot) {
        String destination = StrFormatter.format("{}:{}", topic, tagOrder);
        rocketMQTemplate.syncSend(destination, lot.getAuctionIdentifier().toString());
        log.info("[RocketMQ Producer]订单消息发送成功");
    }

    public void sendBidMessage(AuctionDetail auctionDetail) {
        String destination = StrFormatter.format("{}:{}", topic, tagBid);
        BidDTO bidDTO = BeanUtils.copy(auctionDetail, BidDTO.class);
        rocketMQTemplate.syncSend(destination, bidDTO);
        log.info("[RocketMQ Producer]出价消息发送成功:{}", bidDTO);
    }

    public void sendSmsMessage(String phone) {
        String destination = StrFormatter.format("{}:{}", topic, tagSMS);
        rocketMQTemplate.syncSend(destination, phone);
        log.info("[RocketMQ Producer]验证码消息发送成功:{}", phone);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTagOrder(String tagOrder) {
        this.tagOrder = tagOrder;
    }

    public void setTagSMS(String tagSMS) {
        this.tagSMS = tagSMS;
    }

    public void setTagBid(String tagBid) {
        this.tagBid = tagBid;
    }
}
