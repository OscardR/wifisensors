import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimuladorSensorEHealthUDPWifi extends JPanel {
    public static JLabel labelTemperatura = null;
    public static JLabel labelCalma = null;
    public static JLabel labelFecha = null;

    public static double airFlowValue = 0.0;
    public static double ECGValue = 0.0;
    public static double systValue = 0.0;
    public static double diastValue = 0.0;
    public static double glucoseValue = 0.0;
    public static double temperatureValue = 0.0;
    public static double BPMValue = 0.0;
    public static double SPO2Value = 0.0;
    public static double conductanceValue = 0.0;

    public SimuladorSensorEHealthUDPWifi() {
        setLayout(new GridLayout(3, 1));
        getSensorValues();
        labelTemperatura = new JLabel("Temperature = " + temperatureValue);
        labelTemperatura.setFont(new java.awt.Font("Sans", java.awt.Font.BOLD, 63));

        labelCalma = new JLabel("Calma = " + conductanceValue);
        labelCalma.setFont(new java.awt.Font("Sans", java.awt.Font.BOLD, 63));

        labelFecha = new JLabel("Tiempo = " + (new Date()).toString());
        labelFecha.setFont(new java.awt.Font("Sans", java.awt.Font.BOLD, 63));

        add(labelTemperatura);
        add(labelCalma);
        add(labelFecha);
    }

    public static void getSensorValues() {
        airFlowValue = Math.random() * 50.0;
        ECGValue = Math.random() * 50.0;
        systValue = Math.random() * 50.0;
        diastValue = Math.random() * 50.0;
        glucoseValue = Math.random() * 50.0;
        temperatureValue = Math.random() * 12.0 + 30; //* 50.0;
        BPMValue = Math.random() * 50.0;
        SPO2Value = Math.random() * 50.0;
        conductanceValue = Math.random() * 180.0; // * 50.0;
    }

    public static void main(String args[]) {

        JFrame frame = new JFrame("E-TERMOMETER SERVER");

        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });

        frame.setContentPane(new SimuladorSensorEHealthUDPWifi());
        frame.pack();
        frame.setVisible(true);

        try {
            String datosStr = "";
            DatagramSocket unSocket = new DatagramSocket(12346);

            byte[] bufer = new byte[1000];
            while (true) {
                for (int i = 0; i < 1000; i++) {
                    bufer[i] = 0;
                }
                DatagramPacket peticion = new DatagramPacket(bufer, bufer.length);
                System.out.println("Waiting for request");
                unSocket.receive(peticion);

                getSensorValues();

                datosStr = ""
                        + airFlowValue + "#"
                        + ECGValue + "#"
                        + systValue + "#"
                        + diastValue + "#"
                        + glucoseValue + "#"
                        + temperatureValue + "#"
                        + BPMValue + "#"
                        + SPO2Value + "#"
                        + conductanceValue + "#"
                        + (new Date()).toString();
                System.out.println("Sending:" + datosStr);

                DatagramPacket respuesta = new DatagramPacket(datosStr.getBytes(),
                        datosStr.length(), peticion.getAddress(), peticion.getPort());
                unSocket.send(respuesta);

                labelTemperatura.setText("Temperature = " + temperatureValue);
                labelCalma.setText("Calma = " + conductanceValue);


                if (temperatureValue > 38) {
                    labelTemperatura.setForeground(java.awt.Color.RED);
                    java.awt.Toolkit.getDefaultToolkit().beep();
                } else {
                    labelTemperatura.setForeground(java.awt.Color.BLUE);
                }
                Date fecha1 = new Date();
                System.out.println(fecha1.toString() + "#" + temperatureValue + "#" + conductanceValue);
                labelFecha.setText(fecha1.toString());
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}

/*
FORMATO INFORMACION ARDUINO:

 //Data sensor must be sent in this order to mobile android application 
 Serial.print(int(airFlow));     Serial.print("#");
 Serial.print(ECG);              Serial.print("#");
 Serial.print(syst);             Serial.print("#");
 Serial.print(diast);            Serial.print("#");
 Serial.print(int(0));           Serial.print("#"); //Glucose is not implemented yet
 Serial.print(temperature);      Serial.print("#");
 Serial.print(int(BPM));         Serial.print("#");
 Serial.print(int(SPO2));        Serial.print("#");
 Serial.print(conductance);      Serial.print("#");
 Serial.print(int(resistance));  Serial.print("#");
 Serial.print(int(airFlow));     Serial.print("#");
 Serial.print(int(pos));         Serial.print("#");
 Serial.print("\n");    
*/