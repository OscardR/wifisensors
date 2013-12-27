package com.oscargomez.wifisensors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class SensoresFragment extends ItemDetailFragment {

    private class Sensores {
        private String temperatura;
        private String calma;
        private String fecha;

        public String getTemperatura() {
            return temperatura;
        }

        public String getCalma() {
            return calma;
        }

        public String getFecha() {
            return fecha;
        }

        public void setTemperatura(String temperatura) {
            this.temperatura = temperatura;
        }

        public void setCalma(String calma) {
            this.calma = calma;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }
    }

    Handler uiHandler = new Handler();
    Thread readSensorsThread;

    //private static final String IP_ADDRESS = "10.1.19.33";
    private static final String IP_ADDRESS = "192.168.1.5";
    public ProgressBar barTemperatura, barCalma, barTiempo;
    public TextView txtTemperatura, txtTemperaturaTitle, txtCalma, txtCalmaTitle, txtTiempo, txtTiempoTitle;
    public Activity act;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SensoresFragment() {
        super();
    }

    public SensoresFragment(Context ctx) {
        super(ctx);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("wifisensors", "context: " + context);
        makeToast("Iniciando Sensores...");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        act = getActivity();

        // UI
        barTemperatura = (ProgressBar) rootView.findViewById(R.id.barTemperatura);
        barCalma = (ProgressBar) rootView.findViewById(R.id.barCalma);
        barTiempo = (ProgressBar) rootView.findViewById(R.id.barTiempo);
        txtCalma = (TextView) rootView.findViewById(R.id.txtCalma);
        txtCalmaTitle = (TextView) rootView.findViewById(R.id.txtCalmaTitle);
        txtTiempo = (TextView) rootView.findViewById(R.id.txtTiempo);
        txtTiempoTitle = (TextView) rootView.findViewById(R.id.txtTiempoTitle);
        txtTemperatura = (TextView) rootView.findViewById(R.id.txtTemperatura);
        txtTemperaturaTitle = (TextView) rootView.findViewById(R.id.txtTemperaturaTitle);

        readSensors();

        return rootView;
    }

    private void readSensors() {
        makeToast("Leyendo Sensores...");

        readSensorsThread = new Thread() {
            @Override
            public void run() {
                try {
                    String datosStr = "";
                    DatagramSocket unSocket = new DatagramSocket(12345);


                    byte[] bufer = new byte[1000];
                    while (true) {
                        for (int i = 0; i < 1000; i++) {
                            bufer[i] = 0;
                        }

                        DatagramPacket peticion = new DatagramPacket(bufer, 0, 1000, InetAddress.getByName(IP_ADDRESS), 12346);
                        Log.d("wifisensors", "Sending packet to get sensor values");
                        unSocket.send(peticion);

                        DatagramPacket respuesta = new DatagramPacket(bufer, 1000);
                        Log.d("wifisensors", "Waiting for response from the server");
                        unSocket.receive(respuesta);

                        datosStr = new String(bufer);
                        //Log.d("wifisensors", "datosStr: " + datosStr);

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
                        sensores.setFecha(fecha1.toString());

                        Runnable uiUpdater = new Runnable() {
                            public void run() {
                                setTemperatura(sensores.getTemperatura());
                                setCalma(sensores.getCalma());
                                setFecha(sensores.getFecha());
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

                        Thread.sleep(1000);
                    }
                } catch (SocketException e) {
                    Log.d("wifisensors", "Socket: " + e.getMessage());
                } catch (IOException e) {
                    Log.d("wifisensors", "IO: " + e.getMessage());
                } catch (InterruptedException e) {
                    Log.d("wifisensors", e.getMessage());
                }
            }
        };
        readSensorsThread.start();
        makeToast("Sensores Lanzados!");
    }

    private void setFecha(String s) {
        try {
            txtTiempo.setText(s.toString());
            barTiempo.setProgress(0);
        } catch (NullPointerException npe) {
            Toast.makeText(getActivity(), "NullPointerException en setFecha, neng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setCalma(String conductance) {
        try {
            txtCalma.setText("" + conductance + "mV");
            barCalma.setProgress(Integer.getInteger(conductance));
        } catch (NullPointerException npe) {
            Toast.makeText(getActivity(), "NullPointerException en setCalma, neng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTemperatura(String temperature) {
        try {
            txtTemperatura.setText("" + temperature + "º");
            barTemperatura.setProgress(Integer.getInteger(temperature));

            float tempFloat = Float.parseFloat(temperature);
            if (tempFloat > 38) {
                txtTemperatura.setTextColor(Color.RED);
            } else {
                txtTemperatura.setTextColor(Color.BLUE);
            }
        } catch (NullPointerException npe) {
            Toast.makeText(getActivity(), "NullPointerException en setTemperatura, neng!", Toast.LENGTH_SHORT).show();
            Log.d("wifisensors", "No hay txtTemperatura!");
        }
    }
}
