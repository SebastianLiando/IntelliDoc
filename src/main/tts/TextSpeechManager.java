package main.tts;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

import javax.sound.sampled.AudioInputStream;

public class TextSpeechManager {
    private final String DEFAULT_VOICE = "dfki-prudence-hsmm";

    private MaryInterface maryInterface;
    private AudioPlayer player;

    public TextSpeechManager() {
        configureInterface();
    }

    public void speak(String message) {
        stop();
        try {
            player = new AudioPlayer();
            AudioInputStream audio = maryInterface.generateAudio(message);
            player.setAudio(audio);
            player.start();
        } catch (SynthesisException e) {
            System.out.println("Unable to synthesis audio");
            e.printStackTrace();
        }
    }

    public void stop(){
        if (player != null) player.cancel();
    }

    public void setVolume(double volume){
        float vol = (float) volume / 100;
        configureInterface();
        maryInterface.setAudioEffects("Volume(amount:" + vol + ")");
    }

    private void configureInterface(){
        try {
            maryInterface = new LocalMaryInterface();
            maryInterface.setVoice(DEFAULT_VOICE);
            maryInterface.setAudioEffects("Volume(amount:1.00)");
        } catch (MaryConfigurationException e) {
            System.out.println("Unable to load mary interface");
            e.printStackTrace();
        }
    }
}
