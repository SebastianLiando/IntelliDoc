package core;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.jpl7.Query;
import org.jpl7.Term;

import javax.tools.Tool;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Controller {
    @FXML
    private Text gestureText, questionText;
    @FXML
    private ImageView moodImage, painImage;

    private boolean isPainSet = false;
    private boolean isMoodSet = false;
//    private final String SCRIPT_PATH = "@../pl/sympathetic_doctor.pl";
    private final String TMP_PATH = "C:/Users/Sebastian/Documents/Prolog/sympathetic_doctor.pl";

    private IManager queryManager;

    public void beginConversation(){
        try {
            consultScript(TMP_PATH);
        }catch (ConsultException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        askPain();
    }

    private void consultScript(String PATH) throws ConsultException{
        String queryString = "['"+ PATH +"']";
        Query query = new Query(queryString);
        System.out.println("Loaded script: " + query.hasSolution());
    }

    private void askPain(){
        queryManager = new PainManager();
        showQuestion();
    }

    private void askMood(){
        queryManager = new MoodManager();
        showQuestion();
    }

    private void showQuestion(){
        String gesture = queryManager.getGesture();
        gestureText.setText("(" + gesture + ") ");
        String question = queryManager.getQuestion();
        questionText.setText(question);

        System.out.println("(" + gesture + ") " + question); //Debug
    }

    public void onClickYes(ActionEvent actionEvent) {
        queryManager.onClickYes();
        if (!isPainSet){
            isPainSet = true;
            askMood();
        }else if (!isMoodSet){
            isMoodSet = true;
            setIcon();

            //TODO: hello
        }else{

            //TODO: hello
        }
    }

    public void onClickNo(ActionEvent actionEvent) {
        queryManager.onClickNo();
        showQuestion();
    }

    private void setIcon(){
        if (isPainSet) {
            Query tmp = new Query("patient_pain(X)");
            String pain = tmp.nextSolution().get("X").toString();
            String imagePath = "res/img/pain/" + pain + ".png";
            loadImage(imagePath, painImage, pain);
        }

        if (isMoodSet){
            Query tmp = new Query("patient_mood(X)");
            String mood =tmp.nextSolution().get("X").toString();
            String imagePath = "res/img/mood/" + mood + ".png";
            loadImage(imagePath, moodImage, mood);
        }
    }

    private void loadImage(String path, ImageView imageView, String tooltip){
        Image image = new Image(path);
        imageView.setImage(image);
        Tooltip.install(imageView, new Tooltip(tooltip));
    }
}
