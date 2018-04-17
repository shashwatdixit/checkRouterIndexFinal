//package service;
//
//import com.jcabi.ssh.SSHByPassword;
//import com.jcabi.ssh.Shell;
//
//
//import java.io.IOException;
//
//public class FetchDataFromDevice {
//    public String sendCommandSSH(String host, String cmd){
//
//        String stdout;
//        try{
//
//            Shell shell1= new SSHByPassword(host,22,"","");
//            stdout = new Shell.Plain(shell1).exec(cmd);
////            System.out.println(stdout);
//            return stdout;
//        }
//        catch(IOException e){
//            e.printStackTrace();
//            stdout="Socket Time Out";
//        }
//        return stdout;
//    }
//}
