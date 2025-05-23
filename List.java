// Version 1

/*
 * @author: Asifur Rahman
 * @course: CSc 345
 * @program description: A program that implements a custom circular linked list. 
 */


package myList;

public class List
{
    private static Node pool = null; // Pool of free nodes

    private static class Node // List node
    {
	int  item;
	Node next;
	Node prev;

        final static int BLOCK_SIZE = 32; // Number of nodes in a block

        // allocate -- Allocate a node by taking one from the pool
        //
        public static Node allocate ()
        {
            // If the pool is empty, allocate a block of nodes and put them
            // into the pool
            //
            if (pool == null)
	    {
                // Allocate a block of nodes
		//
		Node[] block = new Node[BLOCK_SIZE];
                for (int i = 0; i < BLOCK_SIZE; i++)
		    block[i] = new Node(); // (Darn you, Java!)

		// Link the nodes in the block into a pool
		//
		pool = block[0];
		for (int i = 0; i < BLOCK_SIZE - 1; i++)
		    block[i].next = block[i + 1];
		block[BLOCK_SIZE - 1].next = null;
	    }

            // Take a node off the pool
            //
	    Node v = pool;
	    pool = v.next;

	    return v;
	}
	public static Node allocate (int item, Node next, Node prev)
	{
	    Node v = allocate();

	    // Initialize the node fields
            //
	    v.item = item;
	    v.next = next;
	    v.prev = prev;

	    return v;
	}

        // free -- Free a node by putting it into the pool
        //
        public void free ()
	{
	    this.next = pool;
	    pool = this;
	}
    };

    public static class Handle // Safe pointer to a list node
    {
	private Node myNode; // Node pointed at 
	private Node myHead; // Header node of the list containing myNode

        // Handle constructors
        //
        public Handle (Node myNode, Node myHead)
        {
	    this.myNode = myNode;
	    this.myHead = myHead;
	}
        public Handle (Handle h)
	{
	    this(h.myNode, h.myHead);
	}
        public Handle (List l)
	{
	    this(l.header, l.header);
	}

        // copy -- Copy a handle
        //
        public Handle copy ()
	{
	    return new Handle(this);
	}

	// item -- Return the item in the node pointed at by a handle
	//
	public int item ()
	{
//	    return (myNode != myHead) ? myNode.item : null;
	    return myNode.item;
	}

        // forward -- Move the handle to the next node; returns true iff
	//            this does not fall off the list
	//
	public boolean forward ()
	{
	    myNode = myNode.next;
	    return myNode != myHead;
	}

        // backward -- Move the handle to the previous node; returns true iff
	//             this does not fall off the list
	//
	public boolean backward ()
	{
	    myNode = myNode.prev;
	    return myNode != myHead;
	}

        // next -- Return a new handle that points to the next node
	//
	public Handle next ()
	{
	    return new Handle(myNode.next, myHead);
	}

        // prev -- Return a new handle that points to the previous node
	//
	public Handle prev ()
	{
	    return new Handle(myNode.prev, myHead);
	}

	// insertAfter -- Insert an item after the handle
	//
	public Handle insertAfter (int item)
	{
	    Node v = Node.allocate(item, myNode.next, myNode);
	    myNode.next.prev = v;
	    myNode.next = v;
	    return new Handle(v, myHead);
	}

	// insertBefore -- Insert an item before the handle
	//
	public Handle insertBefore (int item)
	{
	    return prev().insertAfter(item);
	}

        // equal -- Are handles equal?
	//
	public boolean equal (Handle h)
	{
	    return myNode == h.myNode && myHead == h.myHead;
	}

        // delete -- Remove from the list the element pointed at by the handle
	//
	// Returns the item stored in the deleted element.
	//
	public int delete ()
	{
	    myNode.next.prev = myNode.prev;
	    myNode.prev.next = myNode.next;
	    int item = myNode.item;
	    myNode.free();
	    return item;
	}

        // eject -- Unlink from the list the node pointed at by the handle
	//
	// This does not free the ejected node.
	//
	public void eject ()
	{
	    myNode.next.prev = myNode.prev;
	    myNode.prev.next = myNode.next;
	}

        // inject -- Link back into the list an ejected node
	//
	// An eject followed by an inject restores the prior state of the list
	// if the series of eject and inject operations are performed in a
	// stack-like manner.
	//
	public void inject ()
	{
	    myNode.next.prev = myNode;
	    myNode.prev.next = myNode;
	}
    }

    Node header; // Header node for the list

    // List constructor
    //
    // Creates an empty list.
    //
    public List ()
    {
		header = Node.allocate();
        header.next = header;
        header.prev = header;
    }

    // handle -- Return a new handle for the list
    //
    // The handle is initialized ready for iteration over the list.
    //
    public Handle handle ()
    {
	return new Handle(this);
    }


    // copy -- Make a copy of a list
    //
    public List copy ()
    {
		// empty new list
		List nList = new List();
		
		// now iterate through the list and add each of its elements to the new copied list
		for (Handle handle = new Handle(this); handle.forward();) {
			nList.put(handle.item());
		}
		return nList;
    }



    // push -- Push an element onto the top of a stack
    //
    public Handle push (int item)
    {
	return (new Handle(this)).insertAfter(item);
    }

    // pop -- Pop the element off the top of a stack
    //
    public int pop ()
    {
	return (new Handle(header.next, header)).delete();
    }

    // put -- Put an element onto the rear of a queue
    //
    public Handle put (int item)
    {
	return (new Handle(this)).insertBefore(item);
    }

    // get -- Get the element off the front of a queue
    //
    public int get ()
    {
	return pop();
    }

    // You will implement the following methods "pull", "top", "front", "rear",
    // "concatenate", "splitBefore", "splitAfter", and "empty"

