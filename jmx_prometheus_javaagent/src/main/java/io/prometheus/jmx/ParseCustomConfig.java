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
  Map<String, Object> yaml;// = (Map<String, Object>)new Yaml();
  String host;
  int port;

  public ParseCustomConfig (String configFile, String configHost, int configPort){
         //= (Map<String, Object>)new Yaml().load(fr);
    try {
      FileReader fileRead = new FileReader(configFile);
      this.yaml = (Map<String, Object>)new Yaml().load(fileRead);
      this.host = configHost;
      this.port = configPort;
    } catch (Exception e) {
      System.err.println("Error reading config file;" + e);
    }
  }

  public void initialize () {
    try {
      String consulHost = "localhost";
      String consulPort = "8500";
      String consulType = "internal";
      String defaultServiceName = "jmx-exporter";
      List<String> defaultTagList = new ArrayList<String>();
      boolean register = false;
      boolean peoplesoft = false;
      
      if (yaml.containsKey("consulRegister") && (Boolean)yaml.get("consulRegister")) {
        //defaults false
        register = (Boolean)yaml.get("consulRegister");
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
      }
      if (yaml.containsKey("peoplesoft") && (Boolean)yaml.get("peoplesoft")) {
        peoplesoft = (Boolean)yaml.get("peoplesoft");
      }
      if (register){
        ConsulService consul = new ConsulService(consulHost, consulPort);
        if (!peoplesoft) {
          if (consulType.equals("external")) {
            consul.registerExternalService(defaultServiceName,port, defaultTagList);
          }
          else {
            consul.registerInternalService(defaultServiceName, port, defaultTagList);
          }
        }
        if (peoplesoft){
          PeopleSoftService service = new PeopleSoftService();
          if (service.isPeopleSoft()){
            if (consulType.equals("external")) {
              consul.registerExternalService(service.getServiceName(), port, service.getTagList());
            }
            else {
              consul.registerInternalService(service.getServiceName(), port, service.getTagList());
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