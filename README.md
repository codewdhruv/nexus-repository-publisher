#### bitbucket-nexus-platform-pipe-demo

This is a demo repository which makes use of the `sonatype/bitbucket-platform-pipe` pipe.

See https://bitbucket.org/sonatype/bitbucket-nexus-platform-pipe/

FROM sonatype/nexus-platform-cli:0.0.20190220-163049.9ebe2a7

ENV SONATYPE_DIR=/opt/sonatype 

COPY NexusPublisher.groovy ${SONATYPE_DIR}/bin/

CMD ["sh", "-c", "groovy ${SONATYPE_DIR}/bin/NexusPublisher.groovy --username ${PLUGIN_USERNAME} --password ${PLUGIN_PASSWORD} \
    --serverurl=${PLUGIN_SERVERURL} --filename=${PLUGIN_FILENAME} --format=${PLUGIN_FORMAT} --repository=${PLUGIN_REPOSITORY} \
    --component-attributes=${PLUGIN_COMPONENT_ATTRIBUTES} --asset-attributes=${PLUGIN_ASSET_ATTRIBUTES}"]

