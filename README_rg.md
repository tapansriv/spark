Need to build this with Scala version 2.13

Follow directions listed [here](https://spark.apache.org/docs/3.5.0/building-spark.html#change-scala-version).

Run `./dev/change-scala-version.sh 2.13`
Before running `./build/mvn -DskipTests -Pscala-2.13 clean package`
