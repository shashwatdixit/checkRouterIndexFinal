package service;

import java.io.IOException;
import java.util.List;

public interface ParseQos {

    public List<List<String>> parseServiceDetails(String dirPath, String configPath) throws IOException,javax.xml.bind.JAXBException;




}
