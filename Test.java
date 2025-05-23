package myTree;

import myTree.*;

public class Test
{
    public static void main (String[] args)
    {
	Tree A = new Tree();
	for (int i = 1; i <= 10; i += 1)
	    A.insert(i, 2 * i);

	Tree B = A.copy();
	for (int i = 1; i <= 10; i += 2)
	    B.delete(i);

	System.out.println("A is");
	for (Integer k = A.min(); k != null; k = A.next(k))
	{
	    Integer x = (Integer) A.find(k);
	    System.out.print("(" + k + ", " + x + ") ");
	}
	System.out.println();

	System.out.println("B is");
	for (Integer k = B.min(); k != null; k = B.next(k))
	{
	    Integer x = (Integer) B.find(k);
	    System.out.print("(" + k + ", " + x + ") ");
	}
	System.out.println();
    }
}
