
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import controller.ReadCsvPath;
import controller.WriteDeviceOutputToFile;

import org.apache.sshd.client.ClientFactoryManager;
import org.apache.sshd.client.SshClient;

import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

import org.apache.sshd.common.FactoryManager;
import org.apache.sshd.common.PropertyResolverUtils;

import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.common.channel.SttySupport;
import org.apache.sshd.common.util.io.NoCloseInputStream;
import org.apache.sshd.common.util.io.NoCloseOutputStream;
import org.bouncycastle.util.io.TeeOutputStream;
import service.ParseIndexNumber;

import java.io.*;
import java.nio.charset.Charset;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;




public class MainClass {

    public static void main(String[] args) throws IOException{

//        FetchDataFromDevice fetchDataFromDevice = new FetchDataFromDevice();

        System.out.println("Enter File Path :");
        Scanner scanner = new Scanner(System.in);

        String sourceFilePath = scanner.nextLine();

//      Test Path for File
//      String sourceFilePath ="D://Springframework/Spring5/checkRouterIndex/src/main/java/domain/sample.csv";
        ReadCsvPath readCsvPath = new ReadCsvPath(sourceFilePath);
        Path pathToFile= Paths.get(sourceFilePath);

//      String fileOutputPath = filepath.concat("/../outputIndex.csv");
        Path configFile= Paths.get(pathToFile.getParent().toString().concat("/configtest.xml"));
        WriteDeviceOutputToFile writeDeviceOutputToFile= new WriteDeviceOutputToFile();

        List<String[]> ipList=readCsvPath.getIpList();


        for (String[] ip:ipList) {

            System.out.println("Trying..."+ip[0]);

            String deviceOutput="Timeout: 10 sec Device Unreachable , Please check manual connectivity";


           SshClient client=SshClient.setUpDefaultClient();
           client.start();
            try {


                ClientSession session = client.connect("", ip[0], 22).verify(35000).getSession();

                session.addPasswordIdentity(""); // for password-based authentication

                session.auth().verify(15000);



                // start using the session to run commands, do SCP/SFTP, create local/remote port forwarding, etc...

                final long idleTimeoutValue = TimeUnit.SECONDS.toMillis(20L);
                PropertyResolverUtils.updateProperty(session,
                        FactoryManager.IDLE_TIMEOUT, idleTimeoutValue);
                PropertyResolverUtils.updateProperty(client, ClientFactoryManager.HEARTBEAT_INTERVAL, TimeUnit.SECONDS.toMillis(2L));


                //Try Using Exec



////                //Command 1
//                ClientChannel channel2= session.createExecChannel("screen-length 0 temporary");
//
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                ByteArrayOutputStream errStream= new ByteArrayOutputStream();
//
//                channel2.setOut(outputStream);
//                channel2.setErr(errStream);
//                channel2.open().await(1000);
//                channel2.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),TimeUnit.SECONDS.toMillis(1L));
//
//                System.out.println(new String(outputStream.toByteArray()));
//                channel2.close()


//                try using shell//

                 String TTY =
                        "speed 9600 baud; 36 rows; 180 columns;\n" +
                                "lflags: icanon isig iexten echo echoe -echok echoke -echonl echoctl\n" +
                                "\t-echoprt -altwerase -noflsh -tostop -flusho pendin -nokerninfo\n" +
                                "\t-extproc\n" +
                                "iflags: -istrip icrnl -inlcr -igncr ixon -ixoff ixany imaxbel iutf8\n" +
                                "\t-ignbrk brkint -inpck -ignpar -parmrk\n" +
                                "oflags: opost onlcr -oxtabs -onocr -onlret\n" +
                                "cflags: cread cs8 -parenb -parodd hupcl -clocal -cstopb -crtscts -dsrflow\n" +
                                "\t-dtrflow -mdmbuf\n" +
                                "cchars: discard = ^O; dsusp = ^Y; eof = ^D; eol = <undef>;\n" +
                                "\teol2 = <undef>; erase = ^?; intr = ^C; kill = ^U; lnext = ^V;\n" +
                                "\tmin = 1; quit = ^\\; reprint = ^R; start = ^Q; status = ^T;\n" +
                                "\tstop = ^S; susp = ^Z; time = 0; werase = ^W;";

                Map<PtyMode, Integer> tty = SttySupport.parsePtyModes(TTY);

//                ClientChannel channel1= session.createShellChannel();
                ChannelShell channel1 = (ChannelShell) session.createShellChannel();
                channel1.setPtyModes(tty);


                ByteArrayOutputStream sent = new ByteArrayOutputStream();
                PipedOutputStream pipedIn = new PipedOutputStream();
                PipedInputStream pipedOut = new PipedInputStream(pipedIn);

                channel1.setIn(new NoCloseInputStream(pipedOut));

                OutputStream teeOut = new TeeOutputStream(sent, pipedIn);
                ByteArrayOutputStream outShell = new ByteArrayOutputStream();
                ByteArrayOutputStream errShell = new ByteArrayOutputStream();

                channel1.setOut(new NoCloseOutputStream(outShell));
                channel1.setErr(new NoCloseOutputStream(errShell));
                channel1.open().await(10000);
                channel1.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),TimeUnit.SECONDS.toMillis(8L));


                teeOut.write("\r\rscreen-length 0 temporary\r\r".getBytes(Charset.forName("UTF8")));
                teeOut.flush();


                teeOut.write("display current configuration | include index\r\r\r\r".getBytes(Charset.forName("UTF8")));
                teeOut.flush();


                //Timeout wait for single Command
                channel1.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),TimeUnit.SECONDS.toMillis(8L));
                System.out.println(new String(outShell.toByteArray()));

                //Write to Device output

                deviceOutput=new String(outShell.toByteArray());
                session.close();



            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                client.stop();
            }

            //Write data to File
            String  deviceOutputFilePath=writeDeviceOutputToFile.writeToFile(ip[0],deviceOutput,sourceFilePath);

            ParseIndexNumber parseIndexNumber = new ParseIndexNumber();

            try {
                ip[1]=String.valueOf(parseIndexNumber.getDeviceIndexArray(deviceOutputFilePath,configFile.toString()));
            }catch (javax.xml.bind.JAXBException e){
                e.printStackTrace();
            }

        }

        readCsvPath.writeCsvOutput(ipList);

        System.out.println("Ran Completely");

    }
}
