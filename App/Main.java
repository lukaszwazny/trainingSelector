package trainingSelector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	
	static Configuration conf;
	
	private static ObservableList<Training> trainings = FXCollections.observableArrayList();
	
	public static void main(String[] args) {
		
		//read configuration
		conf = Parser.readConfiguration();
		//if configuration doesn't exist - make it default
		boolean changedConf = false;
		if(conf == null) {
			conf = new Configuration();
			changedConf = true;
		}
		
		for(int i = 0; i < conf.getLength(); i++) {
			trainings.add(new Training(conf.getName(i)));
		}
		
		Application.launch(args);
		
		Scanner in = new Scanner(System.in);
		int i;
		while(true) {
			if(in.hasNext()) {
				i = in.nextInt();
				trainings.get(i).addEntrance();
				System.out.println(trainings.get(i).getName() + ": " + trainings.get(i).getEntrances());
	        }
		}
		
	}
	
	//GUI
	private TableView<Training> table = new TableView<Training>();
	
	public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Training Counter");
        stage.setWidth(300);
        stage.setHeight(500);
 
        SimpleDateFormat ft = 
			      new SimpleDateFormat("E, d.M.y");
        final Label label = new Label(ft.format(new Date()));
        label.setFont(new Font("Arial", 20));
 
        TableColumn<Training, String> trainingNameCol = new TableColumn<Training, String>("Trening");
        trainingNameCol.setCellValueFactory(
                new PropertyValueFactory<Training, String>("name"));
        
        TableColumn<Training, Integer> entrancesAmountCol = new TableColumn<Training, Integer>("Iloœæ wejœæ");
        entrancesAmountCol.setCellValueFactory(
                new PropertyValueFactory<Training, Integer>("entrances"));
        
        TableColumn deleteEntranceCol = new TableColumn("");
        
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
