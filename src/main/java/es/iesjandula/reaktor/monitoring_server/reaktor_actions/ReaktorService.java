package es.iesjandula.reaktor.monitoring_server.reaktor_actions;

import es.iesjandula.reaktor.models.*;
import es.iesjandula.reaktor.models.DTO.*;
import es.iesjandula.reaktor.models.Id.MotherboardMalwareId;
import es.iesjandula.reaktor.monitoring_server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ReaktorService
{
    /** Attribute iMotherboardMalwareRepository*/
    @Autowired
    private IMotherboardMalwareRepository iMotherboardMalwareRepository;

    /** Attribute iMotherboardRepository*/
    @Autowired
    private IMotherboardRepository iMotherboardRepository;

    /** Attribute iCpuRepository*/
    @Autowired
    private ICpuRepository iCpuRepository;

    /** Attribute iGraphicCardRepository*/
    @Autowired
    private IGraphicCardRepository iGraphicCardRepository;

    /** Attribute iHardDiskRepository*/
    @Autowired
    private IHardDiskRepository iHardDiskRepository;

    /** Attribute iNetworkCardRepository*/
    @Autowired
    private INetworkCardRepository iNetworkCardRepository;

    /** Attribute iRamRepository*/
    @Autowired
    private IRamRepository iRamRepository;

    /** Attribute iSoundCardRepository*/
    @Autowired
    private ISoundCardRepository iSoundCardRepository;

    /** Attribute iPartitionRepository*/
    @Autowired
    private IPartitionRepository iPartitionRepository;

    /** Attribute iMalwareRepository*/
    @Autowired
    private IMalwareRepository iMalwareRepository;



    /**
     * Method getSimpleComputerDTO
     * @return
     */
    public List<SimpleComputerDTO> getSimpleComputerDTO()
    {
        List<SimpleComputerDTO> simpleComputerDTOS = new ArrayList<>();

        List<Motherboard> motherboardList = this.iMotherboardRepository.findAll();

        for (Motherboard motherboard : motherboardList)
        {
            Long malwareCount = this.iMotherboardMalwareRepository.countByMotherboardMalwareId_MotherBoardSerialNumber(motherboard.getMotherBoardSerialNumber());

            simpleComputerDTOS.add(new SimpleComputerDTO(motherboard.getMotherBoardSerialNumber(), malwareCount, motherboard.getClassroom(), motherboard.getTeacher(), motherboard.getComputerOn(), motherboard.getIsAdmin(),motherboard.getComputerNumber()));
        }
        return simpleComputerDTOS;
    }

    /**
     * Method getInformationReaktor
     * @param idComputer
     * @return
     */
    public ReaktorDTO getInformationReaktor(String idComputer)
    {
        CpuDTO cpu = this.convertToCpuDTO(this.iCpuRepository.findCpuById_Motherboard_MotherBoardSerialNumber(idComputer));

        MotherboardDTO motherboard = this.convertToMotherboardDTO(this.iMotherboardRepository.findByMotherBoardSerialNumber(idComputer));

        List<GraphicCardDTO> graphicCardList = this.iGraphicCardRepository.findAll()
                .stream().filter(graphicCard -> Objects.equals(graphicCard.getId().getMotherboard().getMotherBoardSerialNumber(), idComputer))
                .map(this::convertToGraphicCardDTO).toList();

        List<HardDiskDTO> hardDiskList = this.iHardDiskRepository.findAll()
                .stream().filter(hardDisk -> Objects.equals(hardDisk.getId().getMotherboard().getMotherBoardSerialNumber(), idComputer))
                .map(this::convertToHardDiskDTO).toList();

        List<NetworkCardDTO> networkCardList = this.iNetworkCardRepository.findAll()
                .stream().filter(networkCard -> Objects.equals(networkCard.getId().getMotherboard().getMotherBoardSerialNumber(), idComputer))
                .map(this::convertToNetworkCardDTO).toList();

        List<RamDTO> ramList = this.iRamRepository.findAll()
                .stream().filter(ram -> Objects.equals(ram.getId().getMotherboard().getMotherBoardSerialNumber(), idComputer))
                .map(this::convertToRamDTO).toList();

        List<SoundCardDTO> soundCardList = this.iSoundCardRepository.findAll()
                .stream().filter(soundCard -> Objects.equals(soundCard.getId().getMotherboard().getMotherBoardSerialNumber(), idComputer))
                .map(this::convertToSoundCardDTO).toList();

        List<PartitionDTO> partitionList = this.iPartitionRepository.findAll()
                .stream().filter(partition -> Objects.equals(partition.getId().getHardDisk().getId().getMotherboard().getMotherBoardSerialNumber(), idComputer))
                .map(this::convertToPartitionDTO).toList();

        List<MalwareDTO> malwareList = this.iMotherboardMalwareRepository.findAll()
                .stream().filter(motherboardMalware -> Objects.equals(motherboardMalware.getMotherBoardSerialNumber().getMotherBoardSerialNumber(), idComputer))
                .map(MotherboardMalware::getName)
                .map(this::convertToMalwareDTO).toList();

        return new ReaktorDTO(motherboard,cpu,graphicCardList,hardDiskList,networkCardList,partitionList,ramList,soundCardList,malwareList);
    }

    /**
     * Method convertToMalwareDTO
     * @param malware
     * @return
     */
    private MalwareDTO convertToMalwareDTO(Malware malware)
    {
        return new MalwareDTO(malware.getName(),malware.getDescription());
    }


    /**
     * Method convertToPartitionDTO
     * @param partition
     * @return
     */
    private PartitionDTO convertToPartitionDTO(Partition partition)
    {
        return new PartitionDTO(partition.getId().getIdPartition(),partition.getSize(),partition.getLetter(),partition.getOperatingSystem());
    }


    /**
     * Method convertToSoundCardDTO
     * @param soundCard
     * @return
     */
    private SoundCardDTO convertToSoundCardDTO(SoundCard soundCard)
    {
        return new SoundCardDTO(soundCard.getId().getIdSoundCard(),soundCard.getModel(),soundCard.getDriver());
    }

    /**
     * Method convertToRamDTO
     * @param ram
     * @return
     */
    private RamDTO convertToRamDTO(Ram ram)
    {
        return new RamDTO(ram.getId().getSerialNumberRam(),ram.getSize(),ram.getOccupiedSlots(),ram.getModel(),ram.getType(),ram.getSize());
    }

    /**
     * Method convertToNetworkCardDTO
     * @param networkCard
     * @return
     */
    private NetworkCardDTO convertToNetworkCardDTO(NetworkCard networkCard)
    {
        return new NetworkCardDTO(networkCard.getId().getIdNetworkCard(),networkCard.getMacAddress(),networkCard.getRj45IsConnected(),networkCard.getModel(),networkCard.getIsWireless());
    }

    /**
     * Method convertToHardDiskDTO
     * @param hardDisk
     * @return
     */
    private HardDiskDTO convertToHardDiskDTO(HardDisk hardDisk)
    {
        return new HardDiskDTO(hardDisk.getId().getSerialNumberHardDisk(),hardDisk.getSize(),hardDisk.getModel());
    }

    /**
     * Method convertToGraphicCardDTO
     * @param graphicCard
     * @return
     */
    private GraphicCardDTO convertToGraphicCardDTO(GraphicCard graphicCard)
    {
        return new GraphicCardDTO(graphicCard.getId().getIdGraphicCard(), graphicCard.getModel());
    }

    /**
     * Method convertToMotherboardDTO
     * @param motherboard
     * @return
     */
    private MotherboardDTO convertToMotherboardDTO(Motherboard motherboard)
    {
       return new MotherboardDTO(motherboard.getMotherBoardSerialNumber(),motherboard.getModel(),motherboard.getClassroom(),motherboard.getTrolley(),motherboard.getAndaluciaId(),motherboard.getComputerNumber(),motherboard.getTeacher(),motherboard.getComputerSerialNumber(),motherboard.getLastConnection(),motherboard.getLastUpdateComputerOn(),motherboard.getComputerOn());
    }
    
    /**
     * Method convertToCpuDTO
     * @param cpu
     * @return
     */
    private CpuDTO convertToCpuDTO(Cpu cpu)
    {
        return new CpuDTO(cpu.getId().getIdCpu(), cpu.getCores(), cpu.getFrequency(), cpu.getThreads());
    }


    /**
     * Method deleteMalware
     * @param idMalware
     */
    public void deleteMalware(String idMalware)
    {
        Malware malwareRemove = this.iMalwareRepository.findById(idMalware).orElseThrow(
                () -> new IllegalArgumentException("Invalid malware Id:" + idMalware)
        );

        List<String> motherboardMalwareIdList = this.iMotherboardMalwareRepository.findIdMotherboardOfMalware(idMalware);

        for (String id : motherboardMalwareIdList)
        {
            Motherboard motherboard = this.iMotherboardRepository.findByMotherBoardSerialNumber(id);
            motherboard.getMalware().remove(new MotherboardMalware(new MotherboardMalwareId(idMalware, id), malwareRemove, motherboard));
        }

        this.iMalwareRepository.delete(malwareRemove);
    }


    /**
     * Method getMalwareWeb
     * @return
     */
    public List<MalwareDTOWeb> getMalwareWeb()
    {

        List<Object[]> malwareWeb = this.iMalwareRepository.malwareWeb();

        List<MalwareDTOWeb> malwareDTOWebList = new ArrayList<>();

        for (Object malware : malwareWeb)
        {
            MalwareDTOWeb malwareDTOWeb = new MalwareDTOWeb();
            malwareDTOWeb.setName((String) ((Object[]) malware)[0]);
            malwareDTOWeb.setDescription((String) ((Object[]) malware)[1]);
            malwareDTOWeb.setNumOccurrences((Long) ((Object[]) malware)[2]);
            malwareDTOWebList.add(malwareDTOWeb);
        }

        return malwareDTOWebList;
    }
}
