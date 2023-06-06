import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class PiecePicker extends JFrame {
	
	private GameBoard gb;
	private JLabel selectedImg;
	private int selectedId;
	
	public PiecePicker(GameBoard gb){
		this.gb = gb;
		setLayout(new FlowLayout());
		int colOff = gb.getToMove() == 1 ? 0 : 1;
		addPiece(2+colOff, 2);
		addPiece(4+colOff, 3);
		addPiece(6+colOff, 4);
		addPiece(8+colOff, 5);
		JPanel buttonPanel = new JPanel();
		JButton submit = new JButton("Promote");
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gb.setPromotion(selectedId);
				dispose();
			}
		});
		buttonPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
		submit.setPreferredSize(new Dimension(80, 40));
		buttonPanel.add(submit);
		add(buttonPanel);
		setTitle("Promote Piece");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void addPiece(int imgIndex, int id){
		JPanel holder = new JPanel();
		JLabel img = new JLabel(new ImageIcon(gb.getPieceImgs()[imgIndex]));
		img.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(selectedImg != null) 
					((JPanel) selectedImg.getParent()).setBorder(new LineBorder(getBackground()));
				selectedImg = (JLabel) e.getComponent();
				selectedId = id;
				((JPanel) selectedImg.getParent()).setBorder(new LineBorder(Color.BLACK));
			}
		});
		if(id == 5){
			selectedImg = img;
			selectedId = 5;
			holder.setBorder(new LineBorder(Color.BLACK));
		}
		else holder.setBorder(new LineBorder(getBackground()));
		holder.add(img);
		add(holder);
	}
}
