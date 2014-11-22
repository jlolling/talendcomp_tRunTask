#! /bin/bash
cd `dirname $0`

java -cp .:../lib/* org.talend.gwtadministrator.server.remoteconnection.MetaServletCaller $*

