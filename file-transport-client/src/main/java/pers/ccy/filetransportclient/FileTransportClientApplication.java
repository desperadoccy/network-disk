package pers.ccy.filetransportclient;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pers.ccy.filetransportclient.controller.MainViewController;
import pers.ccy.filetransportclient.domain.Server;
import pers.ccy.filetransportclient.utils.ServerReader;
import pers.ccy.filetransportclient.views.MainView;

import java.io.IOException;

@SpringBootApplication
public class FileTransportClientApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(FileTransportClientApplication.class, MainView.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("四火云盘");
        super.start(stage);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
