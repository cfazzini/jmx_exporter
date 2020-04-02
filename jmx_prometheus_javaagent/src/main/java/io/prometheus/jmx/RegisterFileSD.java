// package io.prometheus.jmx;

// import java.util.Arrays;
// import java.util.List;
// import java.net.InetAddress;

// public class RegisterFileSD extends ConsulClient{
  
//   String ;
//   String serviceName;
//   int servicePort;
//   Arrays labels;

//   public RegisterFileSD() {


//   }
//   public void registerService(int servicePort, String serviceName, List<String> tags) {
//           // try {
//         //     // create json object
//         //     JSONObject postJsonObj = new JSONObject();
//         //     Map labelsMap = new LinkedHashMap();
//         //     // TODO get hostname/IP
//         //     postJsonObj.put("target","test:9999");
//         //     // TODO make loop for any custom labels
//         //     labelsMap.put("labelkey1","labelvalue2");
//         //     labelsMap.put("labelkey2", "labelvalue2");
//         //     postJsonObj.put("labels", labelsMap);

//         //     // TODO URL 
//         //     URL url = new URL("http://oc-docker1.ongov.net:5000/targets");
//         //     HttpURLConnection httpConnection  = (HttpURLConnection) url.openConnection();
//         //     httpConnection.setDoOutput(true);
//         //     httpConnection.setRequestMethod("POST");
//         //     httpConnection.setRequestProperty("Content-Type", "application/json");

//         //     DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
//         //     wr.write(postJsonObj.toString().getBytes());
//         //     Integer responseCode = httpConnection.getResponseCode();
//         // //     System.err.println("STDERR response code: " + responseCode);
//         // //     System.out.println("STDOUT response code: " + responseCode);

//         // //     // BufferedReader bufferedReader;
//         // //     // // Creates a reader buffer
//         // //     // if (responseCode > 199 && responseCode < 300) {
//         // //     //     bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
//         // //     // } else {
//         // //     //     bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
//         // //     // }

//         // //     // // To receive the response
//         // //     // StringBuilder content = new StringBuilder();
//         // //     // String line;
//         // //     // while ((line = bufferedReader.readLine()) != null) {
//         // //     //     content.append(line).append("\n");
//         // //     // }
//         // //     // bufferedReader.close();

//   }

// }