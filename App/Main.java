package trainingSelector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {
	
	static Configuration conf;
	
	protected static ObservableList<Training> trainings = FXCollections.observableArrayList();
	
	public static boolean listen;
	
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
		
		for(int i = 0; i < conf.length(); i++) {
			trainings.add(new Training(conf.getName(i)));
			System.out.println(trainings.get(i).getName());
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
        stage.setHeight(550);
 
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
        deleteEntranceCol.setCellFactory(ActionButtonTableCell.<Training>forTableColumn("Usuñ", (Training p) -> {
            p.deleteEntrance();
            table.refresh();
            return p;
        }));
        
        table.setItems(trainings);
        table.getColumns().addAll(trainingNameCol, entrancesAmountCol, deleteEntranceCol);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(40, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        
        //menu
        MenuBar menuBar = new MenuBar();
        
        Menu menuConf = new Menu();
        Label confLabel = new Label("Konfiguracja");
        confLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage configure = new Stage();
                configure.initModality(Modality.APPLICATION_MODAL);
                configure.setTitle("Konfiguracja");
                configure.setWidth(300);
                configure.setHeight(550);
                
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 20, 20, 20));

                Scene configureScene = new Scene(grid); 
                
                ObservableList<String> trainingNames = FXCollections.observableArrayList();
                trainingNames.addAll(conf.namesArray());
                if(trainingNames.size() < 20) {
                	for(int i = trainingNames.size(); i < 20; i++) {
                		trainingNames.add("");
                	}
                }
                
                TableView<String> trainingNamesTable = new TableView<String>();
                trainingNamesTable.setEditable(true);
                
                TableColumn<String, Integer> buttonNrCol = 
                		new TableColumn<String, Integer>("Numer przycisku");
                buttonNrCol.setCellFactory(col -> {
                return new TableCell<String, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);            
                        setText(this.getTableRow().getIndex() + "");
                    }
                };
                });
                
                TableColumn<String, String> nameCol = 
                		new TableColumn<String, String>("Nazwa treningu");
                nameCol.setCellValueFactory(
                		new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<String, String> p) {
                        return new SimpleStringProperty(p.getValue());
                    }
                 });
                nameCol.setCellFactory(TextFieldTableCell.<String>forTableColumn());
                nameCol.setOnEditCommit(
                        (CellEditEvent<String, String> t) -> {
                            trainingNames.set(t.getTablePosition().getRow(), t.getNewValue());
                            changedConf = true;
                        }
                );
                
                trainingNamesTable.setItems(trainingNames);
                trainingNamesTable.getColumns().addAll(buttonNrCol, nameCol);
                
                final Button applyButton = new Button("Zapisz i wyjdŸ");
                applyButton.setOnAction(e -> {
                	if(changedConf) {
                		conf.clear();
                		for(int i = 0; i < 20; i++) {
                    		if(!nameCol.getCellData(i).isEmpty()) {
                    			conf.addName(nameCol.getCellData(i));
                    		}
                    	}
                	}
                	configure.close();
                	stage.close();
                });
                
                final Button cancelButton = new Button("Anuluj");
                cancelButton.setOnAction(e -> {
                	configure.close();
                });
                
                grid.add(applyButton, 0, 0);
                grid.add(cancelButton, 1, 0);
                grid.add(trainingNamesTable, 0, 1, 2, 1);
                
                configure.setScene(configureScene);
                configure.show();
            }
        });
        confLabel.setStyle("-fx-padding: -7");
        menuConf.setGraphic(confLabel);
 
        // --- Menu Edit
        Menu menuReport = new Menu("Raport");
 
        // --- Menu View
        Menu menuClose = new Menu();
        Label closeLabel = new Label("Zapisz i wyjdŸ");
        closeLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.close();
            }
        });
        closeLabel.setStyle("-fx-padding: -7");
        menuClose.setGraphic(closeLabel);
 
        menuBar.getMenus().addAll(menuConf, menuReport, menuClose);
 
 
        ((Group) scene.getRoot()).getChildren().addAll(menuBar);
 
        stage.setScene(scene);
        stage.show();
    }

}
