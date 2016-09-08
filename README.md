# spark-2-streaming-nosuchmethod-arrowassoc
An illustration of the following issues: 
http://stackoverflow.com/questions/39395521/spark-2-0-0-streaming-job-packed-with-sbt-assembly-lacks-scala-runtime-methods
http://stackoverflow.com/questions/36635586/nosuchmethoderror-spark-kafka-java

I'm not sure where exactly the problem is, so I recreated it as briefly as I could:
* packaging jobs for Spark 2.0.0 with [sbt-assembly 0.14.3](https://github.com/sbt/sbt-assembly) 
* getting errors about missing methods from Scala runtime, like `java.lang.NoSuchMethodError: scala.Predef$.ArrowAssoc`

It doesn't have to be the driver code, even if you avoid using `->`, the same error is seen when using 
`spark-streaming-kafka-0-8_2.11` v2.0.0, and that's the real problem.

Original example: [NetworkWordCount.scala](https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/NetworkWordCount.scala)

Checkout the original example and see it working:
    
    git checkout b359e722b9b88d3e2be44e56de1a8cf16144384e

#### Build

    sbt assembly

#### Run the working example

Runs ok with precompiles Spark 2.0.0, 
throw some words at netcat (GNU version used here) and behold your Spark app counting them:
    
    nc -l -p 5555
    Spark
    go
    count
    some
    words
    and 
    print
    some
    numbers
    
    spark-2.0.0-bin-hadoop2.7/bin/spark-submit \
    --class org.apache.spark.examples.streaming.NetworkWordCount \
    --master local[2] \
    --deploy-mode client \
    ./target/scala-2.11/spark-2-streaming-nosuchmethod-arrowassoc-assembly-1.0.jar \
    localhost 5555
    
    ...
    -------------------------------------------
    Time: 1473344668000 ms
    -------------------------------------------
    (some,2)
    (go,1)
    (numbers,1)
    (Spark,1)
    (print,1)
    (count,1)
    (words,1)
    (and,1)


#### Then see what happens if we use `ArrowAssoc`:
    
    git checkout master

See the []lines changed](https://github.com/utgarda/spark-2-streaming-nosuchmethod-arrowassoc/blob/master/src/main/scala/org/apache/spark/examples/streaming/NetworkWordCount.scala#L56) 

    wordCounts.map{
      case (w, c) => Map(w -> c)
    }.print()

Build and run version 1.1: 

    sbt assembly

    spark-2.0.0-bin-hadoop2.7/bin/spark-submit \
    --class org.apache.spark.examples.streaming.NetworkWordCount \
    --master local[2] \
    --deploy-mode client \
    ./target/scala-2.11/spark-2-streaming-nosuchmethod-arrowassoc-assembly-1.1.jar \
    localhost 5555

give it some words to count:

    Exception in thread "main" org.apache.spark.SparkException: Job aborted due to stage failure: Task 0 in stage 72.0 failed 1 times, most recent failure: Lost task 0.0 in stage 72.0 (TID 37, localhost): java.lang.NoSuchMethodError: scala.Predef$.ArrowAssoc(Ljava/lang/Object;)Ljava/lang/Object;
            at org.apache.spark.examples.streaming.NetworkWordCount$$anonfun$main$1.apply(NetworkWordCount.scala:57)

event if we explicitly tell sbt-assembly to include Scala runtime.

And it works ok if we set master in the driver code, remove `"provied"` from `build.sbt`
and just run it with `sbt "run localhost 5555"`