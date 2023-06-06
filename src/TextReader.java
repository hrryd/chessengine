import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;


public class TextReader extends JFrame{
	public TextReader(GameBoard gb){
		setLayout(new FlowLayout());
		JTextField textField = new JTextField();
		JButton submit = new JButton("Save");
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileManager.saveBoard(textField.getText(), gb);
				dispose();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}	
		});
		textField.setPreferredSize(new Dimension(160, 40));
		submit.setPreferredSize(new Dimension(60, 40));
		cancel.setPreferredSize(new Dimension(70, 40));
		add(textField);
		add(submit);
		add(cancel);
		setTitle("Save Name");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
