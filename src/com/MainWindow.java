package com;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.GroupLayout.Group;

public class MainWindow extends JFrame {

    	private JFileChooser fileChooser;
	private JButton download;
	private JLabel label;
	private JTextField URL;
        private Group hGroup, vGroup;
        private LinkedList<Task> tasks;
        private GroupLayout lay;
        private TaskManager tm;
        private String location;
        
	public MainWindow(final int width,final int height) {	
            this.setTitle("Downloader");
            
            location = "/";

            tasks = new LinkedList<Task>();
            label = new JLabel("URL:");
            URL = new JTextField(40);
            download = new JButton("Download");
            tm = new TaskManager();
            tm.start();

            GroupLayout layout = new GroupLayout(this.getContentPane());
            lay = layout;
            getContentPane().setLayout(layout);
            layout.setAutoCreateContainerGaps(true);
            
            hGroup = layout.createParallelGroup();
            vGroup = layout.createSequentialGroup();            
     
            layout.setVerticalGroup( 
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)  
                        .addComponent(label)
                        .addComponent(URL)
                        .addComponent(download)                        
                    )
                    .addGroup(vGroup)
            );

            layout.setHorizontalGroup( 
                layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50)
                        .addComponent(label)
                        .addGap(50)
                        .addComponent(URL)
                        .addGap(50)
                        .addComponent(download) 
                        .addGap(50)
                    )
                    .addGroup(hGroup)
            );

            JLabel label1 = new JLabel("Tasks");

            // Akcja dla buttona
            download.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                        // TODO Auto-generated method stub
                    if(URL.getText().length()>5) {

                        fileChooser = new JFileChooser();
                        fileChooser.setCurrentDirectory(new java.io.File(location));
                        fileChooser.setDialogTitle("");
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                        File directoryToInstall;
                        String destination = new String();
                        int returnVal = fileChooser.showOpenDialog(MainWindow.this);

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            directoryToInstall = fileChooser.getSelectedFile();
                            destination = directoryToInstall.getPath();
                            location = destination;

                            Task frame2 = new Task(width,height, destination, URL.getText());
                            URL.setText("");
                            vGroup.addComponent(frame2);
                            hGroup.addComponent(frame2);
                            frame2.setVisible(true);
                            tasks.add(frame2);
                            pack();
                            //frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        }
                    }
                }
            });
            
            pack();
            
            this.setLocation((width-this.getWidth())/2, (height-this.getHeight())/2);
            this.setResizable(false);
	}
	private void turnOff() {
		this.setVisible(false);
                tm.terminate();
                try {
                    tm.join(100 );
                } catch (InterruptedException ex) {}
	}
        
	public class TaskManager extends Thread {
	
            private boolean cancel;
            
            @Override
            public void run() {
                this.cancel = false;
                
                while (!cancel)
                {
                
                    for (Iterator<Task> it = tasks.iterator(); it.hasNext();) {
                        Task task = it.next();
                        if ((task.state == Task.EState.CANCELED) || 
                             (task.state == Task.EState.FINISHED))
                        {
                            task.removeAll();
                            task.setVisible(false);
                            task.invalidate();
                            tasks.remove(task);
                            System.out.println();
                            pack();
                        } 
                    }
                    try {
                        sleep(200);
                    } catch (InterruptedException ex) {}
                }
            }
            
            public void terminate ()
            {
                this.cancel = true;
            }
        }		
}
