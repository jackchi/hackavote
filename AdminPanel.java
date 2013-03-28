/*
 * "Hack-a-vote", a Direct-Recording Electronic (DRE) voting machine
 * software implementation.
 * 
 * Copyright 2003, Rice University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the
 * distribution.
 * 
 * - Neither the name of Rice University (RICE) nor the names of its
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided by RICE and the contributors on an "as
 * is" basis, without any representations or warranties of any kind,
 * express or implied including, but not limited to, representations
 * or warranties of non-infringement, merchantability or fitness for a
 * particular purpose. In no event shall RICE or contributors be
 * liable for any direct, indirect, incidental, special, exemplary, or
 * consequential damages (including, but not limited to, procurement
 * of substitute goods or services; loss of use, data, or profits; or
 * business interruption) however caused and on any theory of
 * liability, whether in contract, strict liability, or tort
 * (including negligence or otherwise) arising in any way out of the
 * use of this software, even if advised of the possibility of such
 * damage.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;
import javax.swing.*;

import java.awt.*;
import javax.swing.event.*;

/**
 * AdminPanel<p>
 *
 * Panel that goes on the BallotGUI that enables administrative functionality.
 * You can click to expose a password field; when the password is correct,
 * administrative buttons appear.
 *
 * @author Dave Price <dwp@alumni.rice.edu>
 */
@SuppressWarnings("serial")
public class AdminPanel extends JPanel {

	/**
	 * All the buttons controlled by this panel
	 */
	LinkedList allButtons;

	/**
	 * The text field where you enter the passwd
	 */
	JPasswordField passwdField;

	/**
	 * The password that enables this panel
	 */
	String adminPasswd;
	
	/**
	 * The list of characters considered to be weak passwords
	 */
	Vector<String> weakPswd;

	/**
	 * Construct an adminpanel.
	 * @param allButtons The LinkedList of all administrative buttons.
	 * @param adminPasswd The password you input to enable them.
	 */
	public AdminPanel(LinkedList allButtons, String adminPasswd) {
		super();
		this.allButtons = allButtons;
		this.adminPasswd = adminPasswd;

		// ask the Console for weak password condition
		try {
			Socket s= new Socket(BallotControl.authHost, BallotControl.authPort);
			PrintStream sout = new PrintStream(s.getOutputStream());
			BufferedReader sin = new BufferedReader(new InputStreamReader(s
					.getInputStream()));
			
			sin.readLine();
			sout.println("50 Get Weak Passwords");
			if (!sin.readLine().contains("700")) {
				throw new Exception("Console has been compromised!");
			}
			StringTokenizer st= new StringTokenizer(sin.readLine(), ",", false);
			weakPswd = new Vector<String>();
			while(st.hasMoreTokens()){
				weakPswd.addElement(st.nextToken());
			}
			
			if (!checkPasswordStrength(this.adminPasswd))
				this.adminPasswd = weakPswd.elementAt(0);
		} catch (Exception e) {
			// Eh, we'll live.
		}
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel passwdLabel = new JLabel("Administer machine");
		passwdLabel.setVisible(true);
		this.add(passwdLabel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		passwdField = new JPasswordField(10);
		passwdField.setVisible(true);
		bottomPanel.add(passwdField, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 1));
		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
		bottomPanel.setVisible(true);
		buttonPanel.setVisible(true);

		passwdField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				if (passwdField.isEnabled()) {
					checkPasswd();
				}
			}
		});

		ListIterator it = allButtons.listIterator();
		while (it.hasNext()) {
			JButton thisButton = (JButton) it.next();
			buttonPanel.add(thisButton);
		}

		this.add(bottomPanel);

		buttonsVisibility(false);
	}

	/**
	 * Makes buttons visible if the password is correct.
	 */
	public void checkPasswd() {
		String passwd = new String(passwdField.getPassword());
		buttonsVisibility(adminPasswd.equals(passwd));
	}

	/**
	 * Check to see if the password given is strong or weak
	 * @param passwd The String password to be checking
	 * @return True/False if the password is validated as strong
	 */
	public boolean checkPasswordStrength(String passwd){
		for (String pswd : weakPswd) {
			if (passwd.contains(pswd.toLowerCase())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Makes buttons visible.
	 *
	 * @param visibility Boolean 
	 */
	public void buttonsVisibility(boolean visibility) {
		ListIterator it = allButtons.listIterator();
		while (it.hasNext()) {
			JButton thisButton = (JButton) it.next();
			thisButton.setVisible(visibility);
		}
		this.revalidate();
	}

	/**
	 * Overrides javax.swing.JComponent.setEnabled().  Clears the
	 * password field.
	 *
	 * @param enabled Boolean indicating whether the component should
	 * be enabled or disabled.
	 */
	public void setEnabled(boolean enabled) {
		passwdField.setText("");
		passwdField.setEnabled(enabled);
		passwdField.setEditable(enabled);
		buttonsVisibility(false);
	}
}