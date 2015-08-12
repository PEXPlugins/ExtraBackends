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
package ninja.leaping.permissionsex.extrabackends.groupmanager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.permissionsex.data.ContextInheritance;
import ninja.leaping.permissionsex.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupManagerContextInheritance implements ContextInheritance {
    private final Map<String, List<Map.Entry<String, String>>> worlds;

    public GroupManagerContextInheritance(ConfigurationNode mirrorsNode) {
        worlds = new HashMap<>();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : mirrorsNode.getChildrenMap().entrySet()) {
            final Map.Entry<String, String> worldContext = Maps.immutableEntry("world", entry.getKey().toString());
            for (Object child : entry.getValue().getChildrenMap().keySet()) {
                List<Map.Entry<String, String>> world = worlds.get(child.toString());
                if (world == null) {
                    world = new ArrayList<>();
                    worlds.put(child.toString(), world);
                }
                world.add(worldContext);
            }
        }
    }


    @Override
    public List<Map.Entry<String, String>> getParents(Map.Entry<String, String> context) {
        if (!context.getKey().equals("world")) {
            return ImmutableList.of();
        }

        List<Map.Entry<String, String>> ret = worlds.get(context.getValue());
        if (ret == null) {
            return ImmutableList.of();
        }
        return ret;
    }

    @Override
    public ContextInheritance setParents(Map.Entry<String, String> context, List<Map.Entry<String, String>> parents) {
        return this;
    }

    @Override
    public Map<Map.Entry<String, String>, List<Map.Entry<String, String>>> getAllParents() {
        final ImmutableMap.Builder<Map.Entry<String, String>, List<Map.Entry<String, String>>> ret = ImmutableMap.builder();
        for (Map.Entry<String, List<Map.Entry<String, String>>> entry : worlds.entrySet()) {
            ret.put(Util.subjectFromString(entry.getKey()), entry.getValue());
        }
        return ret.build();
    }
}
