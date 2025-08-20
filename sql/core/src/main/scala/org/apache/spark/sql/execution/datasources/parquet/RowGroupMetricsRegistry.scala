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
import java.util.concurrent.ConcurrentHashMap

object RowGroupMetricsRegistry {
  private val byExecId = new ConcurrentHashMap[String, RowGroupAccumulator]()
  val keys = new util.ArrayList[String]()

  def put(execId: String, acc: RowGroupAccumulator): Unit = {
    keys.add(execId)
    byExecId.put(execId, acc)
  }

  def get(execId: String): Option[RowGroupAccumulator] =
    Option(byExecId.get(execId))

  def remove(execId: String): Unit = {
    keys.remove(execId)
    byExecId.remove(execId)
  }
}



