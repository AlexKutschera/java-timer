package alexa;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;

public class Main extends Application {

    @FXML
    private static Label label_full, label_current, label_date, label_time;
    @FXML
    private static Button button_reset_full, button_reset_current, button_pause;

    private static boolean paused = true;
    private static boolean launched = false;

    private static long fullAdder = 0, fullStart, currentAdder = 0, currentStart;

    public static void main(String[] args) {
        loop.setDaemon(true);
        loop.start();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Timer");
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("timer.png")));

        Parent root = FXMLLoader.load(getClass().getResource("timer.fxml"));

        label_full = (Label) root.lookup("#label_full");
        label_current = (Label) root.lookup("#label_current");

        label_date = (Label) root.lookup("#label_date");
        label_time = (Label) root.lookup("#label_time");

        button_pause = (Button) root.lookup("#button_pause");
        button_pause.setOnMouseClicked(event -> {
            if (paused){
                button_pause.setText("PAUSE");
                button_pause.setStyle("-fx-background-color:#d8632d");
                fullStart = System.currentTimeMillis();
                currentStart = System.currentTimeMillis();
                paused = false;
            } else {
                button_pause.setText("WEITER");
                button_pause.setStyle("-fx-background-color: #48a821");
                fullAdder += System.currentTimeMillis() - fullStart;
                currentAdder += System.currentTimeMillis() - currentStart;
                fullStart = System.currentTimeMillis();
                currentStart = System.currentTimeMillis();
                paused = true;
            }
        });

        button_reset_full = (Button) root.lookup("#button_reset_full");
        button_reset_full.setOnMouseClicked(event -> {
            button_pause.setText("STARTEN");
            button_pause.setStyle("-fx-background-color: #48a821");
            label_full.setText("00:00:00:000");
            label_current.setText("00:00:00:000");
            fullAdder = 0;
            currentAdder = 0;
            paused = true;
        });

        button_reset_current = (Button) root.lookup("#button_reset_current");
        button_reset_current.setOnMouseClicked(event -> {
            currentAdder = 0;
            currentStart = System.currentTimeMillis();
        });

        Scene scene = new Scene(root, 300, 90);
        primaryStage.setScene(scene);
        primaryStage.show();

        launched = true;
    }

    private static void tick(){
        DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        DecimalFormat format2 = new DecimalFormat("00");
        DecimalFormat format3 = new DecimalFormat("000");

        label_date.setText(formatDate.format(new Date()));
        label_time.setText(formatTime.format(new Date()));

        if (!paused){
            long fullCounter = System.currentTimeMillis() - fullStart + fullAdder;
            long currentCounter = System.currentTimeMillis() - currentStart + currentAdder;

            int fullMilliseconds = (int) (fullCounter % 1000);
            int fullSeconds = (int) (fullCounter / 1000 % 60);
            int fullMinutes = (int) (fullCounter / 1000 / 60 % 60);
            int fullHours = (int) (fullCounter / 1000 / 60 / 60 % 24);

            int currentMilliseconds = (int) (currentCounter % 1000);
            int currentSeconds = (int) (currentCounter / 1000 % 60);
            int currentMinutes = (int) (currentCounter / 1000 / 60 % 60);
            int currentHours = (int) (currentCounter / 1000 / 60 / 60 % 24);

            label_full.setText(format2.format(fullHours) + ":" + format2.format(fullMinutes) + ":" + format2.format(fullSeconds) + ":" + format3.format(fullMilliseconds));
            label_current.setText(format2.format(currentHours) + ":" + format2.format(currentMinutes) + ":" + format2.format(currentSeconds) + ":" + format3.format(currentMilliseconds));
        }
    }

    private static Thread loop = new Thread(() -> {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true){
            try {
                Platform.runLater(() -> {
                    if (launched){
                        tick();
                    }
                });
                TimeUnit.MILLISECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
}
