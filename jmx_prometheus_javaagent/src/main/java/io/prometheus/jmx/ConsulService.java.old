package io.prometheus.jmx;

import java.util.Arrays;
import java.util.List;
import java.net.InetAddress;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.agent.model.NewCheck;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.Self;
import com.ecwid.consul.v1.agent.model.Service;
import com.ecwid.consul.v1.catalog.model.CatalogRegistration;
import com.ecwid.consul.v1.catalog.model.CatalogNode;
import com.ecwid.consul.v1.session.model.NewSession;


public class ConsulService extends ConsulClient{
  
  String consulHost;
  String serviceName;
  int servicePort;
  Arrays labels;

  public ConsulService(String consulHost) {
  // String serviceName, int servicePort, Arrays labels) {
    this.consulHost = consulHost;
    // this.serviceName = serviceName;
    // this.servicePort = servicePort;
    // this.labels = labels;
  }
  
  public void registerService(int servicePort, String serviceName, List<String> tags) {
    try {
      // this host 
      String hostName = InetAddress.getLocalHost().getCanonicalHostName();
      System.out.println("Got hostname of: " + hostName);

      // Config config = parseConfig(agentArgument, host);
      ConsulClient client = new ConsulClient(consulHost);
      NewService newService = new NewService();
      // newService.setId("myapp_01");
      newService.setName(serviceName);
      newService.setTags(tags);
      newService.setPort(servicePort);
      newService.setAddress(hostName);
      // NewService.Check check = new NewService.Check();
      // check.setInterval("10s");
      // check.setHttp("http://"+hostName+":"+servicePort+"/metrics");
      // // check.setService
      // check.setDeregisterCriticalServiceAfter("1m");
      // newService.setCheck(check);
      client.agentServiceRegister(newService);
    } 
    catch (Exception e) {
        System.err.println("Consul client failed" + e.getMessage());
    }
  }
}