/**
 * PermissionsEx
 * Copyright (C) zml and PermissionsEx contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.leaping.permissionsex.extrabackends;

import ninja.leaping.permissionsex.data.ImmutableOptionSubjectData;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ReadOnlySubjectData implements ImmutableOptionSubjectData {
    @Override
    public ImmutableOptionSubjectData setOption(Set<Map.Entry<String, String>> contexts, String key, String value) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData setOptions(Set<Map.Entry<String, String>> contexts, Map<String, String> values) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData clearOptions(Set<Map.Entry<String, String>> contexts) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData clearOptions() {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData setPermission(Set<Map.Entry<String, String>> contexts, String permission, int value) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData setPermissions(Set<Map.Entry<String, String>> contexts, Map<String, Integer> values) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData clearPermissions() {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData clearPermissions(Set<Map.Entry<String, String>> contexts) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData addParent(Set<Map.Entry<String, String>> contexts, String type, String identifier) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData removeParent(Set<Map.Entry<String, String>> contexts, String type, String identifier) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData setParents(Set<Map.Entry<String, String>> contexts, List<Map.Entry<String, String>> parents) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData clearParents() {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData clearParents(Set<Map.Entry<String, String>> contexts) {
        return this;
    }

    @Override
    public ImmutableOptionSubjectData setDefaultValue(Set<Map.Entry<String, String>> contexts, int defaultValue) {
        return this;
    }
}
