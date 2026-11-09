#!/bin/sh
set -eu

# Render entrega el puerto en la variable PORT. Tomcat, por defecto, escucha
# en 8080; se ajusta el conector antes de arrancar.
PORT="${PORT:-8080}"

sed -i "s/<Connector port=\"8080\"/<Connector port=\"${PORT}\"/" \
    "$CATALINA_HOME/conf/server.xml"

exec "$CATALINA_HOME/bin/catalina.sh" run
