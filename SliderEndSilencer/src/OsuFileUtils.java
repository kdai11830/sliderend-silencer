import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class OsuFileUtils {
	
	private static final String GENERAL_SECTION = "[General]";
	private static final String EDITOR_SECTION = "[Editor]";
	private static final String METADATA_SECTION = "[Metadata]";
	private static final String DIFFICULTY_SECTION = "[Difficulty]";
	private static final String EVENTS_SECTION = "[Events]";
	private static final String TIMING_SECTION = "[TimingPoints]";
	private static final String COLOURS_SECTION = "[Colours]";
	private static final String HITOBJECTS_SECTION = "[HitObjects]";
	
	private File osuFile;
	private int generalSectionStart;
	private int editorSectionStart;
	private int metadataSectionStart;
	private int difficultySectionStart;
	private int eventsSectionStart;
	private int timingSectionStart;
	private int coloursSectionStart;
	private int hitObjectSectionStart;
	
	private ArrayList<Slider> sliders = new ArrayList<Slider>();
	private ArrayList<HitCircle> hitCircles = new ArrayList<HitCircle>();
	private ArrayList<TimingPoint> timingPoints = new ArrayList<TimingPoint>();
	private double baseMultiplier;
	
	public OsuFileUtils(File osuFile) throws FileNotFoundException, UnsupportedEncodingException {
		this.osuFile = osuFile;
		final BufferedReader sc = new BufferedReader(new InputStreamReader(new FileInputStream(this.osuFile), "UTF-8"));
		this.generalSectionStart = findSection(GENERAL_SECTION, sc);
		this.editorSectionStart = findSection(EDITOR_SECTION, sc);
		this.metadataSectionStart = findSection(METADATA_SECTION, sc);
		this.difficultySectionStart = findSection(DIFFICULTY_SECTION, sc);
		this.eventsSectionStart = findSection(EVENTS_SECTION, sc);
		this.timingSectionStart = findSection(TIMING_SECTION, sc);
		this.coloursSectionStart = findSection(COLOURS_SECTION, sc);
		this.hitObjectSectionStart = findSection(HITOBJECTS_SECTION, sc);
		
		initSlidersAndCircles();
		initBaseMultiplier();
		initTimingPoints();
	}
	
	private int findSection(final String regex, BufferedReader sc) {
		String read;
		int line = 0;
		try {
			while ((read = sc.readLine()) != null) {
				if (read.contains(regex)) {
					break;
				} else {
					line++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	private void initSlidersAndCircles() {
		try {
			BufferedReader sc = new BufferedReader(new InputStreamReader(new FileInputStream(this.osuFile), "UTF-8"));
			String read;
			boolean isHitObject = false;
			while ((read = sc.readLine()) != null) {
				if (read.contains(HITOBJECTS_SECTION)) {
					isHitObject = true;
					continue;
				}
				
				if (isHitObject && read.length() == 0) {
					isHitObject = false;
				}
				
				if (isHitObject) {
					String[] object = read.split(",");
					if (read.contains("B") || read.contains("P") || read.contains("L")) {
						this.sliders.add(new Slider(object));
					} else {
						this.hitCircles.add(new HitCircle(object));
					}
				}
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void initBaseMultiplier() {
		try {
			@SuppressWarnings("resource")
			BufferedReader sc = new BufferedReader(new InputStreamReader(new FileInputStream(this.osuFile), "UTF-8"));
			String read;
			boolean isDifficulty = false;
			while ((read = sc.readLine()) != null) {
				if (read.contains(DIFFICULTY_SECTION)) 
					isDifficulty = true;
				
				if (isDifficulty && read.length() == 0) {
					isDifficulty = false;
				}
				
				if (isDifficulty) {
					if (read.contains("SliderMultiplier")) {
						this.baseMultiplier = Double.parseDouble(read.substring(17));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void initTimingPoints() {
		try {
			BufferedReader sc = new BufferedReader(new InputStreamReader(new FileInputStream(this.osuFile), "UTF-8"));
			String read;
			boolean isTiming = false;
			while ((read = sc.readLine()) != null) {
				if (read.contains(TIMING_SECTION)) {
					isTiming = true;
					continue;
				}
				
				if (isTiming && read.length() == 0) {
					isTiming = false;
				}
				
				if (isTiming) {
					String[] object = read.split(",");
					this.timingPoints.add(new TimingPoint(object));					
				}
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public int getTimingSectionStart() {
		return this.timingSectionStart;
	}
	
	public int getColoursSectionStart() {
		return this.coloursSectionStart;
	}
	
	public ArrayList<Slider> getSliders() {
		return this.sliders;
	}
	
	public double getBaseMultiplier() {
		return this.baseMultiplier;
	}
	
	public ArrayList<TimingPoint> getTimingPoints() {
		return this.timingPoints;
	}
	
	public ArrayList<HitCircle> getHitCircles() {
		return this.hitCircles;
	}
	
	public File getFile() {
		return this.osuFile;
	}
	
	public double getSliderSV(Slider slider) {
		for (int i = 0; i < this.timingPoints.size(); i++) {
			if (slider.getStartTime() < this.timingPoints.get(i).getOffset()) {
				return Math.round(this.timingPoints.get(i - 1).getSVMultiplier() * 100.0) / 100.0;
			}
		}
		return 1.0;
	}
	
	public double getSliderEnd(Slider slider) {
		TimingPoint currentPoint = getCurrentTimingSection(slider);
		double sliderBeats = Math.round(slider.getSliderBeats(this.baseMultiplier * 
				currentPoint.getSVMultiplier()) * 100.0) / 100.0;
		double sliderTime = sliderBeats * Math.round(getCurrentBPM(slider));
		return slider.getStartTime() + sliderTime;
	}
	
	public double resetVolume(Slider slider) {
		double sliderEnd = getSliderEnd(slider);
		double sliderTime = ((Math.round(getCurrentBPM(slider)) * 100.0) / 100.0) / 4.0;
		return sliderEnd + sliderTime;
	}
	
	public TimingPoint getCurrentTimingSection(Slider slider) {
		double sOffset = slider.getStartTime();
		int i = 0;
		TimingPoint currentPoint = this.timingPoints.get(0);
		while (sOffset >= this.timingPoints.get(i).getOffset()) {
			currentPoint = this.timingPoints.get(i);
			i++;
		}
		//System.out.println(currentPoint.getOffset());
		return currentPoint;
	}
	
	private double getCurrentBPM(Slider slider) {
		double sOffset = slider.getStartTime();
		int i = 0;
		TimingPoint currentTimingPoint = this.timingPoints.get(0);
		while (sOffset >= this.timingPoints.get(i).getOffset()) {
			if (this.timingPoints.get(i).getBPM() > 0) {
				currentTimingPoint = this.timingPoints.get(i);
			}
			i++;
		}
		return currentTimingPoint.getBPM();
	}
	
	public double getCurrentHitCircle(Slider slider) {
		double sOffset = getSliderEnd(slider);
		for (int i = 0; i < this.hitCircles.size(); i++) {
			if (this.hitCircles.get(i).getOffset() > sOffset) {
				return this.hitCircles.get(i).getOffset();
			}
		}
		return this.hitCircles.get(0).getOffset();
	}
	
}

