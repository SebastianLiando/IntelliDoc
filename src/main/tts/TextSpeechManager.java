package main.tts;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

import javax.sound.sampled.AudioInputStream;

/**
 * This is a controller class for the text-to-speech feature.
 */
public class TextSpeechManager {
    private final String DEFAULT_VOICE = "dfki-prudence-hsmm";

    private MaryInterface maryInterface;
    private AudioPlayer player;

    public TextSpeechManager() {
        configureInterface();
    }

    /**
     * Synthesizes the message and output it as an audio. The audio is played asynchronously.
     *
     * @param message the message to speak
     */
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

    /**
     * Stops the thread playing the audio, hence, stopping the speech.
     */
    public void stop() {
        if (player != null) player.cancel();
    }

    /**
     * Sets the volume of the audio
     *
     * @param volume the desired volume (0-100)
     */
    public void setVolume(double volume) {
        float vol = (float) volume / 100;
        configureInterface();
        maryInterface.setAudioEffects("Volume(amount:" + vol + ")");
    }

    /**
     * This method is used to setup the <code>MaryInterface</code>
     */
    private void configureInterface() {
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
