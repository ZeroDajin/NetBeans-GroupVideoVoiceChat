package VideoCallClient;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class VideoCallClient {
    private JLabel myVideoLabel;
    private JLabel receivedVideoLabel;
    private VideoCapture videoCapture;
    private InetAddress otherUserAddress;
    private DatagramSocket datagramSocket;
    private int port;

    public VideoCallClient(JLabel myVideoLabel, JLabel receivedVideoLabel) throws SocketException {
        this.myVideoLabel = myVideoLabel;
        this.receivedVideoLabel = receivedVideoLabel;
        this.datagramSocket = new DatagramSocket(6865);
        this.videoCapture = new VideoCapture(0);
        try {
            datagramSocket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendVideoToOtherUser(byte[] videoData, InetAddress partnerAddress, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(videoData, videoData.length, partnerAddress, port);
            datagramSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public byte[] receiveVideoFromOtherUser() {
        try {
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);
            return Arrays.copyOf(buffer, packet.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void startOwnWebcam() {
        if (videoCapture.isOpened()) {
            Mat webcamImage = new Mat();
            videoCapture.read(webcamImage);
            if (!webcamImage.empty()) {
                BufferedImage image = matToBufferedImage(webcamImage);
                displayOnLabel(myVideoLabel, image);
            }
        }
    }
    
    public void displayReceivedVideo(BufferedImage image) {
        displayOnLabel(receivedVideoLabel, image);
    }
    public void stop() {
        videoCapture.release();
    }
    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.data().get(b);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    private void displayOnLabel(JLabel label, BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);
        label.setIcon(icon);
    }
    public Mat captureVideoFrame() {
        Mat frame = new Mat();
        if (videoCapture.read(frame)) {
            return frame;
        } else {
            return null;
        }
    }
public byte[] convertVideoFrameToByteArray(Mat frame) {
    int dataSize = frame.cols() * frame.rows() * (int)frame.elemSize();
    byte[] videoData = new byte[dataSize];
    frame.data().get(videoData);
    return videoData;
}


}

