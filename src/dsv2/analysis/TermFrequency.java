package dsv2.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TermFrequency<T> implements VectorUnit<T>
{
	private final Map<T, Integer> frequency;
	
	public TermFrequency(final TermFrequency<T> tf)
	{
		this.frequency = new HashMap<T, Integer>(tf.frequency);
	}
	
	public TermFrequency(final Collection<T> tf)
	{
		this.frequency = new HashMap<T, Integer>(tf.size()/2);
		for(T obj : tf)
			increment(obj);
	}

	public TermFrequency(final Map<T, Integer> frequency)
	{
		this.frequency = frequency;
	}
	
	public double getWeight(T obj)
	{
		int termFreq = getTermFrequency(obj);
		double termLog = Math.log(termFreq);
		if(termLog < 0)
			return 0;
		return 1 + termLog;
	}
	
	public Map<T, Double> getWeights()
	{
		Map<T, Double> weights = new HashMap<T, Double>(frequency.size());
		for(T key : frequency.keySet())
		{
			final double weight = getWeight(key);
			weights.put(key, weight);
		}
		
		return weights;
	}
	
	public int getTermFrequency(final T obj)
	{
		if(!frequency.containsKey(obj))
			return 0;

		return frequency.get(obj);
	}
	
	public boolean add(final T obj)
	{
		return increment(obj) > 0;
	}
	
	protected int increment(T element)
	{
		if(element == null)
			return 0;
		if(!frequency.containsKey(element))
		{
			frequency.put(element, 1);
			return 1;
		}
		else
		{
			final int tokenFrequency = frequency.get(element) + 1;
			frequency.put(element, tokenFrequency);
			return tokenFrequency;
		}
	}
	
	public boolean change(final T currentKey, final T newKey)
	{
		if(currentKey.equals(newKey))
			return false;
		
		if(!frequency.containsKey(currentKey))
			return false;
		
		int addition = 0;
		
		if(frequency.containsKey(newKey))
			addition = frequency.get(newKey);

		final int value = frequency.get(currentKey) + addition;
		frequency.remove(currentKey);
		frequency.put(newKey, value);
		
		return true;
	}
	
	public int size()
	{
		return frequency.size();
	}

	public void clear()
	{
		frequency.clear();
	}

	public int remove(final T obj)
	{
		if(!frequency.containsKey(obj))
			return 0;
		return frequency.remove(obj);
	}

	@Override
	public Map<T, Integer> vectors()
	{
		return frequency;
	}

}
