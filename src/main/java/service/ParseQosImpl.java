package service;

import com.sonalake.utah.Parser;
import com.sonalake.utah.config.Config;
import com.sonalake.utah.config.ConfigLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParseQosImpl implements ParseQos {
    @Override
    public List<List<String>> parseServiceDetails(String dirPath,String configPath) throws IOException,javax.xml.bind.JAXBException {

        List<List<String>> deviceData = new ArrayList<List<String>>();

        File[] files = new File(dirPath).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile()) {

                FileReader configFile =new FileReader(configPath);

                BufferedReader reader= new BufferedReader(configFile);
                Config config= new ConfigLoader().loadConfig(reader);

                // load a file and iterate through the records
                List<Map<String, String>> observedValues = new ArrayList<Map<String, String>>();
                try  {
                    //hardcoded sample data for test env
                    Reader in = new InputStreamReader(new FileInputStream(file.getPath()));
                    Parser parser = Parser.parse(config, in);
                    while (true) {
                        Map<String, String> record = parser.next();
                        if (null == record) {
                            break;
                        } else {
                            observedValues.add(record);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                for (int i=0;i<observedValues.size();i++){
                    System.out.println(observedValues.get(i).get("description"));
                    System.out.println(observedValues.get(i).get("bandwidth"));
                    String serviceDescription=observedValues.get(i).get("description");
                    String interfaceDetail=observedValues.get(i).get("interface");
                    String bandwidth = observedValues.get(i).get("bandwidth");
                    String servicePolicyInputDetail=observedValues.get(i).get("service-policy input");
                    String servicePolicyOutputDetail=observedValues.get(i).get("service-policy output");

                    if(serviceDescription!=null&&serviceDescription.contains("-ML3-")){
                        List<String> record = new ArrayList<String>();
                        record.add(interfaceDetail);
                        record.add(serviceDescription);
                        record.add(bandwidth);
                        record.add(servicePolicyInputDetail);
                        record.add(servicePolicyOutputDetail);
                        deviceData.add(record);
                    }

                }

            }
        }

        return deviceData;
    }
}
