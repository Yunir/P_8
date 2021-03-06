package controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import objects.TableviewObservableLists.AimsHolder;
import objects.TableviewObservableLists.NotesHolder;
import objects.TableviewObservableLists.ProjectsHolder;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import general_classes.Main;
import objects.Aim;
import objects.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import utils.Lang;
import utils.LocaleManager;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static general_classes.Main.cc;
import static general_classes.Main.toServer;

public class MainController extends Observable implements Initializable {
    public static volatile boolean confirmationReceived = false;
    public static AimsHolder aimsHolder;
    public static ProjectsHolder projectsHolder;
    public static Class c = Aim.class;
    private static final String RU_CODE = "ru";
    private static final String EN_CODE = "en_ca";
    private static final String HR_CODE = "hr_ru";
    private static final String BE_CODE = "be_by";
    private static final String ES_CODE = "es";
    private static final String NO_CODE = "no_NO";
    private static final String WG_CODE = "wg";
    private static final String CS_CODE = "cs";
    public static Pattern pattern = Pattern.compile(
            "[" +
                    "a-zA-Zа-яА-ЯёЁ" +
                    "\\s" +         //знаки-разделители (пробел, табуляция и т.д.)
                    "]" +
                    "*");


    @FXML
    private volatile TableView<Project> projectsTable;
    @FXML
    private TableColumn<Project, String> nameOfProject;
    @FXML
    private TableColumn<Project, Integer> amountOfAims;
    @FXML
    private volatile TableView<Aim> aimsTable;
    @FXML
    private VBox main;
    @FXML
    private TableColumn<Aim, String> nameOfAim;
    @FXML
    private TableColumn<Aim, Integer> priorityOfAim;
    @FXML
    private Button PUpdate;
    @FXML
    private Button PDelete;
    @FXML
    private Button ACreate;
    @FXML
    private Button AUpdate;
    @FXML
    private Button ADelete;
    @FXML
    private ComboBox comboLocales;
    private ResourceBundle resourceBundle;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
        nameOfProject.setCellValueFactory(new PropertyValueFactory<Project, String>("name"));
        amountOfAims.setCellValueFactory(new PropertyValueFactory<Project, Integer>("amount"));

