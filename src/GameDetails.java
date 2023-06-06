import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GameDetails extends JFrame{
	
	private GameBoard gb;
	private String firstName;
	private String lastName;
	
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField wTimeField;
	private JTextField bTimeField;
	
	public GameDetails(GameBoard gb){
		this.gb = gb;
		setLayout(new GridLayout(0, 2));

		init();
		
		setTitle("Game Details");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setAlwaysOnTop(true);
	}
	
	public void reset(){
		firstName = "";
		lastName = "";
	}
	
	public void init(){
		JPanel holder = new JPanel();
		JLabel nameLabel = new JLabel("First Name:");
		holder.add(nameLabel);
		firstNameField = new JTextField();
		firstNameField.setPreferredSize(new Dimension(85, 30));
		holder.add(firstNameField);
		add(holder);
		
		holder = new JPanel();
		nameLabel = new JLabel("Last Name:");
		holder.add(nameLabel);
		lastNameField = new JTextField();
		lastNameField.setPreferredSize(new Dimension(85, 30));
		holder.add(lastNameField);
		add(holder);
		
		holder = new JPanel();
		nameLabel = new JLabel("Human Time:");
		holder.add(nameLabel);
		wTimeField = new JTextField();
		wTimeField.setPreferredSize(new Dimension(85, 30));
		holder.add(wTimeField);
		add(holder);
		
		holder = new JPanel();
		nameLabel = new JLabel("Computer Time:");
		holder.add(nameLabel);
		bTimeField = new JTextField();
		bTimeField.setPreferredSize(new Dimension(85, 30));
		holder.add(bTimeField);
		add(holder);
		
		holder = new JPanel();
		nameLabel = new JLabel("White:");
		holder.add(nameLabel);
		JCheckBox whiteCheckBox = new JCheckBox();
		whiteCheckBox.setSelected(true);
		whiteCheckBox.setPreferredSize(new Dimension(40, 30));
		JCheckBox blackCheckBox = new JCheckBox();
		blackCheckBox.setPreferredSize(new Dimension(40, 30));
		whiteCheckBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!whiteCheckBox.isSelected()){
					whiteCheckBox.setSelected(true);
				}
				blackCheckBox.setSelected(false);
				Constant.HUMAN_PLAYER = 1;
			}
		});
		blackCheckBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!blackCheckBox.isSelected()){
					blackCheckBox.setSelected(true);
				}
				whiteCheckBox.setSelected(false);
				Constant.HUMAN_PLAYER = -1;
			}
		});
		holder.add(whiteCheckBox);
		nameLabel = new JLabel("Black:");
		holder.add(nameLabel);
		holder.add(blackCheckBox);
		add(holder);
		
		holder = new JPanel();
		JButton submit = new JButton("Submit");
		submit.setPreferredSize(new Dimension(85, 30));
		submit.setAlignmentX(JButton.CENTER_ALIGNMENT);
		submit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					Constant.P1_START_TIME = (long) (Double.parseDouble(wTimeField.getText())*60*1000);
					Constant.P2_START_TIME = (long) (Double.parseDouble(bTimeField.getText())*60*1000);
					if(Constant.P1_START_TIME <= 0 || Constant.P2_START_TIME <= 0){
						throw new NumberFormatException();
					}
					firstName = firstNameField.getText();
					lastName = lastNameField.getText();
					
					for(char c : (firstName+lastName).toCharArray()){
						if(!Character.isAlphabetic(c)) throw new InvalidName();
					}
					
					gb.getTimeManager().reset();
					setVisible(false);
				} catch (NumberFormatException error) {
					wTimeField.setText("error");
					bTimeField.setText("error");
					setVisible(true);
				} catch (InvalidName error){
					firstNameField.setText("error");
					lastNameField.setText("error");
					setVisible(true);
				}
			}
		});
		holder.add(submit);
		add(holder);
	}

	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}

	public void setFirstLastName(String firstName2, String lastName2) {
		firstName = firstName2;
		lastName = lastName2;
	}
}
