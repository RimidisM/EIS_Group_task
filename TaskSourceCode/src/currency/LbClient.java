package currency;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rimid
 */
public class LbClient {

    private final URL _url;
    private Map<String, String> _requestParameetrs;
    private XmlParser _xmlParser;

    public LbClient(String url) throws MalformedURLException {
        _requestParameetrs = new HashMap<>();
        _url = new URL(url);
        _xmlParser = new XmlParser();
    }

    public List<CurencyValueModel> getData() throws IOException {
        // URLConnection conn = url.openConnection();
        HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        //defining an HTTP request type
        connection.setRequestMethod("GET");
        // Set the doOutput flag to true
        connection.setDoOutput(true);

        byte[] requestDataBytes = formRequestData();

        // Get the output stream of the connection instance
        // and add the parameter to the request
        try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            writer.write(requestDataBytes);

            // Always flush and close
            writer.flush();
            writer.close();

            // To store our response
            StringBuilder content;

            // Get the input stream of the connection
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {

                content = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());

                }
                line = content.toString();

                List<CurencyValueModel> result = _xmlParser.parseData(line);

                return result;
            }

        } finally {
            connection.disconnect();
        }
    }

    public void addParameters(String parameterName, String parameterValue) {
        _requestParameetrs.put(parameterName, parameterValue);
    }

    public void resetParameters() {
        _requestParameetrs = new HashMap<>();
    }

    private byte[] formRequestData() throws UnsupportedEncodingException {
        // Instantiate a requestData object to store our data
        StringBuilder requestData = new StringBuilder();

        for (Map.Entry<String, String> param : _requestParameetrs.entrySet()) {
            if (requestData.length() != 0) {
                requestData.append('&');
            }
            // Encode the parameter based on the parameter map we've defined
            // and append the values from the map to form a single parameter
            requestData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            requestData.append('=');
            requestData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        // Convert the requestData into bytes 
        byte[] requestDataBytes = requestData.toString().getBytes("UTF-8");
        return requestDataBytes;
    }

}
