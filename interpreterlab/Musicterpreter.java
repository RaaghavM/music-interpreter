import java.io.*;
import java.util.*;

public class Musicterpreter
{
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args)
    {
        //String fileName = "Test_Program_1";
        String fileName = "Music_Loop_Program";
        
        String file = load("C:\\Users\\abm00\\Desktop\\" + fileName + ".abc\\" + fileName + ".abc");
        //System.out.println(file);
        ABCCompiler compiler = new ABCCompiler();
        ArrayList<ArrayList<Integer>>[] intervals = compiler.compile(file);
        //System.out.println(intervals[0].toString());
        //System.out.println(intervals[1].toString());
        MusicState s = new MusicState();
        for (int i = 0; i < intervals[0].size(); i++)
        {
            //System.out.println(numerizeMeasure(intervals[0].get(i)) + " " + numerizeMeasure(intervals[1].get(i)));
            //System.out.print(intervals[0].get(i).get(0));
            //System.out.print(intervals[1].get(i).get(0));
            //System.out.println(intervals[0].get(i).get(0).equals(intervals[1].get(i).get(0)));
            if (intervals[0].get(i).get(0).equals(intervals[1].get(i).get(0)) && intervals[0].get(i).get(0) > 1000) //loop here
            {
                //System.out.println("repeat detected");
                int repeatCount = intervals[0].get(i).get(0)-1000;
                int index = 0;
                for (int j = 0; j < repeatCount; j++)
                {
                    index = i;
                    while(!intervals[0].get(index).get(intervals[0].get(index).size()-1).equals(repeatCount+1000))
                    {
                        //System.out.println(intervals[0].get(index).get(intervals[0].get(index).size()-1));
                        //System.out.println(numerizeMeasure(intervals[0].get(index)) + " " + numerizeMeasure(intervals[1].get(index)));
                        //System.out.print(intervals[0].get(i).get(0));
                        runStatement(numerizeMeasure(intervals[0].get(index)), numerizeMeasure(intervals[1].get(index)), s);
                        index++;
                    }
                    //System.out.println(numerizeMeasure(intervals[0].get(index)) + " " + numerizeMeasure(intervals[1].get(index)));
                    runStatement(numerizeMeasure(intervals[0].get(index)), numerizeMeasure(intervals[1].get(index)), s);
                }
                i = index;
            }
            else
            {
                //System.out.println(numerizeMeasure(intervals[0].get(i)) + " " + numerizeMeasure(intervals[1].get(i)));
                runStatement(numerizeMeasure(intervals[0].get(i)), numerizeMeasure(intervals[1].get(i)), s);
            }
        }
       
    }
    
    public static void test()
    {
        ABCCompiler compiler = new ABCCompiler();
        
        System.out.println(compiler.getNextMeasure("^f2a2 g6 ^f2e2^f2 | edcB A^FG2 G6 f2 :::::|", 0));
    }
    
    
    private static void runStatement(int num1, int num2, MusicState s)
    {
        if (s.getCurrOp() == -1) 
        {
            s.changeCurrOp(num2);
            s.changeCurrAdd(num1);
        }
        else //expression modifier and statement executer
        {
            switch (s.getCurrOp())
            {
                case 0: //Print
                    int out = s.getVariableValue(s.getCurrAdd());
                    
                    switch(num2)
                    {
                        case 0: //add something before printing
                           out += num1;
                           break;
                    }
                    System.out.println(out);
                    break;
                case 1:
                    break;
                case 11: //Let (assign statement)
                    switch(num2)
                    {
                        case 0: //something in memory plus something
                            s.setVariableValue(s.getCurrAdd(), s.getCurrAcc()+num1);
                            break;
                        case 1: //something else
                            break;
                        case 2: //something else
                            s.changeAcc(s.getVariableValue(s.getCurrAdd()) + num1);
                            break;
                        case 3: //something else
                            break;
                        case 4: //positive constant int (given as arg)
                            s.setVariableValue(s.getCurrAdd(), num1);
                            break; 
                    }
                    break;
                
            }
            s.changeCurrOp(-1);
            s.changeCurrAdd(-1);
        }
            
    }
    
    private static int numerizeMeasure(ArrayList<Integer> measure)
    {
        int mod = 12;
        int total = 0;
        for (int i = 0; i < measure.size(); i++)
        {
            if (measure.get(i) <= 1000)
                total += measure.get(i);
        }
        int ret = total%mod;
        //System.out.println(ret);
        if (ret < 0)
            ret += mod;
        return ret;
    }

    public static String load(String fileName)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null)
            {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
