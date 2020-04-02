package io.prometheus.jmx;

import java.util.Arrays;
import java.util.List;
import java.net.InetAddress;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;

public class ConsulExternalService { 
  
  String consulHost;
  String serviceName;
  int servicePort;
  // Arrays labels;
  

  public ConsulExternalService(String consulHost) {
    this.consulHost = consulHost;
    // System.out.println("Instantiate consulHost: " + consulHost);
  }
  public void registerService(String serviceName, int servicePort, List<String> tags, boolean healthCheck) {
          try {
            String fqdnHostName = InetAddress.getLocalHost().getCanonicalHostName();
            String hostName = InetAddress.getLocalHost().getHostName();
            // create json object
            JSONObject postJsonObj = new JSONObject();
            Map labelsMap = new LinkedHashMap();
            Map nodeMetaMap = new LinkedHashMap();
            Map serviceMap = new LinkedHashMap();
            Map checksMap = new LinkedHashMap();
            Map checkDefMap = new LinkedHashMap();
            // Map 
            // TODO get hostname/IP
            postJsonObj.put("Node",fqdnHostName);
            postJsonObj.put("Address",fqdnHostName);
            // TODO make loop for any custom labels
            nodeMetaMap.put("external-node","true");
            nodeMetaMap.put("external-probe", "true");
            postJsonObj.put("NodeMeta", nodeMetaMap);
            serviceMap.put("ID",hostName+"-"+servicePort);
            serviceMap.put("Service", serviceName);
            serviceMap.put("Port", servicePort);
            serviceMap.put("Tags", tags);
            serviceMap.put("Address", hostName);
            postJsonObj.put("Service",serviceMap);
            if (healthCheck){
              checksMap.put("Name","http-check");
              checksMap.put("status","passing");
              checkDefMap.put("http", "http://"+hostName+":"+servicePort+"/metrics");
              checkDefMap.put("interval", "30s");
              checksMap.put("Definition", checkDefMap);
              postJsonObj.put("Check",checksMap);
            }

            // System.out.println("\n\n");
            System.out.println(postJsonObj);
            // System.out.println("\n\n");
            
            // TODO URL 
            URL url = new URL("http://" + consulHost + "/v1/catalog/register");
            HttpURLConnection httpConnection  = (HttpURLConnection) url.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("PUT");
            httpConnection.setRequestProperty("Content-Type", "application/json");

            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.write(postJsonObj.toString().getBytes());
            Integer responseCode = httpConnection.getResponseCode();
            System.out.println("STDOUT response code: " + responseCode);

            // BufferedReader bufferedReader;
            // // Creates a reader buffer
            // if (responseCode > 199 && responseCode < 300) {
            //     bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            // } else {
            //     bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            // }

            // // To receive the response
            // StringBuilder content = new StringBuilder();
            // String line;
            // while ((line = bufferedReader.readLine()) != null) {
            //     content.append(line).append("\n");
            // }
            // bufferedReader.close();
          }
            catch (Exception e) {
              System.err.println("Consul registration failed" + e.getMessage());
          }

  }

}