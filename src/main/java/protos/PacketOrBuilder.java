// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: packet.proto

package protos;

public interface PacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Packet)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 id = 8;</code>
   * @return The id.
   */
  int getId();

  /**
   * <code>string type = 1;</code>
   * @return The type.
   */
  java.lang.String getType();
  /**
   * <code>string type = 1;</code>
   * @return The bytes for type.
   */
  com.google.protobuf.ByteString
      getTypeBytes();

  /**
   * <code>int32 size = 2;</code>
   * @return The size.
   */
  int getSize();

  /**
   * <code>int32 sequenceNum = 3;</code>
   * @return The sequenceNum.
   */
  int getSequenceNum();

  /**
   * <code>string checksum = 4;</code>
   * @return The checksum.
   */
  java.lang.String getChecksum();
  /**
   * <code>string checksum = 4;</code>
   * @return The bytes for checksum.
   */
  com.google.protobuf.ByteString
      getChecksumBytes();

  /**
   * <code>string fileName = 5;</code>
   * @return The fileName.
   */
  java.lang.String getFileName();
  /**
   * <code>string fileName = 5;</code>
   * @return The bytes for fileName.
   */
  com.google.protobuf.ByteString
      getFileNameBytes();

  /**
   * <code>int32 swarmId = 6;</code>
   * @return The swarmId.
   */
  int getSwarmId();

  /**
   * <code>bytes data = 7;</code>
   * @return The data.
   */
  com.google.protobuf.ByteString getData();

  /**
   * <code>string hostLocation = 9;</code>
   * @return The hostLocation.
   */
  java.lang.String getHostLocation();
  /**
   * <code>string hostLocation = 9;</code>
   * @return The bytes for hostLocation.
   */
  com.google.protobuf.ByteString
      getHostLocationBytes();
}
