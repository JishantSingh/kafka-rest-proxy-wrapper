package com.bounce.producer

import java.io.File

import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.file.DataFileReader
import org.apache.avro.generic.{GenericData, GenericDatumReader, GenericRecord}
import org.apache.avro.{Schema, SchemaBuilder}
import scalaj.http.{Http, HttpResponse}

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

object WrappedKafkaAvroProducer
  extends WrappedKafkaProducer with App {
  def apply() = {

  }
  val apiEndpoint:String = "http://localhost:8082"
  def valueDataSchema(schema: Schema) = SchemaBuilder
    .record(WrappedKafkaProducer.WrapperArrayElementName)
    .fields()
    .name(WrappedKafkaProducer.WrapperArraySubElementName)
    .`type`(schema)
    .noDefault()
    .endRecord()


  override def send(record: GenericRecord, topic: String): HttpResponse[String] = {
    val wrappedObject = getWrappedObject(record, getWrappedSchema(record.getSchema))
    val injection = GenericAvroCodecs.toJson[GenericRecord](wrappedObject.getSchema)
    val serializedRecord = injection.apply(wrappedObject)
    request(serializedRecord)(topic)
  }

//  def send(records: Array[GenericRecord], topic: String): HttpResponse[String] = {
//
//  }

  def send(records: Array[Integer]) = records.foreach(println)

  override def request(data: String)(topic: String):HttpResponse[String] = {
    Http(s"$apiEndpoint/topics/$topic")
      .postData(data)
      .header("Content-type", WrappedKafkaProducer.HttpContentType).asString
  }

  def getWrappedObject(record: GenericRecord, wrappedSchema: Schema): GenericRecord = {
    val wrappedObject = new GenericData.Record(wrappedSchema)
    wrappedObject.put(WrappedKafkaProducer.WrapperValueSchemaFieldName, record.getSchema.toString)
    val valueData = new GenericData.Record(valueDataSchema(record.getSchema))
    valueData.put(WrappedKafkaProducer.WrapperArraySubElementName, record)
    val recordsArray = new GenericData.Array[GenericRecord](wrappedSchema.getField(WrappedKafkaProducer.WrapperRecordsArrayName).schema(), List(valueData))
    wrappedObject.put(WrappedKafkaProducer.WrapperRecordsArrayName, recordsArray)
    wrappedObject
  }

  def getWrappedSchema(schema: Schema): Schema =
    SchemaBuilder
      .record("main")
      .fields()
      .name("value_schema")
      .`type`.stringType()
      .noDefault()
      .name("records")
      .`type`.array()
      .items(valueDataSchema(schema))
      .noDefault()
      .endRecord()


//  val x = (new Schema.Parser).parse(scala.io.Source.fromFile("avro/userdatawrapper.avsc").mkString).toString(false)
//
//  val fileReader = new DataFileReader[GenericRecord](new File("avro/userdata1.avro"), new GenericDatumReader[GenericRecord](schema))
//  val y = fileReader.next
//  println(getWrappedObject(y, getWrappedSchema(y.getSchema)))
//
//  println(send(y,"fifth"))
}
