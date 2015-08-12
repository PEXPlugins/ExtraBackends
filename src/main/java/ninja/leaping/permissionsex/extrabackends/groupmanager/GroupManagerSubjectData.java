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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.permissionsex.backend.ConversionUtils;
import ninja.leaping.permissionsex.extrabackends.ReadOnlySubjectData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupManagerSubjectData extends ReadOnlySubjectData {
    private static final TypeToken<Map<String, String>> TYPE_MAP_STRING_STRING = new TypeToken<Map<String, String>>() {};
    private final String identifier;
    private final GroupManagerDataStore dataStore;
    private final EntityType type;

    public GroupManagerSubjectData(final String identifier, GroupManagerDataStore dataStore, EntityType type) {
        this.identifier = identifier;
        this.dataStore = dataStore;
        this.type = type;
    }

    private static boolean isValidContexts(Set<Map.Entry<String, String>> contexts) {
        if (contexts.size() == 1 && contexts.iterator().next().getKey().equals("world")) {
            return true;
        } else if (contexts.isEmpty()) {
            return true;
        }
        return false;
    }

    private ConfigurationNode getNodeForContexts(Set<Map.Entry<String, String>> contexts) {
        if (!isValidContexts(contexts)) {
            return null;
        }

        ConfigurationNode rootNode;
        if (contexts.isEmpty()) {
            rootNode = this.type.getGlobalNode(this.dataStore);
        } else {
            rootNode = this.type.getWorldNode(this.dataStore, contexts.iterator().next().getValue());
        }

        if (rootNode != null) {
            ConfigurationNode ret = this.type.getNodeForSubject(rootNode, this.identifier);
            if (!ret.isVirtual()) {
                return ret;
            }
        }

        return null;
    }

    @Override
    public Map<Set<Map.Entry<String, String>>, Map<String, String>> getAllOptions() {
        return Maps.filterValues(Maps.asMap(getActiveContexts(), new Function<Set<Map.Entry<String, String>>, Map<String, String>>() {
            @Nullable
            @Override
            public Map<String, String> apply(Set<Map.Entry<String, String>> input) {
                return getOptions(input);
            }
        }), new Predicate<Map<String, String>>() {
            @Override
            public boolean apply(@Nullable Map<String, String> input) {
                return input != null && !input.isEmpty();
            }
        });
    }

    @Override
    public Map<String, String> getOptions(Set<Map.Entry<String, String>> contexts) {
        ConfigurationNode specificNode = getNodeForContexts(contexts);
        if (specificNode == null) {
            return ImmutableMap.of();
        }
        try {
            return specificNode.getNode("info").getValue(TYPE_MAP_STRING_STRING, ImmutableMap.<String, String>of());
        } catch (ObjectMappingException e) {
            return ImmutableMap.of();
        }
    }

    @Override
    public Map<Set<Map.Entry<String, String>>, Map<String, Integer>> getAllPermissions() {
        return Maps.filterValues(Maps.asMap(getActiveContexts(), new Function<Set<Map.Entry<String, String>>, Map<String, Integer>>() {
            @Nullable
            @Override
            public Map<String, Integer> apply(Set<Map.Entry<String, String>> input) {
                return getPermissions(input);
            }
        }), new Predicate<Map<String, Integer>>() {
            @Override
            public boolean apply(@Nullable Map<String, Integer> input) {
                return input != null && !input.isEmpty();
            }
        });
    }

    @Override
    public Map<String, Integer> getPermissions(Set<Map.Entry<String, String>> contexts) {
        ConfigurationNode specificNode = getNodeForContexts(contexts);
        if (specificNode == null) {
            return ImmutableMap.of();
        }
        final Map<String, Integer> ret = Maps.newHashMap();
        for (ConfigurationNode node : specificNode.getNode("permissions").getChildrenList()) {
            String perm = node.getString();
            if (perm == null) {
                continue;
            }
            if (perm.equals("*")) {
                continue;
            }

            int val = 1;
            if (perm.startsWith("-")) {
                val = -1;
                perm = perm.substring(1);
            }
            perm = ConversionUtils.convertLegacyPermission(perm);
            ret.put(perm, val);
        }
        return ret;
    }

    @Override
    public Map<Set<Map.Entry<String, String>>, List<Map.Entry<String, String>>> getAllParents() {
        return Maps.filterValues(Maps.asMap(getActiveContexts(), new Function<Set<Map.Entry<String, String>>, List<Map.Entry<String, String>>>() {
            @Nullable
            @Override
            public List<Map.Entry<String, String>> apply(Set<Map.Entry<String, String>> input) {
                return getParents(input);
            }
        }), new Predicate<List<Map.Entry<String, String>>>() {
            @Override
            public boolean apply(@Nullable List<Map.Entry<String, String>> input) {
                return input != null && !input.isEmpty();
            }
        });
    }

    @Override
    public List<Map.Entry<String, String>> getParents(Set<Map.Entry<String, String>> contexts) {
        ConfigurationNode specificNode = getNodeForContexts(contexts);
        if (specificNode == null) {
            return ImmutableList.of();
        }

        try {
            return Lists.transform(specificNode.getNode(this.type.getInheritanceKey()).getList(TypeToken.of(String.class)), new Function<String, Map.Entry<String, String>>() {
                @Nullable
                @Override
                public Map.Entry<String, String> apply(@Nullable String input) {
                    if (input.startsWith("g:")) {
                        input = input.substring(2);
                    }
                    return Maps.immutableEntry("group", input);
                }
            });
        } catch (ObjectMappingException e) {
            return ImmutableList.of();
        }
    }

    @Override
    public int getDefaultValue(Set<Map.Entry<String, String>> contexts) {
        ConfigurationNode specificNode = getNodeForContexts(contexts);
        if (specificNode == null) {
            return 0;
        }
        List<Object> values = specificNode.getNode("permissions").getList(Functions.identity());
        if (values.contains("*")) {
            return 1;
        } else if (values.contains("-*")) {
            return -1;
        }

        return 0;
    }

    @Override
    public Set<Set<Map.Entry<String, String>>> getActiveContexts() {
        ImmutableSet.Builder<Set<Map.Entry<String, String>>> activeContextsBuilder = ImmutableSet.builder();
        if (getNodeForContexts(ImmutableSet.<Map.Entry<String, String>>of()) != null) {
            activeContextsBuilder.add(ImmutableSet.<Map.Entry<String, String>>of());
        }

        for (String world : this.dataStore.getKnownWorlds()) {
            final Set<Map.Entry<String, String>> worldContext = ImmutableSet.of(Maps.immutableEntry("world", world));
            if (getNodeForContexts(worldContext) != null) {
                activeContextsBuilder.add(worldContext);
            }
        }

        return activeContextsBuilder.build();
    }

    @Override
    public Map<Set<Map.Entry<String, String>>, Integer> getAllDefaultValues() {
        return Maps.filterValues(Maps.asMap(getActiveContexts(), new Function<Set<Map.Entry<String, String>>, Integer>() {
            @Nullable
            @Override
            public Integer apply(Set<Map.Entry<String, String>> input) {
                return getDefaultValue(input);
            }
        }), new Predicate<Integer>() {
            @Override
            public boolean apply(@Nullable Integer input) {
                return input != null && input != 0;
            }
        });
    }
}
