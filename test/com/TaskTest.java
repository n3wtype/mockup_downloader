/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.File;

/**
 *
 * @author wooden
 */
public class TaskTest {
    
    public TaskTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of humanize_size method, of class Task.
     */
    @Test
    public void testHumanize_size() {
        System.out.println("Testing static method: humanize_size");
        long size = 1024L;
        String expResult = "1KiB";
        String result = Task.humanize_size(size);
        assertEquals(expResult, result);
        
        size = 1024L * 1024L;
        expResult = "1MiB";
        result = Task.humanize_size(size);
        assertEquals(expResult, result);
        
        size = 1024L * 1024L * 1024L;
        expResult = "1GiB";
        result = Task.humanize_size(size);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    @Test
    public void testInvalidURL() {
        System.out.println("Testing handling of invalid URL");
        Task t = new Task(100,100, ".", "incorrectURL", true);
        t.stop();
        assertEquals(Task.EState.ERROR, t.state);
    }    
    
    @Test
    public void testValidURL() {
        System.out.println("Testing downloading of well known file (http://www.google.pl/images/srpr/logo3w.png)");
        
        
        File f = new File("./logo3w.png");
        if (!f.exists())
            f.delete();
        
        Task t = new Task(100,100, ".", "http://www.google.pl/images/srpr/logo3w.png", true);
        t.stop();
        assertEquals(Task.EState.FINISHED, t.state);
        
        f = new File("./logo3w.png");

        // Make sure the file or directory exists and isn't write protected
        if (!f.exists())
            fail("Delete: no such file or directory: " + "./logo3w.png");

        if (!f.canWrite())
            fail("Delete: write protected: " + "./logo3w.png");

        // If it is a directory, make sure it is empty
        if (f.isDirectory()) {
        String[] files = f.list();
        if (files.length > 0)
            fail ("Is a directory " + "./logo3w.png");
        }

        // Attempt to delete it
        boolean success = f.delete();
        
        if (!success)
            fail ("Could not delete test file");
        
    } 
}
