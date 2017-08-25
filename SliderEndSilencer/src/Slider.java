import java.awt.Point;
import java.util.ArrayList;

public class Slider {
	
	private int xValue;
	private int yValue;
	private long startTime;
	private int hitsound;
	private char sliderType;
	private ArrayList<Point> anchors;
	private int repeat;
	private double pixelLength;
	private ArrayList<Integer> endHitsounds;
	private ArrayList<Point> edgeAdditions;
	private int[] endAdditions;
	private String fileAddition;
	
	public Slider(String[] sliderElements) {
		this.anchors = new ArrayList<Point>();
		this.endHitsounds = new ArrayList<Integer>();
		this.edgeAdditions = new ArrayList<Point>();
		
		this.xValue = Integer.parseInt(sliderElements[0]);
		this.yValue = Integer.parseInt(sliderElements[1]);
		this.startTime = Long.parseLong(sliderElements[2]);
		this.hitsound = Integer.parseInt(sliderElements[4]);
		this.sliderType = sliderElements[5].charAt(0);
		
		String[] anchorStrings = sliderElements[5].substring(2).split("\\|");
		for (int i = 0; i < anchorStrings.length; i++) {
			int x = Integer.parseInt(anchorStrings[i].split(":")[0]);
			int y = Integer.parseInt(anchorStrings[i].split(":")[1]);
			this.anchors.add(new Point(x, y));
		}
		
		this.repeat = Integer.parseInt(sliderElements[6]);
		this.pixelLength = Double.parseDouble(sliderElements[7]);
		
		try {
			String[] hitsoundStrings = sliderElements[8].split("|");
			for (int i = 0; i < hitsoundStrings.length; i++) {
				this.endHitsounds.add(Integer.parseInt(hitsoundStrings[i]));
			}
		} catch (IndexOutOfBoundsException e) {
			return;
		}
		try {
			String[] edgeAdditionStrings = sliderElements[9].split("|");
			for (int i = 0; i < edgeAdditionStrings.length; i++) {
				int x = Integer.parseInt(edgeAdditionStrings[i].split(":")[0]);
				int y = Integer.parseInt(edgeAdditionStrings[i].split(":")[1]);
				this.edgeAdditions.add(new Point(x, y));
			}
		} catch (IndexOutOfBoundsException e) {
			return;
		}
		try {	
			this.endAdditions = new int[4];
			String[] endAdditionsStrings = sliderElements[10].split(":");
			for (int i = 0; i < endAdditions.length; i++) {
				this.endAdditions[i] = Integer.parseInt(endAdditionsStrings[i]);
			}
			if (endAdditionsStrings.length == 5) {
				this.fileAddition = endAdditionsStrings[5];
			} else {
				this.fileAddition = "";
			}
		} catch (IndexOutOfBoundsException e) {
			return;
		}
		
	}
	
	public Point getXY() {
		return new Point(this.xValue, this.yValue);
	}
	
	public long getStartTime() {
		return this.startTime;
	}
	
	public int getHitsound() {
		return this.hitsound;
	}
	
	public char sliderType() {
		return this.sliderType;
	}
	
	public ArrayList<Point> getAnchors() {
		return this.anchors;
	}
	
	public int getRepeat() {
		return this.repeat;
	}
	
	public double getPixelLength() {
		return this.pixelLength;
	}
	
	public ArrayList<Integer> getEndHitsounds() {
		return this.endHitsounds;
	}
	
	public ArrayList<Point> getEdgeAdditions() {
		return this.edgeAdditions;
	}
	
	public int[] getEndAdditions() {
		return this.endAdditions;
	}
	
	public String getFileAddition() {
		return this.fileAddition;
	}
	
	public double getSliderBeats(double multiplier) {
		double oneBeatLength = multiplier * 100.0;
		return this.pixelLength / oneBeatLength;
	}
}
