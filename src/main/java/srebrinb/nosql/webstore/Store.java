/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srebrinb.nosql.webstore;

/**
 *
 * @author srebr
 */
public interface Store {

    /**
     *
     * @return
     */
    Store getStore();
    void close();
}
