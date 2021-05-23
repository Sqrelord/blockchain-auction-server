package edu.dhu.auction.web.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.corda.client.jackson.JacksonSupport;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class NodeRPCConfigurer implements WebMvcConfigurer {

    @Bean(destroyMethod = "")
    public CordaRPCOps getCordaRPCOps() {
        CordaRPCClient cordaRPCClient = new CordaRPCClient(NetworkHostAndPort.parse("localhost:10006"));
        return cordaRPCClient.start("user", "123456").getProxy();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = JacksonSupport.createDefaultMapper(getCordaRPCOps());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        return converter;
    }
}
