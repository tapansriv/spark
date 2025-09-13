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

package org.apache.spark.sql.execution.datasources.parquet

import scala.collection.mutable

import org.apache.spark.util.AccumulatorV2

package object parquet {
  type RowGroupInfo = (Int, Long, Long)
  type FileGroups = (String, Seq[RowGroupInfo])
  type AccumulatorType = AccumulatorV2[FileGroups, Map[String, Seq[RowGroupInfo]]]
}

// class RowGroupAccumulator extends AccumulatorV2[(String, Seq[(Int, Long, Long)]),
// Map[String, Seq[(Int, Long, Long)]]] {
class RowGroupAccumulator extends parquet.AccumulatorType {
    private val backing = mutable.HashMap.empty[String, Vector[parquet.RowGroupInfo]]
    override def isZero: Boolean = backing.isEmpty

    override def copy(): parquet.AccumulatorType = {
      val c = new RowGroupAccumulator
      c.backing ++= backing
      c
    }

    override def reset(): Unit = this.backing.clear()

    override def add(v: parquet.FileGroups): Unit = {
      val (file, groups) = v
      val prev = backing.getOrElse(file, Vector.empty)
      backing.update(file, prev ++ groups)
    }

    override def merge(other: parquet.AccumulatorType): Unit = {
      other.value.foreach { case (file, groups) =>
        val prev = backing.getOrElse(file, Vector.empty)
        backing.update(file, prev ++ groups)
      }
    }

    override def value: Map[String, Seq[parquet.RowGroupInfo]] =
      backing.view.mapValues(_.toSeq).toMap
}
