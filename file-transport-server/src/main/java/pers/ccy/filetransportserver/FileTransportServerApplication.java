package pers.ccy.filetransportserver;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pers.ccy.filetransportserver.views.MainView;

@SpringBootApplication
public class FileTransportServerApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(FileTransportServerApplication.class, MainView.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("四火云盘服务端");
        super.start(stage);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

}
