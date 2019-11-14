package com.bounce.producer

import org.apache.avro.generic.GenericRecord
import scalaj.http._

trait WrappedKafkaProducer {
  def send(record: GenericRecord, topic: String): HttpResponse[String]
  def request(topic: String)(data: String): HttpResponse[String]
}
