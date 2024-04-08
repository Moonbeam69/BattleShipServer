package battleship;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.context.*;

import javax.annotation.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SpringBootApplication
public class BattleShipServer {

	@Value("${server.address}")
	private String host;

	@Value("${server.port}")
	private String port;

	public static void main(String[] args) {

		// Old server start
		//SpringApplication.run(BattleShipServer.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(BattleShipServer.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
	}

	@PostConstruct
	public void login() {

		JFrame frame = new JFrame(host + ":" + port);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		GridBagConstraints c = new GridBagConstraints();
		frame.setLayout(new GridBagLayout());

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 3;
		c.gridheight = 3;

		JButton ok_button = new JButton("Shutdown!");
		ok_button.setSize(new Dimension (50, 20));
		ok_button.setBackground(Color.RED);
		ok_button.setForeground(Color.WHITE);

		ok_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(0);
			}
		});

		frame.add(ok_button, c);
		frame.setPreferredSize(new Dimension (400, 200));
		frame.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		frame.setLocation(new Point(0, 0));
		frame.setVisible(true);
	}
}
