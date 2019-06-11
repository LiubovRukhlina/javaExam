package lab3.geoPosition1;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import lab1.geoPosition.GeoPosition;
import lab2.geoPosition.GeoRoute;

public class grouteGUI {
	
	int fHeight = 768 + 50;
	public int fWidth = 1024;
	
	
	//constructor called in main to draw the window
	public grouteGUI() {
		JFrame frame = new JFrame("Route builder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(fWidth, fHeight);
		
		InfoPanel infopanel = new InfoPanel();
		mapPanel mappanel = new mapPanel(infopanel);
		ButtonPanel buttonpanel = new ButtonPanel(mappanel);
		ControlPanel controlpanel = new ControlPanel(infopanel, buttonpanel);
		
		
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		contentPane.add(controlpanel);
		contentPane.add(mappanel);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new grouteGUI();
	}

}

class mapPanel extends JPanel implements MouseListener, ActionListener, MouseMotionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage mapImage;
	GeoRoute route = new GeoRoute("mapRoute");
	double top = 54.5720556;
	double left = 8.4375;
	double bottom = 53.3325;
	double right = 11.24725;
	int fHeight = 768;
	int fWidth = 1024;
	ControlPanel controlpanel;
	InfoPanel infopanel;
	ArrayList<Point> pts = new ArrayList<Point>();
	
	public mapPanel(InfoPanel infopanel) {
		//pts.add(new Point(50, 100));
		this.infopanel = infopanel;
		addMouseListener(this);
		addMouseMotionListener(this);
		//this.infoPanel = i;
	}
	
	private void distanceUpdate() {
		if (pts.size() > 1) {
			
			double distance = route.getDistance();
			
			infopanel.refreshDistance(distance);
		} else {
			infopanel.refreshDistance(0);
		}
	}
	
	public void positionUpdate(GeoPosition newPosition) {
		if (pts.size() > 0) {
			infopanel.refreshPosition(newPosition);
		}else {
			infopanel.refreshPosition(new GeoPosition(0,0));
		}
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		try {
			mapImage = ImageIO.read(new File("lab3/geoPosition1/OSM_Map.png"));
			g.drawImage(mapImage, 0, 0, null);
			g.setColor(Color.red);
			for (Point i : pts) {
				
				g.fillOval(i.x - 7, i.y - 7, 15, 15);
				
			}
			
			Graphics2D g2 = (Graphics2D) g; 
			g2.setColor(Color.blue);
			g2.setStroke(new BasicStroke(4.0f));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if (route.getNumberWaypoints() > 1) {
				for (int i = 0; i < route.getNumberWaypoints() - 1; i ++) {
					g2.drawLine(pts.get(i).x, pts.get(i).y, pts.get(i + 1).x, pts.get(i + 1).y);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GeoPosition pointToGeoPosition(int x, int y) {
		double coordX  = left + (right - left) * x / fWidth;
		double coordY  = top - (top - bottom) * y / fHeight;
		return new GeoPosition(coordX, coordY);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		route = new GeoRoute("r");
		pts = new ArrayList<Point>();
		this.distanceUpdate();
		this.positionUpdate(new GeoPosition(0.0,0.0));
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		Point pt = new Point(e.getX(),e.getY());
		GeoPosition newPosition = pointToGeoPosition(e.getX(),e.getY());
		
		route.addWaypoint(newPosition);
		pts.add(pt);
		System.out.println(route.getDistance());
		System.out.println(pts.size());
		this.distanceUpdate();
		this.positionUpdate(newPosition);
		this.repaint();
		System.out.println("Pressed");
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

class InfoPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JLabel distanceLabel = new JLabel("Total distance: ", JLabel.CENTER);
	JLabel positionLabel = new JLabel("Position: ", JLabel.CENTER);
	
	public InfoPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	
		this.add(distanceLabel);
		this.add(positionLabel);
	}
	
	public void refreshDistance(double distance) {
		distanceLabel.setText(String.format("Total distance: %.3f km", distance));
		repaint();
	}
	
	public void refreshPosition(GeoPosition gposition) {
		positionLabel.setText(String.format("Position: %.3f, %.3f", gposition.getLongitude(), gposition.getLatitude()));
		repaint();
	}
}

class ButtonPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton deleteRoute = new JButton("Delete route");
	public ButtonPanel(mapPanel mappanel){
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		deleteRoute.addActionListener(mappanel);
		deleteRoute.setAlignmentX(CENTER_ALIGNMENT);
		this.add(deleteRoute);
	}
}

class ControlPanel extends JPanel{
	InfoPanel info;
	public ControlPanel(InfoPanel info, ButtonPanel buttons) {
		this.info = info;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(buttons);
		this.add(info);
		
	}
	
}