import java.io.*;
import java.util.*;

public class Interpreter
{
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args)
    {
        //Runtime r = Runtime.getRuntime();
        //Process pr = r.exec("echo hi");
        Parser p = new Parser();
        p.addPrefix("print", 1);  // print PREcedes 1 argument (e.g. "print 3")
        p.addInfix("+", 2);  // + falls IN between 2 arguments (e.g. "2 + 3")
        p.addInfix("-", 2);
        p.addDelimiters("{", "}");
        p.addDelimiters("(", ")");
        p.addInfix("=", 2);

        Object program = p.parse(load("LucasMonster.txt"));
        State s = new State();
        //use to parse a string literal
        //Object program = p.parse(load("program.txt"));  //use to parse a program stored in a file
        //Object program = p.parse(input());  //use to parse a string entered by the user

        eval(program, s);
    }

    public static Object eval(Object exp, State s)
    {
        //System.out.println(exp.toString());
        if (exp instanceof Integer)
        {
            //the value of an integer is itself
            return exp;
        }
        else if (exp instanceof String)
        {
            String varName = (String)(exp);
            Object value = s.getVariableValue(varName);
            if (value != null)
            {
                return value;
            }
            else
            {
                throw new RuntimeException("cannot find variable:  " + varName);
            }
        }
        else
        {
            //must be a List
            List list = (List)exp;

            if (list.get(0).equals("print"))  // print EXP
            {
                Object argument = list.get(1);
                System.out.println(eval(argument, s));
                return "OK";
            }

            if (list.get(1).equals("+"))  // EXP + EXP
            {
                Object argument1 = list.get(0);
                Object argument2 = list.get(2);
                return (Integer)(eval(argument1, s)) + (Integer)(eval(argument2, s));
            }
            
            if (list.get(1).equals("-"))  // EXP - EXP
            {
                Object argument1 = list.get(0);
                Object argument2 = list.get(2);
                return (Integer)(eval(argument1, s)) - (Integer)(eval(argument2, s));
            }
            
            if (list.get(0).equals("(") && list.get(2).equals(")"))
            {
                Object expression = list.get(1);
                return (Integer)(eval(expression, s));
            }
            
            if (list.get(0).equals("{"))
            {
                int i = 1;
                while (!list.get(i).equals("}"))
                {
                    eval(list.get(i), s);
                    i++;
                }
                return "OK";
            }
            if (list.get(1).equals("="))
            {
                s.setVariableValue((String)list.get(0), eval(list.get(2), s));
                return "OK";
            }
            throw new RuntimeException("unable to evaluate:  " + exp);
        }
    }

    public static String input()
    {
        return in.nextLine();
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