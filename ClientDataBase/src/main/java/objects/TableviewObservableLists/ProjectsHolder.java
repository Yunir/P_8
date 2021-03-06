package objects.TableviewObservableLists;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import objects.Project;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class ProjectsHolder {
    private ObservableList<Project> ProjectsObsList;

    public ProjectsHolder() { ProjectsObsList = FXCollections.observableArrayList(); }

    //Commands
    public void create(String nameOfProject) {
        ProjectsObsList.add(new Project(nameOfProject, 0, OffsetDateTime.now()));
        System.out.println(ProjectsObsList.get(ProjectsObsList.size()-1).getName() + " added successfully.");
    }
    public void update(String oldName, String newName) {
        for (int i = 0; i < ProjectsObsList.size(); i++) {
            if(ProjectsObsList.get(i).getName().equals(oldName)) {
                ProjectsObsList.get(i).setName(newName);
                break;
            }
        }
    }


    public void read() {}
    public void delete() {}

    /*Getters, setters*/
    public ObservableList<Project> getProjectsObsList() {
        return ProjectsObsList;
    }
    public void setProjectsObsList(ObservableList<Project> projectsObsList) {ProjectsObsList = projectsObsList;}
    public void refreshProjectObsList(ArrayList<Project> projectsList) {
        this.ProjectsObsList.removeAll(this.ProjectsObsList);
        this.ProjectsObsList = FXCollections.observableArrayList(projectsList);

    }
    public void showAllProjects() {
        System.out.println();
        for (int i = 0; i < ProjectsObsList.size(); i++) {
            System.out.print(ProjectsObsList.get(i).getName() + "   ");
        }
        System.out.println();
    }
}
