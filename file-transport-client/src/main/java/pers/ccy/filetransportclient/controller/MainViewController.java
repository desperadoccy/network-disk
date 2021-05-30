package pers.ccy.filetransportclient.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import pers.ccy.filetransportclient.client.FileClient;
import pers.ccy.filetransportclient.domain.MyFile;
import pers.ccy.filetransportclient.domain.Server;
import pers.ccy.filetransportclient.utils.ServerReader;

import java.io.File;
import java.net.URL;
import java.util.*;

@FXMLController
public class MainViewController implements Initializable {

    @FXML
    private TableView<Server> server_list;
    @FXML
    private TableColumn<Server, String> serverName_list;
    @FXML
    private TextArea console;
    @FXML
    private AnchorPane dir_pane;
    @FXML
    private TableView<MyFile> view_dir;
    @FXML
    private TableColumn<MyFile, String> col_fileName;
    @FXML
    private TableColumn<MyFile, String> col_updateTime;
    @FXML
    private TableColumn<MyFile, String> col_type;
    @FXML
    private TableColumn<MyFile, String> col_size;

    private ServerReader serverReader;
    private ResourceBundle resourceBundle;
    private ObservableList<Server> items;
    private FileClient fileClient = null;
    private Thread task = null;
    private double outX = 0;

    @Autowired
    public void setUserReader(ServerReader serverReader) {
        this.serverReader = serverReader;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        items = FXCollections.observableArrayList(serverReader.getXmlContent());
        server_list.setItems(items);
        serverName_list.setCellValueFactory(a -> a.getValue().serverNameProperty());
        view_dir.setItems(null);
        view_dir.setOnMouseClicked(e -> tableClick(e));
        col_fileName.setCellValueFactory(a -> a.getValue().fileNameProperty());
        col_updateTime.setCellValueFactory(a -> a.getValue().updateTimeProperty());
        col_type.setCellValueFactory(a -> a.getValue().typeProperty());
        col_size.setCellValueFactory(a -> a.getValue().sizeProperty());
        resourceBundle = resources;
    }

    @FXML
    public void create_server(ActionEvent actionEvent) {
        Dialog<Server> dialog = new Dialog<>();
        dialog.setTitle("创建新会话");
        dialog.setHeaderText("填写会话属性");

        ButtonType loginButtonType = new ButtonType("创建", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        TextField serverName = new TextField();
        serverName.setPromptText("名称");
        TextField serverIP = new TextField();
        serverIP.setPromptText("主机");
        TextField serverPort = new TextField();
        serverPort.setPromptText("端口号");
        TextField userName = new TextField();
        userName.setPromptText("用户名");
        PasswordField password = new PasswordField();
        password.setPromptText("密码");

        grid.add(new Label("ServerName:"), 0, 0);
        grid.add(serverName, 1, 0);
        grid.add(new Label("ServerIp:"), 0, 1);
        grid.add(serverIP, 1, 1);
        grid.add(new Label("ServerPort:"), 0, 2);
        grid.add(serverPort, 1, 2);
        grid.add(new Label("UserName:"), 0, 3);
        grid.add(userName, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(password, 1, 4);

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        serverName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> serverName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Server server = new Server();
                server.setServerName(serverName.getText());
                server.setIp(serverIP.getText());
                server.setPort(serverPort.getText());
                server.setUserName(userName.getText());
                server.setPassword(password.getText());
                return server;
            }
            return null;
        });

