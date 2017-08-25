import java.awt.Point;
import java.io.File;

public class HitCircle {

	private Point coordinates;
	private double offset;
	/*private int type;
	private int hitsound;
	private int additions[] = new int[4];
	private File fileAddition;*/
	
	public HitCircle(String[] circleElements) {
		int x = Integer.parseInt(circleElements[0]);
		int y = Integer.parseInt(circleElements[1]);
		this.coordinates = new Point(x, y);
		this.offset = Double.parseDouble(circleElements[2]);
		/*this.type = Integer.parseInt(circleElements[3]);
		this.hitsound = Integer.parseInt(circleElements[4]);
		String[] additionsStrings = circleElements[5].split(":");
		for (int i = 0; i < 4; i++) {
			this.additions[i] = Integer.parseInt(additionsStrings[i]);
		}
		if (additionsStrings.length == 5) {
			this.fileAddition = new File(additionsStrings[4]);
		}
		System.out.println(offset);*/
	}
	
	public double getOffset() {
		return this.offset;
	}
	
	public Point getCoordinates() {
		return this.coordinates;
	}
}
