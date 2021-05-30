package pers.ccy.filetransportclient.utils;

import org.springframework.stereotype.Component;
import pers.ccy.filetransportclient.domain.Server;
import pers.ccy.filetransportclient.wrapper.ServerListWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

@Component
public class ServerReader {
    public List<Server> getXmlContent() {
        try {
            File file = new File(getClass().getClassLoader().getResource("serverList.xml").getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(ServerListWrapper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ServerListWrapper serverListWrapper = (ServerListWrapper) jaxbUnmarshaller.unmarshal(file);
            return serverListWrapper.getServers();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setXmlContent(List<Server> servers) {
        try {
            ServerListWrapper listWrapper = new ServerListWrapper();
            listWrapper.setServers(servers);
            File file=new File("src/main/resources/serverList.xml");
            JAXBContext jc=JAXBContext.newInstance(ServerListWrapper.class);
            Marshaller jaxbMarshaller=jc.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(listWrapper, file);
        } catch (JAXBException e) {
            System.out.println("input xml error!");
            e.printStackTrace();
        }
    }
}
