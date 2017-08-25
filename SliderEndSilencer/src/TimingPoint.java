
public class TimingPoint {
	
	private double offset;
	private double bpm;
	private double sv;
	private int meter;
	private int sampleType;
	private int sampleSet;
	private int volume;
	private boolean inherited;
	private boolean kiai;
	private String[] timingElements;
	
	public TimingPoint(String[] timingElements) {
		this.offset = Double.parseDouble(timingElements[0]);
		this.inherited = (Integer.parseInt(timingElements[6]) == 1);
		this.meter = Integer.parseInt(timingElements[2]);
		this.sampleType = Integer.parseInt(timingElements[3]);
		this.sampleSet = Integer.parseInt(timingElements[4]);
		this.volume = Integer.parseInt(timingElements[5]);
		this.kiai = (Integer.parseInt(timingElements[7]) == 1);
		this.bpm =  Double.parseDouble(timingElements[1]);
		if (bpm < 0)
			this.sv = -(bpm);
		else
			this.sv = 100;
		this.timingElements = timingElements;
	}
	
	public TimingPoint(String[] timingElements, double bpm) {
		this(timingElements);
		this.bpm = bpm;
		this.sv = -(Double.parseDouble(timingElements[1]));
	}
	
	public double getOffset() {
		return this.offset;
	}
	
	public double getBPM() {
		return this.bpm;
	}
	
	public double getSV() {
		return this.sv;
	}
	
	public double getSVMultiplier() {
		return 100.0 / this.sv;
	}
	
	public double getBPMInBeats() {
		return (1 / (this.bpm / 1000.0)) * 60.0;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.timingElements.length - 1; i++) {
			sb.append(this.timingElements[i]);
			sb.append(",");
		}
		sb.append(this.timingElements[this.timingElements.length - 1]);
		
		return sb.toString();
	}
	
	public String[] getTimingElements() {
		return this.timingElements;
	}

}
