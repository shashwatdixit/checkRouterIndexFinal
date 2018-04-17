package controller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class WriteDeviceOutputToFile {

    public String writeToFile(String filename, String outputToWrite, String sourceFilePath) throws IOException{

//      String filePath = sourceFilePath +"/../DeviceOutput".concat(filename);
        Path pathToFile= Paths.get(sourceFilePath);
        Path outputFile= Paths.get(pathToFile.getParent().toString().concat("/"+filename));

        System.out.println("Creating "+outputFile.toString());

        File file =new File(outputFile.toString());

        if(file.createNewFile()){
            System.out.println("File is created");
        }else {
            System.out.println("File already exists");
        }

        //Write Content
        FileWriter writer = new FileWriter(file);
        writer.write(outputToWrite);
        writer.close();
        return outputFile.toString();
    }
}
