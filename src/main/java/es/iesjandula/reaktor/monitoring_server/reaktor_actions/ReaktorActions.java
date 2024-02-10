package es.iesjandula.reaktor.monitoring_server.reaktor_actions;

import es.iesjandula.reaktor.models.*;
import es.iesjandula.reaktor.models.Id.MotherboardMalwareId;
import es.iesjandula.reaktor.monitoring_server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReaktorActions
{

    @Autowired
    private ICpuRepository iCpuRepository;

    @Autowired
    private IGraphicCardRepository iGraphicCardRepository;

    @Autowired
    private IHardDiskRepository iHardDiskRepository;

    @Autowired
    private IinternetConnectionNetworkCardRepository iinternetConnectionNetworkCardRepository;

    @Autowired
    private IinternetConnectionRepository iinternetConnectionRepository;

    @Autowired
    private IMalwareRepository iMalwareRepository;

    @Autowired
    private IMotherboardMalwareRepository iMotherboardMalwareRepository;

    @Autowired
    private IMotherboardRepository iMotherboardRepository;

    @Autowired
    private INetworkCardRepository iNetworkCardRepository;

    @Autowired
    private IPartitionRepository iPartitionRepository;

    @Autowired
    private IRamRepository iRamRepository;

    @Autowired
    private ISoundCardRepository iSoundCardRepository;

    /**
     * This method is used to save the Pc information
     * @param reaktor the Pc information
     */
    public void saveReaktor(Reaktor reaktor)
    {

        if (reaktor.getMotherboard() != null)
        {
            this.saveMotherboard(reaktor.getMotherboard());
        }

        if (reaktor.getCpu() != null)
        {
            this.saveCpu(reaktor.getCpu());
        }

        if (reaktor.getGraphicCard() != null)
        {
            this.saveGraphicCard(reaktor.getGraphicCard());
        }

        if (reaktor.getRam() != null)
        {
            this.saveRam(reaktor.getRam());
        }

        if (reaktor.getSoundCard() != null)
        {
            this.saveSoundCard(reaktor.getSoundCard());
        }

        if (reaktor.getNetworkCard() != null)
        {
            this.saveNetworkCard(reaktor.getNetworkCard());
        }

        if (reaktor.getHardDisk() != null)
        {
            this.saveHardDisk(reaktor.getHardDisk());
        }

        if (reaktor.getPartition() != null)
        {
            this.savePartition(reaktor.getPartition());
        }

        if (reaktor.getInternetConnection() != null)
        {
            this.saveInternetConnection(reaktor.getInternetConnection());
        }

    }

    private void savePartition(List<Partition> partition)
    {
        this.iPartitionRepository.saveAllAndFlush(partition);
    }

    private void saveNetworkCard(List<NetworkCard> networkCard)
    {
        this.iNetworkCardRepository.saveAllAndFlush(networkCard);
    }

    private void saveSoundCard(List<SoundCard> soundCard)
    {
        this.iSoundCardRepository.saveAllAndFlush(soundCard);
    }

    private void saveRam(List<Ram> ram)
    {
        this.iRamRepository.saveAllAndFlush(ram);
    }

    private void saveInternetConnection(InternetConnection internetConnection)
    {
        this.iinternetConnectionRepository.saveAndFlush(internetConnection);
    }

    private void saveHardDisk(List<HardDisk> hardDisk)
    {
        this.iHardDiskRepository.saveAllAndFlush(hardDisk);
    }

    private void saveGraphicCard(List<GraphicCard> graphicCard)
    {
        this.iGraphicCardRepository.saveAllAndFlush(graphicCard);
    }

    private void saveMotherboard(Motherboard motherboard)
    {
        this.iMotherboardRepository.saveAndFlush(motherboard);
    }

    private void saveCpu(Cpu cpu)
    {
        this.iCpuRepository.saveAndFlush(cpu);
    }

    public void removeMalwareFromMotherboard(String motherBoardSerialNumber)
    {
        this.iMotherboardMalwareRepository.deleteByMotherboardMalwareId_SerialNumber(motherBoardSerialNumber);
    }

    public void insertMalwareMotherboard(String motherBoardSerialNumber, List<Malware> malwareList)
    {
        this.removeMalwareFromMotherboard(motherBoardSerialNumber);
        Motherboard motherboard = this.iMotherboardRepository.findByMotherBoardSerialNumber(motherBoardSerialNumber);

        for (Malware malware : malwareList)
        {
            this.iMotherboardMalwareRepository.saveAndFlush(new MotherboardMalware(new MotherboardMalwareId(malware.getName(), motherBoardSerialNumber), malware, motherboard));
        }
    }


}
