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

import java.util
import java.util.concurrent.{ConcurrentHashMap, CopyOnWriteArrayList}

object RowGroupMetricsRegistry {
  // ParquetFileFormat registers one accumulator per Parquet scan, so a query with several
  // scans (any join, or one table read twice) registers several under one execution id.
  // put() used to replace the previous one, so only the last scan's row groups could be read
  // back: a join of partsupp and part reported part only. Keep all of them.
  private val byExecId = new ConcurrentHashMap[String, CopyOnWriteArrayList[RowGroupAccumulator]]()
  // put() runs on whichever thread plans the scan, and the scans of one query can be planned
  // concurrently (scalar subqueries are). A plain ArrayList corrupts on concurrent add; it
  // surfaced in the placement harness as "ArrayIndexOutOfBoundsException: Index 34 out of
  // bounds for length 33" on TPC-DS q6. Synchronized, and still a java.util.List for callers.
  val keys: util.List[String] = util.Collections.synchronizedList(new util.ArrayList[String]())

  def put(execId: String, acc: RowGroupAccumulator): Unit = {
    keys.add(execId)
    byExecId.computeIfAbsent(execId, _ => new CopyOnWriteArrayList[RowGroupAccumulator]()).add(acc)
  }

  /** Every scan's accumulator for this execution, in registration order. */
  def getAll(execId: String): Seq[RowGroupAccumulator] = {
    val accs = byExecId.get(execId)
    if (accs == null) Seq.empty
    else accs.toArray(new Array[RowGroupAccumulator](0)).toSeq
  }

  /** The most recently registered accumulator only, i.e. the old single-scan behaviour, kept
   *  so existing callers compile. Use getAll to see every scan. */
  def get(execId: String): Option[RowGroupAccumulator] =
    getAll(execId).lastOption

  def remove(execId: String): Unit = {
    keys.remove(execId)
    byExecId.remove(execId)
  }
}
