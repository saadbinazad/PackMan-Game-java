package game;


import javax.swing.JFrame;

public class Pacman extends JFrame{

	public Pacman() {
		this.add(new Body());
	}
	
	public static void main(String[] args) {
		Pacman p = new Pacman();
		
		p.setTitle("Pacman 2D Game");
		p.setSize(376,423);
		p.setDefaultCloseOperation(EXIT_ON_CLOSE);
		p.setLocationRelativeTo(null);
		p.setVisible(true);
		
	}

}
