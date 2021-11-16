import java.util.*;

public class ABCCompiler //converts ABC notation into CHUMPanese
{
    private Map<String, Integer> prefixMap;
    private Map<String, Integer> infixMap;
    private Map<String, String> delimiterMap;
    private Map<String, Integer> noteMap;
    private Tokenizer tokenizer;
    private String token;

    public ABCCompiler()
    {
        prefixMap = new TreeMap<String, Integer>();
        infixMap = new TreeMap<String, Integer>();
        delimiterMap = new TreeMap<String, String>();
        noteMap = new TreeMap<String, Integer>();
    }

    public ArrayList<ArrayList<Integer>>[] compile(String abc)
    {
        loadNoteMap();
        abc = condense(abc);
        abc = abc.replace(".", "");
        abc = abc.replace("$", "");
        abc = abc.replace("%", "");
        abc = abc.replaceAll("\\d", "");
        abc = abc.replaceAll("\\s", "");
        abc = abc.replace("/", "");
        abc = abc.replace("<", "");
        abc = abc.replace(">", "");
        String[] voices = abc.split("V:");
   
        ArrayList<ArrayList<Integer>>[] ret = new ArrayList[2];
        ret[0] = new ArrayList<ArrayList<Integer>>();
        ret[1] = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 2; i++)
        {
            String currentVoice = voices[i];
            int index = 0;
            String measure = getNextMeasure(currentVoice, 0);
            //System.out.println(measure);
            while(!measure.equals(""))
            {
                ArrayList<Integer> intervals = intervalize(measure);
                //System.out.println(measure);
                //System.out.println(intervals.toString());
                ret[i].add(intervals);
                index = currentVoice.indexOf(measure) + measure.length()+1;
                measure = index+1 < currentVoice.length() ? getNextMeasure(currentVoice,index) : "";
            }   
        }
        return ret;
    }

    private ArrayList<Integer> intervalize(String measure)
    {
        //System.out.println(measure);
        ArrayList<Integer> ret = new ArrayList<Integer>();
        String n = " ";
        for (int i = 0; i < measure.length()-1; i += n.length())
        {
            int numRepeats = 0;
            n = getNextNote(measure, i);
            
            
            /*if (n.equals(":"))
            {
                numRepeats = 1;
                int i2 = i+1;
                while (getNextNote(measure, i2).equals(":"))
                {
                    i2++;
                    numRepeats++;
                }
            }*/
                
            //System.out.println(i + " " + measure + " " + n.length());
            String n2;
            if (i+n.length() < measure.length())
                n2 = getNextNote(measure, i+n.length());
            else
                n2 = " ";
            Integer note1 = noteMap.get(n);
            Integer note2 = noteMap.get(n2);
            //System.out.print(n + " " + n2 + "   ");
            if (note1 != null && note2 != null)
            {
                ret.add(note2 - note1);
                
            }
            else if (n.equals(":"))
            {
                int repeatCount = 0;
                int newi = i;
                while(newi < measure.length() && getNextNote(measure, newi).equals(":"))
                {
                    repeatCount++;
                    newi++;
                }
                i = newi-1;
                ret.add(1000 + repeatCount);
            }
            /*if (numRepeats > 0)
            {
                ret.add(
            }*/
        }
        
        
        return ret;
        
    }

    private void loadNoteMap()
    {
        addNoteTypes("_C", 11);
        addNoteTypes("C", 0);
        addNoteTypes("^C", 1);
        addNoteTypes("_D", 1);
        addNoteTypes("D", 2);
        addNoteTypes("^D", 3);
        addNoteTypes("_E", 3);
        addNoteTypes("E", 4);
        addNoteTypes("^E", 5);
        addNoteTypes("_F", 4);
        addNoteTypes("F", 5);
        addNoteTypes("^F", 6);
        addNoteTypes("_G", 6);
        addNoteTypes("G", 7);
        addNoteTypes("^G", 8);
        addNoteTypes("_A", 8);
        addNoteTypes("A", 9);
        addNoteTypes("^A", 10);
        addNoteTypes("_B", 10);
        addNoteTypes("B", 11);
        addNoteTypes("^B", 0);
    }

    private void addNoteTypes(String note, Integer value)
    {
        noteMap.put(note, value);
        noteMap.put(note.toLowerCase(), value+12);
        noteMap.put(note + ",", value-12);
        noteMap.put(note.toLowerCase() + ",", value);
        noteMap.put(note + "'", value+12);
        noteMap.put(note.toLowerCase() + "'", value+24);
        noteMap.put(note + ",,", value-24);
        noteMap.put(note.toLowerCase() + "''", value+36);
    }

    private String condense(String abc)
    {
        int i = 0;
        String s = "";
        while (!s.equals("V:1"))
        {
            i++;
            s = abc.substring(i, i+3);
        }
        i++;
        s = abc.substring(i, i+3);
        while (!s.equals("V:1"))
        { 
            i++;
            s = abc.substring(i, i+3);
        }
        return abc.substring(i+4, abc.length());
    }
    
    
    
    public String getNextMeasure(String line, int startIndex)
    {
        int i;
        String s = "";
        int i2 = startIndex;
        int i3;
        int preRepeats = 0;
        int postRepeats = 0;
        // while (line.substring(i2, i2+1).equals(":"))
        // {
            // //System.out.println("testing");
            // i2++;
            // preRepeats++;
        // }
        
        i = i2;
        s = line.substring(i, i+1);
        while (!s.equals("|") /*&& !s.equals(":")*/)
        {
            //System.out.println(s);
            //System.out.println("testing2");
            i++;
            s = line.substring(i, i+1);
        }
        
        // i3 = i;
        // while (line.substring(i3, i3+1).equals(":"))
        // {
            // //System.out.println("testing3");
            // i3++;
            // postRepeats++;
        // }
        
        
        String ret = line.substring(i2, i);
        if (preRepeats != 0)
            ret = "r" + preRepeats + ret;
        if (postRepeats != 0)
            ret = ret + "r" + postRepeats;
        //line = line.substring(i+1, line.length());
        return ret;
    }

    private String getNextNote(String measure, int startIndex)
    {
        int i = startIndex;
        boolean acc = false;
        boolean doubleAcc = false;
        String s = measure.substring(i, i+1);
        int currentScanLength = 1;
        int currentLookIndex = 1;
        if (s.equals("^") || s.equals("_"))
        {
            s = measure.substring(i, i+2);
            currentScanLength++;
            currentLookIndex++;
            if (s.equals("^^") || s.equals("__"))
            {
                doubleAcc = true;
                currentScanLength++;
                currentLookIndex++;
            }
            else
                acc = true;
        }
        if (i+currentLookIndex < measure.length())
        {
            if (doubleAcc)
                s = measure.substring(i+3, i+4);
            else if (acc)
                s = measure.substring(i+2, i+3);
            else
                s = measure.substring(i+1, i+2);
        }
        if (s.equals(",") || s.equals("'"))
        {
            //System.out.println("Here!");
            if (i+currentLookIndex+1 < measure.length())
            {
                s = measure.substring(i+currentLookIndex+1, i+currentLookIndex+2);
                if (s.equals(",") || s.equals("'"))
                {
                    if (doubleAcc)
                        return measure.substring(i, i+5);
                    else if (acc)
                        return measure.substring(i, i+4);
                    else
                        return measure.substring(i, i+3);
                }
            }
            if (doubleAcc)
                return measure.substring(i, i+4);
            else if (acc)
                return measure.substring(i, i+3);
            else
                return measure.substring(i, i+2);
            
        }
        if (doubleAcc)
           return measure.substring(i, i+3);
        else if (acc)
           return measure.substring(i, i+2);
        else
           return measure.substring(i, i+1);
        
    }

    public void addPrefix(String key, int numArgs)
    {
        prefixMap.put(key, numArgs);
    }

    public void addInfix(String key, int numArgs)
    {
        infixMap.put(key, numArgs);
    }

    public void addDelimiters(String open, String close)
    {
        delimiterMap.put(open, close);
    }

    public Object parse(String text)
    {
        tokenizer = new Tokenizer(text);
        token = tokenizer.next();
        return parse();
    }

    private void eat(String s)
    {
        if (token.equals(s))
            token = tokenizer.next();
        else
            throw new RuntimeException("expected " + s + " but found " + token);
    }

    private Object parse()
    {
        Object parsed = atom();
        if (infixMap.containsKey(token))
        {
            int numArgs = infixMap.get(token) - 1;
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(parsed);
            list.add(token);
            eat(token);
            while (numArgs > 0)
            {
                list.add(parse());
                numArgs--;
            }
            parsed = list;
        }
        return parsed;
    }

    private Object atom()
    {
        if (delimiterMap.containsKey(token))
            return parseList(token, delimiterMap.get(token));

        if (prefixMap.containsKey(token))
        {
            int numArgs = prefixMap.get(token);
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(token);
            eat(token);
            while (numArgs > 0)
            {
                list.add(parse());
                numArgs--;
            }
            return list;
        }
        String literal = token;
        eat(literal);
        try
        {
            return Integer.parseInt(literal);
        }
        catch(NumberFormatException e)
        {
            return literal;
        }
    }

    private ArrayList<Object> parseList(String open, String close)
    {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(open);
        eat(open);
        while (!token.equals(close))
            list.add(parse());
        list.add(close);
        eat(close);
        return list;
    }
}