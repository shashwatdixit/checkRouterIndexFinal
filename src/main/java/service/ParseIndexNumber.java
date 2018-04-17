package service;

import com.sonalake.utah.Parser;
import com.sonalake.utah.config.Config;
import com.sonalake.utah.config.ConfigLoader;

import java.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParseIndexNumber {

    public int getDeviceIndexArray(String filepath,String configPath) throws IOException,javax.xml.bind.JAXBException{

        FileReader configFile =new FileReader(configPath);

        BufferedReader reader= new BufferedReader(configFile);
        Config config= new ConfigLoader().loadConfig(reader);

        // load a file and iterate through the records
        List<Map<String, String>> observedValues = new ArrayList<Map<String, String>>();
        try  {
            //hardcoded sample data for test env
            Reader in = new InputStreamReader(new FileInputStream(filepath));
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

//        observedValues.get(0).get("index");

        ArrayList<Integer> listIndexInteger=new ArrayList<Integer>();
        for (int i=0;i<observedValues.size();i++){
            String indexVal=observedValues.get(i).get("index");
            if(indexVal!=null) {
                listIndexInteger.add(Integer.parseInt(observedValues.get(i).get("index")));
            }
            else {
                listIndexInteger.add(0);
            }
        }

        Object object= Collections.max(listIndexInteger);

        int maxIndex=(Integer) object;

        return maxIndex;
    }

}
