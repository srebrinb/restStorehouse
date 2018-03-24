/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srebrinb.nosql.webstore.kv;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import org.apache.commons.codec.binary.Base32;

/**
 *
 * @author srebr
 */
public class Blob {
    private static KVStore store=null;
    String majorKey="blob";
    Blob (KVStore store){
        this.store=store;
    }
    public String put(byte[] valueCont) {
        byte[] dig = null;
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {            
            try {            
                sha = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException ex1) {
                Logger.getLogger(Blob.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        long time = System.currentTimeMillis();
        dig = Long.toString(time).getBytes();
        if (sha != null) {
            sha.update(valueCont);
            dig = sha.digest();

        }
        Base32 base32 = new Base32();
        String keyString = base32.encodeToString(dig).replace('=', ' ').trim();

        ArrayList<String> majorComponents = new ArrayList<String>();
        ArrayList<String> minorComponents = new ArrayList<String>();
        majorComponents.add(majorKey);
        minorComponents.add(keyString);
        Key key = Key.createKey(majorComponents, minorComponents);
        getStore().put(key,Value.createValue(valueCont));
        return keyString;
    }
    public byte[] get(String keyString) {
        try {
            ArrayList<String> majorComponents = new ArrayList<String>();
            ArrayList<String> minorComponents = new ArrayList<String>();
            majorComponents.add(majorKey);
            minorComponents.add(keyString);
            Key key = Key.createKey(majorComponents, minorComponents);
            final ValueVersion valueVersion = store.get(key);
            return valueVersion.getValue().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return the store
     */
    public static KVStore getStore() {
        return store;
    }

    /**
     * @param aStore the store to set
     */
    public static void setStore(KVStore aStore) {
        store = aStore;
    }
}
