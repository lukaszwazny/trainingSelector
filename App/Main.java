package trainingSelector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {
	
	static Configuration conf;
	
	protected static ObservableList<Training> trainings = FXCollections.observableArrayList();
	
	public static boolean listen;
	
	public static void main(String[] args) {
		
		//read configuration
		conf = Parser.readConfiguration();
		//if configuration doesn't exist - make it default
		boolean changedConf = false;
		if(conf == null) {
			conf = new Configuration();
			changedConf = true;
		}
		
		for(int i = 0; i < conf.length(); i++) {
			trainings.add(new Training(conf.getName(i)));
		}
		
		listen = true;
		Thread listener = new Thread(new Listener());
		listener.start();
		
		Application.launch(args);
		listen = false;
		
		if(changedConf) {
			Parser.saveConfiguration(conf);
		}
		
		List<Training> trainings_ = Parser.readTrainings();
		if(trainings_ != null) {
			trainings_.addAll(trainings);
			Parser.saveTrainings(trainings_);
		} else {
			Parser.saveTrainings(trainings);
		}
		
	}
	
	//GUI
	public static TableView<Training> table = new TableView<Training>();
	
	@SuppressWarnings("unchecked")
	public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Training Counter");
        stage.setWidth(300);
        stage.setHeight(500);
 
        SimpleDateFormat ft = 
			      new SimpleDateFormat("E, d.M.y");
        final Label label = new Label(ft.format(new Date()));
        label.setFont(new Font("Arial", 20));
        
        table.setEditable(true);
 
        TableColumn<Training, String> trainingNameCol = new TableColumn<Training, String>("Trening");
        trainingNameCol.setCellValueFactory(
                new PropertyValueFactory<Training, String>("name"));
        
        TableColumn<Training, Integer> entrancesAmountCol = new TableColumn<Training, Integer>("Iloœæ wejœæ");
        entrancesAmountCol.setCellValueFactory(
                new PropertyValueFactory<Training, Integer>("entrances"));
        
        TableColumn<Training, Button> deleteEntranceCol = new TableColumn<Training, Button>("");
        deleteEntranceCol.setCellValueFactory(
        		new PropertyValueFactory<>("DUMMY"));
        
        Callback<TableColumn<Training, Button>, TableCell<Training, Button>> cellFactory = //
        		new Callback<TableColumn<Training, Button>, TableCell<Training, Button>>() {
        			@Override
        			public TableCell<Training, Button> call(final TableColumn<Training, Button> param) {
        				final TableCell<Training, Button> cell = new TableCell<Training, Button>() {

        					final Button btn = new Button("Usuñ");

        					@Override
        					public void updateItem(Button item, boolean empty) {
        						super.updateItem(item, empty);
        							btn.setOnAction(event -> {
        								Training training = getTableView().getItems().get(getIndex());
        								training.deleteEntrance();
        								table.refresh();
        							});
        							setGraphic(btn);
        					}
        				};
        				return cell;
        			}
        };

        deleteEntranceCol.setCellFactory(cellFactory);
        
        table.setItems(trainings);
        table.getColumns().addAll(trainingNameCol, entrancesAmountCol, deleteEntranceCol);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        stage.setScene(scene);
        stage.show();
    }

}
