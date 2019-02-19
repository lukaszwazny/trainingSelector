package trainingSelector;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
	protected static Configuration conf;
	
	//list of trainings
	protected static ObservableList<Training> trainings = FXCollections.observableArrayList();
	
	//for checking if configuration file was found
	private static boolean confFileNotFound;
	
	public static void main(String[] args) {
		
		//read configuration
		confFileNotFound = false;
		try {
			conf = Parser.readConfiguration();
		} catch (IOException e){
			confFileNotFound = true;
		}
		
		//if configuration doesn't exist - make it default
		if(conf == null) {
			conf = new Configuration();
			Parser.saveConfiguration(conf);
		}
		
		//fill trainings list with trainings which names are in configuration file
		for(int i = 0; i < conf.length(); i++) {
			trainings.add(new Training(conf.getName(i)));
		}
		
		//check if there are saved trainings with the same date as current - if so
		//set number of entrances the same as in saved trainings
		List<Training> savedTrainings = Parser.readTrainings();	
		trainings.forEach(currentTraining -> {
			
			boolean rightDate = true;
			for(int i = savedTrainings.size() - 1; i >= 0 && rightDate; i--) {
				
				//check if the date of current training equals to date of
				//training in database
				rightDate = savedTrainings.get(i).getDate()
						.equals(currentTraining.getDate());
				
				//check if the name of current training matches the name of training 
				//saved in database
				boolean rightName = Objects.equals(currentTraining.getName(),
						savedTrainings.get(i).getName());
				
				//if all previous true set appropriate number of entrances
				if(rightName && rightDate)
					currentTraining
					.setEntrances(savedTrainings.get(i).getEntrances());
				
			}
		});
		
		//creates an object representing the PC <-> Arduino communication interface
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
        TableColumn<Training, Integer> entrancesAmountCol = new TableColumn<Training, Integer>("Iloœæ wejœæ");
        entrancesAmountCol.setCellValueFactory(
                new PropertyValueFactory<Training, Integer>("entrances"));
        
        //create third column and fill with buttons capable of deleting 
        //one entrance of appropriate training
        TableColumn<Training, Button> deleteEntranceCol = new TableColumn<Training, Button>("");
        deleteEntranceCol.setCellFactory(ActionButtonTableCell.<Training>forTableColumn("Usuñ", 
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
                GUI.configurationStage(stage, conf);
            }
        });
        menuConf.setGraphic(confLabel);
 
        // second menu button - for generating reports
        Menu menuReport = new Menu();
        Label reportLabel = new Label("Raport");
        reportLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	GUI.reportStage(conf);
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
        Label closeLabel = new Label("Zapisz i wyjdŸ");
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
        
        //message saying that configuration file was not found
        if(confFileNotFound)
			GUI.confFileNotFoundException();
    }

}
