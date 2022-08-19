package com.eligolin

import com.eligolin.schema.entities.Proc
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.api.scala._
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import scalapb.validate.Validator

import java.util.concurrent.TimeUnit

object Job {

  def main(args: Array[String]): Unit = {
    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // get input data
    val comps = env.fromElements(Proc("Computer1",Some(23.1),Some(89.2f)))

    val sink: StreamingFileSink[String] = StreamingFileSink
      .forRowFormat(new Path("file:///tmp/flink_sink"), new SimpleStringEncoder[String]("UTF-8"))
      .withRollingPolicy(
        DefaultRollingPolicy.builder()
          .withRolloverInterval(TimeUnit.MINUTES.toMillis(15))
          .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
          .withMaxPartSize(1024 * 1024 * 1024)
          .build())
      .build()

    val validatedComps = validate(comps)

    validatedComps.map(_.toString).addSink(sink)



    // execute program
    env.execute("Flink Serialization demonstration")
  }

  def validate[A:Validator](stream:DataStream[A]): DataStream[A] = {
    stream.filter{a:A => Validator[A].validate(a).isSuccess}
  }
}
