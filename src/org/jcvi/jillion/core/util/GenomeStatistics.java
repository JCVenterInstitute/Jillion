package org.jcvi.jillion.core.util;

import java.util.Collections;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.jcvi.jillion.internal.core.util.GrowableIntArray;
/**
 * {@code GenomeStatistics} is a utility class for computing 
 * different statistical measurements about genomes (for example N50).
 * @author dkatzel
 *
 */
public final class GenomeStatistics {

	private GenomeStatistics(){
		//can not instantiate
	}
	
	/**
	 * Create and execute a {@link java.util.stream.Collector}
	 * that will compute the Nx value of the elements in the given stream.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #nXBuilder(double)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link IntStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if either stream or mapper are null.
	 * 
	 * @throws IllegalArgumentException if percentage >= 1 or <= 0.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 */
	public static OptionalInt nX(IntStream stream, double percentage){
		return stream.collect(  ()->GenomeStatistics.nXBuilder(percentage), 
								(builder, v)-> builder.add(v),
								(a,b)-> a.merge(b))
								.build();
	}
	
	/**
	 * Create and execute a {@link java.util.stream.Collector}
	 * that will compute the Nx value of the elements in the given stream.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #nXBuilder(double)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * 
	 * 
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if either stream or mapper are null.
	 * 
	 * @throws IllegalArgumentException if percentage >= 1 or <= 0.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 */
	public static OptionalInt nX(LongStream stream, double percentage){
		return stream.collect(  ()->GenomeStatistics.nXBuilder(percentage), 
								(builder, v)-> builder.add(v),
								(a,b)-> a.merge(b))
								.build();
	}
	
	/**
	 * Compute the N50 value of the elements in the given {@link IntStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n50Builder()}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link IntStream} of elements to compute;
	 * can not be null.
	 * 
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * 
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #nX(IntStream, double) nX(stream, .5D)}
	 */
	public static  OptionalInt n50(IntStream stream){
			return nX(stream, .5D);		
	}
	/**
	 * Compute the N50 value of the elements in the given {@link LongStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n50Builder()}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * 
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #nX(LongStream, double) nX(stream, .5D)}
	 */
	public static  OptionalInt n50(LongStream stream){
		return nX(stream, .5D);	
	}
	
	/**
	 * Compute the N75 value of the elements in the given {@link IntStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n75Builder()}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link IntStream} of elements to compute;
	 * can not be null.
	 * 
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * 
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #nX(IntStream, double) nX(stream, .75D)}
	 */
	public static  OptionalInt n75(IntStream stream){
		return nX(stream, .75D);		
	}
	/**
	 * Compute the N75 value of the elements in the given {@link LongStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n75Builder()}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * 
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #nX(LongStream, double) nX(stream, .75D)}
	 */
	public static OptionalInt n75(LongStream stream) {
		return nX(stream, .75D);
	}
	
	
	/**
	 * Compute the N90 value of the elements in the given {@link IntStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n90Builder()}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link IntStream} of elements to compute;
	 * can not be null.
	 * 
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * 
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #nX(IntStream, double) nX(stream, .9D)}
	 */
	public static  OptionalInt n90(IntStream stream){
		return nX(stream, .9D);		
	}
	/**
	 * Compute the N90 value of the elements in the given {@link LongStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n90Builder()}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * 
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #nX(LongStream, double) nX(stream, .9D)}
	 */
	public static OptionalInt n90(LongStream stream) {
		return nX(stream, .9D);
	}
	
	
	
	/**
	 * Create and execute a {@link java.util.stream.Collector}
	 * that will compute the NGx value of the elements in the given stream.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ngXBuilder(double)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if either stream is null.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1 or percentage >= 1 or <= 0.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 */
	public static  OptionalInt ngX(LongStream stream, double percentage, long genomeLength){
		return stream
				.collect(()->GenomeStatistics.ngXBuilder(genomeLength, percentage), 
					(builder, v)-> builder.add(v),
					(a,b)-> a.merge(b))
					.build();
	}
	
