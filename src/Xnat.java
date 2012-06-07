/** Imports **/
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;
import java.util.regex.*;

/** Xnat Display Window **/
public class Xnat extends JFrame {

	// Application Version (long)
	private static final long serialVersionUID = 1L;
	private static String JSESSIONID = "";

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
		frame.setBounds(100, 100, 1024, 600);
		
		// Display the login screen
		displayLogin(frame);	
		
		//Display the window
		frame.setVisible(true);		 
	}// End initUI function
	
	
	// Clear the frame
	private static void clearFrame(JFrame frame) {
		frame.getContentPane().removeAll();
		frame.repaint();
		
		return;
	}// End clearFrame
	
	
	// Displays the login features
	private static void displayLogin(final JFrame frame) {

		// Add panel to the frame
		clearFrame(frame);
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		frame.setLayout(new GridBagLayout());
		
		// Add UI elements
		JLabel loginInfo = new JLabel("XNAT Central Login");
		final JTextField usernameInput = new JTextField(20);
		usernameInput.setText("");
		JLabel usernameLabel = new JLabel("Username:");
		final JPasswordField passwordInput = new JPasswordField(20);
		passwordInput.setText("");
		JLabel passwordLabel = new JLabel("Password:");
		
		
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				// Authenticate the user
				String authToken = authUser(frame, usernameInput.getText(), 
						new String(passwordInput.getPassword()));
				passwordInput.setText("");
				
				// Authentication success
				if (authToken != "") {
					JOptionPane.showMessageDialog(frame, authToken, "Authentication", JOptionPane.PLAIN_MESSAGE);
					JSESSIONID = authToken;
					displayFunctionalWindow(frame);
				}// End if
				
				// Authentication failure
				else {
					JOptionPane.showMessageDialog(frame, 
							"Please retry logging in. Your username and/or password seem to be incorrect.", 
							"Login Error", JOptionPane.PLAIN_MESSAGE);
					
					// Reset both fields
					usernameInput.setText("");
					passwordInput.setText("");
				}// End else
			}// End actionPerformed
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
	
	
	// User authentication function
	private static String authUser(JFrame frame, String username, String password)
	{
		try {
		    // Construct data
		    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
		    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
		    data += "&" + URLEncoder.encode("submit", "UTF-8") + "=" + URLEncoder.encode("Login", "UTF-8");

		    // Send data
		    URL url = new URL("https://central.xnat.org/app/action/XDATLoginUser");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();
		    
		    // Get the cookie
		    String LOOKING_FOR = "JSESSIONID";
		    String headerName=null;
		    String name = "";
		    String value = "";
			for (int i=1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
				
			    if (headerName.equalsIgnoreCase("Set-Cookie")) {
			    	
					StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), ";");

					// Pull out first token and assign names
					if (st.hasMoreTokens()) {
					    String token  = st.nextToken();
					    name = token.substring(0, token.indexOf("="));
					    value = token.substring(token.indexOf("=") + 1, token.length());
					    
					    // Look for JSESSIONID
					    if (LOOKING_FOR.equals(name)) {
					    	return value;					    	
					    }// End if
				    }// End if
			    }// End if
			}// End for
			
			return "";
		}// End try 
		
		catch (Exception e) {
			return "";
		}// End catch
	
	}// End authUser function
	
	// User logout function
	private static int logoutUser(JFrame frame)
	{
		try {
		    
		    // Send data
		    URL url = new URL("https://central.xnat.org/app/action/LogoutUser");
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String fullOutput = "";
		    while ((line = rd.readLine()) != null) {
		    	fullOutput += line;
		    	fullOutput += "\n";
		    }// End while
		    rd.close();
		    
		    // Parse the returned text for the Goodbye
		    // Very crude method, could be simplified with libraries
		    String goodbye = "Goodbye";
		    int logoutSuccess = fullOutput.indexOf(goodbye);
		    if (logoutSuccess != -1) {
		    	return 1;
		    }// End if
		    
		    else {
		    	 return 0;
		    }// End else
		    
		}// End try 
		
		catch (Exception e) {
			return 0;
		}// End catch
	
	}// End logoutUser function

	
	// Display XNAT Functional Window
	private static void displayFunctionalWindow(final JFrame frame) {

		// Clear frame, set layout to fill window
		clearFrame(frame);
		frame.setLayout(new GridLayout(1,1));
		
		// Add UI tabbed pane
		JTabbedPane tab = new JTabbedPane();
		frame.add(tab);
		
		// Create Projects Panel
		JPanel projectsPanel = setupProjectsPanel();
		
		// Create Subjects Panel
		JPanel subjectsPanel = new JPanel();
		
		// Create Scans Panel
		JPanel scansPanel = new JPanel();
		
		// Create Projects, Searches, Scans tabs
		final int numTabs = 4;
		tab.add("Projects", projectsPanel);
		tab.add("Subjects", subjectsPanel);
		tab.add("Scans", scansPanel);
		tab.add("Logout", new JPanel());
		
		// Listen for when Logout Tab is clicked
		ChangeListener changeListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent changeEvent) {
		        JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
		        int index = sourceTabbedPane.getSelectedIndex();
		        
		        // Logout functionality
		        if (index == (numTabs - 1))
		        {
		        	// Logout the user
					int result = logoutUser(frame);
					
					// Logout Success
					if (result == 1) {
						JSESSIONID = "";
						displayLogin(frame);
					}// End if
					
					// Logout failure
					else {
						JOptionPane.showMessageDialog(frame, 
								"Please retry logging out.", 
								"Login Error", JOptionPane.PLAIN_MESSAGE);
							
					}// End else
		        }// End if
		      }// End stateChanged
		    };
		    tab.addChangeListener(changeListener);
		
	}// End displayFunctionalWindow function
	
	
	// Display XNAT Functional Window
	private static JPanel setupProjectsPanel() {
		
		// Create main panel
		JPanel mainPanel = new JPanel();
		
		
		// Create left sub-panel for search fields
		JPanel searchPanel = new JPanel();
		searchPanel.setBounds(0, 0, 125, 350);
		searchPanel.setLayout(null);

		
		JLabel label1 = new JLabel("label1");
		label1.setBounds(5, 30, 60, 20);
		searchPanel.add(label1);
		final JTextField input1 = new JTextField(20);
		input1.setText("");
		input1.setBounds(5, 50, 120, 20);
		searchPanel.add(input1);

		JLabel label2 = new JLabel("label2");
		label2.setBounds(5, 80, 60, 20);
		searchPanel.add(label2);
		final JTextField input2 = new JTextField(20);
		input2.setText("");
		input2.setBounds(5, 100, 120, 20);
		searchPanel.add(input2);

		JLabel label3 = new JLabel("label3");
		label3.setBounds(5, 130, 60, 20);
		searchPanel.add(label3);
		final JTextField input3 = new JTextField(20);
		input3.setText("");
		input3.setBounds(5, 150, 120, 20);
		searchPanel.add(input3);

		JLabel label4 = new JLabel("label4");
		label4.setBounds(5, 180, 60, 20);
		searchPanel.add(label4);
		final JTextField input4 = new JTextField(20);
		input4.setText("");
		input4.setBounds(5, 200, 120, 20);
		searchPanel.add(input4);

		JLabel label5 = new JLabel("label5");
		label5.setBounds(5, 230, 60, 20);
		searchPanel.add(label5);
		final JTextField input5 = new JTextField(20);
		input5.setText("");
		input5.setBounds(5, 250, 120, 20);
		searchPanel.add(input5);
		
		
		// Create right sub-panel for projects
		final JPanel projectsPanel = new JPanel();
		projectsPanel.setBounds(130, 0, 1024, 600);
		projectsPanel.setLayout(new BorderLayout());
		projectsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		// Dynamically get list of projects from central.xnat.org
		String[] temp = loadXNATProjects();
		
		// Populate list with projects
		final JList list = new JList(temp);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String object = (String) list.getSelectedValue();
                    JOptionPane.showMessageDialog(projectsPanel.getRootPane(), loadXNATsubject(object), "Subjects", JOptionPane.PLAIN_MESSAGE);
                }// End if
            }// End valueChanged
        });
        
        // Add scroll pane
        JScrollPane pane = new JScrollPane();
        pane.getViewport().add(list);
        projectsPanel.add(pane);
		
		// Add panels and arrange
        mainPanel.setLayout(null);
		mainPanel.add(searchPanel);
		mainPanel.add(projectsPanel);
		
		return mainPanel;
	}// End setupProjectsPanel function
	
	
	
	// Load XNAT projects
	private static String[] loadXNATProjects() {
		
		try {
		    // Construct data
		    String cookie = "JSESSIONID=" + JSESSIONID;

		    // Send data
		    URL url = new URL("https://central.xnat.org/data/archive/projects?format=json");
		    URLConnection conn = url.openConnection();
		    conn.setRequestProperty("Cookie", cookie);
		    conn.setDoOutput(true);

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    ArrayList<String> fullOutput = new ArrayList<String>();
		    String regex = "\"ID\":\"(\\w*)\"";	// regex to find the project ID "ID":"(\w*)"
		    Pattern pattern = Pattern.compile(regex);
		    while ((line = rd.readLine()) != null) {
		    	Matcher matcher = pattern.matcher(line);
		    	while (matcher.find())
		    		fullOutput.add(matcher.group(1));	// group 1 is the actual project name
		    }// End while
		    rd.close();
		    
		    // Convert from ArrayList to String[]
		    String[] elements = new String[fullOutput.size()];
			for (int i = 0; i < fullOutput.size(); i++) {
				elements[i] = fullOutput.get(i);
			}// End for
			
			return elements;
		}// End try 
		
		catch (Exception e) {
			String[] temp = new String[] {"Error accessing XNAT Central Database"};
			return temp;
		}// End catch
	}// End loadXNATProjects function
	
	private static String loadXNATsubject(String projectName) {
		try {
		    // Construct data
		    String cookie = "JSESSIONID=" + JSESSIONID;

		    // Send data
		    URL url = new URL("https://central.xnat.org/data/archive/subjects?project="+projectName+"&format=json");
		    URLConnection conn = url.openConnection();
		    conn.setRequestProperty("Cookie", cookie);
		    conn.setDoOutput(true);

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String retLine = "";
		    new ArrayList<String>();
		    String regex = "\"ID\":\"(\\w*)\"";	// regex to find the subject ID "ID":"(\w*)"
		    Pattern pattern = Pattern.compile(regex);
		    while ((line = rd.readLine()) != null) {
		    	Matcher matcher = pattern.matcher(line);
		    	while (matcher.find())
		    		retLine += matcher.group(1) + '\n';	// group 1 is the actual subject name
		    }// End while
		    rd.close();
			
			return retLine;
		}// End try 
		
		catch (Exception e) {
			String temp = "Error accessing XNAT Central Database";
			return temp;
		}// End catch
	}// End loadXNATsubject
	
}// End Xnat class
