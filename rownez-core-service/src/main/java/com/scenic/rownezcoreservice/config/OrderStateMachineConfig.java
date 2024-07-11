package com.scenic.rownezcoreservice.config;

import com.scenic.rownezcoreservice.orderState.OrderEvent;
import com.scenic.rownezcoreservice.orderState.OrderState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
                .withStates()
                .initial(OrderState.PENDING)
                .states(EnumSet.allOf(OrderState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderState.PENDING).target(OrderState.RECEIVED)
                .event(OrderEvent.RECEIVE)
                .and()
                .withExternal()
                .source(OrderState.RECEIVED).target(OrderState.PROCESSING)
                .event(OrderEvent.PROCESS)
                .and()
                .withExternal()
                .source(OrderState.PROCESSING).target(OrderState.COMPLETED)
                .event(OrderEvent.COMPLETE)
                .and()
                .withExternal()
                .source(OrderState.PENDING).target(OrderState.CANCELED)
                .event(OrderEvent.CANCEL)
                .and()
                .withExternal()
                .source(OrderState.RECEIVED).target(OrderState.CANCELED)
                .event(OrderEvent.CANCEL);
    }
}