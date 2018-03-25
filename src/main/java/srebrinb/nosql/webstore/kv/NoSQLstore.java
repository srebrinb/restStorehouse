package srebrinb.nosql.webstore.kv;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.KeyValueVersion;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import org.apache.commons.codec.binary.Base32;

/**
 *
 * @author sbalabanov
 */
public class NoSQLstore {

    private static KVStore store=null;

    public NoSQLstore() {
        //java -Xmx256m -Xms256m -jar lib\kvstore.jar kvlite -root KVROOT -secure-config disable       
        String storeName = "kvstore";
        String hostName = "127.0.0.1";
        String hostPort = "5000";
        store = KVStoreFactory.getStore(new KVStoreConfig(storeName, hostName + ":" + hostPort));

    }

    public void close() {
        store.close();
    }

    /**
     *
     * @return
     */
    public static KVStore getStore() {
        if (store==null){
            new NoSQLstore();
        }
        return store;
    }

    public void put(String keyString, byte[] valueCont) {
        put(keyString, valueCont, 0);
    }

    public void put(String keyString, byte[] valueCont, int lifeTimeMin) {
        ArrayList<String> majorComponentsO = new ArrayList<String>();
        ArrayList<String> minorComponentsO = new ArrayList<String>();
        majorComponentsO.add("blob");
        minorComponentsO.add(keyString);
        Key key = Key.createKey(majorComponentsO, minorComponentsO);
        store.put(key,
                Value.createValue(valueCont));
        if (lifeTimeMin > 0) {
            long curTimeMin = System.currentTimeMillis() / 1000 / 60;
            long expTime = curTimeMin + lifeTimeMin;
            ArrayList<String> majorComponents = new ArrayList<String>();
            ArrayList<String> minorComponents = new ArrayList<String>();
            majorComponents.add("session");
            majorComponents.add("expireTime");
            minorComponents.add(Long.toString(expTime));
            minorComponents.add(keyString);
            Key key2 = Key.createKey(majorComponents, minorComponents);
            store.put(key2, Value.createValue(keyString.getBytes()));
        }
    }

    String bytArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public String getMeta(String keyString, String keyMetadata) {
        ArrayList<String> majorComponents = new ArrayList<String>();
        ArrayList<String> minorComponents = new ArrayList<String>();
        majorComponents.add("metadata");
        minorComponents.add(keyString);
        minorComponents.add(keyMetadata);
        Key key = Key.createKey(majorComponents, minorComponents);

        final ValueVersion valueVersion = store.get(key);
        return new String(valueVersion.getValue().getValue());
    }

    public void putMeta(String keyString, String keyMetadata, String valMetadata) {
        ArrayList<String> majorComponents = new ArrayList<String>();
        ArrayList<String> minorComponents = new ArrayList<String>();
        majorComponents.add("metadata");
        minorComponents.add(keyString);
        minorComponents.add(keyMetadata);
        Key key = Key.createKey(majorComponents, minorComponents);
        System.out.println(key);
        store.put(key,
                Value.createValue(valMetadata.getBytes()));
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
                ex1.printStackTrace();
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
        majorComponents.add("blob");
        minorComponents.add(keyString);
        Key key = Key.createKey(majorComponents, minorComponents);
        store.put(key,Value.createValue(valueCont));
        return keyString;
    }
    public byte[] get(String keyString) {
        return get("blob", keyString);
    }
    public byte[] get(String type,String keyString) {
        try {
            ArrayList<String> majorComponents = new ArrayList<String>();
            ArrayList<String> minorComponents = new ArrayList<String>();
            majorComponents.add(type);
            minorComponents.add(keyString);
            Key key = Key.createKey(majorComponents, minorComponents);
            final ValueVersion valueVersion = store.get(key);
            return valueVersion.getValue().getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public List list(String type) {
        List files = new ArrayList();
        ArrayList<String> majorComponents = new ArrayList<String>();
        ArrayList<String> minorComponents = new ArrayList<String>();
        majorComponents.add(type);
        Key kay = Key.createKey(majorComponents);
        Iterator<KeyValueVersion> i
                = store.multiGetIterator(Direction.FORWARD, 0,
                        kay, null, null);
        int icount = 0;
        while (i.hasNext()) {
            KeyValueVersion kv = i.next();
            //   Value v = i.next().getValue();
            Key k = kv.getKey();
            File f = new File(k.toString());
            String fileName = f.getName();
            files.add(fileName);
            icount++;
        }
        return files;
    }

    public void delete(String keyString) {
        try {
//            System.out.println("delete:"+keyString);
            store.delete(Key.createKey(keyString));
//            ArrayList<String> majorComponents = new ArrayList<String>();
//            ArrayList<String> minorComponents = new ArrayList<String>();
//            majorComponents.add("session");
//            minorComponents.add(keyString);
//            Key key = Key.createKey(majorComponents, minorComponents);
//            store.delete(key);
        } catch (Exception e) {
            return;
        }
    }

    public void clean() {
        long curTimeMin = System.currentTimeMillis() / 1000 / 60;
        ArrayList<String> majorComponents = new ArrayList<String>();
        majorComponents.add("session");
        Key kay = Key.createKey(majorComponents);
        Iterator<KeyValueVersion> i
                = store.multiGetIterator(Direction.FORWARD, 0,
                        kay, null, null);
        while (i.hasNext()) {
            KeyValueVersion kv = i.next();
            //   Value v = i.next().getValue();
            Key k = kv.getKey();
            File f = new File(k.toString());
            String sessID = f.getName();
            // System.out.println("CLEAN "+sessID);
            Value v = kv.getValue();
            try {
                long expTime = new Long(new String(v.getValue()));
                if (expTime != 0 && expTime < curTimeMin) {
                    delete(sessID);
                    delete(k.toString());
                }
            } catch (Exception e) {
                continue;
            }
            // Do some work with the Value here
        }
    }

}
