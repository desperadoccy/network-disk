package pers.ccy.filetransportclient.wrapper;

import pers.ccy.filetransportclient.domain.Server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "servers")
public class ServerListWrapper {
    private List<Server> servers;

    @XmlElement(name = "server")
    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
}
