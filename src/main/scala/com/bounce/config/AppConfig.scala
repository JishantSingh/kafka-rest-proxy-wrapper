package com.bounce.config
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._

final class AppConfig(conf: Config, args:Map[String,String]) {
  conf.checkValid(ConfigFactory.defaultReference(), "test")
  def this() {
    this(ConfigFactory.load(), Map.empty[String,String])
  }
  val kafkaConfig: Config = conf.getConfig(AppConfig.KeyNames.KAFKA_REST_CONFIG)
  val restEndpoint: String = kafkaConfig.getString(AppConfig.KeyNames.REST_PROXY_ENDPOINT)
  val httpContentType: String = kafkaConfig.getString(AppConfig.KeyNames.HTTP_CONTENT_TYPE)
  val wrapperSchema: Config = conf.getConfig(AppConfig.KeyNames.WRAPPER_SCHEMA_CONFIG)
  val fieldValueSchema: String = wrapperSchema.getString(AppConfig.KeyNames.FIELD_VALUE_SCHEMA)
  val fieldRecordsArray: String = wrapperSchema.getString(AppConfig.KeyNames.FIELD_RECORDS_ARRAY)
  val arrayElementName: String = wrapperSchema.getString(AppConfig.KeyNames.ARRAY_ELEMENT_NAME)
  val fieldArraySubElement: String = wrapperSchema.getString(AppConfig.KeyNames.FIELD_ARRAY_SUB_ELEMENT)
  def printer = println(restEndpoint)
}

object AppConfig{

  def configProps(subConfig: Config): Map[String, String] = {
    val keys = subConfig.entrySet().asScala.map(_.getKey).toSet
    keys.foldLeft(Map.empty[String, String])((acc, key) => acc + (key -> subConfig.getString(key)))
  }

  def configProp(subConfig: Config, key: String): String =
    subConfig.getString(key)

  object KeyNames {
    val KAFKA_REST_CONFIG = "kafka_rest_proxy"
    val REST_PROXY_ENDPOINT = "rest_endpoint"
    val HTTP_CONTENT_TYPE = "http_content_type"
    val WRAPPER_SCHEMA_CONFIG = "wrapper_schema"
    val FIELD_VALUE_SCHEMA = "field_value_schema"
    val FIELD_RECORDS_ARRAY = "field_records_array"
    val ARRAY_ELEMENT_NAME = "value_data"
    val FIELD_ARRAY_SUB_ELEMENT = "field_array_sub_element"
  }
}

