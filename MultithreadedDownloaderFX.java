import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class MultithreadedDownloaderFX extends Application {

    private final VBox downloadsBox = new VBox(10);
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Multithreaded File Downloader");

        TextField urlField = new TextField();
        urlField.setPromptText("Enter file URL");

        Button addButton = new Button("Download");
        addButton.setOnAction(e -> {
            String url = urlField.getText();
            if (!url.isEmpty()) {
                DownloadTask task = new DownloadTask(url);
                DownloadPane pane = new DownloadPane(task);
                downloadsBox.getChildren().add(pane.getPane());
                executor.submit(task);
                urlField.clear();
            }
        });

        HBox inputBox = new HBox(10, urlField, addButton);
        inputBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(downloadsBox);
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(10, inputBox, scrollPane);
        root.setPadding(new Insets(10));

        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // =================== Download Task ==========================
    class DownloadTask extends Task<Void> {
        private final String url;
        private final String fileName;
        private final StringProperty speed = new SimpleStringProperty("");
        private final StringProperty timeLeft = new SimpleStringProperty("");
        private final BooleanProperty paused = new SimpleBooleanProperty(false);
        private final BooleanProperty cancelled = new SimpleBooleanProperty(false);

        public DownloadTask(String url) {
            this.url = url;
            this.fileName = url.substring(url.lastIndexOf("/") + 1);
        }

        public StringProperty speedProperty() { return speed; }
        public StringProperty timeLeftProperty() { return timeLeft; }
        public BooleanProperty pausedProperty() { return paused; }

        public void pause() { paused.set(true); }
        public void resumeDownload() { paused.set(false); }
        public void cancelDownload() { cancelled.set(true); }

        @Override
        protected Void call() {
            try {
                URL downloadURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) downloadURL.openConnection();
                int contentLength = connection.getContentLength();
                if (contentLength < 0) {
                    updateMessage("Unknown file size");
                    return null;
                }

                File file = new File(fileName);
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

                byte[] data = new byte[1024];
                long downloaded = 0;
                long startTime = System.nanoTime();

                int x;
                while ((x = in.read(data, 0, 1024)) >= 0) {
                    if (cancelled.get()) {
                        bout.close();
                        file.delete();
                        updateMessage("Cancelled");
                        return null;
                    }

                    while (paused.get()) {
                        Thread.sleep(100);
                    }

                    bout.write(data, 0, x);
                    downloaded += x;

                    updateProgress(downloaded, contentLength);

                    double elapsedSec = (System.nanoTime() - startTime) / 1e9;
                    double speedKBs = downloaded / 1024.0 / elapsedSec;
                    double remainingTime = (contentLength - downloaded) / 1024.0 / speedKBs;

                    Platform.runLater(() -> {
                        speed.set(String.format("%.2f KB/s", speedKBs));
                        timeLeft.set(String.format("%.1f s left", remainingTime));
                    });
                }

                bout.close();
                updateMessage("Download Complete");

            } catch (IOException | InterruptedException e) {
                updateMessage("Failed: " + e.getMessage());
            }
            return null;
        }
    }

    // =================== UI Download Pane ==========================
    class DownloadPane {
        private final HBox pane = new HBox(10);
        private final ProgressBar progressBar = new ProgressBar(0);
        private final Label speedLabel = new Label();
        private final Label timeLabel = new Label();
        private final Label statusLabel = new Label("Starting...");

        private final Button pauseBtn = new Button("Pause");
        private final Button removeBtn = new Button("Cancel");

        public DownloadPane(DownloadTask task) {
            progressBar.setPrefWidth(300);

            pauseBtn.setOnAction(e -> {
                if (task.pausedProperty().get()) {
                    task.resumeDownload();
                    pauseBtn.setText("Pause");
                } else {
                    task.pause();
                    pauseBtn.setText("Resume");
                }
            });

            removeBtn.setOnAction(e -> {
                task.cancelDownload(); // still canceling to clean up resources
                downloadsBox.getChildren().remove(pane);
            });

            progressBar.progressProperty().bind(task.progressProperty());
            statusLabel.textProperty().bind(task.messageProperty());
            speedLabel.textProperty().bind(task.speedProperty());
            timeLabel.textProperty().bind(task.timeLeftProperty());

            VBox infoBox = new VBox(5, speedLabel, timeLabel, statusLabel);
            HBox buttonBox = new HBox(5, pauseBtn, removeBtn);
            VBox rightBox = new VBox(5, progressBar, infoBox, buttonBox);

            pane.setPadding(new Insets(5));
            pane.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 10;");
            pane.getChildren().addAll(new Label(task.fileName), rightBox);
        }

        public HBox getPane() {
            return pane;
        }
    }
}
