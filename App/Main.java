package app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	
	//configuration object
	static Configuration conf;
	
	//list of trainings
	protected static ObservableList<Training> trainings = FXCollections.observableArrayList();
	
	//for controlling listening to input stream
	public static boolean listen;
	
	//for saving information about fact if configuration was changed
	static boolean changedConf;
	
	public static void main(String[] args) {
		
		//read configuration
		conf = Parser.readConfiguration();
		
		//if configuration doesn't exist - make it default
		changedConf = false;
		if(conf == null) {
			conf = new Configuration();
			changedConf = true;
		}
		
		//fill trainings list with trainings which names are in configuration file
		for(int i = 0; i < conf.length(); i++) {
			trainings.add(new Training(conf.getName(i)));
		}

        //creates an objcet representing the PC <-> Arduino communication interface
        ArduinoCommunicator comm = new ArduinoCommunicator();

        //find the arduino-port and open it
        comm.initializeArduino();

        //check if the arduino board has been initialized succesfully
        if(comm.isArduinoInitialized())
            System.out.println("The Arduino board has been initialized and waits for commands.");
        else {
            System.out.println("ERROR. No Arduino board has been initialized.");
            return;
        }

        //listen to arduino
        comm.launchArduinoListener();

		//launch GUI
		Application.launch(args);
		
		//if changed configuration - save it
		if(changedConf) {
			Parser.saveConfiguration(conf);
		}
		
	}
	
	//GUI
	
	//create table which will be filled with trainings data
	public static TableView<Training> table = new TableView<Training>();
	
	//main stage of application
	@SuppressWarnings("unchecked")
	public void start(Stage stage) {
        stage.setTitle("Training Counter");
        stage.setWidth(400);
        stage.setHeight(550);
        
        //main grid with all elements
        GridPane mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.TOP_CENTER);
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);
        mainGrid.setPadding(new Insets(0, 20, 20, 20));
        
        Scene scene = new Scene(mainGrid);
        
        //label showing current date
        SimpleDateFormat ft = 
			      new SimpleDateFormat("E, d.M.y");
        final Label label = new Label(ft.format(new Date()));
        label.setFont(new Font("Arial", 20));
        HBox hbLabel = new HBox(10);
        hbLabel.setAlignment(Pos.CENTER);
        hbLabel.getChildren().add(label);
        mainGrid.add(hbLabel, 0, 1);
        
        //make table editable
        table.setEditable(true);
        
        //create first column and fill with training names
        TableColumn<Training, String> trainingNameCol = new TableColumn<Training, String>("Trening");
        trainingNameCol.setCellValueFactory(
                new PropertyValueFactory<Training, String>("name"));
        
        //create second column and fill with number of entrances of each training
        TableColumn<Training, Integer> entrancesAmountCol = new TableColumn<Training, Integer>("Ilość wejść");
        entrancesAmountCol.setCellValueFactory(
                new PropertyValueFactory<Training, Integer>("entrances"));
        
        //create third column and fill with buttons capable of deleting 
        //one entrance of appropriate training
        TableColumn<Training, Button> deleteEntranceCol = new TableColumn<Training, Button>("");
        deleteEntranceCol.setCellFactory(ActionButtonTableCell.<Training>forTableColumn("Usuń",
        		(Training p) -> {
        			p.deleteEntrance();
        			table.refresh();
        			return p;
        }));
        
        //set list from which comes all data to fill in table
        table.setItems(trainings);
        
        //add columns to table
        table.getColumns().addAll(trainingNameCol, entrancesAmountCol, deleteEntranceCol);
        
        //add table to main grid
        mainGrid.add(table, 0, 2);
        
        //create menu
        MenuBar menuBar = new MenuBar();
        
        //first menu button - for configuration
        Menu menuConf = new Menu();
        Label confLabel = new Label("Konfiguracja");
        confLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                GUI.configurationStage(stage);
            }
        });
        menuConf.setGraphic(confLabel);
 
        // second menu button - for generating reports
        Menu menuReport = new Menu();
        Label reportLabel = new Label("Raport");
        reportLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	GUI.reportStage();
            }
        });
        menuReport.setGraphic(reportLabel);
        
        //third menu button for saving trainings list to json
        Menu menuSave = new Menu();
        Label saveLabel = new Label("Zapisz");
        saveLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	List<Training> trainings_ = Parser.readTrainings();
        		if(trainings_ != null) {
        			trainings_.addAll(trainings);
        			Parser.saveTrainings(trainings_);
        		} else {
        			Parser.saveTrainings(trainings);
        		}
        		GUI.saved();
            }
        });
        menuSave.setGraphic(saveLabel);
        
        //fourth menu button for saving trainings list to json and closing application
        Menu menuClose = new Menu();
        Label closeLabel = new Label("Zapisz i wyjdź");
        closeLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	List<Training> trainings_ = Parser.readTrainings();
        		if(trainings_ != null) {
        			trainings_.addAll(trainings);
        			Parser.saveTrainings(trainings_);
        		} else {
        			Parser.saveTrainings(trainings);
        		}
                stage.close();
            }
        });
        menuClose.setGraphic(closeLabel);
        
        //add all menu buttons to menu
        menuBar.getMenus().addAll(menuConf, menuReport, menuSave, menuClose);
 
        //add menu to grid
        mainGrid.add(menuBar, 0, 0);
 
        //add scene to stage and show
        stage.setScene(scene);
        stage.show();

    }

}
