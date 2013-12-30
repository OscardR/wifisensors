package com.oscargomez.wifisensors;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class SensoresFragment extends ItemDetailFragment {

    Handler uiHandler = new Handler();
    SensoresThread readSensorsThread;

    //private static final String IP_ADDRESS = "10.1.19.33";
    private static final String IP_ADDRESS = "192.168.1.5";
    public ProgressBar barTemperatura, barCalma, barTiempo;
    public TextView txtTemperatura, txtTemperaturaTitle, txtCalma, txtCalmaTitle, txtTiempo, txtTiempoTitle;
    public Activity act;
    View rootView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SensoresFragment() {
        super();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("wifisensors", "SensoresFragment.context: " + context);
        makeToast("Iniciando Sensores...");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // UI
        txtCalma = (TextView) rootView.findViewById(R.id.txtCalma);
        txtCalmaTitle = (TextView) rootView.findViewById(R.id.txtCalmaTitle);
        txtTiempo = (TextView) rootView.findViewById(R.id.txtTiempo);
        txtTiempoTitle = (TextView) rootView.findViewById(R.id.txtTiempoTitle);
        txtTemperatura = (TextView) rootView.findViewById(R.id.txtTemperatura);
        txtTemperaturaTitle = (TextView) rootView.findViewById(R.id.txtTemperaturaTitle);
        barTemperatura = (ProgressBar) rootView.findViewById(R.id.barTemperatura);
        barCalma = (ProgressBar) rootView.findViewById(R.id.barCalma);
        barTiempo = (ProgressBar) rootView.findViewById(R.id.barTiempo);

        readSensors();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (readSensorsThread != null) {
            readSensorsThread.terminate();
        }
    }

    private void readSensors() {
        makeToast("Leyendo Sensores...");
        readSensorsThread = new SensoresThread();
        readSensorsThread.start();
        makeToast("Sensores Lanzados!");
    }

    private void updateUI(Sensores sensores) {

        int prgCalma, prgTemperatura, prgTiempo;

        prgCalma = (int) Math.floor(sensores.getCalma());
        prgTemperatura = (int) Math.floor(sensores.getTemperatura()) - 30;

        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(sensores.getFecha());   // assigns calendar to given date
        prgTiempo = calendar.get(Calendar.HOUR_OF_DAY);

        try {
            txtTemperatura.setText("" + sensores.getTemperatura() + "º");
            barTemperatura.setProgress(prgTemperatura);

            if (prgTemperatura > 38)
                txtTemperatura.setTextColor(Color.RED);
            else
                txtTemperatura.setTextColor(Color.BLUE);

            txtCalma.setText("" + sensores.getCalma() + "mV");
            barCalma.setProgress(prgCalma);

            txtTiempo.setText(sensores.getFecha().toString());
            barTiempo.setProgress(prgTiempo);
        } catch (NullPointerException npe) {
            Log.e("wifisensors", "NullPointerException!: " + npe.getMessage());
        }
    }

    private class Sensores {
        private Float temperatura;
        private Float calma;
        private Date fecha;

        public Float getTemperatura() {
            return temperatura;
        }

        public Float getCalma() {
            return calma;
        }

        public Date getFecha() {
            return fecha;
        }

        public void setTemperatura(String temperatura) {
            this.temperatura = Float.parseFloat(temperatura);
        }

        public void setCalma(String calma) {
            this.calma = Float.parseFloat(calma);
        }

        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }
    }

    private class SensoresThread extends Thread {
        private boolean running = true;
        DatagramSocket socket;

        public void terminate() {
            running = false;
        }

        @Override
        public void run() {

            String datosStr = "";
            try {
                socket = new DatagramSocket(12345);
            } catch (SocketException e) {
                makeToast("No se puede crear el socket!");
                running = false;
            }

            byte[] bufer = new byte[1000];
            while (running) {
                try {
                    for (int i = 0; i < 1000; i++) {
                        bufer[i] = 0;
                    }

                    Thread.sleep(1000);

                    DatagramPacket peticion = new DatagramPacket(bufer, 0, 1000, InetAddress.getByName(IP_ADDRESS), 12346);
                    //Log.d("wifisensors", "Mandando paquete...");
                    socket.send(peticion);

                    DatagramPacket respuesta = new DatagramPacket(bufer, 1000);
                    //Log.d("wifisensors", "Esperando respuesta...");
                    socket.receive(respuesta);

                    datosStr = new String(bufer);
                    StringTokenizer tokens = new StringTokenizer(datosStr, "#");

                    String airFlow = tokens.nextToken();
                    String ECG = tokens.nextToken();
                    String systStr = tokens.nextToken();
                    String diast = tokens.nextToken();
                    String glucose = tokens.nextToken();
                    String temperature = tokens.nextToken();
                    String BPM = tokens.nextToken();
                    String SPO2 = tokens.nextToken();
                    String conductance = tokens.nextToken();
                    Date fecha1 = new Date();

                    // Usar el handler y el objeto wrapper para actualizar la UI desde el loop principal
                    final Sensores sensores = new Sensores();
                    sensores.setTemperatura(temperature);
                    sensores.setCalma(conductance);
                    sensores.setFecha(fecha1);

                    Runnable uiUpdater = new Runnable() {
                        @Override
                        public void run() {
                            updateUI(sensores);
                        }
                    };
                    uiHandler.post(uiUpdater);

                    //Message completeMessage = uiHandler.obtainMessage(0, sensores);
                    //completeMessage.sendToTarget();
                    //uiHandler.sendMessageDelayed(completeMessage, 1000);

                    /*Log.d("wifisensors", "AIRFLOW=" + airFlow);
                    Log.d("wifisensors", "ECG=" + ECG);
                    Log.d("wifisensors", "systStr=" + systStr);
                    Log.d("wifisensors", "diast=" + diast);
                    Log.d("wifisensors", "glucose=" + glucose);
                    Log.d("wifisensors", "temperature=" + temperature);
                    Log.d("wifisensors", "BPM=" + BPM);
                    Log.d("wifisensors", "SPO2=" + SPO2);
                    Log.d("wifisensors", "conductance=" + conductance);
                    Log.d("wifisensors", fecha1.toString() + "#" + temperature + "#" + conductance);*/

                } catch (IOException e) {
                    Log.d("wifisensors", "IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    Log.d("wifisensors", "Interrupt: " + e.getMessage());
                    running = false;
                }
            }
            socket.close();
        }
    }
}