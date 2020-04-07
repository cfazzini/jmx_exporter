package io.prometheus.jmx;

import org.yaml.snakeyaml.Yaml;
import java.io.FileReader;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.io.File;

public class ParseCustomConfig {
  Map<String, Object> yaml;
  String host;
  int port;

  public ParseCustomConfig (String configFile, String configHost, int configPort){
    try {
      FileReader fileRead = new FileReader(configFile);
      this.yaml = (Map<String, Object>)new Yaml().load(fileRead);
      this.host = configHost;
      this.port = configPort;
    } catch (Exception e) {
      System.err.println("Error reading config file." + e);
    }
  }

  public void initialize () {
    try {
      String consulHost = "localhost";
      String consulPort = "8500";
      String consulType = "internal";
      String defaultServiceName = "jmx-exporter";
      List<String> tagList = new ArrayList<String>();
      boolean register = false;
      boolean peoplesoftKey = false;
      boolean peoplesoftRegionKey = false;

      if (yaml.containsKey("consulRegister") && (Boolean)yaml.get("consulRegister")) {
        //defaults false
        register = (Boolean)yaml.get("consulRegister");
      }
      if (yaml.containsKey("consulHost")) {
          //defaults to localhost
          consulHost = (String)yaml.get("consulHost");
      }
      if (yaml.containsKey("consulPort")) {
        //defaults to 8500
        consulPort = (String)yaml.get("consulPort");
      }
      if (yaml.containsKey("consulType")) {
        consulType = (String)yaml.get("consulType");
        if (!consulType.equals("external") && !consulType.equals("internal")){
          System.err.println("consulType MUST be internal or external, defaulting to internal");
          consulType = "internal";
        }
        else {
          consulType = "external";
        }
      }
      if (yaml.containsKey("consulTags")){
        tagList = (List<String>) yaml.get("consulTags");
      }
      if (yaml.containsKey("peoplesoft") && (Boolean)yaml.get("peoplesoft")) {
        peoplesoftKey = (Boolean)yaml.get("peoplesoft");
      }
      if (yaml.containsKey("peoplesoftRegion")){
        peoplesoftRegionKey = true;
      }
      if (register){
        ConsulService consul = new ConsulService(consulHost, consulPort);
        if (!peoplesoftKey) {
          if (consulType.equals("external")) {
            consul.registerExternalService(defaultServiceName,port, tagList);
          }
          else {
            consul.registerInternalService(defaultServiceName, port, tagList);
          }
        }
        if (peoplesoftKey){
          PeopleSoftService service = new PeopleSoftService();
          if (service.isPeopleSoft()){
            if (peoplesoftRegionKey) {
              tagList.add("psregion="+yaml.get("peoplesoftRegion"));
            }
            else {
              tagList.add("psregion="+service.getPSDomain());
            }
            tagList.addAll(service.getTagList());
            if (consulType.equals("external")) {
              consul.registerExternalService(service.getServiceName(), port, tagList);
            }
            else {
              consul.registerInternalService(service.getServiceName(), port, tagList);
            }
          } else {
            System.err.println("Peoplesoft mode specified, but could not parse directory.");
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error registering to consul" + e);
    }
  }
}