package io.prometheus.jmx;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import io.prometheus.jmx.ConsulService;
import io.prometheus.jmx.PeopleSoftService;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import org.yaml.snakeyaml.Yaml;

public class JavaAgent {
  static HTTPServer server;
  
  public static void agentmain(String agentArgument, Instrumentation instrumentation) throws Exception {
    premain(agentArgument, instrumentation);
  }
  
  public static void premain(String agentArgument, Instrumentation instrumentation) throws Exception {
    // Bind to all interfaces by default (this includes IPv6).
    String host = "0.0.0.0";       
    
    // Map<String, Object> yamlConfig = new HashMap<String, Object>();
    
    try {
      Config config = parseConfig(agentArgument, host);
      
      new BuildInfoCollector().register();
      new JmxCollector(new File(config.file)).register();
      DefaultExports.initialize();
      server = new HTTPServer(config.socket, CollectorRegistry.defaultRegistry, true);
      
      }
      catch (IllegalArgumentException e) {
        System.err.println("Usage: -javaagent:/path/to/JavaAgent.jar=[host:]<port>:<yaml configuration file> " + e.getMessage());
        System.exit(1);
      }


      try {
        String consulHost = "localhost";
        String consulPort = "8500";
        String consulType = "internal";
        
        boolean register = false;
        boolean peoplesoft = false;
        
        Config config = parseConfig(agentArgument, host);
        // Map<String, Object> yaml = parseYaml(config.file);
        FileReader fr = new FileReader(config.file);
        Map<String, Object> yaml = (Map<String, Object>)new Yaml().load(fr);
        
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

        if (register && peoplesoft){
          System.err.println("Peoplesoft AND Register TRUE");
          System.err.println(consulType);
        PeopleSoftService service = new PeopleSoftService();
        ConsulService consul = new ConsulService(consulHost, consulPort);
        if (service.isPeopleSoft()){
          if (consulType.equals("external")) {
            consul.registerExternalService(service.getServiceName(),config.port, service.getTagList());
          }
          else {
            consul.registerInternalService(service.getServiceName(), config.port, service.getTagList());
          }
          
        }
      }
    } catch (Exception e) {
      System.err.println("Error registering to consul" + e);
    }
    
  }

  // public static YamlConfig parseYaml(String file) {
  //     FileReader fr = new FileReader(config.file);
  //     Map<String, Object> yaml = (Map<String, Object>)new Yaml().load(fr);
  //     return yaml;
  // }
  /**
  * Parse the Java Agent configuration. The arguments are typically specified to the JVM as a javaagent as
  * {@code -javaagent:/path/to/agent.jar=<CONFIG>}. This method parses the {@code <CONFIG>} portion.
  * @param args provided agent args
  * @param ifc default bind interface
  * @return configuration to use for our application
  */
  public static Config parseConfig(String args, String ifc) {
    Pattern pattern = Pattern.compile(
    "^(?:((?:[\\w.]+)|(?:\\[.+])):)?" +  // host name, or ipv4, or ipv6 address in brackets
    "(\\d{1,5}):" +              // port
    "(.+)");                     // config file
    
    Matcher matcher = pattern.matcher(args);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Malformed arguments - " + args);
    }
    
    String givenHost = matcher.group(1);
    String givenPort = matcher.group(2);
    String givenConfigFile = matcher.group(3);
    
    int port = Integer.parseInt(givenPort);
    
    InetSocketAddress socket;
    if (givenHost != null && !givenHost.isEmpty()) {
      socket = new InetSocketAddress(givenHost, port);
    }
    else {
      socket = new InetSocketAddress(ifc, port);
      givenHost = ifc;
    }
    
    return new Config(givenHost, port, givenConfigFile, socket);
  }
  
  static class Config {
    String host;
    int port;
    String file;
    InetSocketAddress socket;
    
    Config(String host, int port, String file, InetSocketAddress socket) {
      this.host = host;
      this.port = port;
      this.file = file;
      this.socket = socket;
    }
  }
}
