/** Imports **/
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Xnat Display Window **/
public class Xnat extends JFrame {

	// Application Version (long)
	private static final long serialVersionUID = 1L;

	/** Main Function **/
	public static void main(String[] args) {
		
		// Run XnatWindow
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ShowXnatWindow();
            }
        });
	}// End main function
	
	
	// Window Display Application
	private static void ShowXnatWindow() {
		// Initialize the UI
		initUI();
	}// 
	
	// XnatWindow Initialization Function
	private static void initUI() {
		// Declare, initialize window variables
		JFrame frame = new JFrame("LONI Xnat Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 600, 400);
		frame.setLayout(new GridBagLayout());
		
		// Display the login screen
		displayLogin(frame);	
		
		//Display the window
		//frame.pack(); // Makes the window as small as possible
		frame.setVisible(true);		 
	}// End initUI function
	
	
	// Displays the login features
	private static void displayLogin(JFrame frame) {

		// Add panel to the frame
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		// Add UI elements
		JLabel loginInfo = new JLabel("XNAT Central Login");
		JTextField usernameInput = new JTextField(20);
		JLabel usernameLabel = new JLabel("Username:");
		JPasswordField passwordInput = new JPasswordField(20);
		JLabel passwordLabel = new JLabel("Password:");
		
		
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		// Set the panel layout
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// Main Horizontal Group (contains everything)
		layout.setHorizontalGroup(layout.createSequentialGroup()
			// Group for Inputs and Labels
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				// Login Information
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(loginInfo))
				.addGroup(layout.createSequentialGroup()
					// Group for Labels
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					    .addComponent(usernameLabel)
					    .addComponent(passwordLabel))
					// Group for Inputs
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					    .addComponent(usernameInput)
					    .addComponent(passwordInput)))
				// Login Button
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(loginButton)))
		);
		
		// Main Vertical Group (contains everything)
		layout.setVerticalGroup(layout.createSequentialGroup()
			//Login Information
			.addComponent(loginInfo)
			// Username Group
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(usernameLabel)
				.addComponent(usernameInput))
			// Password Group
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(passwordLabel)
				.addComponent(passwordInput))
			// Login Button
			.addComponent(loginButton)
		);
		
	}// End displayLogin function

}// End Xnat class
