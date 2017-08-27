import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class WindowOption extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JCheckBox chckbxSilenceKicksliders;
	private JCheckBox chckbxSilence;
	private JRadioButton rdbtnHalfTime;
	private JRadioButton rdbtnNormalTime;
	private JRadioButton rdbtnDoubleTime;
	private JButton btnSilenceSliderends;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private File osuFile;
	
	/**
	 * Create the frame.
	 */
	public WindowOption(File osuFile) {
		this.osuFile = osuFile;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 201, 284);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		this.chckbxSilenceKicksliders = new JCheckBox("Silence Kicksliders");
		chckbxSilenceKicksliders.setBounds(32, 30, 116, 23);
		contentPane.add(chckbxSilenceKicksliders);
		
		this.rdbtnHalfTime = new JRadioButton("Half Time");
		buttonGroup.add(rdbtnHalfTime);
		rdbtnHalfTime.setBounds(32, 88, 109, 23);
		contentPane.add(rdbtnHalfTime);
		
		this.rdbtnNormalTime = new JRadioButton("Normal Time");
		buttonGroup.add(rdbtnNormalTime);
		rdbtnNormalTime.setBounds(32, 114, 109, 23);
		contentPane.add(rdbtnNormalTime);
		
		this.rdbtnDoubleTime = new JRadioButton("Double Time");
		buttonGroup.add(rdbtnDoubleTime);
		rdbtnDoubleTime.setBounds(32, 140, 109, 23);
		contentPane.add(rdbtnDoubleTime);
		
		this.btnSilenceSliderends = new JButton("Silence SliderEnds");
		btnSilenceSliderends.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					btnSilenceSliderendsActionPerformed(arg0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnSilenceSliderends.setBounds(32, 183, 124, 36);
		contentPane.add(btnSilenceSliderends);
		
		chckbxSilence = new JCheckBox("Silence 1/3");
		chckbxSilence.setBounds(32, 56, 97, 23);
		contentPane.add(chckbxSilence);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
	private void btnSilenceSliderendsActionPerformed(ActionEvent evt) throws IOException {
		boolean halfTime = false;
		boolean doubleTime = false;
		if (rdbtnHalfTime.isSelected()) halfTime = true;
		else if (rdbtnDoubleTime.isSelected()) doubleTime = true;
		
		SliderEndSilencer ses = new SliderEndSilencer(this.osuFile, this.chckbxSilenceKicksliders.isSelected(), 
				halfTime, doubleTime, this.chckbxSilence.isSelected());
		if (ses.writeToFile()) {
			WindowComplete window = new WindowComplete();
			window.setVisible(true);
			this.dispose();
		} else {
			JOptionPane.showMessageDialog(contentPane, "Error writing to file. Please restart.");
			WindowChooser window = new WindowChooser();
			window.setVisible(true);
			this.dispose();
		}
	}
}
