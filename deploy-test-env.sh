#!/usr/bin/env bash
HBASE_VERSION="2.0.0"
echo "Deploying HBase $HBASE_VERSION"
mkdir -p ./build/testing
cd ./build/testing
wget http://mirror.linux-ia64.org/apache/hbase/$HBASE_VERSION/hbase-$HBASE_VERSION-bin.tar.gz

tar -xf hbase-2.0.0-bin.tar.gz
cd hbase-2.0.0
echo "Starting HBase $HBASE_VERSION"
./bin/start-hbase.sh
