package stream;

import java.util.Arrays;

/*
** Stream tutorial from: http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples
**
** Notes:
** 01. Stream operations are either intermediate or terminal
** 02. Stream interface doc: http://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html
** 03. Stream.of("a1", "a2", "a3");
** 04. Java 8 ships with special streams for primitive data types: IntStream, LongStream and DoubleStream
**     a. IntStream.range(1, 4).forEach(System.out::println);
**     b. Primitive streams support the additional terminal aggregate operations sum() and average()
**     c. To transform an object stream to a primitive stream use mapToInt(), mapToLong(), or mapToDouble
**     d. Primitive streams can be transformed to object streams via mapToObj()
** 05. The order of stream operations can reduce the total amount of processing (e.g. put filters first)
** 06. Sorting is a special kind of intermediate operation. It's a so called stateful operation.
**     a. The sort operation is executed on the entire input collection (i.e. "horizontally")
** 07. Java 8 streams cannot be reused. As soon as you call any terminal operation the stream is closed.
**     a. To overcome this limitation we could create a stream supplier to construct a new stream
**        with all intermediate operations already set up
** 08. Collect is a terminal operation to transform the elements of the stream into a different kind of result,
**     e.g. a List, Set or Map (Collectors.toList(), Collectors.toSet(), Collectors.toMap())
**     a. Also Collectors.groupingBy(), Collectors.summarizingInt(), Collectors.joining()
**     b. Use Collector.of(), specifying a supplier, accumulator, combiner, and finisher, to create a custom collector
** 09. flatMap transforms each element of the stream into a stream of other objects
**     a. Optionals flatMap operation can be utilized to prevent nasty null checks
** 10. The reduce() operation combines all elements of the stream into a single result
**     a. The reduce method accepts a BinaryOperator accumulator function
**     b. The first one reduces a stream of elements to exactly one element of the stream
**     c. The second accepts both an identity value and a BinaryOperator accumulator
**     d. The third accepts an identity value, a BiFunction accumulator, and a combiner function of type BinaryOperator
** 11. Collections support the method parallelStream() to create a parallel stream of elements
**     a. You can call the intermediate method parallel() to convert a sequential stream to a parallel stream
**     b. Parallel streams can bring be a nice performance boost to streams with a large amount of input elements
**     c. But some parallel stream operations like reduce and collect need additional computations (combine operations)
**        which aren't needed when executed sequentially
**     d. All parallel stream operations share the same JVM-wide common ForkJoinPool,
**        so avoid implementing slow blocking stream operations
*/
public class StreamTutorial1 {

}
