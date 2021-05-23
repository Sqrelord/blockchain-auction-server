package edu.dhu.auction.web.common.rocketmq;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.google.common.collect.ImmutableMap;
import edu.dhu.auction.web.bean.dto.BidDTO;
import edu.dhu.auction.web.service.NodeService;
import edu.dhu.auction.web.util.AssertUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
public class RocketMQConsumer {

    private static final Logger log = LogManager.getLogger(RocketMQConsumer.class);

    @Resource
    private IAcsClient client;
    @Resource
    private NodeService nodeService;
    @Resource
    private RedisTemplate<String, String> redis;

    @Component
    @RocketMQMessageListener(topic = "${rocketmq.topic:}", consumerGroup = "${rocketmq.group-order:}", selectorExpression = "${rocketmq.tag-order:}")
    class OrderMessageListener implements RocketMQListener<String> {
        @Override
        public void onMessage(String auctionIdentifier) {
            nodeService.finalizeAuction(UUID.fromString(auctionIdentifier));
            log.info("[RocketMQ Consumer]订单消息消费成功:{}", auctionIdentifier);
        }
    }

    @Component
    @RocketMQMessageListener(topic = "${rocketmq.topic:}", consumerGroup = "${rocketmq.group-bid:}", selectorExpression = "${rocketmq.tag-bid:}")
    static class BidMessageListener implements RocketMQListener<BidDTO> {
        @Override
        public void onMessage(BidDTO bid) {
            log.info(bid);
        }
    }

    @Component
    @RocketMQMessageListener(topic = "${rocketmq.topic:}", consumerGroup = "${rocketmq.group-sms:}", selectorExpression = "${rocketmq.tag-sms:}")
    class SmsMessageListener implements RocketMQListener<String> {
        @Override
        public void onMessage(String phone) {
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("<your domain>");
            request.setSysVersion("your version");
            request.setSysAction("SendSms");
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName", "<your sign name>");
            request.putQueryParameter("TemplateCode", "<your template code>");
            String code = String.valueOf(RandomUtil.randomInt(100000, 1000000));
            String captcha = JSON.toJSONString(ImmutableMap.of("code", code));
            request.putQueryParameter("TemplateParam", captcha);
            try {
                CommonResponse response = client.getCommonResponse(request);
                JSONObject result = JSON.parseObject(response.getData());
                AssertUtils.isTrue(Objects.equals("OK", result.get("Code")), result.toString());
                redis.opsForValue().set(phone, code, Duration.ofMinutes(5).toMillis(), TimeUnit.MILLISECONDS);
                log.info("[RocketMQ Consumer]验证短信发送成功:{} {}", phone, result.toString());
            } catch (ClientException e) {
                e.printStackTrace();
                log.info("[RocketMQ Consumer]验证短信发送失败:{}", phone);
            }
        }
    }
}
