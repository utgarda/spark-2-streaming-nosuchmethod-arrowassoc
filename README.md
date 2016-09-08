# spark-2-streaming-nosuchmethod-arrowassoc
An illustration of the issue http://stackoverflow.com/questions/36635586/nosuchmethoderror-spark-kafka-java

I'm not sure where exactly the problem is, so I recreated it as briefly as I could:
* packaging jobs for Spark 2.0.0 with [sbt-assembly 0.14.3](https://github.com/sbt/sbt-assembly) 
* getting errors about missing methods from Scala runtime, like `java.lang.NoSuchMethodError: scala.Predef$.ArrowAssoc`

Suppose we want to use 
Original example: [NetworkWordCount.scala](https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/NetworkWordCount.scala)


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
