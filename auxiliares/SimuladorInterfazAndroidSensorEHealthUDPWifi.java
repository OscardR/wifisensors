package com.oscargomez.auxiliares;

import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimuladorInterfazAndroidSensorEHealthUDPWifi extends JPanel {
    public static JLabel labelTemperatura=null;
    public static JLabel labelCalma=null;
    public static JLabel labelFecha=null;
    
    
    
    public SimuladorInterfazAndroidSensorEHealthUDPWifi(){
        setLayout(new GridLayout(3,1));
        labelTemperatura=new JLabel("TEMPERATURE= CONECTANDO...");
        labelTemperatura.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 63));

        labelCalma=new JLabel("CALMA= CONECTANDO...");
        labelCalma.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 63));

        labelFecha=new JLabel("TIEMPO= CONECTANDO...");
        labelFecha.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 63));

        
        add(labelTemperatura);
        add(labelCalma);
        add(labelFecha);
    }
    
	public static void main(String args[]){
        
        JFrame frame = new JFrame("E-TERMOMETER ANDROID INTERFACE");
        
        frame.addWindowListener(new WindowAdapter(){
            
            
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        
        });
        
        frame.setContentPane(new SimuladorInterfazAndroidSensorEHealthUDPWifi());
        frame.pack();
        frame.setVisible(true);
        
	  try{
          String datosStr="";
	      DatagramSocket unSocket = new DatagramSocket(12345);
		  
		  
	      byte[] bufer = new byte[1000];
 	      while(true){
              for (int i=0; i<1000;i++){
                  bufer[i]=0;
              }
			  
			  DatagramPacket peticion = new DatagramPacket(bufer, 0, 1000, InetAddress.getByName("localhost"), 12346);
			  System.out.println("Sending packet to get sensor values");
			  unSocket.send(peticion);
			  
 	          DatagramPacket respuesta = new DatagramPacket(bufer, 1000);
			  System.out.println("Waiting for response from the server");
  	          unSocket.receive(respuesta);
    	          
              datosStr= new String(bufer);
              //System.out.println(datosStr);
              StringTokenizer tokens = new StringTokenizer(datosStr,"#");
              
              String airFlow = tokens.nextToken();
              System.out.println("AIRFLOW="+airFlow);
              
              String ECG = tokens.nextToken();
              System.out.println("ECG="+ECG);
              
              String systStr= tokens.nextToken();
              System.out.println("systStr="+systStr);
              
              String diast = tokens.nextToken();
              System.out.println("diast="+diast);
              
              String glucose = tokens.nextToken();
              System.out.println("glucose="+glucose);
              
              String temperature = tokens.nextToken();
              System.out.println("temperature="+temperature);
              
              String BPM = tokens.nextToken();
              System.out.println("BPM="+BPM);     
              
              String SPO2 = tokens.nextToken();
              System.out.println("SPO2="+SPO2);
              
              String conductance = tokens.nextToken();
              System.out.println("conductance="+conductance);
              
              labelTemperatura.setText("TEMPERATURE="+temperature);
              labelCalma.setText("CALMA="+conductance);
              
              
              float tempFloat=Float.parseFloat(temperature);
              
              if (tempFloat>38){
                  labelTemperatura.setForeground(java.awt.Color.RED);
                  java.awt.Toolkit.getDefaultToolkit().beep();
              }else{
                  labelTemperatura.setForeground(java.awt.Color.BLUE);
              }
              Date fecha1 = new Date ();
              System.out.println(fecha1.toString()+"#"+temperature+"#"+conductance);
              labelFecha.setText(fecha1.toString());
            }
	    }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
	    }catch (IOException e) {System.out.println("IO: " + e.getMessage());}
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