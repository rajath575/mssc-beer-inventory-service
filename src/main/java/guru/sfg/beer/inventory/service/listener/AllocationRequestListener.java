package guru.sfg.beer.inventory.service.listener;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.beer.inventory.service.services.AllocationService;
import guru.sfg.brewery.model.events.AllocateBeerOrderRequest;
import guru.sfg.brewery.model.events.AllocateBeerOrderResult;
import guru.sfg.brewery.model.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationRequestListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(@Payload AllocateBeerOrderRequest request) {

        log.debug("Received new allocate beer order event {}", request.getBeerOrderDto());
        AllocateBeerOrderResult.AllocateBeerOrderResultBuilder resultBuilder = AllocateBeerOrderResult.builder()
                .orderId(request.getBeerOrderDto().getId());
        try {
            boolean isPendingInventory = !(allocationService.allocateOrder(request.getBeerOrderDto()));
            resultBuilder.isValid(true).isPendingInventory(isPendingInventory);
        } catch (Exception ex) {
            resultBuilder.isValid(false);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, resultBuilder.build());

    }
}
