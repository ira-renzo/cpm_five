package com.example.cpm_five;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SensorUIController {
    @FXML
    private AreaChart<String, Number> tempChart;
    @FXML
    private AreaChart<String, Number> soundChart;
    private SerialPort comPort;

    @FXML
    public void initialize() {
        XYChart.Series<String, Number> tempSeries = new XYChart.Series<>();
        tempChart.getData().add(tempSeries);
        XYChart.Series<String, Number> soundSeries = new XYChart.Series<>();
        soundChart.getData().add(soundSeries);

        AtomicInteger count = new AtomicInteger(0);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        InputStream in = comPort.getInputStream();

        scheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            try {
                comPort.flushIOBuffers();
                float temp = extractData('T', in);
                float sound = extractData('S', in);
                System.out.println("Temp: " + temp);
                System.out.println("Sound: " + sound);

                if (tempSeries.getData().size() > 100) {
                    tempSeries.getData().remove(0);
                    soundSeries.getData().remove(0);
                }
                tempSeries.getData().add(new XYChart.Data<>(String.valueOf(count.getAndIncrement()), temp));
                soundSeries.getData().add(new XYChart.Data<>(String.valueOf(count.getAndIncrement()), sound));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }), 0, 100, TimeUnit.MILLISECONDS);
    }

    private float extractData(char ch, InputStream in) throws IOException {
        char tmp_ch = (char) in.read();
        while (tmp_ch != ch) {
            tmp_ch = (char) in.read();
        }
        StringBuilder builder = new StringBuilder();
        while (tmp_ch != '\n') {
            builder.append(tmp_ch);
            tmp_ch = (char) in.read();
        }
        return Float.parseFloat(builder.substring(builder.indexOf(":") + 2));
    }

    @FXML
    protected void buttonOnClicked() {
        byte[] bytes = "On\n".getBytes();
        comPort.writeBytes(bytes, bytes.length);
    }

    @FXML
    protected void buttonOffClicked() {
        byte[] bytes = "Off\n".getBytes(StandardCharsets.UTF_8);
        comPort.writeBytes(bytes, bytes.length);
    }
}