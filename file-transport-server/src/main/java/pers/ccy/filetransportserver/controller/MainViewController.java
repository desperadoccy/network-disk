package pers.ccy.filetransportserver.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import pers.ccy.filetransportserver.domain.MyFile;
import pers.ccy.filetransportserver.server.FileServer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

@FXMLController
public class MainViewController implements Initializable {
    @FXML
    private Button btn_start;
    @FXML
    private Button btn_end;
    @FXML
    private TextField txt_port;
    @FXML
    private TextField txt_dir;
    @FXML
    private TableView<MyFile> view_files;
    @FXML
    private TableColumn<MyFile, String> col_fileName;
    @FXML
    private TableColumn<MyFile, String> col_updateTime;
    @FXML
    private TableColumn<MyFile, String> col_size;
    @FXML
    private TableColumn<MyFile, String> col_type;
    @FXML
    private AnchorPane dir_pane;
    @FXML
    private TextArea txt_console;

    private Thread task = null;
    private double outX = 0;

    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_start.setVisible(true);
        btn_start.setManaged(true);
        btn_end.setVisible(false);
        btn_end.setManaged(false);
        view_files.setOnMouseClicked((a)->tableClick(a));
        resourceBundle = resources;
    }

    @FXML
    public void start_server(ActionEvent actionEvent) {
        String dir = txt_dir.getText();
        String port = txt_port.getText();
        File fileFolder = new File(dir);
        ArrayList<MyFile> myFiles = new ArrayList<>();
        if (!fileFolder.exists() && !fileFolder.isDirectory()) {
            fileFolder.mkdirs();
            txt_console.appendText("根路径不存在，已自动创建");
        }else if (!fileFolder.isDirectory()){
            txt_console.setText("路径非法");
            return;
        }
        myFiles = getFiles(dir);
        ObservableList<MyFile> dirList = FXCollections.observableArrayList(myFiles);
        view_files.setItems(dirList);
        col_fileName.setCellValueFactory(a -> a.getValue().fileNameProperty());
        col_updateTime.setCellValueFactory(a -> a.getValue().updateTimeProperty());
        col_type.setCellValueFactory(a -> a.getValue().typeProperty());
        col_size.setCellValueFactory(a -> a.getValue().sizeProperty());

        btn_start.setVisible(false);
        btn_start.setManaged(false);
        btn_end.setVisible(true);
        btn_end.setManaged(true);
        txt_console.setText("开启连接,绑定端口" + port + "\n");
        if (!dir.endsWith("/")) dir = dir + "/";
        FileServer fileServer = new FileServer(Integer.parseInt(port), txt_console, this, dir, view_files, dir_pane);
        task = new Thread(()->{
            fileServer.run();
        });
        task.start();
        //set dir
        createButton(fileFolder.getAbsolutePath(), "~/");
    }

    @FXML
    public void end_server(ActionEvent actionEvent) {
        outX = 0;
        txt_console.setText("断开连接");
        btn_start.setVisible(true);
        btn_start.setManaged(true);
        btn_end.setVisible(false);
        btn_end.setManaged(false);
        view_files.getItems().clear();
        dir_pane.getChildren().clear();
        //task.interrupt();
    }

    public ArrayList<MyFile> getFiles(String path) {
        File fileFolder = new File(path);
        ArrayList<MyFile> myFiles = new ArrayList<>();
        File[] files = fileFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                myFiles.add(new MyFile(files[i].getName(), files[i].lastModified(), files[i].length(), "file", files[i].getAbsolutePath()));
            }
            if (files[i].isDirectory()) {
                myFiles.add(new MyFile(files[i].getName(), files[i].lastModified(), files[i].length(), "dir", files[i].getAbsolutePath()));
            }
        }
        return myFiles;
    }

    public void tableClick(MouseEvent actionEvent){
        if (actionEvent.getClickCount() == 2){
            MyFile selectedItem = view_files.getSelectionModel().getSelectedItem();
            if (selectedItem.getType().equals("dir")) {
                String url = selectedItem.getUrl();
                createButton(url, selectedItem.getFileName()+"/");
                ArrayList<MyFile> myFiles = getFiles(selectedItem.getUrl());
                ObservableList<MyFile> dirList = FXCollections.observableArrayList(myFiles);
                view_files.setItems(dirList);
            }
        }
    }

    public void dirChangeButton(ActionEvent e){
        Button buttonDir = (Button) e.getSource();
        outX = buttonDir.getWidth() + buttonDir.getLayoutX();
        ObservableList<Node> btnList = dir_pane.getChildren();
        Iterator<Node> iterator = btnList.iterator();
        boolean flag = false;
        while (iterator.hasNext()){
            Button next = (Button)iterator.next();
            if (next == buttonDir){
                flag = true;
            }else if (flag){
                iterator.remove();
            }
        }
        ArrayList<MyFile> myFiles = getFiles(buttonDir.getId());
        ObservableList<MyFile> dirList = FXCollections.observableArrayList(myFiles);
        view_files.setItems(dirList);
    }

    public void createButton(String dir, String text){
        Button button = new Button();
        button.setId(dir);
        button.setText(text);
        button.setLayoutX(outX);
        dir_pane.getChildren().add(button);
        dir_pane.applyCss();
        dir_pane.layout();
        double width = button.getWidth();
        outX += width;
        button.setOnAction(e->dirChangeButton(e));
    }
}
