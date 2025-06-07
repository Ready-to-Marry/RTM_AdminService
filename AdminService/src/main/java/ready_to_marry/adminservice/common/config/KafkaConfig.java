package ready_to_marry.adminservice.common.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ready_to_marry.adminservice.event.dto.request.CouponKafkaRequest;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, CouponKafkaRequest> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        //TODO 환경 변수 사용으로 추후 리팩
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.134.233:9094,10.0.146.105:9095,10.0.166.50:9096");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "coupon-consumer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 역직렬화 설정 (ErrorHandling + Json)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ready_to_marry.adminservice.event.dto.request");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CouponKafkaRequest.class.getName());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CouponKafkaRequest> kafkaListenerContainerFactory(
            ConsumerFactory<String, CouponKafkaRequest> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, CouponKafkaRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
