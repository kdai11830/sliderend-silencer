import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SliderEndSilencer {
	
	// new line for .osu file
	public final static String nl = System.getProperty("line.separator");
	
	private OsuFileUtils ofu;
	private File osuFile;
	private boolean kickSliderSilence, halfTime, doubleTime, thirdSilence;
	
	public SliderEndSilencer(File osuFile, boolean kickSliderSilence, boolean halfTime, boolean doubleTime, boolean thirdSilence) 
			throws FileNotFoundException, UnsupportedEncodingException {
		
		this.ofu = new OsuFileUtils(osuFile);
		this.osuFile = this.ofu.getFile();
		this.kickSliderSilence = kickSliderSilence;
		this.halfTime = halfTime;
		this.doubleTime = doubleTime;
		this.thirdSilence = thirdSilence;
	}
	
	private ArrayList<String> writeSilencedTimingPoints() {
		ArrayList<String> output = new ArrayList<String>();
		TimingPoint currentTiming = this.ofu.getTimingPoints().get(0);
		//System.out.println(this.ofu.getTimingPoints().size());
		for (int i = 0; i < this.ofu.getSliders().size(); i++) {
			if (needsSilence(this.ofu.getSliders().get(i))) {
				System.out.println(this.ofu.getSliders().get(i).getStartTime());
				double sliderEnd = this.ofu.getSliderEnd(this.ofu.getSliders().get(i));
				System.out.println(sliderEnd);
				System.out.println(this.ofu.fourthAfter(this.ofu.getSliders().get(i)));
				double volumeReset = this.ofu.getNextHitObject(this.ofu.getSliders().get(i));
				currentTiming = this.ofu.getCurrentTimingSection(this.ofu.getSliders().get(i));
				String[] temp = currentTiming.getTimingElements();
				temp[0] = String.valueOf(sliderEnd);
				temp[5] = String.valueOf(5);
				TimingPoint silence = new TimingPoint(temp);
				output.add(silence.toString());
				boolean needsReset = true;
				for (int j = 0; j < this.ofu.getTimingPoints().size(); j++) {
					if (this.ofu.getTimingPoints().get(j).getOffset() == volumeReset)
						needsReset = false;
				}
				if (needsReset) {
					temp[0] = String.valueOf(volumeReset);
					temp[5] = String.valueOf(currentTiming.getVolume());
					TimingPoint reset = new TimingPoint(temp);
					output.add(reset.toString());
				}
			}
		}
		return output;
	}
	
	// if over 1/2 (1/3 if checked), check for note less than 1/2
	private boolean needsSilence(Slider slider) {
		double minLength;
		if (thirdSilence)
			minLength = 0.33333;
		else
			minLength = 0.5;
		
		if (halfTime) 
			minLength /= 2.0;
		else if (doubleTime)
			minLength *= 2.0;
		
		if (kickSliderSilence)
			minLength /= 2.0;
		
		double beats = 
				Math.round(slider.getSliderBeats(this.ofu.getBaseMultiplier() * this.ofu.getSliderSV(slider)) * 100.0) / 100.0;
		double fourthAfter = this.ofu.fourthAfter(slider);
		boolean silence = false;
		if (beats >= minLength) {
			for (int i = 0; i < this.ofu.getSliders().size(); i++) {
				if (this.ofu.getSliders().get(i).getStartTime() > this.ofu.getSliderEnd(slider) &&
						(this.ofu.getSliders().get(i).getStartTime() <= fourthAfter ||
						this.ofu.getSliders().get(i).getStartTime() <= fourthAfter - 1 ||
						this.ofu.getSliders().get(i).getStartTime() <= fourthAfter + 1)) {
					silence = true;
				}
			}
			for (int j = 0; j < this.ofu.getHitCircles().size(); j++) {
				if (this.ofu.getHitCircles().get(j).getOffset() > this.ofu.getSliderEnd(slider) &&
						(this.ofu.getHitCircles().get(j).getOffset() <= fourthAfter) ||
						this.ofu.getHitCircles().get(j).getOffset() <= fourthAfter - 1 ||
						this.ofu.getHitCircles().get(j).getOffset() <= fourthAfter + 1) {
					if (!(beats % 0.5 == 0))
						silence = true;
				}
			}
			if (slider.getRepeat() > 1) {
				silence = false;
			}
		}
		return silence;
		
		/*double mod;
		if (halfTime)
			mod = 0.25;
		else if (doubleTime)
			mod = 1.0;
		else
			mod = 0.5;
		
		double beats = 
				Math.round(slider.getSliderBeats(this.ofu.getBaseMultiplier() * this.ofu.getSliderSV(slider)) * 100.0) / 100.0;
		if (this.kickSliderSilence) {
			return (beats % mod != 0);
		} else {
			return (beats % mod != 0) && (beats > (mod / 2.0));
		}*/
	}
	
	public boolean writeToFile() {
		String output = "";
		String read;
		boolean completed = false;
		boolean isTiming = false;
		boolean isTimingEnd = false;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.osuFile), "UTF-8"))) {

			while ((read = reader.readLine()) != null) {
				if (read.contains("[TimingPoints]")) {
					isTiming = true;
				}
				if (isTiming && read.length() == 0) {
					isTiming = false;
					isTimingEnd = true;
				} else if (!isTiming) {
					isTimingEnd = false;
				}
				
				output += read + nl;
				
				if (isTimingEnd) {
					ArrayList<String> silences = writeSilencedTimingPoints();
					for (int i = 0; i < silences.size(); i++) {
						output += (silences.get(i) + nl);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			completed = false;
		}
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(osuFile.getPath()))) {
			writer.write(output);
			completed = true;
			writer.close();
		} catch (IOException e) {
			completed = false;
		}
		
		return completed;
	}
}
