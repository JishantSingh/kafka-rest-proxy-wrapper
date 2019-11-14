package com.bounce.producer

import com.bounce.config.AppConfig
import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.{Schema, SchemaBuilder}
import scalaj.http.{Http, HttpResponse}

import scala.collection.JavaConversions._

object WrappedKafkaAvroProducer extends WrappedKafkaProducer with App {

  val appConfig: AppConfig = new AppConfig

  def valueDataSchema(schema: Schema) =
    SchemaBuilder
      .record(appConfig.arrayElementName)
      .fields()
      .name(appConfig.fieldArraySubElement)
      .`type`(schema)
      .noDefault()
      .endRecord()

  override def send(record: GenericRecord,
                    topic: String): HttpResponse[String] = {
    val wrappedObject =
      getWrappedObject(record, getWrappedSchema(record.getSchema))
    val injection =
      GenericAvroCodecs.toJson[GenericRecord](wrappedObject.getSchema)
    val serializedRecord = injection.apply(wrappedObject)
    request(serializedRecord)(topic)
  }

//  def send(records: Array[GenericRecord], topic: String): HttpResponse[String] = {
//    if(records.isEmpty)
//      return HttpResponse("Empty array of records, nothing to do", 200,)
//  }

  def send(records: Array[Integer]) = records.foreach(println)

  override def request(data: String)(topic: String): HttpResponse[String] = {
    Http(s"${appConfig.restEndpoint}/topics/$topic")
      .postData(data)
      .header("Content-type", appConfig.httpContentType)
      .asString
  }

  def getWrappedObject(record: GenericRecord,
                       wrappedSchema: Schema): GenericRecord = {
    val wrappedObject = new GenericData.Record(wrappedSchema)
    wrappedObject.put(appConfig.fieldValueSchema, record.getSchema.toString)
    val valueData = new GenericData.Record(valueDataSchema(record.getSchema))
    valueData.put(appConfig.fieldArraySubElement, record)
    val recordsArray = new GenericData.Array[GenericRecord](
      wrappedSchema.getField(appConfig.fieldRecordsArray).schema(),
      List(valueData)
    )
    wrappedObject.put(appConfig.fieldRecordsArray, recordsArray)
    wrappedObject
  }

  def getWrappedSchema(schema: Schema): Schema =
    SchemaBuilder
      .record("main")
      .fields()
      .name(appConfig.fieldValueSchema)
      .`type`
      .stringType()
      .noDefault()
      .name(appConfig.fieldRecordsArray)
      .`type`
      .array()
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