        nameOfAim.setCellValueFactory(new PropertyValueFactory<Aim, String>("name"));
        priorityOfAim.setCellValueFactory(new PropertyValueFactory<Aim, Integer>("priority"));
        aimsTable.setPlaceholder(new Label(resourceBundle.getString("main.no_chosen_project")));
        ACreate.setDisable(true);
        AUpdate.setDisable(true);
        ADelete.setDisable(true);
        PUpdate.setDisable(true);
        PDelete.setDisable(true);
        aimsHolder = new AimsHolder();
        projectsHolder = new ProjectsHolder();
        initListeners();
        fillLangComboBox();
        fillData();
    }

    public void putDataToObservableList () {
        System.out.println("Refresh projectsHolder and projectsTable");
        projectsHolder.setProjectsObsList(FXCollections.observableArrayList(Main.dataHolder.getProjects()));
        projectsTable.setItems(projectsHolder.getProjectsObsList());
        projectsTable.refresh();
        projectsHolder.showAllProjects();
    }

    private void fillData() {
        System.out.println("putting Data to ObservableLists");
        if(projectsHolder != null) {
            projectsHolder.setProjectsObsList(FXCollections.observableArrayList(Main.dataHolder.getProjects()));
            projectsTable.setItems(projectsHolder.getProjectsObsList());
        } else {
            projectsHolder = new ProjectsHolder();
            projectsHolder.setProjectsObsList(FXCollections.observableArrayList());
            projectsTable.setItems(projectsHolder.getProjectsObsList());
        }
    }

    public void openAimsOfProject(MouseEvent mouseEvent) {
        Project selectedProject = (Project) ((TableView)mouseEvent.getSource()).getSelectionModel().getSelectedItem();
        if(selectedProject == null)return;
        c = Project.class;
        UpdateProjectController.oldProjectName = selectedProject.getName();
        aimsHolder.setAimsObsList(FXCollections.observableArrayList(selectedProject.getAimsList()));
        aimsTable.setItems(aimsHolder.getAimsObsList());
        ACreate.setDisable(false);
        PUpdate.setDisable(false);
        PDelete.setDisable(false);
        aimsTable.refresh();
        projectsTable.refresh();
    }

    public void showCreateProjectDialog(ActionEvent actionEvent) {
        Stage stage = cc.showCreateProjectDialog(actionEvent, resourceBundle.getString("create_project_title"));
        stage.show();
        CreateProjectController.CreateProjectStage = stage;
        CreateProjectController.prTable = projectsTable;
        CreateProjectController.aiTable = aimsTable;
    }
    public void showUpdateProjectDialog(ActionEvent actionEvent) {
        Stage stage = cc.showUpdateProjectDialog(actionEvent, resourceBundle.getString("update_project_title"));
        stage.show();
        UpdateProjectController.UpdateProjectStage = stage;
        UpdateProjectController.prTable = projectsTable;
        UpdateProjectController.aiTable = aimsTable;
    }
    public void deleteProject(ActionEvent actionEvent) {
        toServer.getConnector().actionEventSolver.deleteProject(projectsTable.getSelectionModel().getSelectedItem().getName());

    }
    public void showCreateAimDialog(ActionEvent actionEvent) {
        Stage stage = cc.showCreateAimDialog(actionEvent, resourceBundle.getString("create_aim_title"));
        CreateAimController.CreateAimStage = stage;
        CreateAimController.projectName = projectsTable.getSelectionModel().getSelectedItem().getName();
        stage.show();
    }
    public void showUpdateAimDialog(ActionEvent actionEvent) {
        Stage stage = cc.showUpdateAimDialog(actionEvent, resourceBundle.getString("update_aim_title"));
        UpdateAimController.UpdateAimStage = stage;
        UpdateAimController.projectName = projectsTable.getSelectionModel().getSelectedItem().getName();
        UpdateAimController.oldAimName = aimsTable.getSelectionModel().getSelectedItem().getName();
        stage.show();

    }
    public void deleteAim(ActionEvent actionEvent) {
        toServer.getConnector().actionEventSolver.deleteAim(projectsTable.getSelectionModel().getSelectedItem().getName(), aimsTable.getSelectionModel().getSelectedItem().getName());
        aimsTable.refresh();
        projectsTable.refresh();

        aimsHolder = new AimsHolder();
        aimsHolder.setAimsObsList(FXCollections.observableArrayList());
        aimsTable.setItems(aimsHolder.getAimsObsList());
    }

    public void createObjTable(ActionEvent actionEvent) {
        toServer.getConnector().actionEventSolver.createObj();
    }

    public void unlockButtons(MouseEvent mouseEvent) {
        Aim temp = (Aim) ((TableView)mouseEvent.getSource()).getSelectionModel().getSelectedItem();
        if(temp==null)return;
        c = Aim.class;
        AUpdate.setDisable(false);
        ADelete.setDisable(false);
    }
    public void disableUpdateDeleteButtons() {
        PUpdate.setDisable(true);
        PDelete.setDisable(true);
        ACreate.setDisable(true);
        AUpdate.setDisable(true);
        ADelete.setDisable(true);
    }
    public TableView getProjectsTable() {
        return projectsTable;
    }
    public TableView getAimsTable() {
        return aimsTable;
    }

    private void fillLangComboBox() {
        Lang langRU = new Lang(0, RU_CODE, resourceBundle.getString("ru"), LocaleManager.RU_LOCALE);
        Lang langEN = new Lang(1, EN_CODE, resourceBundle.getString("en"), LocaleManager.EN_LOCALE);
        Lang langBE = new Lang(2, BE_CODE, resourceBundle.getString("be"), LocaleManager.BE_LOCALE);
        Lang langHR = new Lang(3, HR_CODE, resourceBundle.getString("hr"), LocaleManager.HR_LOCALE);
        Lang langES = new Lang(4, ES_CODE, resourceBundle.getString("es"), LocaleManager.ES_LOCALE);
        Lang langNO = new Lang(5, NO_CODE, resourceBundle.getString("no"), LocaleManager.NO_LOCALE);
        Lang langWG = new Lang(6, WG_CODE, resourceBundle.getString("wg"), LocaleManager.WG_LOCALE);
        Lang langCS = new Lang(7, CS_CODE, resourceBundle.getString("cs"), LocaleManager.CS_LOCALE);

        comboLocales.getItems().add(langRU);
        comboLocales.getItems().add(langEN);
        comboLocales.getItems().add(langBE);
        comboLocales.getItems().add(langHR);
        comboLocales.getItems().add(langES);
        comboLocales.getItems().add(langNO);
        comboLocales.getItems().add(langWG);
        comboLocales.getItems().add(langCS);

        if(LocaleManager.getCurrentLang()==null){
            comboLocales.getSelectionModel().select(0);
            Lang selectedLang = (Lang) comboLocales.getSelectionModel().getSelectedItem();
            LocaleManager.setCurrentLang(selectedLang);
        } else {
            comboLocales.getSelectionModel().select(LocaleManager.getCurrentLang().getIndex());
        }
    }

    public void tryToChangeLanguage() {
        if(LocaleManager.getCurrentLang().getIndex() == 0) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    comboLocales.getSelectionModel().select(1);
                    comboLocales.getSelectionModel().select(0);
                }
            });
        } else {
            int iii = LocaleManager.getCurrentLang().getIndex();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    comboLocales.getSelectionModel().select(0);
                    comboLocales.getSelectionModel().select(iii);
                }
            });
        }


    }

    private void initListeners() {
        comboLocales.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("you changed language");
                Lang selectedLang = (Lang) comboLocales.getSelectionModel().getSelectedItem();
                LocaleManager.setCurrentLang(selectedLang);

                setChanged();
                notifyObservers(selectedLang);
            }
        });
    }

    public void showReflTable(ActionEvent actionEvent) {
        Stage stage = cc.showReflTable(actionEvent, resourceBundle.getString("refl_title"));
        stage.show();
    }
}
