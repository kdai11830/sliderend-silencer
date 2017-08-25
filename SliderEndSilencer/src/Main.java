import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		File osuFile = new File("C:/Program Files/osu!/Songs/654640 Minase Inori - Yume no Tsubomi/Minase Inori - Yume no Tsubomi (Kami-senpai) [test].osu");
		SliderEndSilencer ses = new SliderEndSilencer(osuFile, false, false, false);
		if (ses.writeToFile())
			return;
		else
			System.out.println("failed");
	}
}
