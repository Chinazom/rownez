package com.scenic.rownezcoreservice.config;


import com.scenic.rownezcoreservice.exception.ApiException;
import com.scenic.rownezcoreservice.order.state.OrderEvent;
import com.scenic.rownezcoreservice.order.state.OrderState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
                .withStates()
                .initial(OrderState.PENDING)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.COMPLETED)
                .end(OrderState.CANCELED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderState.PENDING).target(OrderState.RECEIVED)
                .event(OrderEvent.RECEIVE)
                .guard(stateGuard(true))
                .and()
                .withExternal()
                .source(OrderState.RECEIVED).target(OrderState.PROCESSING)
                .event(OrderEvent.PROCESS)
                .guard(context -> true)
                .and()
                .withExternal()
                .source(OrderState.PROCESSING).target(OrderState.COMPLETED)
                .event(OrderEvent.COMPLETE)
                .guard(context -> true)
                .and()
                .withExternal()
                .source(OrderState.PENDING).target(OrderState.CANCELED)
                .event(OrderEvent.CANCEL)
                .guard(context -> true)
                .and()
                .withExternal()
                .source(OrderState.RECEIVED).target(OrderState.CANCELED)
                .event(OrderEvent.CANCEL)
                .guard(context -> true);
    }
    @Bean
    public Guard<OrderState, OrderEvent> stateGuard(final boolean value) {
        return context -> {
            if (value){
                return true;
            }else {
                throw new ApiException("Status update is not allowed", HttpStatus.BAD_REQUEST);
            }
        };
    }
}