    // pull -- Remove the element at the rear of a dequeue
    //
    public int pull ()
    {
    	return (new Handle(header.prev, header)).delete();
    }

    // top -- Return the item at the top of a stack
    //
    public int top ()
    {
    	// if list is empty, program will throw a RuntimeException error.
    	if (empty()) {
    		throw new RuntimeException("List is empty! ");
    	}
    	
    	// returning item top of a stack means returning next item right after header node
    	return header.next.item;
    }

    // front -- Return the item at the front of a queue
    //
    public int front ()
    { return top(); }

    // rear -- Return the item at the rear of a queue
    //
    public int rear ()
    {
    	// if list is empty program will throw a RuntimeException error.
    	if (empty()) {
    		throw new RuntimeException("List is empty! ");
    	}
    	return header.prev.item;
    } 

    // concatenate -- Destructively concatenate in O(1) time
    //
    // Concatenate places all the elements of the list argument after the
    // elements in this list, makes the list argument empty, and returns
    // this list.
    //
    public List concatenate (List l)
    {
    	// if the new list "l" is empty, then do nothing and return the actual list 
    	if (l.empty()) {
    		return this;
    	}
    	
    	// if the actual list is empty, then add list "l" items to the actual list and clear the list "l".
    	if (this.empty()) {
    		header.next = l.header.next;
    		header.prev = l.header.prev;
    		header.next.prev = header;
    		header.prev.next = header;
    		
    		// clear list "l"
    		l.header.next = l.header;
    		l.header.prev = l.header;
    		return this;
    	}
    	
    	// if both lists are non-empty
    	Node thisLast = header.prev;		// last node in the current list
    	Node lFirst = l.header.next;		// first node in list l
    	Node lLast = l.header.prev;			// last node in list l
    	
    	// linking the last node of the actual list to the first node of l
    	thisLast.next = lFirst;
    	lFirst.prev = thisLast;
    	
    	// linking the first node of the actual list to the last node of l
    	lLast.next = header;
    	header.prev = lLast;
    	
    	// clearing the list l
    	l.header.next = l.header;
    	l.header.prev = l.header;
    	
    	return this;
    }

    // splitBefore -- Split a list before a given element, destructively
    //
    // Returns a new list containing all elements after the handle, including
    // the handle element.
    //
    public List splitBefore (Handle h)
    {
    	// creating an empty new list
    	List nList = new List();
    	
    	// the new list should start with the node pointed by the handle
    	Node newFirst = h.myNode;
    	
    	// save the node immediately before the split point
    	// this node will become last node of the old list
    	Node prevSplit = newFirst.prev;
    	
    	// the last node of the new list is the last node of the current list
    	Node newLast = header.prev;
    	
    	// hook up the nList-> its first node is newFirst and last is newLast
    	nList.header.next = newFirst;
    	nList.header.prev = newLast;
    	
    	// updating the pointers in the new list so that it is circular
    	newFirst.prev = nList.header;
    	newLast.next = nList.header;
    	
    	// adjusting the current list to remove the nodes that have been moved
    	// the new last node of the current list is the node right before newFirst
    	header.prev = prevSplit;
    	prevSplit.next = header;
    	
    	return nList;
    } 

    // splitAfter -- Split a list after a given element, destructively
    //
    // Returns a new list containing all elements after the handle.
    //
    public List splitAfter (Handle h)
    {
    	// creates an empty list
    	List nList = new List();
    	
    	// node after the handle is where the new list starts
    	Node newFirst = h.myNode.next;
    	
    	// if myNode is the last element, then the newFirst equals header and there are no elements to split
    	// in this case, returns an empty list
    	if (newFirst == header) {
    		return nList;
    	}
    	
    	// new list's last node is the current list last node.
        Node newLast = header.prev;
        
        // the new list-> its first node is newFirst and its last node is newLast
        nList.header.next = newFirst;
        nList.header.prev = newLast;
        
        // updates the pointers in the new list so that it is circular
        newFirst.prev = nList.header;
        newLast.next = nList.header;
        
        // adjust the original list to remove the nodes that have been move
        // The original list should now end at h.myNode
        h.myNode.next = header;
        header.prev = h.myNode;
        
        return nList;
    }
    
    
    // empty -- Is the list empty?
    //
    public boolean empty ()
    {
		// If the linked list is empty, then the header's next pointer will point to the header itself
		return header.next == header;
	}



    // size -- Count the number of nodes on the list
    //
    // This is a linear-time operation.
    //
    public long size ()
    {
	long i = 0;
	for (Handle h = new Handle(this); h.forward(); )
	    i++;
	return i;
    }

    // You will implement the following methods "reverse" and "clear"

    // reverse -- Reverse the elements on the list
    //
    public void reverse ()
    {
    	for (Node current =  header; ;) {
    		Node temporary = current.next;		// save the original next pointer
    		current.next = current.prev;		// swap
    		current.prev = temporary;			// swap
    		current = temporary;				// move to what was originaly the next node
    		if (current == header) {			// once we circle back to header, its done
    			break;
    		}
    	}
    	
    }

    // clear -- Make a list empty in O(1) time
    //
    // Clear places all the list nodes in the pool.
    //
    public void clear ()
    {
    	// check if list is already empty, do nothing
    	if (empty()) {
    		return;
    	}
    	
    	// save chain of nodes (all the nodes between header.next and header.prev)
    	Node first = header.next;
    	Node last = header.prev;
    	
    	// attach chain to the front of pool
    	// last node's next pointer now pounts to the current pool
    	// and then update pool to start with the first node of our chain
    	
    	last.next = pool;
    	pool = first;
    	
    	// resets the list to be emptu
    	header.next = header;
    	header.prev = header;
    }
}
