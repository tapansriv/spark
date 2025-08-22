Need to build this with Scala version 2.13

Follow directions listed [here](https://spark.apache.org/docs/3.5.0/building-spark.html#change-scala-version).

Run `./dev/change-scala-version.sh 2.13`
Before running `./build/mvn -DskipTests -Pscala-2.13 clean package`




Need to install Scala 2.13 (recommendation is to use Coursier) [here](https://www.scala-lang.org/download/).
Then need to make sure javac is also installed along with java 17. Run `sudo apt
install openjdk-17-jre-headless` to install javac in the correct folder as well.

