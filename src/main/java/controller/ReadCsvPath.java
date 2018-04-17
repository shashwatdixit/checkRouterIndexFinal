package controller;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadCsvPath {

    private String filepath;
    private CSVReader csvReader;
    private CSVWriter csvWriter;

    public ReadCsvPath(String filepath) throws IOException {
        this.filepath = filepath;
        try {
            csvReader = new CSVReader(new FileReader(filepath));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        Path pathToFile= Paths.get(filepath);

//      String fileOutputPath = filepath.concat("/../outputIndex.csv");
        Path outputFile= Paths.get(pathToFile.getParent().toString().concat("/outputIndex.csv"));

        System.out.println("Creating "+ outputFile.toString());

        csvWriter=new CSVWriter(new FileWriter(outputFile.toString()));

    }

    public List<String[]> getIpList(){

        List<String[]> ipAddressList = new ArrayList<String[]>();
        try{
            List<String[]> ipList = csvReader.readAll();
            ipAddressList=ipList;
        }catch (IOException e){
            e.printStackTrace();
        }
        return ipAddressList;
    }


    public void writeCsvOutput(List<String[]> ipIndexList) throws IOException{
        csvWriter.writeAll(ipIndexList);
        csvWriter.close();
    }
}
