/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srebrinb.nosql.webstore;

import srebrinb.nosql.webstore.kv.blob.NoSQLstore;
import java.util.List;
import oracle.kv.KVStore;
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
    public void testPut_byteArr() {
        System.out.println("put");
        byte[] valueCont = new String("Test").getBytes();
        NoSQLstore instance = new NoSQLstore();
        String expResult = "KMXKVPMVOSEA3P3WXG4MYAEDFQQKN3ARHVUCFGKVBV5G4DZULYSQ";
        String result = instance.put(valueCont);
        assertEquals(expResult, result);
    }

    @Test
    public void testPut_get() {
        testPut_byteArr();
        System.out.println("get");
        String key = "KMXKVPMVOSEA3P3WXG4MYAEDFQQKN3ARHVUCFGKVBV5G4DZULYSQ";
        NoSQLstore instance = new NoSQLstore();
        String expResult = "Test";
        String result = new String(instance.get(key));
        assertEquals(expResult, result);
    }

}
