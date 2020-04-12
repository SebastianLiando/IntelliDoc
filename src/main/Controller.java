package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import main.exception.ConsultException;
import main.manager.AbstractManager;
import main.manager.DiseaseManager;
import main.manager.MoodManager;
import main.manager.PainManager;
import main.model.PotentialDisease;
import org.jpl7.Query;
import main.tts.TextSpeechManager;

import java.util.ArrayList;

public class Controller implements DiseaseManager.Listener {
    @FXML
    private Text gestureText, questionText;
    @FXML
    private ImageView moodImage, painImage, volumeImage;
    @FXML
    private JFXListView<String> symptomsListView;
    @FXML
    private HBox midHBox;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXButton buttonYes, buttonNo;

    private boolean isPainSet = false;
    private boolean isMoodSet = false;
    private boolean isDiagnosed = false;
    //    private final String SCRIPT_PATH = "../pl/sympathetic_doctor.pl";
    private final String TMP_PATH = "C:/Users/Sebastian/Documents/Prolog/sympathetic_doctor.pl";

    private AbstractManager queryManager;
    private TextSpeechManager speechManager;

    public void beginConversation() {
        try {
            consultScript(TMP_PATH);
        } catch (ConsultException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        initVolumeSlider();
        speechManager = new TextSpeechManager();
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
        speechManager.stop();
        gestureText.setText("(" + gesture + ") ");
        questionText.setText(question);
        speechManager.speak(question);
        System.out.println("(" + gesture + ") " + question); //Debug
    }

    /**
     * Callback function for the "yes" button
     *
     * @param actionEvent
     */
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

    /**
     * Callback function for the "No" button
     *
     * @param actionEvent the click event
     */
    public void onClickNo(ActionEvent actionEvent) {
        if (!isDiagnosed) queryManager.onClickNo();
        if (!isDiagnosed) displayResponse();
    }

    /**
     * Loads the mood and pain icon as a feedback to the user.
     */
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

    /**
     * Loads the image given in the path to the image view with tooltip
     *
     * @param path      the image resource path
     * @param imageView the ImageView to be set the image
     * @param tooltip   the tooltip message for the image
     */
    private void loadImage(String path, ImageView imageView, String tooltip) {
        Image image = new Image(path);
        imageView.setImage(image);
        Tooltip.install(imageView, new Tooltip(tooltip));
    }

    /**
     * Callback function when the patient answers yes for an asked symptom.
     * The function adds the symptom to the ListView.
     *
     * @param symptom the symptoms confirmed
     */
    @Override
    public void onSymptomsYes(String symptom) {
        System.out.println(symptom);
        symptomsListView.getItems().add(symptom);
    }


    /**
     * Callback function when there is a disease which every symptoms match the patient's
     *
     * @param gesture the gesture the doctor will act
     * @param message the message the doctor will speak
     * @param disease the diagnosed disease
     */
    @Override
    public void onDiagnosePerfectMatch(String gesture, String message, String disease) {
        isDiagnosed = true;
        displayResponse(gesture, message + disease);
        disableButtons();
    }

    /**
     * Callback function when there is no disease that matches the patient's symptoms perfectly, but there are
     * diseases that match some of the patient's symptoms.
     *
     * @param gesture  the gesture the doctor will act
     * @param message  the message the doctor will speak
     * @param diseases potential diseases array list
     */
    @Override
    public void onDiagnosePartialMatch(String gesture, String message, ArrayList<PotentialDisease> diseases) {
        isDiagnosed = true;
        displayResponse(gesture, message);
        displayChart(diseases);
        disableButtons();
    }

    /**
     * Callback function when the patient is healthy.
     * This means the patient does not match any single symptom of all diseases.
     *
     * @param gesture the gesture the doctor will act
     * @param message the message the doctor will speak
     */
    @Override
    public void onDiagnoseNoMatch(String gesture, String message) {
        isDiagnosed = true;
        displayResponse(gesture, message);
        disableButtons();
    }

    /**
     * Used to setup and display the bar chart of potential diseases.
     *
     * @param diseases array list of potential diseases
     */
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

    /**
     * Used to display the values above the bars in the bar chart.
     *
     * @param data the data that holds the node to the value
     */
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

    private void initVolumeSlider() {
        volumeSlider.setValue(100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                double volume = volumeSlider.getValue();

                if (volume == 0) {
                    volumeImage.setImage(new Image("res/img/utilities/mute.png"));
                    speechManager.stop();
                } else {
                    volumeImage.setImage(new Image("res/img/utilities/volume.png"));
                }

                speechManager.setVolume(volume);
            }
        });
    }

    private void disableButtons(){
        buttonYes.setDisable(true);
        buttonNo.setDisable(true);
    }
}
