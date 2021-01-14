package guru.sfg.beer.inventory.service.listener;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(@Payload NewInventoryEvent inventoryEvent) {

        log.debug("Received new inventory event {}", inventoryEvent.getBeerDto());
        BeerInventory beerInventory = BeerInventory.builder()
                .beerId(inventoryEvent.getBeerDto().getId())
                .upc(inventoryEvent.getBeerDto().getUpc())
                .quantityOnHand(inventoryEvent.getBeerDto().getQuantityOnHand())
                .build();
        beerInventoryRepository.saveAndFlush(beerInventory);
    }
}
