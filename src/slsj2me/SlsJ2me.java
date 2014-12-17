package slsj2me;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Stack;

public class SlsJ2me extends MIDlet implements CommandListener {

    Command exitCommand, SELECT_COMMAND, BACK_COMMAND;

    Display display;
    DataStore ds;
    Screen currentScreen;
    Stack previousScreens;
    boolean isBackCommand;

    public SlsJ2me() {
        display = Display.getDisplay(this);
        exitCommand = new Command("Exit", Command.EXIT, 0);
        SELECT_COMMAND = new Command("Select", Command.ITEM, 1);
        BACK_COMMAND = new Command("Back", Command.BACK, 2);

        previousScreens = new Stack();
        isBackCommand = false;
    }

    public void startApp() {

        loadData();
        setScreen(ds.get("root"));
        
    }
    
    public void loadData() {
        ds = new SimpleDataProtocol();
        ds.load();
    }

    public void setScreen(final Screen screen) {

        if(!isBackCommand && currentScreen != null) {
            previousScreens.push(currentScreen);
        }
        currentScreen = screen;

        System.out.println("setScreen called for " + screen.title + " " + previousScreens.size());

        
        if(screen.options != null && screen.options.length > 0) {
            System.out.println("Menu items " + screen.options.length);
            final List menu = new List(screen.text, List.IMPLICIT);

            for( int i = 0; i < screen.options.length; i++) {
                menu.append(screen.options[i].title, null);
            }
            System.out.println("Menu items " + menu.size());
            menu.setSelectCommand(SELECT_COMMAND);
            screen.displayable = menu;
        }

        if(screen.text != null && screen.text.length() > 0) {
            Form form = new Form(screen.title);
            form.append(screen.text);

            screen.displayable = form;
        }

        if(screen.displayable != null) {
            System.out.println("setCurrent called for " + screen.displayable);
            screen.displayable.addCommand(exitCommand);

            if(previousScreens.size() > 0) {
                screen.displayable.addCommand(BACK_COMMAND);
            }

            screen.displayable.setCommandListener(this);

            display.setCurrent(screen.displayable);
        }

    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {

        if (c == BACK_COMMAND) {

            if(!previousScreens.isEmpty()) {
                isBackCommand = true;
                setScreen((Screen)previousScreens.pop());
            }
            
            return;
        }

        isBackCommand = false;

        if(c == SELECT_COMMAND) {

            System.out.println("command received on screen " + currentScreen.title);
            if(currentScreen.options != null
                    && currentScreen.getMenu() != null
                    && currentScreen.options.length >  currentScreen.getMenu().getSelectedIndex()) {

                MenuItem selectedItem = currentScreen.options[currentScreen.getMenu().getSelectedIndex()];


                if(selectedItem.targetScreen != null) {
                    Screen target = ds.get(selectedItem.targetScreen);
                    if(target != null) {
                        setScreen(ds.get(selectedItem.targetScreen));
                    }
                }

            }
        } else if( c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        }


    }

    
}
