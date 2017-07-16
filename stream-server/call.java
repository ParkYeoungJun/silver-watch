import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.lang.*;
class Server {

AudioInputStream audioInputStream;
static AudioInputStream ais;
static AudioFormat format;
static boolean status = true;
static int port = 50005;
static int sampleRate = 16000;

static DataLine.Info dataLineInfo;
static SourceDataLine sourceDataLine;

public static void main(String args[]) throws Exception {
/*	
	String command = "LD_LIBRARY_PATH=/home/pi/mjpg-streamer/mjpg-streamer-experimental/ mjpg_streamer -i \"input_uvc.so -d /dev/video0 -n -f 30\" -o \"output_http.so -w /home/pi/mjpg-streamer/mjpg-streamer-experimental/www";
	String command = "ls";
	try{
	Runtime rt = Runtime.getRuntime();
	Process p = rt.exec(command);	
	Process process = Runtime.getRuntime().exec(command);
	process.waitFor();
	System.out.println(process.exitValue());
	process.destroy();
	} catch(Exception e){
		e.printStackTrace();
	}

	System.out.println("shell command complete");
*/
	DatagramSocket serverSocket = new DatagramSocket(port);

    /**
     * Formula for lag = (byte_size/sample_rate)*2
     * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
     * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
     * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
     */

    byte[] receiveData = new byte[4096];

    format = new AudioFormat(sampleRate, 16, 1, true, false);
    dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
    sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
    sourceDataLine.open(format);
    sourceDataLine.start();

    FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
    volumeControl.setValue(1.00f);

    DatagramPacket receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
    ByteArrayInputStream baiss = new ByteArrayInputStream(
            receivePacket.getData());
    while (status == true) {
        serverSocket.receive(receivePacket);
        ais = new AudioInputStream(baiss, format, receivePacket.getLength());
        toSpeaker(receivePacket.getData());
    }
    sourceDataLine.drain();
    sourceDataLine.close();
}

public static void toSpeaker(byte soundbytes[]) {
    try {
        sourceDataLine.write(soundbytes, 0, soundbytes.length);
    } catch (Exception e) {
        System.out.println("Not working in speakers...");
        e.printStackTrace();
    }
}
}
