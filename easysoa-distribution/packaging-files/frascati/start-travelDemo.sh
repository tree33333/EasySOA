#!/bin/bash
LINE="----------------------------------------------------"

echo $LINE
echo "Trip demo SOA"
echo "(Deployed on http://localhost:9000)"
echo "DEPENDENCY: Running services backup"
echo $LINE

export CUSTOM_JAVA_OPTS=-Dcxf.config.file=cxfEsperProxy.xml
./bin/frascati run smart-travel-mock-services.composite -libpath ./sca-apps/easysoa-samples-smarttravel-trip-0.4-SNAPSHOT.jar ./sca-apps/easysoa-samples-smarttravel-summary-model-0.4-SNAPSHOT.jar

#PS: The summary-model library has been moved out from the lib/ folder due to a classloader conflict with the EasySOA Light trip proxy.
