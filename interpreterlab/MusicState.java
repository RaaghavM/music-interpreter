import java.util.*;

public class MusicState
{
    private Map<Integer, Integer> varMap;
    private int currOp;
    private int currAdd;
    private int acc;
    
    public MusicState()
    {
        varMap = new TreeMap<Integer, Integer>();
        currOp = -1;
        currAdd = -1;
        acc = -1;
    }
    
    public void setVariableValue(Integer varName, Integer value)
    {
        varMap.put(varName, value);
    }
    
    public int getVariableValue(Integer varName)
    {
        return varMap.get(varName);
    }
    
    public void changeCurrOp(int newOp)
    {
        currOp = newOp;
    }
    
    public int getCurrOp()
    {
        return currOp;
    }
    
    public void changeCurrAdd(int newAdd)
    {
        currAdd = newAdd;
    }
    
    public int getCurrAdd()
    {
        return currAdd;
    }
    
    public void changeAcc(int newAcc)
    {
        acc = newAcc;
    }
    
    public int getCurrAcc()
    {
        return acc;
    }
}
