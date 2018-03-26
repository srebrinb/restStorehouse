/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srebrinb.nosql.webstore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import srebrinb.nosql.webstore.kv.NoSQLstore;
import java.util.List;
import oracle.kv.KVStore;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author srebr
 */
public class NoSQLstoreTest {

    public NoSQLstoreTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {

    }

    /**
     * Test of getKVStore method, of class NoSQLstore.
     */
    @Test
    public void testGetKVStore() {
        System.out.println("getKVStore");
        NoSQLstore instance = new NoSQLstore();
        KVStore result = instance.getStore();
        result.close();
    }

    /**
     * Test of put method, of class NoSQLstore.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        byte[] valueCont = new String("{\"ok\":true}").getBytes();
        NoSQLstore instance = new NoSQLstore();

        instance.put("1", valueCont);
    }

    @Test
    public void testGetId() {
        testPut();
        System.out.println("get");
        byte[] valueCont = new String("{\"ok\":true}").getBytes();
        NoSQLstore instance = new NoSQLstore();
        byte[] res = instance.get("1");
        System.out.println("res = " + new String(res));
    }

    @Test
    public void testPut_byteArr() {
        System.out.println("put");
        byte[] valueCont = new String("Test").getBytes();
        NoSQLstore instance = new NoSQLstore();
        String expResult = "Y3XJ4M6PLRTRLIORJD6XH5ZRRCCLIGW4XELAEHRLYDUABJOF3WL7KFBBPD3K5CGI7XMY4GX3BTSMRUWFJNPTPMYLPWQZS65THMFYUMI";
        String result = instance.put(valueCont);
        assertEquals(expResult, result);
    }

    @Test
    public void testPut_get() {
        testPut_byteArr();
        System.out.println("get");
        String key = "Y3XJ4M6PLRTRLIORJD6XH5ZRRCCLIGW4XELAEHRLYDUABJOF3WL7KFBBPD3K5CGI7XMY4GX3BTSMRUWFJNPTPMYLPWQZS65THMFYUMI";
        NoSQLstore instance = new NoSQLstore();
        String expResult = "Test";
        String result = new String(instance.get(key));
        assertEquals(expResult, result);
    }
//
    @Test
    public void testPut_getImg() throws FileNotFoundException, IOException {
        System.out.println("get Img");
        String key = "GXFWRRUYA7OL6JR2QZ44L5XTUMRPTICXA57DG72V67S6JHZQQ6RYT65DTT6VGNW6RGVMNBHQLJ2GUJGH6U7YIH4NBBNNN3E4JHJ67AY";
        NoSQLstore instance = new NoSQLstore();
        String expResult = "Test";
        String result = new String();
        byte[] data = instance.get(key);
        OutputStream output=new FileOutputStream("1.jpg");
        IOUtils.writeChunked(data, output);
    }
}
