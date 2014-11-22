#! /bin/bash
cd `dirname $0`

java -cp .:* org.talend.gwtadministrator.server.remoteconnection.MetaServletCaller --tac-url=http://localhost:9999/org.talend.administrator "--json-params={"actionName":"taskLog","taskId":"1","authPass":"lolli","authUser":"jan.lolling@cimt-ag.de"}"

