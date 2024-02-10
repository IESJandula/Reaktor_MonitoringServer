package es.iesjandula.reaktor.monitoring_server.scheduled_task;

import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.utils.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.introspector.Property;

import java.util.List;

/**
 * - Class -
 * This class is used to schedule the tasks of the application
 */
@Slf4j
@Component
public class ScheduledTask
{

    @Autowired
    private IMotherboardRepository iMotherboardRepository;

    @Autowired
    private AppConfig appConfig;

    @Scheduled(fixedDelayString = "${reaktor.cronComputerOn}", initialDelay = 2000)
    public void checkComputerOn()
    {
        log.info("Checking if the computer is on");

        List<Motherboard> motherboards = this.iMotherboardRepository.findAll();

        for (Motherboard motherboard : motherboards)
        {
            if (motherboard.getComputerOn())
            {
                if (System.currentTimeMillis() - motherboard.getLastUpdateComputerOn().getTime() > this.appConfig.getCronComputerOnDifference())
                {
                    motherboard.setComputerOn(false);
                    this.iMotherboardRepository.save(motherboard);
                }
            }
        }
    }
}
