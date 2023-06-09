# Copyright (c) 2019-present Sonatype, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM sonatype/nexus-platform-cli:0.0.20190220-163049.9ebe2a7

ENV SONATYPE_DIR=/opt/sonatype 

COPY NexusPublisher.groovy ${SONATYPE_DIR}/bin/

CMD ["sh", "-c", "groovy ${SONATYPE_DIR}/bin/NexusPublisher.groovy --username ${PLUGIN_USERNAME} --password ${PLUGIN_PASSWORD} \
    --serverurl=${PLUGIN_SERVER_URL} --filename=${PLUGIN_FILENAME} --format=${PLUGIN_FORMAT} --repository=${PLUGIN_REPOSITORY} ${PLUGIN_ATTRIBUTES}"]