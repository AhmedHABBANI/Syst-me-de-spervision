package server;

public class TcpServerMain {

    public static void main(String[] args) {

        AlertStore store = new AlertStore();
        TcpAlertReceiver tcpReceiver = new TcpAlertReceiver(8888, store, null, null);

        Thread tcpThread = new Thread(tcpReceiver);
        tcpThread.start();
    }
}
