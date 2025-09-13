Need to build this with Scala version 2.13

Follow directions listed [here](https://spark.apache.org/docs/3.5.0/building-spark.html#change-scala-version).

Run `./dev/change-scala-version.sh 2.13`
Before running `./build/mvn -DskipTests -Pscala-2.13 clean package`


```
curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
cs install scala:2.13.16 && cs install scalac:2.13.16
```


Need to install Scala 2.13 (recommendation is to use Coursier) [here](https://www.scala-lang.org/download/2.13.16.html) 
and install Coursier for scala [here](https://docs.scala-lang.org/getting-started/index.html#using-the-scala-installer-recommended-way).

Then need to make sure javac is also installed along with java 17. Run 
`sudo apt install openjdk-17-jre-headless` to install javac in the correct folder as well.

