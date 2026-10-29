package com.parkinglot.config;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.SlotType;
import com.parkinglot.repository.ParkingSlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ParkingSlotRepository slotRepository;

    public DataInitializer(ParkingSlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Override
    public void run(String... args) {
        if (slotRepository.count() == 0) {
            log.info("Seeding initial parking slots into database...");
            List<ParkingSlot> initialSlots = Arrays.asList(
                    new ParkingSlot("A1", SlotType.COMPACT),
                    new ParkingSlot("A2", SlotType.COMPACT),
                    new ParkingSlot("A3", SlotType.STANDARD),
                    new ParkingSlot("A4", SlotType.STANDARD),
                    new ParkingSlot("A5", SlotType.STANDARD),
                    new ParkingSlot("B1", SlotType.STANDARD),
                    new ParkingSlot("B2", SlotType.STANDARD),
                    new ParkingSlot("B3", SlotType.LARGE),
                    new ParkingSlot("B4", SlotType.LARGE),
                    new ParkingSlot("EV1", SlotType.EV),
                    new ParkingSlot("EV2", SlotType.EV)
            );
            slotRepository.saveAll(initialSlots);
            log.info("Successfully seeded {} parking slots.", initialSlots.size());
        } else {
            log.info("Parking slots already present in database (count: {}).", slotRepository.count());
        }
    }
}
