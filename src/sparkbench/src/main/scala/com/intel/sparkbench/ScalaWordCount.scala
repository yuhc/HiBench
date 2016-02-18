/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.sparkbench.wordcount

import com.intel.sparkbench.IOCommon
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._

/*
 * Adopted from spark's example: https://spark.apache.org/examples.html
 */
object ScalaWordCount{
  def main(args: Array[String]){
    if (args.length < 2){
      System.err.println(
        s"Usage: $ScalaWordCount <INPUT_HDFS> <OUTPUT_HDFS>"
      )
      System.exit(1)
    }

    val t1 = System.nanoTime()                                                                                

    val sparkConf = new SparkConf().setAppName("ScalaWordCount")
    val sc = new SparkContext(sparkConf)

    val t2 = System.nanoTime()                                                                                

    val io = new IOCommon(sc)
    val data = io.load[String](args(0))
    
    val t3 = System.nanoTime()                                                                                

    val counts = data.flatMap(line => line.split(" "))
                     .map(word => (word, 1))
                     .reduceByKey(_ + _)

    val t4 = System.nanoTime()                                                                                
    io.save(args(1), counts)

    val t5 = System.nanoTime()
    val str = "wordcount benchmark\n" +
              "Elapsed time: " + (t2 - t1)/1e9 + "s to set up context\n" +
              "Elapsed time: " + (t3 - t2)/1e9 + "s to load data\n" +
              "Elapsed time: " + (t4 - t3)/1e9 + "s to finish alogrithm\n" +
              "Elapsed time: " + (t5 - t4)/1e9 + "s to save the output\n\n" 
    scala.tools.nsc.io.File("wordcount.result").writeAll(str)

    sc.stop()
  }
}
