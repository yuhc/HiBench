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

package com.intel.sparkbench.sort

import com.intel.sparkbench.IOCommon
import org.apache.spark._
import org.apache.spark.rdd._

import scala.reflect.ClassTag


object ScalaSort{
  implicit def rddToHashedRDDFunctions[K : Ordering : ClassTag, V: ClassTag]
         (rdd: RDD[(K, V)]) = new ConfigurableOrderedRDDFunctions[K, V, (K, V)](rdd)

  def main(args: Array[String]){
    if (args.length != 2){
      System.err.println(
        s"Usage: $ScalaSort <INPUT_HDFS> <OUTPUT_HDFS>"
      )
      System.exit(1)
    }

    val t1 = System.nanoTime()

    val sparkConf = new SparkConf().setAppName("ScalaSort")
    val sc = new SparkContext(sparkConf)

    val parallel = sc.getConf.getInt("spark.default.parallelism", sc.defaultParallelism)
    val reducer  = IOCommon.getProperty("hibench.default.shuffle.parallelism")
      .getOrElse((parallel / 2).toString).toInt

    val t2 = System.nanoTime()

    val io = new IOCommon(sc)
    val data = io.load[String](args(0)).map((_, 1))

    val t3 = System.nanoTime()

    val partitioner = new HashPartitioner(partitions = reducer)
    val sorted = data.sortByKeyWithPartitioner(partitioner = partitioner).map(_._1)

    val t4 = System.nanoTime()

    io.save(args(1), sorted)
    val t5 = System.nanoTime()

    val str = "sort benchmark\n" +
              "Elapsed time: " + (t2 - t1)/1e9 + "s to set up context\n" +
              "Elapsed time: " + (t3 - t2)/1e9 + "s to load data\n" +
              "Elapsed time: " + (t4 - t3)/1e9 + "s to finish alogrithm\n" +
              "Elapsed time: " + (t5 - t4)/1e9 + "s to save the output\n\n"
    scala.tools.nsc.io.File("sort.result").writeAll(str)

    sc.stop()
  }
}
