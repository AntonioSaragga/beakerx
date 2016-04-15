# Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import plotapi.abstractChart as abstractChart

class Plot(abstractChart.AbstractChart):
    def transform (self):
        out = {}
        out['type'] = "Plot"
        self._transform(out)
        return out

    def _transform (self, out):
        super()._transform(out)


def transformBack (obj):
    return Plot()