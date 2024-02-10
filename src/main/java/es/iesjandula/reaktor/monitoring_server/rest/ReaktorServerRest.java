package es.iesjandula.reaktor.monitoring_server.rest;

import es.iesjandula.reaktor.models.Malware;
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Reaktor;
import es.iesjandula.reaktor.models.DTO.MalwareDTOWeb;
import es.iesjandula.reaktor.models.DTO.ReaktorDTO;
import es.iesjandula.reaktor.models.DTO.SimpleComputerDTO;
import es.iesjandula.reaktor.monitoring_server.reaktor_actions.ReaktorActions;
import es.iesjandula.reaktor.monitoring_server.reaktor_actions.ReaktorService;
import es.iesjandula.reaktor.monitoring_server.repository.IMalwareRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
public class ReaktorServerRest
{
    @Autowired
    private ReaktorActions reaktorActions;

    @Autowired
    private IMotherboardRepository iMotherboardRepository;

    @Autowired
    private IMalwareRepository iMalwareRepository;

    @Autowired
    private ReaktorService reaktorService;


    @RequestMapping(method = RequestMethod.POST, value = "/reaktor")
    public ResponseEntity<?> sendInformation(
            @RequestBody Reaktor reaktor
            )
    {
        log.info("Receiving information from reaktor {}", reaktor);
        this.reaktorActions.saveReaktor(reaktor);
        return ResponseEntity.ok("Reaktor Server is running");
    }

    /**
     * This Method is used to obtain the malware list
     * @return the malware list
     */
    @RequestMapping(method = RequestMethod.POST, value = "/computer-on")
    public ResponseEntity<?> computerOn(
            @RequestHeader String motherBoardSerialNumber
    )
    {
        // Comprobamos que existe la placa base en la base de datos
        if (!this.iMotherboardRepository.existsById(motherBoardSerialNumber))
        {
            return ResponseEntity.ok("Motherboard with serial number " + motherBoardSerialNumber + " does not exist");
        }

        // we get the motherboard
        Motherboard motherboard = this.iMotherboardRepository.findByMotherBoardSerialNumber(motherBoardSerialNumber);

        // we set the computer on
        motherboard.setComputerOn(true);

        // we set the last update computer on
        motherboard.setLastUpdateComputerOn(new Date());

        // we save the motherboard with the new status
        this.iMotherboardRepository.save(motherboard);

        return ResponseEntity.ok("Computer with serial number " + motherBoardSerialNumber + " is on");
    }


    /**
     * This Method is used to obtain the malware list
     * @return the malware list
     */
    @RequestMapping(method = RequestMethod.GET, value = "/malware")
    public List<Malware> getMalware()
    {
        return this.iMalwareRepository.findAll();
    }

    /**
     * This Method is used to obtain the malware list for Web
     * @return the malware list for Web
     */
    @RequestMapping(method = RequestMethod.GET, value = "/malware-web")
    public List<MalwareDTOWeb> getMalwareWeb()
    {
        return this.reaktorService.getMalwareWeb();
    }

    /**
     * This Method is used to create a malware
     * @return Ok if the malware is created
     */
    @RequestMapping(method = RequestMethod.POST, value = "/malware")
    public ResponseEntity<?> createMalware(
            @RequestBody Malware newMalware
    )
    {
        log.info("Creating malware {}", newMalware);
        return ResponseEntity.ok().body(this.iMalwareRepository.save(newMalware));
    }


    /**
     * This Method is used to create a malware
     * @param name malware name
     * @return Ok if the malware is created
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/malware/{name}")
    public ResponseEntity<?> deleteMalware(
            @PathVariable String name
    )
    {
        this.reaktorService.deleteMalware(name);
        return ResponseEntity.ok("Malware has been deleted");
    }

    /**
     * This Method is used to report a malware for a motherboard
     * @return Ok if the malware is reported
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/report-malware")
    public ResponseEntity<?> reportMalware(
            @RequestHeader String motherBoardSerialNumber,
            @RequestBody List<Malware> malwareList
    )
    {
        try
        {
            log.info("Reporting malware for motherboard {}", motherBoardSerialNumber);
            this.reaktorActions.insertMalwareMotherboard(motherBoardSerialNumber, malwareList);
        }
        catch (Exception exception)
        {
            log.warn("Malware not reported", exception);
            return ResponseEntity.ok("Malware not reported");
        }

        return ResponseEntity.ok("Malware reported");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/computer")
    public ResponseEntity<List<SimpleComputerDTO>> getComputer()
    {
        return ResponseEntity.ok(reaktorService.getSimpleComputerDTO());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/reaktor/{idComputer}")
    public ResponseEntity<ReaktorDTO> getReaktor(@PathVariable String idComputer )
    {
        return ResponseEntity.ok(reaktorService.getInformationReaktor(idComputer));
    }
}
