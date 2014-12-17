package slsj2me;

import java.io.InputStream;

/**
 *
 * @author jeremy
 */
public interface DataStore {

    public void load();
    public Screen get(String key);

}