	/**
	 * Create and execute a {@link java.util.stream.Collector}
	 * that will compute the NGx value of the elements in the given stream.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ngXBuilder(double)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if either stream is null.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1 or percentage >= 1 or <= 0.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 */
	public static  OptionalInt ngX(IntStream stream, double percentage, long genomeLength){
		return stream
				.collect(()->GenomeStatistics.ngXBuilder(genomeLength, percentage), 
					(builder, v)-> builder.add(v),
					(a,b)-> a.merge(b))
					.build();
		
		
	}
	
	
	
	
	/**
	 * Compute the NG50 value of the elements in the given {@link IntStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng50Builder(long)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link IntStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #ngX(IntStream, double, long) ngX(stream, .5D, genomeLength)}
	 */
	public static OptionalInt ng50(IntStream stream, long genomeLength){
			return ngX(stream, .5D, genomeLength);		
	}
	/**
	 * Compute the NG50 value of the elements in the given {@link LongStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng50Builder(long)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #ngX(LongStream, double, long) ngX(stream, .5D, genomeLength)}
	 */
	public static OptionalInt ng50(LongStream stream, long genomeLength){
		return ngX(stream, .5D, genomeLength);		
	}
	
	
	/**
	 * Compute the NG75 value of the elements in the given {@link IntStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng75Builder(int)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link IntStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #ngX(IntStream, double, long) ngX(stream, .75D, genomeLength)}
	 */
	public static OptionalInt ng75(IntStream stream, long genomeLength){
			return ngX(stream, .75D, genomeLength);		
	}
	/**
	 * Compute the NG75 value of the elements in the given {@link LongStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng75Builder(int)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #ngX(LongStream, double, long) ngX(stream, .75D, genomeLength)}
	 */
	public static OptionalInt ng75(LongStream stream, long genomeLength){
		return ngX(stream, .75D, genomeLength);		
	}
	
	/**
	 * Compute the NG90 value of the elements in the given {@link IntStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng90Builder(long)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link IntStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #ngX(IntStream, double, long) ngX(stream, .9D, genomeLength)}
	 */
	public static OptionalInt ng90(IntStream stream, long genomeLength){
			return ngX(stream, .9D, genomeLength);		
	}
	/**
	 * Compute the NG90 value of the elements in the given {@link LongStream}.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng90Builder(long)}
	 * and then manually adding each element to the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param stream the {@link LongStream} of elements to compute;
	 * can not be null.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws NullPointerException if stream is null.
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * @apiNote the reason this method exists is because Java 8 collectors
	 * on primitives doesn't have a catch all collect(Collector) method
	 * like the object Stream does.  In order save users from manually creating collectors
	 * themselves, it was easier to create a helper method that creates the collector then executes it.
	 * 
	 * @implNote This is the same as {@link #ngX(LongStream, double, long) ngX(stream, .9D, genomeLength)}
	 */
	public static OptionalInt ng90(LongStream stream, long genomeLength){
		return ngX(stream, .9D, genomeLength);		
	}
	
	
	/**
	 * Helper method for Creating a {@link GenomeStatisticsBuilder}
	 * that will compute N50. This is equivalent to calling
	 * {@link #nXBuilder(double) nxBuilder(0.5D)}
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @see #nXBuilder(double)
	 */
	public static GenomeStatisticsBuilder n50Builder(){
		return new NStatBuilder(.5D);
	}
	/**
	 * Helper method for Creating a {@link GenomeStatisticsBuilder}
	 * that will compute N75. This is equivalent to calling
	 * {@link #nXBuilder(double) nxBuilder(0.75D)}
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @see #nXBuilder(double)
	 */
	public static GenomeStatisticsBuilder n75Builder(){
		return new NStatBuilder(.75D);
	}
	/**
	 * Helper method for Creating a {@link GenomeStatisticsBuilder}
	 * that will compute N90. This is equivalent to calling
	 * {@link #nXBuilder(double) nxBuilder(0.9D)}
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @see #nXBuilder(double)
	 */
	public static GenomeStatisticsBuilder n90Builder(){
		return new NStatBuilder(.9D);
	}
	
