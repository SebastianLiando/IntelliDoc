package core;

import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.jpl7.Query;

import javax.tools.Tool;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

public class Controller implements DiseaseManager.Listener {
    @FXML
    private Text gestureText, questionText;
    @FXML
    private ImageView moodImage, painImage;
    @FXML
    private JFXListView<String> symptomsListView;
    @FXML
    private HBox midHBox;

    private boolean isPainSet = false;
    private boolean isMoodSet = false;
    private boolean isDiagnosed = false;
    //    private final String SCRIPT_PATH = "@../pl/sympathetic_doctor.pl";
    private final String TMP_PATH = "C:/Users/Sebastian/Documents/Prolog/sympathetic_doctor.pl";

    private IManager queryManager;

    public void beginConversation() {
        try {
            consultScript(TMP_PATH);
        } catch (ConsultException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        askPain();
    }

    private void consultScript(String PATH) throws ConsultException {
        String queryString = "['" + PATH + "']";
        Query query = new Query(queryString);
        System.out.println("Loaded script: " + query.hasSolution());
    }

    private void askPain() {
        queryManager = new PainManager();
        displayResponse();
    }

    private void displayResponse() {
        displayResponse(queryManager.getGesture(), queryManager.getQuestion());
    }

    private void displayResponse(String gesture, String question) {
        gestureText.setText("(" + gesture + ") ");
        questionText.setText(question);

        System.out.println("(" + gesture + ") " + question); //Debug
    }

    public void onClickYes(ActionEvent actionEvent) {

        if (!isDiagnosed) queryManager.onClickYes();

        if (!isPainSet) {
            isPainSet = true;
            queryManager = new MoodManager();
        } else if (!isMoodSet) {
            isMoodSet = true;
            loadIcon();
            queryManager = new DiseaseManager(this);
        }

        if (!isDiagnosed) displayResponse();
    }

    public void onClickNo(ActionEvent actionEvent) {
        queryManager.onClickNo();
        displayResponse();
    }

    private void loadIcon() {
        if (isPainSet) {
            Query tmp = new Query("patient_pain(X)");
            String pain = tmp.nextSolution().get("X").toString();
            String imagePath = "res/img/pain/" + pain + ".png";
            loadImage(imagePath, painImage, pain);
        }

        if (isMoodSet) {
            Query tmp = new Query("patient_mood(X)");
            String mood = tmp.nextSolution().get("X").toString();
            String imagePath = "res/img/mood/" + mood + ".png";
            loadImage(imagePath, moodImage, mood);
        }
    }

    private void loadImage(String path, ImageView imageView, String tooltip) {
        Image image = new Image(path);
        imageView.setImage(image);
        Tooltip.install(imageView, new Tooltip(tooltip));
    }

    @Override
    public void onSymptomsYes(String symptom) {
        //TODO: add to symptom list
        System.out.println(symptom);
        symptomsListView.getItems().add(symptom);
    }

    @Override
    public void onDiagnosePerfectMatch(String gesture, String message, String disease) {
        isDiagnosed = true;
        displayResponse(gesture, message + disease);
    }

    @Override
    public void onDiagnosePartialMatch(String gesture, String message, ArrayList<PotentialDisease> diseases) {
        isDiagnosed = true;
        displayResponse(gesture, message);
        displayChart(diseases);
        //TODO: bar chart
    }

    @Override
    public void onDiagnoseNoMatch(String gesture, String message) {
        isDiagnosed = true;
        displayResponse(gesture, message);
    }

    private void displayChart(ArrayList<PotentialDisease> diseases) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> potentialBarChart = new BarChart<String, Number>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Number of matching symptoms");

        for (PotentialDisease disease : diseases) {
            XYChart.Data data = new XYChart.Data<>(disease.getDiseaseName(), disease.getNumOfMatch());
            data.nodeProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (newValue != null) {
                        displayLabel(data);
                    }
                }
            });
            series.getData().add(data);
        }

        potentialBarChart.setVisible(true);
        potentialBarChart.getData().addAll(series);

        potentialBarChart.getYAxis().setTickLabelsVisible(false);
        potentialBarChart.getYAxis().setOpacity(0);

        midHBox.getChildren().add(potentialBarChart);
    }

    private void displayLabel(XYChart.Data data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getYValue() + "");
        node.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                parentGroup.getChildren().add(dataText);
            }
        });

        node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                dataText.setLayoutX(
                        Math.round(
                                bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2
                        )
                );
                dataText.setLayoutY(
                        Math.round(
                                bounds.getMinY() - dataText.prefHeight(-1) * 0.5
                        )
                );
            }
        });
    }
}
