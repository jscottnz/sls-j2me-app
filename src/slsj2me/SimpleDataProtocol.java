package slsj2me;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author jeremy
 * Data format is one line per screen.
 * Fields are | (pipe) delimited and ordered:
 * 0: screen id
 * 1: screen display name
 * 2: encoded menu items
 * 3: text
 *
 * Menu items are encoded:
 * Items are / (forward slash) delimited.
 * Fields in the item are > delimited and ordered:
 * 0: item display name
 * 1: screen id on selection
 */
public class SimpleDataProtocol implements DataStore {

    private static char SEPERATOR = '|';
    private static char NEW_LINE = '\n';
    private static char OPTS_SEPERATOR = '/';
    private static char OPT_SEPERATOR = '>';

    private InputStreamReader reader;
    Hashtable keys = new Hashtable();

    /**
     * Read the data stream and find the item keys, taking note of where
     * in the file they are located.
     */
    public void load() {
   
        try {
            reader = getReader();
            long fileIndex = -1;
            int bufferIndex = -1;
            char[] buffer = new char[20];
            int data = reader.read();
            char lookingFor = SEPERATOR; // first read the key from the front of the line
            long lineStartsAt = 0;
            while(data != -1){
                char theChar = (char) data;
                fileIndex++;
                

                if(theChar == lookingFor) {

                    if(lookingFor == SEPERATOR) {
                        String key = String.valueOf(buffer).trim();
                        System.out.println("Found " + key + " starting at " + lineStartsAt);
                        bufferIndex = -1;
                        buffer = new char[20];

                        keys.put(key, new Long(lineStartsAt));

                        lookingFor = NEW_LINE;
                    
                    } else if(lookingFor == NEW_LINE) {
                        lineStartsAt = fileIndex + 1;
                        lookingFor = SEPERATOR;
                    }

                } else {
                    if(lookingFor == SEPERATOR) {
                        bufferIndex++;
                        buffer[bufferIndex] = theChar;
                    }
                }

                data = reader.read();
            }
            
            System.out.println("root : " + keys.get("root"));
            System.out.println("doihave : " + keys.get("doihave"));
            System.out.println("ebola : " + keys.get("ebola"));


        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public Screen get(String key) {
        
        Long lineStarts = (Long)keys.get(key);
        System.out.println("Getting key *" + key +"* from " + lineStarts );

        if(lineStarts == null) return null;

        try {
            StringBuffer line = new StringBuffer();
            reader = getReader();
            
            reader.skip(lineStarts.longValue());
            int data = reader.read();
            while(data != -1){
                char theChar = (char) data;
                if(theChar == NEW_LINE) {
                    return makeScreen(line.toString());
                } else {
                    line.append(theChar);
                }
                data = reader.read();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    private Screen makeScreen(String line) {

        int index = 0;

        index = line.indexOf(SEPERATOR);
        String name = line.substring(0, index);
        line = line.substring(index+1);

        index = line.indexOf(SEPERATOR);
        String title = line.substring(0, index);
        line = line.substring(index+1);
        
        index = line.indexOf(SEPERATOR);
        String optionsStr = line.substring(0, index);
        line = line.substring(index+1);
        
        MenuItem[] options = null;
        if(optionsStr.length() > 0) {
            optionsStr = optionsStr + "/";
            Vector menuOptions = new Vector();

            int optsIndex = optionsStr.indexOf(OPTS_SEPERATOR);
            while(optsIndex != -1) {
                String optionStr = optionsStr.substring(0, optsIndex);

                int delimAt = optionStr.indexOf(OPT_SEPERATOR);
                String oTitle = optionStr.substring(0, delimAt);
                String oTarget = optionStr.substring(delimAt+1);

                menuOptions.addElement(new MenuItem(oTitle, oTarget));

                optionsStr = optionsStr.substring(optsIndex+1);
                optsIndex = optionsStr.indexOf(OPTS_SEPERATOR);
            }

            if(menuOptions.size() > 0) {
                options = new MenuItem[menuOptions.size()];
                menuOptions.copyInto(options);
            }

        }

        String text = replace(line, "\\n", "\n");
        
        return new Screen(name, title, options, text);
    }

    private InputStreamReader getReader() {
        try {
            return new InputStreamReader(getClass().getResourceAsStream("data.txt"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //http://stackoverflow.com/questions/10626606/replace-string-with-string-in-j2me
    private String replace( String str, String pattern, String replace )
    {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ( (e = str.indexOf( pattern, s ) ) >= 0 )
        {
            result.append(str.substring( s, e ) );
            result.append( replace );
            s = e+pattern.length();
        }
        result.append( str.substring( s ) );
        return result.toString();
    }
}
