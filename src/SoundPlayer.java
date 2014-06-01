import java.io.InputStream;
import java.io.Serializable;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;


public class SoundPlayer extends Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2249581664566809422L;

	public SoundPlayer(InputStream arg0, AudioDevice arg1)
			throws JavaLayerException {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public SoundPlayer(InputStream arg0) throws JavaLayerException {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
