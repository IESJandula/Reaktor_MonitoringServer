package es.iesjandula.reaktor.monitoring_server.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "reaktor")
public class AppConfig
{

    private Integer cronComputerOn;

    private Integer cronComputerOnDifference;

}