        Optional<Server> result = dialog.showAndWait();
        if (result.isPresent()) {
            items.add(result.get());
            serverReader.setXmlContent(items);
        }
    }

    @FXML
    public void delete_server(ActionEvent actionEvent) {
        Server server = server_list.getSelectionModel().getSelectedItem();
        items.remove(server);
        serverReader.setXmlContent(items);
    }

    @FXML
    public void update_server(ActionEvent actionEvent) {
        Server server = server_list.getSelectionModel().getSelectedItem();
        Dialog<Server> dialog = new Dialog<>();
        dialog.setTitle("修改会话");
        dialog.setHeaderText("修改会话属性");

        ButtonType loginButtonType = new ButtonType("修改", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        TextField serverName = new TextField();
        serverName.setPromptText("名称");
        serverName.setText(server.getServerName());
        TextField serverIP = new TextField();
        serverIP.setPromptText("主机");
        serverIP.setText(server.getIp());
        TextField serverPort = new TextField();
        serverPort.setPromptText("端口号");
        serverPort.setText(server.getPort());
        TextField userName = new TextField();
        userName.setPromptText("用户名");
        userName.setText(server.getUserName());
        TextField password = new TextField();
        password.setPromptText("密码");
        password.setText(server.getPassword());

        grid.add(new Label("ServerName:"), 0, 0);
        grid.add(serverName, 1, 0);
        grid.add(new Label("ServerIp:"), 0, 1);
        grid.add(serverIP, 1, 1);
        grid.add(new Label("ServerPort:"), 0, 2);
        grid.add(serverPort, 1, 2);
        grid.add(new Label("UserName:"), 0, 3);
        grid.add(userName, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(password, 1, 4);

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        serverName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> serverName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                server.setServerName(serverName.getText());
                server.setIp(serverIP.getText());
                server.setPort(serverPort.getText());
                server.setUserName(userName.getText());
                server.setPassword(password.getText());
                return server;
            }
            return null;
        });

        dialog.showAndWait();
        serverReader.setXmlContent(items);
    }

    @FXML
    public void connect_server(ActionEvent actionEvent) {
        Server server = server_list.getSelectionModel().getSelectedItem();
        console.appendText("正在连接到服务器" +
                server.getServerName() + "(" + server.getIp() + ")........\n");
        if (fileClient == null) {
            fileClient = new FileClient(server.getIp(), Integer.parseInt(server.getPort()), console, this, view_dir, dir_pane);
            task = new Thread(() -> {
                fileClient.run();
            });
            task.start();
            createButton("./", "~/");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fileClient.sendDirRequest("");
        } else {
            console.setText("当前有连接正在执行");
        }
    }

    @FXML
    public void cancel_connect(ActionEvent actionEvent) {
        if (fileClient != null) {
            fileClient.destroy();
            fileClient = null;
            outX = 0;
            view_dir.getItems().clear();
            dir_pane.getChildren().clear();
        }
    }

    @FXML
    public void download(ActionEvent actionEvent) {
        MyFile myFile = view_dir.getSelectionModel().getSelectedItem();
        if (myFile.getType().equals("dir")) return;
        Window window = view_dir.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择下载的路径");
        File file = directoryChooser.showDialog(new Stage());
        if (file != null) {
            fileClient.download(myFile, file.getAbsolutePath());
        }
    }

    @FXML
    public void upload(ActionEvent actionEvent) {
        Button node = (Button)dir_pane.getChildren().get(dir_pane.getChildren().size() - 1);
        Window window = view_dir.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择上传的文件");
        File file = fileChooser.showOpenDialog(window);
        if (file != null && file.length() != 0) {
            if (node.getText().equals("~/"))
                fileClient.upload(file, "");
            else
                fileClient.upload(file, node.getId());
        }
    }

    public void dirChangeButton(ActionEvent e) {
        Button buttonDir = (Button) e.getSource();
        outX = buttonDir.getWidth() + buttonDir.getLayoutX();
        ObservableList<Node> btnList = dir_pane.getChildren();
        Iterator<Node> iterator = btnList.iterator();
        boolean flag = false;
        while (iterator.hasNext()) {
            Button next = (Button) iterator.next();
            if (next == buttonDir) {
                flag = true;
            } else if (flag) {
                iterator.remove();
            }
        }
        if (buttonDir.getText().equals("~/"))
            fileClient.sendDirRequest("");
        else
            fileClient.sendDirRequest(buttonDir.getId());
    }

    public void createButton(String dir, String text) {
        Button button = new Button();
        button.setId(dir);
        button.setText(text);
        button.setLayoutX(outX);
        dir_pane.getChildren().add(button);
        dir_pane.applyCss();
        dir_pane.layout();
        double width = button.getWidth();
        outX += width;
        button.setOnAction(e -> dirChangeButton(e));
    }

    public void tableClick(MouseEvent actionEvent) {
        if (actionEvent.getClickCount() == 2) {
            MyFile selectedItem = view_dir.getSelectionModel().getSelectedItem();
            if (selectedItem.getType().equals("dir")) {
                String url = selectedItem.getUrl();
                createButton(url, selectedItem.getFileName() + "/");
                fileClient.sendDirRequest(selectedItem.getUrl());
            }
        }
    }
}
