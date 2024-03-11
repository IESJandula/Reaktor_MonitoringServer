package es.iesjandula.reaktor.monitoring_server.reaktor_actions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.models.Cpu;
import es.iesjandula.reaktor.models.GraphicCard;
import es.iesjandula.reaktor.models.HardDisk;
import es.iesjandula.reaktor.models.InternetConnection;
import es.iesjandula.reaktor.models.Malware;
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.MotherboardMalware;
import es.iesjandula.reaktor.models.NetworkCard;
import es.iesjandula.reaktor.models.Partition;
import es.iesjandula.reaktor.models.Ram;
import es.iesjandula.reaktor.models.Reaktor;
import es.iesjandula.reaktor.models.SoundCard;
import es.iesjandula.reaktor.models.Id.MotherboardMalwareId;
import es.iesjandula.reaktor.monitoring_server.repository.ICpuRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IGraphicCardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IHardDiskRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardMalwareRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.INetworkCardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IPartitionRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IRamRepository;
import es.iesjandula.reaktor.monitoring_server.repository.ISoundCardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IinternetConnectionRepository;

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
    private IinternetConnectionRepository iinternetConnectionRepository;

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