	/**
	 * Creates a new {@link GenomeStatisticsBuilder} instance
	 * that will compute the Nx value for any percentage.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @throws IllegalArgumentException if percentage >= 1 or <= 0.
	 */
	public static GenomeStatisticsBuilder nXBuilder(double percentage){
		if(percentage <= 0){
			throw new IllegalArgumentException("percentage must be > 0");
		}
		if(percentage >=1){
			throw new IllegalArgumentException("percentage must be < 1");
		}
		return new NStatBuilder(percentage);
	}
	/**
	 * Helper method for Creating a {@link GenomeStatisticsBuilder}
	 * that will compute NG50. This is equivalent to calling
	 * {@link #ngXBuilder(long, double) nxBuilder(genomeLength, 0.5D)}
	 * 
	 *  @param genomeLength the (expected) genome length which is used to determine
	 * when we have 50% covered.
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1
	 * 
	 * @see #ngXBuilder(long, double)
	 */
	public static GenomeStatisticsBuilder ng50Builder(long genomeLength){
		return new NGStatBuilder(genomeLength, .5D);
	}
	/**
	 * Helper method for Creating a {@link GenomeStatisticsBuilder}
	 * that will compute NG75. This is equivalent to calling
	 * {@link #ngXBuilder(long, double) nxBuilder(genomeLength, 0.75D)}
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have 75% covered.
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1
	 * 
	 * @see #ngXBuilder(long, double)
	 */
	public static GenomeStatisticsBuilder ng75Builder(long genomeLength){
		return new NGStatBuilder(genomeLength, .75D);
	}
	/**
	 * Helper method for Creating a {@link GenomeStatisticsBuilder}
	 * that will compute NG90. This is equivalent to calling
	 * {@link #ngXBuilder(long, double) nxBuilder(genomeLength, 0.9D)}
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have 90% covered.
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1
	 *  
	 * @see #ngXBuilder(long, double)
	 */
	public static GenomeStatisticsBuilder ng90Builder(long genomeLength){
		return new NGStatBuilder(genomeLength, .9D);
	}
	/**
	 * Creates a new {@link GenomeStatisticsBuilder} instance
	 * that will compute the NGx value for any percentage.  For example
	 * to compute NG50, the percentage value is {@code 0.5}.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * @return a new {@link GenomeStatisticsBuilder} instance to add the 
	 * contig lengths to.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1 or
	 * percentage >= 1 or percentage <= 0.
	 */
	public static GenomeStatisticsBuilder ngXBuilder(long genomeLength, double percentage){
		if(genomeLength <= 0){
			throw new IllegalArgumentException("genome length must be > 0");
		}
		if(percentage <= 0){
			throw new IllegalArgumentException("percentage must be > 0");
		}
		if(percentage >=1){
			throw new IllegalArgumentException("percentage must be < 1");
		}
		return new NGStatBuilder(genomeLength, percentage);
	}
	
	
	/**
	 * Interface for computing a Genome Statistic
	 * using the Builder pattern which allows you to delay adding
	 * elements to include in the computation instead of requiring 
	 * them all at once.
	 * 
	 * @author dkatzel
	 *
	 */
	public interface GenomeStatisticsBuilder{
		/**
		 * Add a single length to the computation.
		 * 
		 * @param length the length to add must be >0.
		 * 
		 * @return this
		 * 
		 * @throws IllegalArgumentException if length is negative.
		 * 
		 */
		GenomeStatisticsBuilder add(int length);
		/**
		 * Convenience method for adding a length that is of type
		 * long.  The value must still be between 1 and {@link Integer#MAX_VALUE}.
		 * 
		 * @apiNote this method exists because many Jillion objects
		 * return longs.
		 * 
		 * @implSpec after checking to make sure {@code length} fits inside
		 * an int, this method just delegates to {@link #add(int) add((int)) length)}
		 * casting the length to an int.
		 * 
		 * @param length the length to add must be >0 and <= {@link Integer#MAX_VALUE}.
		 * 
		 * @return this
		 * 
		 * @throws IllegalArgumentException if length is negative or > {@link Integer#MAX_VALUE}.
		 */
		GenomeStatisticsBuilder add(long length);
		
