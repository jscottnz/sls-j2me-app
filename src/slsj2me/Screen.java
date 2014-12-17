package slsj2me;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 *
 * @author jeremy
 */
public class Screen {

    public String name;
    public String title;
    public MenuItem[] options;
    public String text;
    public Displayable displayable;

    public Screen(String name, String title, MenuItem[] options) {
        this(name, title, options, null);
    }

    public Screen(String name, String title, MenuItem[] options, String text) {
        this.name = name;
        this.title = title;
        this.options = options;
        this.text = text;
    }

    public List getMenu() {
        return (List) displayable;
    }
}
