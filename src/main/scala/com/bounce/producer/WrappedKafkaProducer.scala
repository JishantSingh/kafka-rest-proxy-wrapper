package com.bounce.producer

import org.apache.avro.generic.GenericRecord
import scalaj.http._

trait WrappedKafkaProducer {
  val apiEndpoint: String
  def send(record: GenericRecord, topic: String): HttpResponse[String]
  def request(topic: String)(data:String): HttpResponse[String]
}
object WrappedKafkaProducer {
  val WrapperValueSchemaFieldName = "value_schema"
  val WrapperRecordsArrayName = "records"
  val WrapperArrayElementName = "value_data"
  val WrapperArraySubElementName = "value"
  val HttpContentType = "application/vnd.kafka.avro.v1+json"
}
