package com.atguigu.gmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author eternity
 * @create 2019-11-17 14:40
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange exchange(){//交换机

        return  new TopicExchange("OMS-EXCHANGE",true,false,null);

    }

    /**
     * 延时队列
     * @return
     */
    @Bean
    public Queue queue(){//队列

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","OMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "oms.dead");
        arguments.put("x-message-ttl",6000);//单位是毫秒（用于测试，实际根据需求，通常30分钟或者15分钟
        return new Queue("OMS-TTL-QUEUE",true,false,false,arguments);

    }

    /**
     * 延时队列绑定到交换机
     *   rountingKey：wms.unlock
     * @return
     */
    @Bean
    public Binding binding(){

        return new Binding("OMS-TTL-QUEUE", Binding.DestinationType.QUEUE,
                "OMS-EXCHANGE","oms.close",null);
    }

    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue deadQueue(){

        return new Queue("OMS-DEAD-QUEUE",true,false,false,null);
    }

    /**
     * 死信队列绑定到交换机
     * routingKey：wms.ttl
     * @return
     */
    @Bean
    public Binding deadBinding(){
        return new Binding("OMS-DEAD-QUEUE", Binding.DestinationType.QUEUE,
                "OMS-EXCHANGE","oms.dead",null);
    }
}
