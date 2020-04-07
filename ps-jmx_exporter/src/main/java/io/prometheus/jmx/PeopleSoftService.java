package io.prometheus.jmx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PeopleSoftService {

  final static String pattern = ".*(appserv|webserv)(/+|\\\\+)(prcs)?(/+|\\\\+)?(\\w+)";
  final static Pattern r = Pattern.compile(pattern);
  final static String userDir = System.getProperty("user.dir");

  // private boolean isPeopleSoft = false;
  private String serviceName = "";
  private List<String> tagList = new ArrayList<String>();



  public PeopleSoftService() {
   
  }

  public boolean isPeopleSoft(){
    return matchPeopleSoft(userDir);
  }

  public String getServiceName(){
    return this.serviceName;
  }

  public List<String> getTagList(){
    return this.tagList;
  }

  private boolean matchPeopleSoft(String matchUserDir){
    // Look for user.dir pattern used in PeopleSoft
    // Should Match:
    // /any/path/to/cfg/appserv/<domain>
    // /any/path/to/cfg/appserv/prcs/<domain>
    // /any/path/to/cfg/webserv/<domain>
    // D:\\any\\path\\to\\config\\appserv\\<domain>
    // D:\\any\\path\\to\\config\\appserv\\prcs\\<domain>
    // D:\\any\\path\\to\\config\\webserv\\<domain>
    Matcher match = r.matcher(matchUserDir);
    if (match.matches()){
      if (match.group(1).equals("webserv")) {
        this.serviceName = "pswebserv-jmx-exporter";
        this.tagList.add("domain="+match.group(5));
        this.tagList.add("domain_type=webserv");
      } else if (match.group(3) == null ) {
        this.serviceName = "psappserv-jmx-exporter";
        this.tagList.add("domain="+match.group(5));
        this.tagList.add("domain_type=appsrv");
      }else if (match.group(3).equals("prcs")) {
        this.serviceName = "psprcs-jmx-exporter";
        this.tagList.add("domain="+match.group(5));
        this.tagList.add("domain_type=prcs");
      }
      return true;
    }
    else {
      return false;
    }
  }
} 