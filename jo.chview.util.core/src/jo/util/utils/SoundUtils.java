package jo.util.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundUtils 
{
	/**
	 * Saves PCM data to WAV file
	 * 
	 * @param pcmdata
	 *            : byte array containing the PCM data
	 * @param srate
	 *            : Sample rate
	 * @param channel
	 *            : no. of channels
	 * @param format
	 *            : PCM format (16 bit)
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */

	public static void saveWavFile(byte[] pcmdata, int srate, int channel, int format, File out) {

		OutputStream os = null;

		try {
			os = new FileOutputStream(out);
		} catch (FileNotFoundException e) {
		    DebugUtils.error("Could not create the wav file");
			e.printStackTrace();
		}

		byte[] header = new byte[44];
		byte[] data = pcmdata;

		long totalDataLen = data.length + 36;
		long bitrate = srate * channel * format;

		header[0] = 'R';
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = (byte) format;
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;
		header[21] = 0;
		header[22] = (byte) channel;
		header[23] = 0;
		header[24] = (byte) (srate & 0xff);
		header[25] = (byte) ((srate >> 8) & 0xff);
		header[26] = (byte) ((srate >> 16) & 0xff);
		header[27] = (byte) ((srate >> 24) & 0xff);
		header[28] = (byte) ((bitrate / 8) & 0xff);
		header[29] = (byte) (((bitrate / 8) >> 8) & 0xff);
		header[30] = (byte) (((bitrate / 8) >> 16) & 0xff);
		header[31] = (byte) (((bitrate / 8) >> 24) & 0xff);
		header[32] = (byte) ((channel * format) / 8);
		header[33] = 0;
		header[34] = 16;
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (data.length & 0xff);
		header[41] = (byte) ((data.length >> 8) & 0xff);
		header[42] = (byte) ((data.length >> 16) & 0xff);
		header[43] = (byte) ((data.length >> 24) & 0xff);

		try {
			os.write(header, 0, 44);
			os.write(data);
			os.close();
		} catch (IOException e) {
		    DebugUtils.error("Error writing data to wav file");
			e.printStackTrace();
		}

		DebugUtils.trace("wrote Wav File");

		// File saved succesfully so play the audio
		//playWav(fileName);

	}
	
	public static void play(File sound) throws IOException, UnsupportedAudioFileException, LineUnavailableException
	{
		AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
		play(ais);
	}
	
	public static void play(InputStream is) throws IOException, UnsupportedAudioFileException, LineUnavailableException
	{
		AudioInputStream ais = AudioSystem.getAudioInputStream(is);
		play(ais);
	}
	
    public static void play(AudioInputStream rawIn) throws IOException, LineUnavailableException
    {
    	AudioFormat inputFormat = rawIn.getFormat();
    	AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                inputFormat.getSampleRate(),
                16,
                inputFormat.getChannels(),
                inputFormat.getChannels() * 2,
                inputFormat.getSampleRate(),
                false);
    	DebugUtils.trace("Input format: "+inputFormat);
    	DebugUtils.trace("Target format: "+targetFormat);
    	AudioInputStream in = AudioSystem.getAudioInputStream(targetFormat, rawIn);
        
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null)
        {
            // Start
            line.start();
            int nBytesRead = 0;
            while (nBytesRead != -1)
            {
                nBytesRead = in.read(data, 0, data.length);
                DebugUtils.trace("Read "+nBytesRead);
                if (nBytesRead != -1) 
                	line.write(data, 0, nBytesRead);
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            in.close();
        }
    }	

    private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
    {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

}