		/**
		 * Combine the lengths of {@link GenomeStatisticsBuilder}s
		 * into a single list of lengths. This is used by the collector
		 * implementations and probably should not be called directly.
		 * 
		 * @param other the other {@link GenomeStatisticsBuilder} whose lengths are to be merged
		 * into this builder; can not be {@code null}.
		 * 
		 * @return this.
		 * 
		 * @throws NullPointerException if other is null.
		 */
		GenomeStatisticsBuilder merge(GenomeStatisticsBuilder other);
		
		
		/**
		 * Compute the genome statistic.
		 * 
		 * @return an {@link OptionalInt} which either has
		 * a value, or is empty if there was an error computing it.
		 */
		OptionalInt build();
	}
	
	
	private static abstract class AbstractStatBuilder implements GenomeStatisticsBuilder{

		private final GrowableIntArray array = new GrowableIntArray();
		
		private final double percentage;
		
		
		public AbstractStatBuilder(double percentage) {
			this.percentage = percentage;
		}

		@Override
		public GenomeStatisticsBuilder add(int length) {
			if(length < 1){
				throw new IllegalArgumentException("length can not be less than 1");
			}
			array.append(length);
			return this;
		}

		
		
		
		@Override
		public GenomeStatisticsBuilder merge(GenomeStatisticsBuilder other) {
			if( !(other instanceof AbstractStatBuilder)){
				throw new IllegalStateException("can not merge builders of different types");
			}
			array.append( ((AbstractStatBuilder)other).array);
			
			return this;
		}

		@Override
		public GenomeStatisticsBuilder add(long value) {
			if(value > Integer.MAX_VALUE || value < Integer.MIN_VALUE){
				throw new IllegalArgumentException("value must fit into an int : " + value);
			}
			return add( (int) value);
		}
		
	

		@Override
		public OptionalInt build() {
			if(array.getCurrentLength() ==0){
				return OptionalInt.empty();
			}
			array.sort();
			
			int[] sorted = array.toArray();
			
			double divisor = computeDivisor(sorted);
			
			long valueSoFar=0;
			for(int i= sorted.length-1; i>=0; i--){
				int value = sorted[i];
				valueSoFar +=value;
				if( (valueSoFar/divisor) >= percentage){
					return OptionalInt.of(value);
				}
			}
			
			return OptionalInt.empty();
		}

		protected abstract double computeDivisor(int[] sorted);
	}
	
	private static final class NStatBuilder extends AbstractStatBuilder{

	
		public NStatBuilder(double percentage) {
			super(percentage);
		}

		@Override
		protected double computeDivisor(int[] sorted) {
			long sum = 0;
			for(int i=0; i< sorted.length; i++){
				sum +=sorted[i];
			}
			
			return sum;
		}

		
		
	}
	
	
	private static final class NGStatBuilder extends AbstractStatBuilder{

		
		private final long genomeLength;
		
		public NGStatBuilder(long genomeLength, double percentage) {
			super(percentage);
			if(genomeLength <1){
				throw new IllegalArgumentException("genome length must be >= 1");
			}
			this.genomeLength = genomeLength;
		}

		@Override
		protected double computeDivisor(int[] sorted) {
			return genomeLength;
		}

		
	}
	
	private static class NgXCollector<T extends Number> implements Collector<T, GenomeStatisticsBuilder, OptionalInt>{

		private final long genomeLength;
		private final double percentage;
		
		
		public NgXCollector(long genomeLength, double percentage) {
			if(genomeLength <= 0){
				throw new IllegalArgumentException("genome length must be > 0");
			}
			if(percentage <= 0){
				throw new IllegalArgumentException("percentage must be > 0");
			}
			if(percentage >=1){
				throw new IllegalArgumentException("percentage must be < 1");
			}
			this.genomeLength = genomeLength;
			this.percentage = percentage;
		}

		@Override
		public Supplier<GenomeStatisticsBuilder> supplier() {

			return () -> new NGStatBuilder(genomeLength, percentage);
		}

		@Override
		public BiConsumer<GenomeStatisticsBuilder, T> accumulator() {
			return (builder, v)-> builder.add(v.longValue());
		}

		@Override
		public BinaryOperator<GenomeStatisticsBuilder> combiner() {
			return (a, b)-> a.merge(b);
		}

		@Override
		public Function<GenomeStatisticsBuilder, OptionalInt> finisher() {
			return GenomeStatisticsBuilder::build;
		}

