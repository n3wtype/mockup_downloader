package com;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.GroupLayout.Group;

public class Task extends JPanel {
	    
        enum EState {DOWNLOADING, PENDING, FINISHED, CANCELED, DELETED, ERROR};
    
	private String destination, link, filename;
	private JLabel label, description, speed;
	private JProgressBar progbar;
	private JButton cancel, pauseresume;
        public GroupLayout lay;
        private getFile get;        
        protected EState state;
        private File file;
        private boolean test;
        
        public Task (int width,int height, String destination, String url)
        {
            this (width, height, destination, url, false);
        }
    
	public Task(int width,int height, String destination, String url, boolean test) {
		
            this.destination = destination;
            this.link = url;
            this.state = EState.PENDING;
            speed = new JLabel("speed: ");
            this.test = test;

            //this.setTitle("Downloader");
            label = new JLabel("Processing");
            description = new JLabel("...");

            progbar = new JProgressBar(0,100);
            progbar.setValue(0);
            progbar.setStringPainted(true);

            cancel = new JButton("Cancel");
            cancel.setEnabled(true);

            pauseresume = new JButton("Pause/Resume");                
            pauseresume.setEnabled(true);

            get = new getFile();

            cancel.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {                        
                    get.cancel_task();
                }
            });

            pauseresume.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                        if (get.paused) {
                            get.resume_task();
                            pauseresume.setText("Pause");
                        } else {
                            get.pause_task();
                            pauseresume.setText("Resume");
                        }
                }
            });

            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);
            layout.setAutoCreateContainerGaps(true);
            lay = layout;

            layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(description)
                    )
                    .addGroup(layout.createParallelGroup()
                        .addComponent(progbar, 400,400,400)
                        .addComponent(label)
                        .addComponent(speed)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addGap(300)
                        .addComponent(cancel)
                        .addComponent(pauseresume)
                    )
            );
            layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(description)
                )                        
                .addGroup(layout.createSequentialGroup()
                                .addComponent(progbar)
                                .addComponent(label)
                                .addComponent(speed)
                )
                .addGroup(layout.createParallelGroup()
                                .addComponent(cancel)
                                .addComponent(pauseresume)
                )
            );

            //pack();
            //this.setLocation((width-this.getWidth())/2, (height-this.getHeight())/2);
            //this.setResizable(false);		
            get.start();
		
	}
        
        public void stop()
        {
            try {
                get.join();
            } catch (InterruptedException ex) {}
        }
        
        static String humanize_size (long size)
        {
            String ret = Long.toString(size);
            
            if (size >= 1024*1024*1024)
                return Long.toString((size)/(1024*1024*1024)) + "GiB";
            else if (size >= 1024*1024)
                return Long.toString((size)/(1024*1024)) + "MiB";
            else if (size >= 1024)
                return Long.toString((size)/(1024)) + "KiB";
            
            
            return ret + "B";
        }
	
	// InnerClass
	public class getFile extends Thread {
	
            protected boolean paused;
            private boolean cancel;
            
            public void pause_task ()
            {
                this.paused = true;
            }
            
            public void resume_task ()
            {
                this.paused = false;
            }
            
            public void cancel_task()
            {
                if (Task.this.state != EState.CANCELED)
                    Task.this.state = EState.CANCELED;
                else
                    return;

                // Make sure the file or directory exists and isn't write protected
                if (!Task.this.file.exists())
                    return;

                if (!Task.this.file.exists())
                    return;

                // If it is a directory, make sure it is empty
                if (!Task.this.file.exists()) {
                String[] files = Task.this.file.list();
                if (files.length > 0)
                    return;
                }

                // Attempt to delete it
                boolean success = Task.this.file.delete();

                if (!success)
                    return;                
            }
            
            public void delete_finished()
            {
                if (Task.this.state == EState.FINISHED) {
                    Task.this.state = EState.DELETED;
                }
                Task.this.invalidate();
            }
            
            public void invalidate_task()
            {
                Task.this.state = EState.ERROR;
                Task.this.label.setText("Error!");
                Task.this.pauseresume.setVisible(false);
                Task.this.cancel.setText("Delete");
                Task.this.speed.setVisible(false);
            }

            public void run() {
                
                this.paused = false;
                this.cancel = false;
                Task.this.pauseresume.setText("Pause");
                
                
                try {
                    URL url = new URL(Task.this.link);
                    int j=0;
                    for(int i=0;i<Task.this.link.length();i++)
                            if(Task.this.link.charAt(i) == '/')
                                    j=i;
                    // http://upload.wikimedia.org/wikipedia/commons/f/f5/Wtc-photo.jpg
                    Task.this.filename = Task.this.link.substring(j, Task.this.link.length());
                    BufferedInputStream in = new BufferedInputStream(url.openStream());
                    Task.this.file = new File(Task.this.destination+ System.getProperty("file.separator") + Task.this.filename);
                    FileOutputStream fos = new FileOutputStream(Task.this.file);
                    BufferedOutputStream bout = new BufferedOutputStream(fos,1024);

                    String filenametmp;
                    if (Task.this.filename.length() > 50)
                        filenametmp = Task.this.filename.substring(0, 49);
                    else
                        filenametmp = Task.this.filename;
                        
                    
                    Task.this.description.setText(filenametmp);

                    long size = url.openConnection().getContentLengthLong();
                    long suma=0;
                    int value;
                    //Task.this.label.setText("Download...");
                    //Task.this.pauseresume.

                    long currTime = System.currentTimeMillis();
                    long prevTime = System.currentTimeMillis();
                    long currData = 0, prevData = 0;
                    
                    byte[] data = new byte[1024];
                    int x=0;
                    while(true) {
                        if (this.cancel)
                        {
                            break;
                        }
                        if (!this.paused)
                        {
                            
                            if (currTime - prevTime >= 1000)
                            {
                                long tmp;
                                tmp = currData;
                                currData = suma;
                                prevData = tmp;
                                
                                Task.this.speed.setText("speed: " + Task.humanize_size(
                                        (currData - prevData)/((currTime-prevTime)/1000))
                                + "/s");
                                
                                prevTime = currTime;
                                
                            } else {
                                currTime = System.currentTimeMillis();
                            }
                            
                            x=in.read(data,0,1024);
                            if (x>=0)                                    
                            {
                                bout.write(data,0,x);
                                suma+=x;
                                value = (int) ((100*suma)/size);
                                Task.this.progbar.setValue(value);
                                Task.this.label.setText("Download... " 
                                        + Task.humanize_size(suma) + "/"
                                        + Task.humanize_size(size));
                            } else break;
                        } else {
                            Task.this.label.setText("Paused");
                            Task.this.speed.setText("speed: ");
                        }
                    }

                    value = 100;
                    bout.close();
                      in.close();
                    Task.this.label.setText("Done!");
                    Task.this.cancel.setText("Delete Task");
                    Task.this.pauseresume.setVisible(false);

                    Task.this.cancel.setEnabled(true);
                    Task.this.state = EState.FINISHED;

                } catch (MalformedURLException e) {
                    if (!test)
                        JOptionPane.showMessageDialog(null, "Given URL is incorrect","Error",  JOptionPane.ERROR_MESSAGE);
                    //this.cancel_task();
                    this.invalidate_task();
                } catch (IOException e) {
                    if (!test)
                        JOptionPane.showMessageDialog(null, "Failed to open stream","Error",  JOptionPane.ERROR_MESSAGE);
                    //this.cancel_task();
                    this.invalidate_task();
                    //System.exit(0);
                }
            }		
	}

}
