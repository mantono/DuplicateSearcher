package dsv2.analysis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import duplicatesearcher.Token;

/**
 * The {@link InverseDocumentFrequency} measures in how many documents an object
 * occurs. Unlike the {@link TermFrequency}, this class does not take any
 * consideration to how many times an object occurs within each specific
 * document, only in how many documents.
 *
 */
public class InverseDocumentFrequency<T> implements VectorUnit<T>, Serializable
{
	private final static long serialVersionUID = 0L;
	private final Map<T, Integer> frequency = new HashMap<T, Integer>();
	private final Map<Integer, Set<T>> addedObjects = new HashMap<Integer, Set<T>>(100);

	public int getFrequency(final T obj)
	{
		if(!frequency.containsKey(obj))
			return 0;

		return frequency.get(obj);
	}

	public double getWeight(final T obj)
	{
		final double inverseFrequency = getInverseFrequency(obj);
		return 1 + Math.log(inverseFrequency);
	}
	
	public double getInverseFrequency(final T obj)
	{
		return addedObjects.size() / (double) getFrequency(obj);
	}

	public Set<T> getElements()
	{
		return frequency.keySet();
	}

	/**
	 * Add a {@link T} to the counter.
	 * 
	 * @param id is the identifying number of the document from which the object
	 * belongs to.
	 * @param obj is the item that should be added to the counter.
	 * @return true the item had not previously been added for the particular
	 * document id, else false.
	 */
	public boolean add(final int id, final T obj)
	{
		Set<T> savedItems;
		if(addedObjects.containsKey(id))
		{
			savedItems = addedObjects.get(id);
			if(savedItems.contains(obj))
				return false;
		}
		else
		{
			savedItems = new HashSet<T>(4);
			addedObjects.put(id, savedItems);
		}

		savedItems.add(obj);
		return increment(obj);
	}

	/**
	 * Add a {@link Set} of {@link T} objects to the counter.
	 * 
	 * @param id of the document the objects belongs to.
	 * @param input objects that should be added.
	 * @return the number of objects that was added to the counter.
	 */
	public int add(final int id, final Set<T> input)
	{
		Set<T> copyOfInput = new HashSet<T>(input);
		return addObjects(id, copyOfInput);
	}

	private int addObjects(final int id, final Set<T> objects)
	{
		Set<T> savedItems;
		int added = 0;

		if(addedObjects.containsKey(id))
		{
			return 0;
		}
		else
		{
			savedItems = new HashSet<T>(objects.size());
			addedObjects.put(id, savedItems);
		}

		for(T obj : objects)
		{
			if(increment(obj))
			{
				savedItems.add(obj);
				added++;
			}
		}

		return added;
	}

	private boolean increment(T obj)
	{
		if(obj == null)
			return false;
		if(!frequency.containsKey(obj))
		{
			frequency.put(obj, 1);
		}
		else
		{
			final int itemFrequency = frequency.get(obj) + 1;
			frequency.put(obj, itemFrequency);
		}
		return true;
	}

	private boolean reduce(T obj)
	{
		if(obj == null)
			return false;

		if(!frequency.containsKey(obj))
			return false;

		final int itemFrequency = frequency.get(obj) - 1;
		if(itemFrequency == 0)
			frequency.remove(obj);
		else
			frequency.put(obj, itemFrequency);

		return true;
	}

	@Override
	public Map<T, Integer> vectors()
	{
		return frequency;
	}

	public boolean remove(int id, Map<T, Integer> vectors)
	{
		if(!addedObjects.containsKey(id))
			return false;
		
		addedObjects.remove(id);
		
		for(T obj : vectors.keySet())
			reduce(obj);
		
		return true;
	}
}