		@Override
		public Set<java.util.stream.Collector.Characteristics> characteristics() {
			return Collections.singleton(Characteristics.UNORDERED);
		}
		
	}
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the NG50 value.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng50Builder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * 
	 * @implNote This is the same as {@link #ngXCollector(long, double) ngX(genomeLength, .5D)}
	 */
	public static<T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> ng50Collector(long genomeLength){
		return ngXCollector(genomeLength, .5D);
	}
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the NG90 value.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng90Builder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * 
	 * @implNote This is the same as {@link #ngXCollector(long, double) ngX(genomeLength, .9D)}
	 */
	public static<T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> ng90Collector(long genomeLength){
		return ngXCollector(genomeLength, .9D);
	}
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the NG75 value.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ng75Builder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * 
	 * @implNote This is the same as {@link #ngXCollector(long, double) ngX(genomeLength, .75D)}
	 */
	public static<T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> ng75Collector(long genomeLength){
		return ngXCollector(genomeLength, .75D);
	}
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the  NGx value for any percentage.  For example
	 * to compute NG50, the percentage value is {@code 0.5}.
	 * 
	 * The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #ngXBuilder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param genomeLength the (expected) genome length which is used to determine
	 * when we have X% covered.
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1 or
	 * percentage >= 1 or percentage <= 0.
	 * 
	 */
	public static <T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> ngXCollector(long genomeLength, double percentage){
		return new NgXCollector<T>(genomeLength, percentage);
	}
	
	
	private static class NXCollector<T extends Number> implements Collector<T, GenomeStatisticsBuilder, OptionalInt>{

		private final double percentage;
		
		
		public NXCollector(double percentage) {
			this.percentage = percentage;
		}

		@Override
		public Supplier<GenomeStatisticsBuilder> supplier() {

			return () -> new NStatBuilder(percentage);
		}

		@Override
		public BiConsumer<GenomeStatisticsBuilder, T> accumulator() {
			return (builder, v)-> builder.add(v.longValue());
		}

		@Override
		public BinaryOperator<GenomeStatisticsBuilder> combiner() {
			return (a, b)-> a.merge(b);
		}

		@Override
		public Function<GenomeStatisticsBuilder, OptionalInt> finisher() {
			return GenomeStatisticsBuilder::build;
		}

		@Override
		public Set<java.util.stream.Collector.Characteristics> characteristics() {
			return Collections.singleton(Characteristics.UNORDERED);
		}
		
	}
	
	
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the N50 value.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n50Builder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * 
	 * @implNote This is the same as {@link #nXCollector(long, double) ngX(.5D)}
	 */
	public static <T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> n50Collector(){
		return nXCollector(.5D);
	}
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the N90 value.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n90Builder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * 
	 * @implNote This is the same as {@link #nXCollector(long, double) ngX(.9D)}
	 */
	public static <T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> n90Collector(){
		return nXCollector(.9D);
	}
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the N75 value.  The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #n75Builder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if genomeLength < 1.
	 * 
	 * 
	 * @implNote This is the same as {@link #nXCollector(long, double) ngX(.55D)}
	 */
	public static <T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> n75Collector(){
		return nXCollector(.75D);
	}
	
	/**
	 * Collect all the elements of a Stream of Numbers
	 * and compute the  Nx value for any percentage.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * The end result
	 * is the same as creating a {@link GenomeStatisticsBuilder} via {@link #nXBuilder(long)}
	 * and then manually adding each element of the stream to the builder then returning
	 * the resulting  built {@link OptionalInt}.  However, since this uses the new {@link Stream}
	 * and {@link java.util.stream.Collector} classes, it may have better performance if using a
	 * parallel stream.
	 * 
	 * @param percentage the percentage value to compute; must be between
	 * 0 and 1 <em>exclusive</em>.  For example
	 * to compute N50, the percentage value is {@code 0.5}.
	 * 
	 * 
	 * @return an {@link OptionalInt} which either has
	 * a value, or is empty if there was an error computing it.
	 * 
	 * @throws IllegalArgumentException if
	 * percentage >= 1 or percentage <= 0.
	 * 
	 */
	public static <T extends Number> Collector<T, GenomeStatisticsBuilder, OptionalInt> nXCollector( double percentage){
		return new NXCollector<T>(percentage);
	}
	
	
	
}
